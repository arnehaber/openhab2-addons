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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.openhab.binding.avantekwfl1.internal.WFL1LEDState;
import org.openhab.binding.avantekwfl1.internal.WFL1LEDState.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Driver for the WF1L LED.
 *
 * @author Arne Haber - Initial contribution
 */
public class WFL1LEDDriver {

    /**
     * Default port for the WFL1 LED.
     */
    public static final int DEFAULT_PORT = 14580;

    /**
     * Default polling period in seconds.
     */
    public static final int DEFAULT_POLLING_PERIOD = 30;

    public static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    private final Logger logger = LoggerFactory.getLogger(WFL1LEDDriver.class);

    private final String sid;
    private final String host;
    private final int port;
    
    public WFL1LEDDriver(String sid, String host, int port) {
        this.host = host;
        this.port = port;
        this.sid = sid;
    }

    /**
     * Allow to cleanup the driver
     */
    public void shutdown() {

    }

    public void setColor(HSBType color) throws IOException {
        Color col = new Color(color.getRGB());
        String cmd = getColorMsg(col.getRed(), col.getGreen(), col.getBlue());
        send(cmd);
    }
    
    /**
     * @param brightness
     */
    public void setBrightness(PercentType brightness) {
        try {
            WF1LStateDTO dto = getLEDStateDTO();
            HSBType color = dto.getColor();
            HSBType toSet = new HSBType(color.getHue(), color.getSaturation(), brightness);
            setColor(toSet);
        } catch (IOException e) {
            logger.error("Failed to get dto state {}.", e.getMessage());
           
        }
    }
    
    /**
     * @param saturation
     */
    public void setSaturation(PercentType saturation) {
        try {
            WF1LStateDTO dto = getLEDStateDTO();
            HSBType color = dto.getColor();
            HSBType toSet = new HSBType(color.getHue(), saturation, color.getBrightness());
            setColor(toSet);
        } catch (IOException e) {
            logger.error("Failed to get dto state {}.", e.getMessage());
           
        }
    }


    public void setPower(OnOffType command) throws IOException {
        String cmd = getSwitchCmd(command == OnOffType.ON);
        send(cmd);
    }

    public void init() throws IOException {
        getLEDState();
    }

    private synchronized WFL1LEDState getLEDState() throws IOException {
        String statusMsg = getStatusMsg();
        String answer = sendWithAnswer(statusMsg);
        try {
            WFL1LEDState lastStateUpdate = WFL1LEDState.create(answer);
            Result result = lastStateUpdate.getResult();
            logger.debug("State of {} updated. Power: {}, color: ({}, {}, {}).",
                    sid, result.getPower(), result.getRed(), result.getGreen(), result.getBlue());
            return lastStateUpdate;
        } catch (JsonSyntaxException e) {
            String msg = String.format(
                    "Unexpected answer '%s' from %s:%d. Exception: %s.", 
                    answer, host, port, e.getMessage());
            logger.error(msg, e);
            throw new IOException(msg);
        }
    }

    public WF1LStateDTO getLEDStateDTO() throws IOException {
        WFL1LEDState state = getLEDState();
        Result innerState = state.getResult();
        WF1LStateDTO res = WF1LStateDTO.valueOf(
                innerState.getPower(), 
                innerState.getRed(), 
                innerState.getGreen(), 
                innerState.getBlue());
        return res;
    }


    private String sendWithAnswer(String msg) throws IOException {
        return sendWithAnswer(msg, host, port);
    }

    private String sendWithAnswer(String msg, String to, int port) throws IOException {
        byte[] data = msg.getBytes();

        try (DatagramSocket udpSocket = new DatagramSocket()) {
            udpSocket.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);

            InetAddress add = InetAddress.getByName(to);

            DatagramPacket dp = new DatagramPacket(data, data.length, add, port);

            udpSocket.send(dp);

            byte[] answerBytes = new byte[1024];
            DatagramPacket answer = new DatagramPacket(answerBytes, answerBytes.length, add, port);
            udpSocket.receive(answer);

            return (new String(answerBytes).trim());

        }
    }

    private void send(String msg) {
        this.send(msg, host, port);
    }

    private void send(String msg, String to, int port) {
        logger.debug("Sending command '{}' to {}:{}.", msg, to, port);
        byte[] data = msg.getBytes();

        try (DatagramSocket udpSocket = new DatagramSocket()){

            InetAddress add = InetAddress.getByName(to);

            DatagramPacket dp = new DatagramPacket(data, data.length, add, port);

            udpSocket.send(dp);
        }
        catch (Exception e) {
            logger.error("Failed to send message {} to {} due to an exception: {}.", msg, to, e.getMessage());
        }
    }

    private String getStatusMsg() {
        JsonObject res = new JsonObject();
        res.addProperty("uid", "1");
        res.addProperty("sid", sid);
        res.addProperty("cmd", "status");
        return res.toString();
    }

    private String getColorMsg(int r, int g, int b) {
        JsonObject res = new JsonObject();
        res.addProperty("uid", "1");
        res.addProperty("sid", sid);
        res.addProperty("cmd", "color");
        JsonObject col = new JsonObject();
        col.addProperty("r", r);
        col.addProperty("g", g);
        col.addProperty("b", b);
        res.add("arg", col);
        return res.toString();
    }
    
    private String getSwitchCmd(boolean on) {
        JsonObject res = new JsonObject();
        res.addProperty("uid", "1");
        res.addProperty("sid", sid);
        res.addProperty("cmd", "switch");
        JsonObject arg = new JsonObject();
        arg.addProperty("on", on ? 1 : 0);
        res.add("arg", arg);
        return res.toString();
    }




}
