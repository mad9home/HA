package org.openhab.binding.spsbus.handler;

import static org.openhab.binding.spsbus.SPSBusBindingConstants.*;

import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.spsbus.internal.PLCConnector;
import org.openhab.binding.spsbus.internal.exception.IndexMissingException;
import org.openhab.binding.spsbus.internal.exception.NotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPSBusHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SPSBusHandler.class);

    private PLCConnector connector;
    private ScheduledFuture<?> pollingJob;

    public SPSBusHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
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
        pollingJob = scheduler.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.SECONDS);

        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("handleCommand called");
        connector.receive();

        Set<Item> items = linkRegistry.getLinkedItems(channelUID);
        for (Item item : items) {
            logger.debug("item name:" + item.getName());
            try {
                int index = getIndex(item);
                switch (channelUID.getId()) {
                    case CHANNEL_LIGHT:
                        updateState(channelUID, connector.getBoolean(index) == true ? OnOffType.ON : OnOffType.OFF);
                        if (!(command instanceof RefreshType)) {
                            connector.setBoolean(index, true);
                            connector.setBoolean(index, false);
                        }
                        break;
                    case CHANNEL_ROLLERSHUTTER:
                        break;
                    case CHANNEL_TEMPERATURE:
                        updateState(channelUID, new DecimalType(connector.getFloat(index)));
                        break;
                    case CHANNEL_THERMOSTAT:
                        break;
                    case CHANNEL_OUTLET:
                        break;
                    default:
                        logger.error("unknown channelUID: " + channelUID);
                        break;
                }

            } catch (NotConnectedException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Unable to reach SPS");
                logger.error("unable to reach SPS", e);
            } catch (IndexMissingException e) {
                logger.debug("", e);
            }
        }
    }

    @Override
    public void handleUpdate(ChannelUID channelUID, State newState) {
        super.handleUpdate(channelUID, newState);
        logger.debug("handleUpdated called");
    }

    @Override
    public void dispose() {
        super.dispose();
        pollingJob.cancel(true);
        updateStatus(ThingStatus.OFFLINE);
    }

    private int getIndex(Item item) throws IndexMissingException {
        if (item.getTags() == null || item.getTags().toArray().length == 0) {
            throw new IndexMissingException("index missing, might be a group");
        }
        int index = Integer.parseInt((String) item.getTags().toArray()[0]);
        switch (item.getType()) {
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
