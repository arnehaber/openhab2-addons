/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avantekwfl1.handler;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.avantekwfl1.WFL1BindingConstants;
import org.openhab.binding.avantekwfl1.internal.configuration.WFL1WiFiLEDConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link WFL1LEDHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Arne Haber - Initial contribution
 */
public class WFL1LEDHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(WFL1LEDHandler.class);
    private WFL1LEDDriver driver;
    private ScheduledFuture<?> pollingJob;

    public WFL1LEDHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing HFL1LEDHandler handler for '{}'", getThing().getUID());

        WFL1WiFiLEDConfig config = getConfigAs(WFL1WiFiLEDConfig.class);

        int port = (config.getPort() == null) ? WFL1LEDDriver.DEFAULT_PORT : config.getPort();
        String ip = config.getIp();
        String sid = config.getSid();
        driver = new WFL1LEDDriver(sid, ip, port);

        try {
            driver.init();
            logger.debug("Found a WiFi LED device '{}'", getThing().getUID());

        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR, e.getMessage());
            return;
        }
        updateStatus(ThingStatus.ONLINE);

        int pollingPeriod = (config.getPollingPeriod() == null) ? WFL1LEDDriver.DEFAULT_POLLING_PERIOD
                : config.getPollingPeriod();
        if (pollingPeriod > 0) {
            pollingJob = scheduler.scheduleWithFixedDelay(() -> update(), 0, pollingPeriod, TimeUnit.SECONDS);
            logger.debug("Polling job scheduled to run every {} sec. for '{}'", pollingPeriod, getThing().getUID());
        } else {
            logger.error("Configured polling period {} has to be > 0.", pollingPeriod);
        }
    }

    @Override
    public void dispose() {
        logger.debug("Disposing WiFiLED handler '{}'", getThing().getUID());

        if (pollingJob != null) {
            pollingJob.cancel(true);
            pollingJob = null;
        }
        driver.shutdown();
        driver = null;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Handle command '{}' for {}", command, channelUID);

        try {

            if (command == RefreshType.REFRESH) {
                update();
            } else {
                switch (channelUID.getId()) {
                    case WFL1BindingConstants.CHANNEL_POWER:
                        if (command instanceof OnOffType) {
                            driver.setPower((OnOffType) command);
                        }
                        break;
                    case WFL1BindingConstants.CHANNEL_COLOR:
                        if (command instanceof HSBType) {
                            driver.setColor((HSBType) command);
                        } else if  (command instanceof PercentType) {
                            System.out.println(command + " " + channelUID);
                            driver.setBrightness((PercentType) command);
                        } else {
                            logger.error("Unsupported color command {}.", command);
                        }
                        break;
                    default:
                        logger.error("Unknown channel id {}.", channelUID.getId());
                        break;
                }
            }

        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    private synchronized void update() {
        logger.debug("Updating HF1L WiFiLED data '{}'", getThing().getUID());

        try {
            WF1LStateDTO ledState = driver.getLEDStateDTO();
            HSBType color = new HSBType(ledState.getHue(), ledState.getSaturation(), ledState.getBrightness());
            updateState(WFL1BindingConstants.CHANNEL_POWER, ledState.getPower());
            updateState(WFL1BindingConstants.CHANNEL_COLOR, color);

            if (getThing().getStatus().equals(ThingStatus.OFFLINE)) {
                updateStatus(ThingStatus.ONLINE);
            }
        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
        }
    }

}
