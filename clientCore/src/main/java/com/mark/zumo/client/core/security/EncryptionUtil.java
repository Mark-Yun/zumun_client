package com.mark.zumo.client.core.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mark on 18. 5. 7.
 */

public class EncryptionUtil {
    public static final String PREFERENCE_NAME = "b77eafg23ad12`49ab-422dv84df8-#asd123hsdhsdfgQFWQ3.eb8c";
    public static final String SECURE_KEY = "5361a11b-615c-41SDFGSE23dgwasf3-6235123412b-e2c3790ada14";
    public static final String PREF_KEY_EMAIL = "email";
    public static final String PREF_KEY_PASSWORD = "password";
    public static final String PREF_KEY_PKEY = "password_key";

    public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }
}
