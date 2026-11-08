package vn.hueqr.backend.service;

import java.util.regex.Pattern;

public final class DisplayNamePolicy {
	private static final Pattern SPACES = Pattern.compile("[\\p{Zs}\\p{javaWhitespace}]+");
	private static final Pattern GRAPHEME = Pattern.compile("\\X");

	private DisplayNamePolicy() {
	}

	// Kiểm dữ liệu gốc trước chuẩn hóa, giới hạn theo cụm hiển thị thay vì số mã Unicode
	public static String normalize(String raw, int maximum) {
		if (raw == null || raw.codePoints().anyMatch(cp -> Character.isISOControl(cp)
				|| cp == 0x2028 || cp == 0x2029 || Character.getType(cp) == Character.SURROGATE))
			throw new IllegalArgumentException("Tên không được chứa xuống dòng hoặc ký tự điều khiển.");

		String normalized = SPACES.matcher(raw).replaceAll(" ").strip();
		long length = GRAPHEME.matcher(normalized).results().limit(maximum + 1L).count();
		if (length < 1 || length > maximum)
			throw new IllegalArgumentException("Tên cần từ 1 đến " + maximum + " ký tự hiển thị.");
		return normalized;
	}
}
