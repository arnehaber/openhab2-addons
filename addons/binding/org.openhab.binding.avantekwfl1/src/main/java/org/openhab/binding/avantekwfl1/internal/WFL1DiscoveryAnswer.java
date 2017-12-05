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

/**
 * Represents an answer from a WF-L1 bulb when a scan for devices is broadcasted.
 * 
 * @author ArneHaber
 *
 */
public class WFL1DiscoveryAnswer {

    public static class Arg {
        private String devname;

        private String did;

        private String mac;

        private String sid;

        public String getDevname() {
            return devname;
        }

        public String getDid() {
            return did;
        }

        public String getMac() {
            return mac;
        }

        public String getSid() {
            return sid;
        }
    }

    /**
     * 
     * @param jsonRep
     * @return a {@link WFL1DiscoveryAnswer} created from the given jsonRep.
     * 
     * @throws JsonSyntaxException for invalid jsonRep strings
     */
    public static WFL1DiscoveryAnswer create(String jsonRep) throws JsonSyntaxException {
        Gson g = new Gson();
        return g.fromJson(jsonRep, WFL1DiscoveryAnswer.class);
    }

    private Arg arg;

    private String cmd;

    private int rsp;

    public Arg getArg() {
        return arg;
    }

    public String getCmd() {
        return cmd;
    }

    public int getRsp() {
        return rsp;
    }

}
