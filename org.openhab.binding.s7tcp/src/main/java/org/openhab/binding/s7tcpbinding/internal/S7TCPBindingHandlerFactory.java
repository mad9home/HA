/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.s7tcpbinding.internal;

import static org.openhab.binding.s7tcpbinding.internal.S7TCPBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.s7tcpbinding.handler.S7ThingHandler;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link S7TCPBindingHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author David - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.s7tcpbinding", service = ThingHandlerFactory.class)
public class S7TCPBindingHandlerFactory extends BaseThingHandlerFactory {

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_BRIDGE)) {
            return new S7TCPBindingHandler((Bridge) thing);
        } else if (thingTypeUID.equals(THING_TYPE_LIGHT) || thingTypeUID.equals(THING_TYPE_TEMPERATURE)
                || thingTypeUID.equals(THING_TYPE_ROLLERSHUTTER) || thingTypeUID.equals(THING_TYPE_THERMOSTAT)
                || thingTypeUID.equals(THING_TYPE_OUTLET)) {
            return new S7ThingHandler(thing);
        }

        return null;
    }
}