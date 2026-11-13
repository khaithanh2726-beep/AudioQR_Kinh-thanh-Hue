package vn.hueqr.backend.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class StartTicketSigner {
	private static final String ALGORITHM = "HmacSHA256";
	private static final int MIN_KEY_BYTES = 32;
	private static final int SIGNATURE_BYTES = 32;

	private final byte[] signingKey;

	public StartTicketSigner(byte[] signingKey) {
		Objects.requireNonNull(signingKey, "signingKey");
		if (signingKey.length < MIN_KEY_BYTES) {
			throw new IllegalArgumentException("Signing key must contain at least 32 bytes");
		}
		this.signingKey = signingKey.clone();
	}

	public byte[] sign(byte[] payload) {
		Objects.requireNonNull(payload, "payload");

		try {
			// Tạo Mac riêng cho mỗi lần ký để tách trạng thái xử lý
			Mac mac = Mac.getInstance(ALGORITHM);
			mac.init(new SecretKeySpec(signingKey, ALGORITHM));
			return mac.doFinal(payload);
		}
		catch (GeneralSecurityException error) {
			throw new IllegalStateException("Cannot initialize start-ticket HMAC", error);
		}
	}

	public boolean verify(byte[] payload, byte[] signature) {
		Objects.requireNonNull(payload, "payload");
		if (signature == null || signature.length != SIGNATURE_BYTES) {
			return false;
		}

		return MessageDigest.isEqual(sign(payload), signature);
	}
}
