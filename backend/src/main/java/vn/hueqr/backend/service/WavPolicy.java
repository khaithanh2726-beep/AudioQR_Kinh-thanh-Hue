package vn.hueqr.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class WavPolicy {
	public record Metadata(String checksum, long byteSize, BigDecimal duration) {
	}

	private WavPolicy() {
	}

	// Chỉ nhận WAV PCM hoàn chỉnh đúng profile unary đã chọn, không thêm RIFF vào bytes provider
	public static Metadata inspect(byte[] bytes) {
		if (bytes == null || bytes.length < 44 || !tag(bytes, 0, "RIFF") || !tag(bytes, 8, "WAVE")
				|| uint(bytes, 4) != bytes.length - 8L)
			throw new IllegalArgumentException("WAV_INVALID_CONTAINER");
		boolean format = false;
		long dataSize = -1;
		int offset = 12;
		while (offset < bytes.length) {
			if (bytes.length - offset < 8)
				throw new IllegalArgumentException("WAV_TRUNCATED_CHUNK");
			long length = uint(bytes, offset + 4);
			long end = offset + 8L + length;
			if (end + (length & 1) > bytes.length)
				throw new IllegalArgumentException("WAV_TRUNCATED_CHUNK");
			if (tag(bytes, offset, "fmt ")) {
				if (format || length < 16)
					throw new IllegalArgumentException("WAV_INVALID_FORMAT");
				ByteBuffer info = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
				if (info.getShort(offset + 8) != 1 || info.getShort(offset + 10) != 1 || uint(bytes, offset + 12) != 24000
						|| uint(bytes, offset + 16) != 48000 || info.getShort(offset + 20) != 2 || info.getShort(offset + 22) != 16)
					throw new IllegalArgumentException("WAV_UNSUPPORTED_PROFILE");
				format = true;
			}
			if (tag(bytes, offset, "data")) {
				if (!format || dataSize != -1 || length == 0 || (length & 1) != 0)
					throw new IllegalArgumentException("WAV_INVALID_DATA");
				dataSize = length;
			}
			offset = (int) (end + (length & 1));
		}
		if (!format || dataSize <= 0)
			throw new IllegalArgumentException("WAV_MISSING_AUDIO");
		return new Metadata(hash(bytes), bytes.length, BigDecimal.valueOf(dataSize).divide(BigDecimal.valueOf(48000), 9, RoundingMode.HALF_UP));
	}

	public static String hash(byte[] bytes) {
		try {
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
		}
		catch (NoSuchAlgorithmException error) {
			throw new IllegalStateException("SHA-256 unavailable", error);
		}
	}

	private static long uint(byte[] bytes, int offset) {
		return Integer.toUnsignedLong(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt(offset));
	}

	private static boolean tag(byte[] bytes, int offset, String value) {
		return offset + 4 <= bytes.length && value.equals(new String(bytes, offset, 4, StandardCharsets.US_ASCII));
	}
}
