package vn.hueqr.backend.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public final class GuestInputPolicy {
	private static final Set<String> LANGUAGES = Set.of("vi", "en", "fr", "ru");

	public String displayName(String raw) {
		try {
			return DisplayNamePolicy.normalize(raw, 50);
		}
		catch (IllegalArgumentException error) {
			throw validation("displayName", error.getMessage());
		}
	}

	public String language(String language) {
		if (!LANGUAGES.contains(language == null ? "" : language))
			throw validation("initialLanguage", "Chọn vi, en, fr hoặc ru.");
		return language;
	}

	public byte[] credentialHash(String credential) {
		try {
			// 32 byte được biểu diễn bằng 43 ký tự Base64URL khi bỏ padding
			if (credential == null || credential.length() != 43)
				throw new IllegalArgumentException();

			byte[] bytes = Base64.getUrlDecoder().decode(credential);
			if (bytes.length != 32 || !Base64.getUrlEncoder().withoutPadding()
					.encodeToString(bytes).equals(credential))
				throw new IllegalArgumentException();

			return digest(bytes);
		}
		catch (IllegalArgumentException error) {
			throw new GuestSessionException("GUEST_CREDENTIAL_INVALID");
		}
	}

	public UUID requestKey(String key) {
		try {
			UUID parsed = UUID.fromString(key);
			if (!parsed.toString().equals(key))
				throw new IllegalArgumentException();

			return parsed;
		}
		catch (IllegalArgumentException | NullPointerException error) {
			throw validation("Idempotency-Key", "Cần UUID đúng dạng chuẩn.");
		}
	}

	public byte[] ticketDigest(String ticket) {
		return digest(ticket.getBytes(StandardCharsets.UTF_8));
	}

	// Tiền tố độ dài phân biệt các cách chia tên và ngôn ngữ khi ghép byte để tính mã Hash
	public byte[] fingerprint(String name, String language, byte[] ticketDigest) {
		byte[] n = name.getBytes(StandardCharsets.UTF_8);
		byte[] l = language.getBytes(StandardCharsets.UTF_8);
		return digest(ByteBuffer.allocate(8 + n.length + l.length + ticketDigest.length)
				.putInt(n.length).put(n).putInt(l.length).put(l).put(ticketDigest).array());
	}

	private static byte[] digest(byte[] bytes) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(bytes);
		}
		catch (NoSuchAlgorithmException error) {
			throw new IllegalStateException("SHA-256 unavailable", error);
		}
	}

	private static GuestSessionException validation(String field, String message) {
		return new GuestSessionException("VALIDATION_FAILED", Map.of(field, message));
	}
}

