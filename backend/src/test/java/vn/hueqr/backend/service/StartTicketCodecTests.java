package vn.hueqr.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import vn.hueqr.backend.service.StartTicketCodec.Payload;
import vn.hueqr.backend.service.StartTicketTimePolicy.TicketTimes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StartTicketCodecTests {
	private static final UUID REQUEST_ID = UUID.fromString("abcdef01-2345-6789-abcd-0123456789ab");
	private static final UUID EPOCH = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final TicketTimes TIMES = new TicketTimes(
			Instant.parse("2026-11-05T00:00:00.123456Z"), Instant.parse("2026-11-06T00:00:00.123456Z"));
	private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
	private static final String EXPECTED_PAYLOAD = "hueqr-start-v1\n"
			+ "abcdef01-2345-6789-abcd-0123456789ab\n"
			+ "1111111111111111111111111111111111111111111111111111111111111111\n"
			+ "00000000-0000-0000-0000-000000000001\n"
			+ "2026-11-05T00:00:00.123456Z\n2026-11-06T00:00:00.123456Z";

	@Test
	void encodesTheDocumentedPayloadAndTwoUnpaddedBase64UrlParts() {
		String ticket = codec().encode(payload());
		String[] parts = ticket.split("\\.", -1);

		assertThat(parts).hasSize(2);
		assertThat(ticket).doesNotContain("=", "\n", "\r");
		assertThat(new String(DECODER.decode(parts[0]), StandardCharsets.UTF_8)).isEqualTo(EXPECTED_PAYLOAD);
		assertThat(DECODER.decode(parts[1])).hasSize(32);
		assertThat(signer((byte) 0x22).verify(DECODER.decode(parts[0]), DECODER.decode(parts[1]))).isTrue();
	}

	@Test
	void roundTripsAllFieldsIncludingMicroseconds() {
		Payload decoded = codec().decodeAndVerify(codec().encode(payload()));

		assertThat(decoded.requestId()).isEqualTo(REQUEST_ID);
		assertThat(decoded.credentialHash()).containsExactly(hashFixture());
		assertThat(decoded.serviceEpoch()).isEqualTo(EPOCH);
		assertThat(decoded.times()).isEqualTo(TIMES);
		assertThat(decoded.toString()).isEqualTo("StartTicketPayload[redacted]");
	}

	@Test
	void rejectsModifiedPayloadWithoutANewSignature() {
		String[] parts = codec().encode(payload()).split("\\.", -1);
		byte[] changed = DECODER.decode(parts[0]);
		changed[20] ^= 1;

		assertInvalid(ENCODER.encodeToString(changed) + "." + parts[1]);
	}

	@Test
	void rejectsModifiedSignature() {
		String[] parts = codec().encode(payload()).split("\\.", -1);
		byte[] signature = DECODER.decode(parts[1]);
		signature[0] ^= 1;

		assertInvalid(parts[0] + "." + ENCODER.encodeToString(signature));
	}

	@Test
	void rejectsATicketSignedWithAnotherKey() {
		StartTicketCodec otherKey = new StartTicketCodec(signer((byte) 0x33));

		assertInvalid(otherKey.encode(payload()));
	}

	@Test
	void rejectsMissingOversizedOrMalformedEnvelopes() {
		assertInvalid(null);
		assertInvalid("");
		assertInvalid("A".repeat(513));
		assertInvalid("missing-separator");
		assertInvalid(".AA");
		assertInvalid("AA.");
		assertInvalid(codec().encode(payload()) + ".extra");
	}

	@Test
	void rejectsCharactersOutsideBase64Url() {
		String[] parts = codec().encode(payload()).split("\\.", -1);

		assertInvalid("!" + parts[0] + "." + parts[1]);
		assertInvalid(parts[0] + ".!");
		assertInvalid(parts[0] + "\r\n." + parts[1]);
	}

	@Test
	void rejectsPaddingAndAlternateUnusedBase64Bits() {
		String[] parts = codec().encode(payload()).split("\\.", -1);

		assertInvalid(parts[0] + "." + parts[1] + "=");

		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
		int last = parts[1].length() - 1;
		int canonicalIndex = alphabet.indexOf(parts[1].charAt(last));
		// Đổi bit dư ở ký tự cuối để tạo chuỗi khác có cùng byte giải mã
		String alias = parts[1].substring(0, last) + alphabet.charAt(canonicalIndex | 1);

		assertThat(DECODER.decode(alias)).containsExactly(DECODER.decode(parts[1]));
		assertInvalid(parts[0] + "." + alias);
	}

	@Test
	void rejectsAnUnknownVersionEvenWithAValidSignature() {
		assertInvalid(signText(EXPECTED_PAYLOAD.replace("hueqr-start-v1", "hueqr-start-v2")));
	}

	@Test
	void rejectsSignedNonCanonicalFieldSpellings() {
		assertInvalid(signText(EXPECTED_PAYLOAD.replace(REQUEST_ID.toString(), REQUEST_ID.toString().toUpperCase())));
		assertInvalid(signText(EXPECTED_PAYLOAD.replace("1".repeat(64), "A".repeat(64))));
		assertInvalid(signText(EXPECTED_PAYLOAD.replace(".123456Z", ".123456000Z")));
	}

	@Test
	void rejectsSignedMalformedOrExtraFields() {
		assertInvalid(signText(EXPECTED_PAYLOAD.replace(REQUEST_ID.toString(), "invalid-uuid")));
		assertInvalid(signText(EXPECTED_PAYLOAD.replace("1".repeat(64), "1".repeat(62))));
		assertInvalid(signText(EXPECTED_PAYLOAD.replace("2026-11-05T00:00:00.123456Z", "not-a-time")));
		assertInvalid(signText(EXPECTED_PAYLOAD.replace(".123456Z", ".123456001Z")));
		assertInvalid(signText(EXPECTED_PAYLOAD.substring(0, EXPECTED_PAYLOAD.lastIndexOf('\n'))));
		assertInvalid(signText(EXPECTED_PAYLOAD + "\nextra"));
	}

	@Test
	void preservesExpiredTimesForLaterLedgerLookup() {
		Payload decoded = codec().decodeAndVerify(codec().encode(payload()));
		StartTicketTimePolicy later = new StartTicketTimePolicy(
				Clock.fixed(TIMES.ticketExpiresAt().plusSeconds(1), ZoneOffset.UTC));

		assertThatCode(() -> later.validateTimes(decoded.times())).doesNotThrowAnyException();
		assertThat(later.isExpiredForFirstCreation(decoded.times())).isTrue();
	}

	@Test
	void defensivelyCopiesTheCredentialHash() {
		byte[] input = hashFixture();
		Payload payload = new Payload(REQUEST_ID, input, EPOCH, TIMES);
		Arrays.fill(input, (byte) 0);
		byte[] returned = payload.credentialHash();
		Arrays.fill(returned, (byte) 0);

		assertThat(codec().decodeAndVerify(codec().encode(payload)).credentialHash()).containsExactly(hashFixture());
	}

	@Test
	void rejectsInvalidHashLengthsAndSubMicrosecondTimes() {
		assertThatThrownBy(() -> new Payload(REQUEST_ID, new byte[31], EPOCH, TIMES))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new Payload(REQUEST_ID, new byte[33], EPOCH, TIMES))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new Payload(REQUEST_ID, hashFixture(), EPOCH,
				new TicketTimes(TIMES.issuedAt().plusNanos(1), TIMES.ticketExpiresAt())))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new Payload(REQUEST_ID, hashFixture(), EPOCH,
				new TicketTimes(TIMES.issuedAt(), TIMES.ticketExpiresAt().plusNanos(1))))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private static void assertInvalid(String ticket) {
		assertThatThrownBy(() -> codec().decodeAndVerify(ticket))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid start ticket");
	}

	// Ký dữ liệu sai bằng khóa kiểm thử để đi đến nhánh kiểm định dạng sau chữ ký
	private static String signText(String text) {
		byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
		return ENCODER.encodeToString(bytes) + "." + ENCODER.encodeToString(signer((byte) 0x22).sign(bytes));
	}

	private static StartTicketCodec codec() {
		return new StartTicketCodec(signer((byte) 0x22));
	}

	private static StartTicketSigner signer(byte marker) {
		byte[] key = new byte[32];
		Arrays.fill(key, marker);
		return new StartTicketSigner(key);
	}

	private static Payload payload() {
		return new Payload(REQUEST_ID, hashFixture(), EPOCH, TIMES);
	}

	private static byte[] hashFixture() {
		byte[] hash = new byte[32];
		Arrays.fill(hash, (byte) 0x11);
		return hash;
	}
}
