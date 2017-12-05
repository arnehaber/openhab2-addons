/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avantekwfl1;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link WFL1BindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Arne Haber - Initial contribution
 */
public class WFL1BindingConstants {

    public static final String BINDING_ID = "wfl1";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_WIFILED = new ThingTypeUID(BINDING_ID, "wfl1");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_WIFILED);

    // List of all Channel IDs
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_COLOR = "color";

}
