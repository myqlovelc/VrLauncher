package com.cgl.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class AES {

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
        String text = "文字在线加密解密、散列/哈希、BASE64、SHA1、SHA224、SHA256、SHA384、SHA512、MD5、HmacSHA1、HmacSHA224、HmacSHA256、HmacSHA384";
        String key = "1994111012345678";
        byte[] bytes = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -124, 0,
        		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 31, 0, 0};
        
        String str = new String(bytes);
        
        for (int i = 0; i < str.getBytes().length; i++) {
        	System.out.print(str.getBytes()[i] + " ");
        }
        System.out.println();
        try {
			String cryperText = aesEncrypt(str, key);
			//System.out.println("加密前的明文:" + str);
			//System.out.println("加密后的密文:" + cryperText);
			//System.out.println("解密后的明文:" + aesDecrypt(cryperText, key));
			for (int i = 0; i < cryperText.getBytes().length; i++) {
	        	System.out.print(cryperText.getBytes()[i] + " ");
	        }
			System.out.println();
			
			String des = aesDecrypt(cryperText, key);
			for (int i = 0; i < des.getBytes().length; i++) {
	        	System.out.print(des.getBytes()[i] + " ");
	        }
	        System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}*/

	public static byte[] aesEncrypt(byte[] str, String key) throws Exception {
        if (str == null || key == null) return null;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("ASCII"), "AES"));
        byte[] bytes = cipher.doFinal(str);
        String retString = new BASE64Encoder().encode(bytes);
        return retString.getBytes("ASCII");
    }

    public static byte[] aesDecrypt(byte[] str, String key) throws Exception {
        if (str == null || key == null) return null;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("ASCII"), "AES"));
        byte[] bytes = new BASE64Decoder().decodeBuffer(new String(str, "ASCII"));
        bytes = cipher.doFinal(bytes);
        //return new String(bytes, "utf-8");
        return bytes;
    }
}
