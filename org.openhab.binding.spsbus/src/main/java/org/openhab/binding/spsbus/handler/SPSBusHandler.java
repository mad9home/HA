/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.spsbus.handler;

import static org.openhab.binding.spsbus.SPSBusBindingConstants.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.spsbus.internal.PLCConnector;
import org.openhab.binding.spsbus.internal.exception.NotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SPSBusHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author mad9home - Initial contribution
 */
public class SPSBusHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SPSBusHandler.class);

    private PLCConnector connector;
    private ScheduledFuture<?> pollingJob;

    public SPSBusHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleUpdate(ChannelUID channelUID, State newState) {
        super.handleUpdate(channelUID, newState);
        connector.receive();
        if (channelUID.getId().equals(SWITCH)) {
            try {
                logger.info("connector.getBoolean(0) = " + connector.getBoolean(0));
            } catch (NotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.info("handleCommand called");
        connector.receive();
        if (channelUID.getId().equals(TEMPERATURE)) {
            try {
                updateState(channelUID, new DecimalType(connector.getFloat(0)));
            } catch (NotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // try {
            // updateState(channelUID, new DecimalType(connector.getFloat(0)));
            // } catch (NotConnectedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        } else if (channelUID.getId().equals(SWITCH)) {
            try {
                logger.info("connector.getBoolean(0) = " + connector.getBoolean(0));
                updateState(channelUID, connector.getBoolean(0) == true ? OnOffType.ON : OnOffType.OFF);
            } catch (NotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            connector.setBoolean(0, false);
            connector.setBoolean(0, true);
        }
    }

    @Override
    public void initialize() {
        connector = PLCConnector.getInstance();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                connector.receive();
            }
        };
        pollingJob = scheduler.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.SECONDS);

        // try {
        // socket = new Socket(HOST, PORT);
        // socket.setSoTimeout(SOCKET_TIMEOUT);
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    @Override
    public void dispose() {
        super.dispose();
        pollingJob.cancel(true);
    }
}
