package org.openhab.binding.avantekwfl1.internal;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonSyntaxException;

public class WFL1DiscoveryAnswerTest {
    
    @Test
    public void testCreate() {
        int expRsp = 2002;
        String expCommand = "devfind";
        String expSid = "65465465485";
        String expDevname = "WiFi E-Light- a c7f";
        String expDid = "unknown";
        String expMac = "aa:bb:cc:00:ee:ff";
        
        String input = String.format(
                "{\"rsp\":%d,\"cmd\":\"%s\",\"arg\":{"
                        + "\"devname\":\"%s\",\"sid\":\"%s\",\"did\":\"%s\",\"mac\":\"%s\"}"
                + "}", 
                expRsp, expCommand, expDevname, expSid, expDid, expMac);
        WFL1DiscoveryAnswer result = WFL1DiscoveryAnswer.create(input);
        assertEquals(expRsp, result.getRsp());
        assertEquals(expCommand, result.getCmd());
        assertEquals(expDevname, result.getArg().getDevname());
        assertEquals(expSid, result.getArg().getSid());
        assertEquals(expDid, result.getArg().getDid());
        assertEquals(expMac, result.getArg().getMac());
    }
    
    @Test
    public void testCreate_invalidSyntax() {
        String input = "{\"foo\":\"asdf\", \"x\": {}";
        try {
            WFL1DiscoveryAnswer.create(input);
            fail("JsonSyntaxException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof JsonSyntaxException);
        }
    }
}
