/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avantekwfl1.handler;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.ComplexType;
import org.eclipse.smarthome.core.types.PrimitiveType;
import org.eclipse.smarthome.core.types.State;

/**
 * The {@link WF1LStateDTO} class holds the data and the settings for a LED device.
 *
 * @author Arne Haber - Initial contribution
 */
public class WF1LStateDTO extends PercentType implements ComplexType, State, Command {

    // constants for the constituents
    public static final String KEY_BRIGHTNESS = "b";
    public static final String KEY_HUE = "h";
    public static final String KEY_POWER = "p";
    public static final String KEY_SATURATION = "s";
    
    
    private static final long serialVersionUID = 1L;

    /**
     * Static helper which converts the given values into the needed openHab types.
     *
     * @param state 1 = on, other = off
     * @param red
     * @param green
     * @param blue
     * @param lum lumination value
     * @param colorTemp color temperature
     * @return the created DTO
     */
    public static WF1LStateDTO valueOf(int state, int red, int green, int blue) {

        OnOffType power = state == 1 ? OnOffType.ON : OnOffType.OFF;
        float[] hsv = new float[3];
        Color.RGBtoHSB(red, green, blue, hsv);
        long hue = (long) (hsv[0] * 360);
        int saturation = (int) (hsv[1] * 100);
        int brightness = (int) (hsv[2] * 100);

        DecimalType colorHue = new DecimalType(hue);
        PercentType sat = new PercentType(saturation);
        PercentType brigh = new PercentType(brightness);

        return new WF1LStateDTO(power, colorHue, sat, brigh);
    }

    private final PercentType brightness;
    private final BigDecimal colorHue;

    private final OnOffType power;

    private final PercentType saturation;
    public WF1LStateDTO(
            OnOffType power,
            DecimalType colorHue,
            PercentType saturation,
            PercentType brightness) {
        this.power = power;
        this.saturation = saturation;
        this.brightness = brightness;
        this.colorHue = colorHue.toBigDecimal();
    }
    public PercentType getBrightness() {
        return brightness;
    }


    public HSBType getColor() {
        return new HSBType(getHue(), getSaturation(), getBrightness());
    }

    @Override
    public SortedMap<String, PrimitiveType> getConstituents() {
        TreeMap<String, PrimitiveType> map = new TreeMap<String, PrimitiveType>();
        map.put(KEY_HUE, getHue());
        map.put(KEY_SATURATION, getSaturation());
        map.put(KEY_BRIGHTNESS, getBrightness());
        map.put(KEY_POWER, getPower());
        return map;
    }

    public DecimalType getHue() {
        return new DecimalType(colorHue);
    }


    public OnOffType getPower() {
        return power;
    }

    public PercentType getSaturation() {
        return saturation;
    }

}
