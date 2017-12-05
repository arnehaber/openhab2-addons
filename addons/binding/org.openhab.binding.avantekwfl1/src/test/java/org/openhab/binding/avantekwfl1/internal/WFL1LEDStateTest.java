package org.openhab.binding.avantekwfl1.internal;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonSyntaxException;

public class WFL1LEDStateTest {


    /**
     * Tests {@link WFL1LEDState} object creation using {@link WFL1LEDState#create(String)}.
     */
    @Test
    public void testCreate() {
        int expRsp = 0;
        String expSid = "65465465485";
        String expCommand = "status";
        int expPower = 1;
        int expLum = 1;
        int expColorTemp = 2;
        int expR = 3;
        int expG = 4;
        int expB = 255;
        
        String input = String.format(
                "{\"rsp\":%d,\"sid\":\"%s\",\"cmd\":\"%s\",\"result\":{"
                        + "\"switch\":%d,\"lum\":%d,\"color-temp\":%d,\"r\":%d,\"g\":%d,\"b\":%d}"
                + "}", 
                expRsp, expSid, expCommand, expPower, expLum, expColorTemp, expR, expG, expB);
        WFL1LEDState result = WFL1LEDState.create(input);
        assertEquals(expRsp, result.getRsp());
        assertEquals(expSid, result.getSid());
        assertEquals(expCommand, result.getCmd());
        assertEquals(expPower, result.getResult().getPower());
        assertEquals(expR, result.getResult().getRed());
        assertEquals(expG, result.getResult().getGreen());
        assertEquals(expB, result.getResult().getBlue());
    }
    
    @Test
    public void testCreate_invalidSyntax() {
        String input = "{\"foo\":\"asdf\", \"x\": {}";
        try {
            WFL1LEDState.create(input);
            fail("JsonSyntaxException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof JsonSyntaxException);
        }
    }

}
