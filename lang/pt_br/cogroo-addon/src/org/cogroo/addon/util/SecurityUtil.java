/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.addon.util;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.cogroo.addon.LoggerImpl;

public class SecurityUtil {

    private static final String UTF8 = "UTF-8";
    protected static Logger LOG = LoggerImpl.getLogger(SecurityUtil.class.getCanonicalName());

    /**
     * Encrypt data using an key encrypted with a private key.
     * @param privateKey the private key to decrypt the secret key
     * @param encryptedSecretKey a encrypted secret key
     * @param data the data to encrypt
     * @return the encrypted data
     * @throws InvalidKeyException one of the keys is invalid
     */
    public byte[] encrypt(PrivateKey privateKey, byte[] encryptedSecretKey, String data) throws InvalidKeyException {
        byte[] encryptedData = null;
        try {
            byte[] chave = privateKey.getEncoded();
            // Decrypt secret symmetric key with private key
            Cipher rsacf = Cipher.getInstance("RSA");
            rsacf.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] secretKey = rsacf.doFinal(encryptedSecretKey);

            encryptedData = encrypt(secretKey, data);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception encrypting data", e);
        }

        return encryptedData;
    }

    private byte[] encrypt(byte[] secretKey, String data) throws InvalidKeyException {
        byte[] encryptedData = null;
        try {
            // Encrypt data using the secret key
            Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
            aescf.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(secretKey, "AES"), ivspec);
            encryptedData = aescf.doFinal(data.getBytes(UTF8));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception encrypting data", e);
        }
        return encryptedData;
    }

    private byte[] decryptSecretKey(PrivateKey privatekey, byte[] encryptedSecretKey) {
        byte[] result = null;
        try {
            Cipher rsacf = Cipher.getInstance("RSA");
            rsacf.init(Cipher.DECRYPT_MODE, privatekey);
            result = rsacf.doFinal(encryptedSecretKey);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
        return result;
    }

    public byte[] decrypt(PrivateKey privateKey, byte[] encryptedSecretKey, byte[] encryptedText) {
        byte[] text = null;
        try {
            byte[] secretKey = decryptSecretKey(privateKey, encryptedSecretKey);
            text = decrypt(secretKey, encryptedText);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Should not happen", e);
        }

        return text;
    }

    private byte[] decrypt(byte[] secretKey, byte[] encryptedText) {
        byte[] text = null;
        try {
            Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
            aescf.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(secretKey, "AES"), ivspec);
            text = aescf.doFinal(encryptedText);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Should not happen", e);
        }

        return text;
    }
    private static final int RSAKEYSIZE = 1024;

    public KeyPair genKeyPair() {
        KeyPair kpr = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(new RSAKeyGenParameterSpec(RSAKEYSIZE,
                    RSAKeyGenParameterSpec.F4));
            kpr = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOG.log(Level.SEVERE, "Error generating key pair", e);
        } catch (InvalidAlgorithmParameterException e) {
            LOG.log(Level.SEVERE, "Error generating key pair", e);
        }
        return kpr;
    }

    public String encode(byte[] key) {
        return StringUtils.newStringUtf8(Base64.encodeBase64(key, false));
    }

    public static String encodeURLSafe(String data) {
        String ret = null;
        try {
            ret = URLEncoder.encode(data, UTF8);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Should not happen", e);
        }
        return ret;
    }

    public String encodeURLSafe(byte[] key) {
        String encKey = null;
        try {
        	if(LOG.isLoggable(Level.FINE)) {
        		LOG.fine("encode 01: key " + Arrays.toString(key));
        	}
            String value = encode(key);
        	if(LOG.isLoggable(Level.FINE)) {
        		LOG.fine("encode 02 value: " + value);
        	}
            encKey = URLEncoder.encode(value, UTF8);
        } catch (UnsupportedEncodingException e) {
            LOG.log(Level.SEVERE, "Should not happen", e);
        }
        return encKey;
    }

    public byte[] decodeURLSafe(String encoded) {
        byte[] bytes = null;
        try {
            bytes = Base64.decodeBase64(URLDecoder.decode(encoded, UTF8).getBytes(UTF8));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error decoding string: " + encoded, e);
        }
        return bytes;
    }

    /**
     * Encrypt a string using SHA
     * @param plaintext the original text
     * @return resultant hash
     */
    public synchronized String encrypt(String plaintext) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
            md.update(plaintext.getBytes(UTF8));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Should not happen!", e);
        }
        byte raw[] = md.digest();

        return encode(raw);
    }
}
