/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.spsbusbinding.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link SPSBusBindingBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author David - Initial contribution
 */
@NonNullByDefault
public class SPSBusBindingBindingConstants {

    private static final String BINDING_ID = "spsbusbinding";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_UID = new ThingTypeUID(BINDING_ID, "spsbusbindingthing");

    // List of all Channel ids
    public static final String CHANNEL_LIGHT = "switch_light";
    public static final String CHANNEL_ROLLERSHUTTER = "rollershutter";
    public static final String CHANNEL_TEMPERATURE = "sensor_temperature";
    public static final String CHANNEL_THERMOSTAT = "thermostat_setpoint";
    public static final String CHANNEL_OUTLET = "outlet";

}
