package com.i2mobi.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DESEncryption {

	private static final String DESKEY="MRJMRJMR";
	private static final String ALGORITHM_DES="DES";
	 /** 
     * DES加密 
     * @param data 要加密的数据 
     * @param key 密钥 
     * @return 返回加密后的数据(经过base64编码) 
     */  
    public static String DESEncrypt(String data) {  
        return DESCipher(data, DESKEY, Cipher.ENCRYPT_MODE);  
    }
    
    /** 
     * DES解密 
     * @param data 要解密的数据 
     * @param key 密钥 
     * @return 返回解密后的数据 
     */  
    public static String DESDecrypt(String data) {  
        return DESCipher(data, DESKEY, Cipher.DECRYPT_MODE);  
    }
  
    /** 
     * DES的加密解密 
     * @param data 要加密或解密的数据 
     * @param key 密钥 
     * @param mode 加密或解密模式 
     * @return 返回加密或解密的数据 
     */  
    private static String DESCipher(String data, String key, int mode) {  
        try {  
            Key k = toKey(key,ALGORITHM_DES);  
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);  
            cipher.init(mode, k);  
            return mode == Cipher.DECRYPT_MODE?new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data))):TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }

    /** 
     * 将base64编码后的密钥字符串转换成密钥对象 
     * @param key 密钥字符串 
     * @param algorithm 加密算法 
     * @return 返回密钥对象 
     */  
    private static Key toKey(String key,String algorithm) {  
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);  
        return secretKey;  
    }
}
