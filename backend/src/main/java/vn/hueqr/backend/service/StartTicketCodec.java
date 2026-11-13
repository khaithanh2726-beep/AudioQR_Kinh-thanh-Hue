package vn.hueqr.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

import vn.hueqr.backend.service.StartTicketTimePolicy.TicketTimes;

public final class StartTicketCodec {
	private static final String FORMAT = "hueqr-start-v1";
	private static final int MAX_TICKET_CHARS = 512;
	private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
	private static final HexFormat HEX = HexFormat.of();

	private final StartTicketSigner signer;

	public StartTicketCodec(StartTicketSigner signer) {
		this.signer = Objects.requireNonNull(signer, "signer");
	}

	public String encode(Payload payload) {
		byte[] bytes = payloadBytes(Objects.requireNonNull(payload, "payload"));
		return ENCODER.encodeToString(bytes) + "." + ENCODER.encodeToString(signer.sign(bytes));
	}

	public Payload decodeAndVerify(String ticket) {
		if (ticket == null || ticket.isEmpty() || ticket.length() > MAX_TICKET_CHARS) {
			throw invalidTicket();
		}

		String[] parts = ticket.split("\\.", -1);
		if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
			throw invalidTicket();
		}

		byte[] bytes = decodeCanonicalPart(parts[0]);
		byte[] signature = decodeCanonicalPart(parts[1]);
		if (!signer.verify(bytes, signature)) {
			throw invalidTicket();
		}

		try {
			String[] fields = new String(bytes, StandardCharsets.UTF_8).split("\n", -1);
			if (fields.length != 6 || !FORMAT.equals(fields[0])) {
				throw invalidTicket();
			}

			Payload payload = new Payload(UUID.fromString(fields[1]), HEX.parseHex(fields[2]),
					UUID.fromString(fields[3]), new TicketTimes(
							Instant.parse(fields[4]), Instant.parse(fields[5])));

			// Dạng biểu diễn chuẩn giúp cùng dữ liệu vé luôn cho cùng mã Hash
			if (!Arrays.equals(bytes, payloadBytes(payload))) {
				throw invalidTicket();
			}

			return payload;
		}
		catch (IllegalArgumentException | DateTimeException error) {
			// Thông báo lỗi cố định bảo vệ nội dung vé và các trường xác thực
			throw invalidTicket();
		}
	}

	// Từ chối padding hoặc bit dư tạo cách biểu diễn Base64URL không chuẩn
	private static byte[] decodeCanonicalPart(String part) {
		try {
			byte[] decoded = DECODER.decode(part);
			if (!ENCODER.encodeToString(decoded).equals(part)) {
				throw invalidTicket();
			}

			return decoded;
		}
		catch (IllegalArgumentException error) {
			throw invalidTicket();
		}
	}

	private static byte[] payloadBytes(Payload payload) {
		return String.join("\n", FORMAT, payload.requestId().toString(),
				HEX.formatHex(payload.credentialHash()), payload.serviceEpoch().toString(),
				payload.times().issuedAt().toString(), payload.times().ticketExpiresAt().toString())
				.getBytes(StandardCharsets.UTF_8);
	}

	private static IllegalArgumentException invalidTicket() {
		return new IllegalArgumentException("Invalid start ticket");
	}

	public record Payload(UUID requestId, byte[] credentialHash, UUID serviceEpoch, TicketTimes times) {
		public Payload {
			Objects.requireNonNull(requestId, "requestId");
			Objects.requireNonNull(credentialHash, "credentialHash");
			Objects.requireNonNull(serviceEpoch, "serviceEpoch");
			Objects.requireNonNull(times, "times");

			if (credentialHash.length != 32) {
				throw new IllegalArgumentException("Credential hash must contain 32 bytes");
			}

			if (times.issuedAt().getNano() % 1_000 != 0 || times.ticketExpiresAt().getNano() % 1_000 != 0) {
				throw new IllegalArgumentException("Ticket times must use microsecond precision");
			}

			credentialHash = credentialHash.clone();
		}

		@Override
		public byte[] credentialHash() {
			return credentialHash.clone();
		}

		@Override
		public String toString() {
			return "StartTicketPayload[redacted]";
		}
	}
}
