package vn.hueqr.backend.service;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ContentTextPolicy {
	private static final Set<String> LANGUAGES = Set.of("vi", "en", "fr", "ru");
	private static final Pattern ETAG = Pattern.compile("\"(place|slot|review|audio-review):([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}):(none|0|[1-9][0-9]*)\"");

	public String language(String language) {
		if (language == null || !LANGUAGES.contains(language))
			throw invalid("language", "Chỉ hỗ trợ vi, en, fr và ru.");
		return language;
	}

	public String tag(String kind, UUID id, Object version) {
		return "\"" + kind + ":" + id + ":" + version + "\"";
	}

	public UUID textId(String value, String field) {
		try {
			UUID id = UUID.fromString(value);
			if (!id.toString().equals(value))
				throw new IllegalArgumentException();
			return id;
		}
		catch (IllegalArgumentException | NullPointerException error) {
			throw invalid(field, "Cần mã văn bản UUID chuẩn chữ thường.");
		}
	}

	public void requireMatch(String supplied, String expected) {
		if (supplied == null || supplied.isBlank())
			throw new AdminCommandException(400, "PRECONDITION_REQUIRED", Map.of("If-Match", "Cần ETag từ lần đọc đối tượng."));
		var match = ETAG.matcher(supplied);
		if (!match.matches() || ("none".equals(match.group(3)) && !java.util.Set.of("review", "audio-review").contains(match.group(1))))
			throw new AdminCommandException(400, "VALIDATION_FAILED", Map.of("If-Match", "Cần một ETag mạnh đúng định dạng nội dung."));
		if (!"none".equals(match.group(3))) {
			try {
				Long.parseLong(match.group(3));
			}
			catch (NumberFormatException error) {
				throw new AdminCommandException(400, "VALIDATION_FAILED", Map.of("If-Match", "Phiên bản vượt phạm vi số nguyên 64 bit."));
			}
		}
		if (!expected.equals(supplied))
			throw new AdminCommandException(412, "VERSION_CONFLICT");
	}

	public byte[] bodyHash(String body) {
		requireText(body, "body");
		try {
			return MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8));
		}
		catch (NoSuchAlgorithmException error) {
			throw new IllegalStateException("SHA-256 unavailable", error);
		}
	}

	public void requireText(String value, String field) {
		if (value == null || value.codePoints().allMatch(c -> Character.isWhitespace(c) || Character.isSpaceChar(c)))
			throw invalid(field, "Không được để trống.");
		requireUnicode(value, field);
	}

	public void requireUnicode(String value, String field) {
		if (value == null)
			return;
		try {
			// Từ chối surrogate lẻ để mã Hash không được tính từ ký tự thay thế của bộ mã hóa
			StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(value));
		}
		catch (CharacterCodingException error) {
			throw invalid(field, "Cần chuỗi Unicode hợp lệ.");
		}
	}

	private AdminCommandException invalid(String field, String message) {
		return new AdminCommandException(422, "VALIDATION_FAILED", Map.of(field, message));
	}
}
