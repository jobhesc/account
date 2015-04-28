package com.ynt.account.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;

public abstract class CharacterEncoder {
	protected PrintStream pStream;

	private byte[] getBytes(ByteBuffer paramByteBuffer) {
		byte[] localObject = null;
		if (paramByteBuffer.hasArray()) {
			byte[] arrayOfByte = paramByteBuffer.array();
			if ((arrayOfByte.length == paramByteBuffer.capacity())
					&& (arrayOfByte.length == paramByteBuffer.remaining())) {
				localObject = arrayOfByte;
				paramByteBuffer.position(paramByteBuffer.limit());
			}
		}
		if (localObject == null) {
			localObject = new byte[paramByteBuffer.remaining()];
			paramByteBuffer.get(localObject);
		}
		return (localObject);
	}

	protected abstract int bytesPerAtom();

	protected abstract int bytesPerLine();

	public String encode(ByteBuffer paramByteBuffer) {
		return encode(getBytes(paramByteBuffer));
	}

	public String encode(byte[] paramArrayOfByte) {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(
				paramArrayOfByte);
		try {
			encode(localByteArrayInputStream, localByteArrayOutputStream);
			String str = localByteArrayOutputStream.toString("8859_1");
			return str;
		} catch (Exception localException) {
			throw new Error("CharacterEncoder.encode internal error");
		}
	}

	public void encode(InputStream paramInputStream,
			OutputStream paramOutputStream) throws IOException {
		int i;
		byte[] arrayOfByte = new byte[bytesPerLine()];
		encodeBufferPrefix(paramOutputStream);
		while (true) {
			i = readFully(paramInputStream, arrayOfByte);
			if (i == 0)
				break;
			encodeLinePrefix(paramOutputStream, i);
			int j = 0;
			while (j < i) {
				if (j + bytesPerAtom() > i) {
					encodeAtom(paramOutputStream, arrayOfByte, j, i - j);
				} else {
					encodeAtom(paramOutputStream, arrayOfByte, j,
							bytesPerAtom());
				}
				j += bytesPerAtom();
			}

			if (i < bytesPerLine())
				break;
			encodeLineSuffix(paramOutputStream);
		}
		encodeBufferSuffix(paramOutputStream);
	}

	public void encode(ByteBuffer paramByteBuffer,
			OutputStream paramOutputStream) throws IOException {
		encode(getBytes(paramByteBuffer), paramOutputStream);
	}

	public void encode(byte[] paramArrayOfByte, OutputStream paramOutputStream)
			throws IOException {
		encode(new ByteArrayInputStream(paramArrayOfByte), paramOutputStream);
	}

	protected abstract void encodeAtom(OutputStream paramOutputStream,
			byte[] paramArrayOfByte, int paramInt1, int paramInt2)
			throws IOException;

	protected void encodeBufferPrefix(OutputStream paramOutputStream)
			throws IOException {
		this.pStream = new PrintStream(paramOutputStream);
	}

	protected void encodeBufferSuffix(OutputStream paramOutputStream)
			throws IOException {
	}

	protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt)
			throws IOException {
	}

	protected void encodeLineSuffix(OutputStream paramOutputStream)
			throws IOException {
		this.pStream.println();
	}

	protected int readFully(InputStream paramInputStream,
			byte[] paramArrayOfByte) throws IOException {
		for (int i = 0; i < paramArrayOfByte.length; i++) {
			int j = paramInputStream.read();
			if (j == -1)
				return i;
			else {
				paramArrayOfByte[i] = (byte) j;
			}
		}

		return paramArrayOfByte.length;
	}
}