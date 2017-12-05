/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avantekwfl1.internal;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

/**
 * @author Arne Haber
 */
public class WFL1LEDState {

    public static class Result {

        private int b;

        @SerializedName("color-temp")
        private int colorTemperature;

        private int g;

        private int lum;

        @SerializedName("switch")
        private int power;

        private int r;

        public Result() {
        }

        public int getBlue() {
            return b;
        }

        public int getGreen() {
            return g;
        }

        public int getLum() {
            return lum;
        }

        public int getPower() {
            return power;
        }

        public int getRed() {
            return r;
        }
    }

    /**
     * Creates a {@link WFL1LEDState} from the given json representation.
     *
     * @param jsonRepresentation
     * @return
     * @throws JsonSyntaxException if the given string is not well formed.
     */
    public static WFL1LEDState create(String jsonRepresentation) throws JsonSyntaxException {
        Gson g = new Gson();
        return g.fromJson(jsonRepresentation, WFL1LEDState.class);
    }

    private String cmd;

    private Result result;

    private int rsp;

    private String sid;

    public WFL1LEDState() {

    }

    public String getCmd() {
        return cmd;
    }

    public Result getResult() {
        return result;
    }

    public int getRsp() {
        return rsp;
    }

    public String getSid() {
        return sid;
    }

}
