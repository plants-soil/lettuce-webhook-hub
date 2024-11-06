package com.plantssoil.common.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Sha512HmacEncrypterTest {

    private String getPlainText(String messageId, long timestamp, String payload) {
        String planText = String.format("%s.%s.%s", messageId, timestamp, payload);
        return planText;
    }

    @Test
    public void testEncrypt() {
        String secretKey = "whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw";
        String plainText = getPlainText("msg_p5jXN8AQM9LWM0D4loKWxJek", 1614265330, "{\"test\": 2432232314}");
        String expected = "g0hM9SsE+OTPJTGt/tmIKtSyZlE3uFJELVlNIOLJ1OE=";

        String encrypted = Sha512HmacEncrypter.getInstance().encrypt(secretKey, plainText);

        assertEquals(expected, encrypted);
    }

}
