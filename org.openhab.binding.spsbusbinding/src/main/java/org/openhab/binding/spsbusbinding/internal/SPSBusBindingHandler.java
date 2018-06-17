/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.spsbusbinding.internal;

import static org.openhab.binding.spsbusbinding.internal.SPSBusBindingBindingConstants.*;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import org.openhab.binding.spsbusbinding.internal.exception.IndexMissingException;
import org.openhab.binding.spsbusbinding.internal.exception.NotConnectedException;
import org.openhab.binding.spsbusbinding.internal.plc.PLCConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David
 *
 */
public class SPSBusBindingHandler extends BaseThingHandler {

    private static final Logger logger = LoggerFactory.getLogger(SPSBusBindingHandler.class);

    private PLCConnector connector;
    private ScheduledFuture<?> pollingJob;

    // @Nullable
    // private SPSBusBindingConfiguration config;

    public SPSBusBindingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        // config = getConfigAs(SPSBusBindingConfiguration.class);

        // Long running initialization should be done asynchronously in background.
        connector = PLCConnector.getInstance();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (Channel channel : getThing().getChannels()) {
                    handleCommand(channel.getUID(), RefreshType.REFRESH);
                }
            }
        };
        pollingJob = scheduler.scheduleWithFixedDelay(runnable, 0, 2, TimeUnit.SECONDS);

        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("handleCommand called");
        connector.receive();
        try {
            int index = getIndex(channelUID);
            logger.debug("index:" + index);
            switch (getThing().getChannel(channelUID.getId()).getChannelTypeUID().getId()) {
                case CHANNEL_SWITCH:
                    updateState(channelUID, connector.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                    if (!(command instanceof RefreshType)) {
                        connector.setBoolean(index, true);
                        connector.setBoolean(index, false);
                    }
                    updateState(channelUID, connector.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                    break;
                case CHANNEL_ROLLERSHUTTER:
                    updateState(channelUID, new PercentType(connector.getShort(index)));
                    if (!(command instanceof RefreshType)) {
                        connector.setShort(index, Short.parseShort(command.toString()));
                    }
                    updateState(channelUID, new PercentType(connector.getShort(index)));
                    break;
                case CHANNEL_SENSOR:
                    updateState(channelUID, new DecimalType(connector.getFloat(index)));
                    break;
                case CHANNEL_SETPOINT:
                    updateState(channelUID, new DecimalType(connector.getFloat(index)));
                    if (!(command instanceof RefreshType)) {
                        connector.setFloat(index, Float.parseFloat(command.toString()));
                    }
                    updateState(channelUID, new DecimalType(connector.getFloat(index)));
                    break;
                case CHANNEL_OUTLET:
                    updateState(channelUID, connector.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                    if (!(command instanceof RefreshType)) {
                        connector.setBoolean(index, true);
                        connector.setBoolean(index, false);
                    }
                    updateState(channelUID, connector.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                    break;
                default:
                    logger.error("unknown channelTypeUID.getId(): " + channelUID);
                    break;
            }
        } catch (NotConnectedException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Unable to reach SPS");
            logger.error("unable to reach SPS", e);
        } catch (IndexMissingException e) {
            logger.debug("", e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        pollingJob.cancel(true);
        updateStatus(ThingStatus.OFFLINE);
    }

    private int getIndex(ChannelUID channelUID) throws IndexMissingException {
        BigDecimal bcIndex = (BigDecimal) getThing().getConfiguration().get("index");
        int index = bcIndex.intValue();
        switch (getThing().getChannel(channelUID.getId()).getAcceptedItemType()) {
            case "Switch":
                if (index < 0 || index > PLCConnector.NUMBER_BOOLEANS) {
                    throw new IllegalStateException("Wrong boolean index for Switch: " + index);
                }
                break;
            case "Number":
                if (index < 0 || index > PLCConnector.NUMBER_FLOATS) {
                    throw new IllegalStateException("Wrong float index for Number: " + index);
                }
                break;
            case "Rollershutter":
                if (index < 0 || index > PLCConnector.NUMBER_SHORTS) {
                    throw new IllegalStateException("Wrong short index for Rollershutter: " + index);
                }
                break;
            default:
                throw new IndexMissingException("type not expected, ignoring");
        }
        return index;

    }
}
