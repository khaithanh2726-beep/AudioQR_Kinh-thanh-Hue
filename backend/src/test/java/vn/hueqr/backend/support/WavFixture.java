package vn.hueqr.backend.support;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public final class WavFixture {
	private WavFixture() {
	}

	public static byte[] oneSecond() {
		ByteBuffer bytes = ByteBuffer.allocate(48044).order(ByteOrder.LITTLE_ENDIAN);
		bytes.put("RIFF".getBytes(StandardCharsets.US_ASCII)).putInt(48036).put("WAVEfmt ".getBytes(StandardCharsets.US_ASCII));
		bytes.putInt(16).putShort((short) 1).putShort((short) 1).putInt(24000).putInt(48000).putShort((short) 2).putShort((short) 16);
		bytes.put("data".getBytes(StandardCharsets.US_ASCII)).putInt(48000);
		// Sóng tổng hợp dùng kiểm container, không phải lời thuyết minh đã nghe duyệt
		for (int i = 0; i < 24000; i++)
			bytes.putShort((short) (Math.sin(i * Math.PI * 2 * 440 / 24000) * 8000));
		return bytes.array();
	}
}
