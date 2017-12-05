/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avantekwfl1.internal.configuration;

/**
 * The {@link WFL1WiFiLEDConfig} class holds the configuration properties of the thing.
 *
 * @author Arne Haber - Initial contribution
 */
public class WFL1WiFiLEDConfig {

    private String ip;
    private Integer pollingPeriod;
    private Integer port;
    private String sid;

    public String getIp() {
        return ip;
    }

    public Integer getPollingPeriod() {
        return pollingPeriod;
    }

    public Integer getPort() {
        return port;
    }

    public String getSid() {
        return sid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPollingPeriod(Integer pollingPeriod) {
        this.pollingPeriod = pollingPeriod;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}
