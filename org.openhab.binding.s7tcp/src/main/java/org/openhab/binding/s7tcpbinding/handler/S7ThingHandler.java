/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.s7tcpbinding.handler;

import static org.openhab.binding.s7tcpbinding.internal.S7TCPBindingConstants.*;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.s7tcpbinding.internal.S7TCPBindingHandler;
import org.openhab.binding.s7tcpbinding.internal.S7TCPBindingHandler.S7TCPClientError;
import org.openhab.binding.s7tcpbinding.internal.config.S7TCPBindingConfiguration;
import org.openhab.binding.s7tcpbinding.internal.exception.NotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David
 *
 */
@NonNullByDefault
public class S7ThingHandler extends BaseThingHandler {
    private static final Logger logger = LoggerFactory.getLogger(S7ThingHandler.class);

    public S7ThingHandler(Thing thing) {
        super(thing);
    }

    private @Nullable S7TCPBindingHandler getBridgeHandler() {
        return (S7TCPBindingHandler) getBridge().getHandler();
    }

    @Override
    public void initialize() {
        logger.debug("Initialising S7Thing handler...");
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("handleCommand called");
        S7TCPBindingHandler bridgeHandler = getBridgeHandler();
        try {
            if (bridgeHandler == null || !bridgeHandler.isConnected()) {
                S7TCPBindingConfiguration config = getBridge().getConfiguration().as(S7TCPBindingConfiguration.class);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        String.format("Could not connect to S7TCP unit %s:%d", config.host, config.port));
            } else {
                int index = ((BigDecimal) getThing().getConfiguration().get("index")).intValue();
                logger.debug("index:" + index);
                switch (getThing().getChannel(channelUID.getId()).getChannelTypeUID().getId()) {
                    case CHANNEL_SWITCH:
                        if (!(command instanceof RefreshType)) {
                            bridgeHandler.setBoolean(index, true);
                            bridgeHandler.setBoolean(index, false);
                        }
                        updateState(channelUID, bridgeHandler.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                        break;
                    case CHANNEL_ROLLERSHUTTER:
                        if (!(command instanceof RefreshType)) {
                            bridgeHandler.setShort(index, Short.parseShort(command.toString()));
                        }
                        updateState(channelUID, new PercentType(bridgeHandler.getShort(index)));
                        break;
                    case CHANNEL_SETPOINT:
                        if (!(command instanceof RefreshType)) {
                            bridgeHandler.setFloat(index, Float.parseFloat(command.toString()));
                        }
                        updateState(channelUID, new DecimalType(bridgeHandler.getFloat(index)));
                        break;
                    case CHANNEL_OUTLET:
                        if (!(command instanceof RefreshType)) {
                            bridgeHandler.setBoolean(index, true);
                            bridgeHandler.setBoolean(index, false);
                        }
                        updateState(channelUID, bridgeHandler.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                        break;
                    default:
                        logger.error("unknown channelTypeUID.getId(): " + channelUID);
                        break;
                }
            }
        } catch (NotConnectedException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Unable to reach SPS");
            logger.error("unable to reach SPS", e);
        } catch (S7TCPClientError e) {
            logger.error("Failed to set channel {} -> {}: {}", channelUID.getId(), command, e.getMessage());
        }
    }

    public void refresh() {
        logger.debug("refresh called");
        S7TCPBindingHandler bridgeHandler = getBridgeHandler();
        try {
            for (Channel channel : getThing().getChannels()) {
                int index = ((BigDecimal) getThing().getConfiguration().get("index")).intValue();
                String channelUID = channel.getChannelTypeUID().getId();
                switch (channelUID) {
                    case CHANNEL_SWITCH:
                        updateState(channelUID, bridgeHandler.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                        break;
                    case CHANNEL_ROLLERSHUTTER:
                        updateState(channelUID, new PercentType(bridgeHandler.getShort(index)));
                        break;
                    case CHANNEL_SENSOR:
                        updateState(channelUID, new DecimalType(bridgeHandler.getFloat(index)));
                        break;
                    case CHANNEL_SETPOINT:
                        updateState(channelUID, new DecimalType(bridgeHandler.getFloat(index)));
                        break;
                    case CHANNEL_OUTLET:
                        updateState(channelUID, bridgeHandler.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                        break;
                    default:
                        logger.error("unknown channelTypeUID.getId(): " + channelUID);
                        break;
                }
            }
        } catch (NotConnectedException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Unable to reach SPS");
            logger.error("unable to reach SPS", e);
        }
    }

}
