package com.ynt.account.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;

public abstract class CharacterDecoder {
	protected abstract int bytesPerAtom();

	protected abstract int bytesPerLine();

	protected void decodeAtom(PushbackInputStream paramPushbackInputStream,
			OutputStream paramOutputStream, int paramInt) throws IOException {
		throw new CEStreamExhausted();
	}

	public void decodeBuffer(InputStream paramInputStream,
			OutputStream paramOutputStream) throws IOException {
		int v3 = 0;
		PushbackInputStream localPushbackInputStream = new PushbackInputStream(
				paramInputStream);
		decodeBufferPrefix(localPushbackInputStream, paramOutputStream);
		while (true) {
			try {
				int v1 = decodeLinePrefix(localPushbackInputStream,
						paramOutputStream);
				int v0 = 0;
				while (v0 + bytesPerAtom() < v1) {
					decodeAtom(localPushbackInputStream, paramOutputStream,
							bytesPerAtom());
					v3 += bytesPerAtom();
					v0 += bytesPerAtom();
				}

				if (v0 + bytesPerAtom() != v1) {
					decodeAtom(localPushbackInputStream, paramOutputStream, v1
							- v0);
					v3 += (v1 - v0);
				} else {
					decodeAtom(localPushbackInputStream, paramOutputStream,
							bytesPerAtom());
					v3 += bytesPerAtom();
				}
				decodeLineSuffix(localPushbackInputStream, paramOutputStream);
			} catch (CEStreamExhausted e) {
				decodeBufferSuffix(localPushbackInputStream, paramOutputStream);
				return;
			}
		}
	}

	public byte[] decodeBuffer(InputStream paramInputStream) throws IOException {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		decodeBuffer(paramInputStream, localByteArrayOutputStream);
		return localByteArrayOutputStream.toByteArray();
	}

	public byte[] decodeBuffer(String paramString) throws IOException {
		byte[] arrayOfByte = new byte[paramString.length()];
		paramString.getBytes(0, paramString.length(), arrayOfByte, 0);
		ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(
				arrayOfByte);
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		decodeBuffer(localByteArrayInputStream, localByteArrayOutputStream);
		return localByteArrayOutputStream.toByteArray();
	}

	protected void decodeBufferPrefix(
			PushbackInputStream paramPushbackInputStream,
			OutputStream paramOutputStream) throws IOException {
	}

	protected void decodeBufferSuffix(
			PushbackInputStream paramPushbackInputStream,
			OutputStream paramOutputStream) throws IOException {
	}

	public ByteBuffer decodeBufferToByteBuffer(InputStream paramInputStream)
			throws IOException {
		return ByteBuffer.wrap(decodeBuffer(paramInputStream));
	}

	public ByteBuffer decodeBufferToByteBuffer(String paramString)
			throws IOException {
		return ByteBuffer.wrap(decodeBuffer(paramString));
	}

	protected int decodeLinePrefix(
			PushbackInputStream paramPushbackInputStream,
			OutputStream paramOutputStream) throws IOException {
		return bytesPerLine();
	}

	protected void decodeLineSuffix(
			PushbackInputStream paramPushbackInputStream,
			OutputStream paramOutputStream) throws IOException {
	}

	protected int readFully(InputStream paramInputStream,
			byte[] paramArrayOfByte, int paramInt1, int paramInt2)
			throws IOException {
		for (int i = 0; i < paramInt2; i++) {
			int j = paramInputStream.read();
			if (j != -1) {
				paramArrayOfByte[(i + paramInt1)] = (byte) j;
			} else {
				if (i != 0)
					return i;
				else
					return -1;
			}
		}

		return paramInt2;
	}
}