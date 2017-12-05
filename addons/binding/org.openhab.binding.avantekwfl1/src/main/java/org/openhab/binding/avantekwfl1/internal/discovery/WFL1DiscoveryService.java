/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avantekwfl1.internal.discovery;

import static org.openhab.binding.avantekwfl1.WFL1BindingConstants.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.avantekwfl1.handler.WFL1LEDDriver;
import org.openhab.binding.avantekwfl1.internal.WFL1DiscoveryAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link WFL1DiscoveryService} class implements a service
 * for discovering supported WiFi LED Devices.
 *
 * @author Arne Haber - Initial contribution
 */
public class WFL1DiscoveryService extends AbstractDiscoveryService {

    private static final int DEFAULT_BROADCAST_PORT = WFL1LEDDriver.DEFAULT_PORT;
    
    private static final byte[] SCAN_CMD;
    
    static {
        JsonObject res = new JsonObject();
        res.addProperty("cmd", "devfind");
        SCAN_CMD =  res.toString().getBytes();
    }

    private Logger logger = LoggerFactory.getLogger(WFL1DiscoveryService.class);

    public WFL1DiscoveryService() {
        super(SUPPORTED_THING_TYPES_UIDS, 15, true);
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("Start WF-L1 LED background discovery");
        scheduler.schedule(() -> discover(), 0, TimeUnit.SECONDS);
    }

    @Override
    public void startScan() {
        logger.debug("Start WF-L1 LED scan");
        discover();
    }

    private synchronized void discover() {
        logger.debug("Try to discover all WF-L1 LED devices");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(5000);

            InetAddress inetAddress = InetAddress.getByName("255.255.255.255");

            DatagramPacket packet = new DatagramPacket(SCAN_CMD, SCAN_CMD.length, inetAddress, DEFAULT_BROADCAST_PORT);
            socket.send(packet);
            logger.debug("Discover message sent: '{}'", new String(SCAN_CMD));

            // wait for responses
            while (true) {
                byte[] rxbuf = new byte[1024];
                packet = new DatagramPacket(rxbuf, rxbuf.length);
                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    break; // leave the endless loop
                }

                byte[] data = packet.getData();
                String responseMsg = new String(data).trim();
                String ip = packet.getAddress().getHostAddress();
                logger.debug("Discover response received: '{}'", responseMsg);

                try {
                    WFL1DiscoveryAnswer response = WFL1DiscoveryAnswer.create(responseMsg);      
                    logger.debug("Adding a new WF-L1 WiFi LED with IP '{}' and MAC '{}' to inbox", ip, response.getArg().getMac());
                    
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("ip", ip);
                    properties.put("sid", response.getArg().getSid());
                    
                    ThingUID uid = new ThingUID(THING_TYPE_WIFILED, response.getArg().getMac().replace(':', '_'));

                    DiscoveryResult result = DiscoveryResultBuilder.create(uid)
                            .withProperties(properties)
                            .withLabel(response.getArg().getDevname())
                            .build();
                    thingDiscovered(result);
                    logger.debug("Thing discovered '{}'", result);
                    
                }
                catch (JsonSyntaxException e) {
                    logger.error("Invalid response received {}. Exception: {}.", responseMsg, e.getMessage());
                    // receive more responses
                    continue;
                }
            }
        } catch (IOException e) {
            logger.debug("No WiFi LED device found. Diagnostic: {}", e.getMessage());
        }
    }
    

    

}
