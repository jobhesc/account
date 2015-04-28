package com.ynt.account.request;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DesEncrypt {
	private static final byte[] DES_IV;
	private static final String DES_KEY = "12345678;";
	private static DesEncrypt inst = null;
	private Cipher cipher = null;
	private AlgorithmParameterSpec iv = null;
	private Key key = null;

	static {
		DES_IV = new byte[] { 18, 52, 86, 120, -112, -85, -51, -17 };
	}

	private DesEncrypt() throws Exception {
		DESKeySpec localDESKeySpec = new DESKeySpec(
				"12345678;".getBytes("UTF-8"));
		this.iv = new IvParameterSpec(DES_IV);
		this.key = SecretKeyFactory.getInstance("DES").generateSecret(
				localDESKeySpec);
		this.cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	}

	public static DesEncrypt getInstance() throws Exception {
		if (inst == null)
			inst = new DesEncrypt();
		return inst;
	}

	public byte[] decode(byte[] paramArrayOfByte) throws Exception {
		try {
			this.cipher.init(2, this.key, this.iv);
			byte[] arrayOfByte = this.cipher.doFinal(paramArrayOfByte);
			return arrayOfByte;
		} finally {
		}
	}

	public byte[] encode(byte[] paramArrayOfByte) throws Exception {
		try {
			this.cipher.init(1, this.key, this.iv);
			byte[] arrayOfByte = this.cipher.doFinal(paramArrayOfByte);
			return arrayOfByte;
		} finally {
		}
	}
}