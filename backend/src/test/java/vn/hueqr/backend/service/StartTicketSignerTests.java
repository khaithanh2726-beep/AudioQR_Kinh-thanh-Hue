package vn.hueqr.backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StartTicketSignerTests {
	private static final byte[] PAYLOAD = "payload-fixture-v1".getBytes(StandardCharsets.UTF_8);

	@Test
	void matchesRfc4231TestCase6() {
		// Dữ liệu chuẩn RFC 4231 mục 4.7 tại https://www.rfc-editor.org/rfc/rfc4231.txt
		StartTicketSigner signer = new StartTicketSigner(keyFixture(131, (byte) 0xaa));
		byte[] payload = "Test Using Larger Than Block-Size Key - Hash Key First"
				.getBytes(StandardCharsets.US_ASCII);
		byte[] expected = HexFormat.of().parseHex(
				"60e431591ee0b67f0d8a26aacbf5b77f8e0bc6213728c5140546040f0ee37f54");

		assertThat(signer.sign(payload)).containsExactly(expected);
		assertThat(signer.verify(payload, expected)).isTrue();
	}

	@Test
	void acceptsSignatureForTheSamePayloadAndKeyAcrossInstances() {
		StartTicketSigner issuer = new StartTicketSigner(keyFixture(32, (byte) 0x11));
		StartTicketSigner verifier = new StartTicketSigner(keyFixture(32, (byte) 0x11));

		byte[] signature = issuer.sign(PAYLOAD);

		assertThat(signature).hasSize(32);
		assertThat(verifier.verify(PAYLOAD, signature)).isTrue();
	}

	@Test
	void rejectsChangedPayload() {
		StartTicketSigner signer = new StartTicketSigner(keyFixture(32, (byte) 0x11));
		byte[] signature = signer.sign(PAYLOAD);
		byte[] changedPayload = PAYLOAD.clone();
		changedPayload[0] ^= 1;

		assertThat(signer.verify(changedPayload, signature)).isFalse();
	}

	@Test
	void rejectsChangedSignature() {
		StartTicketSigner signer = new StartTicketSigner(keyFixture(32, (byte) 0x11));
		byte[] signature = signer.sign(PAYLOAD);
		signature[signature.length - 1] ^= 1;

		assertThat(signer.verify(PAYLOAD, signature)).isFalse();
	}

	@Test
	void rejectsSignatureFromADifferentKey() {
		StartTicketSigner issuer = new StartTicketSigner(keyFixture(32, (byte) 0x11));
		StartTicketSigner wrongKey = new StartTicketSigner(keyFixture(32, (byte) 0x22));

		assertThat(wrongKey.verify(PAYLOAD, issuer.sign(PAYLOAD))).isFalse();
	}

	@Test
	void rejectsMissingTruncatedAndExtendedSignatures() {
		StartTicketSigner signer = new StartTicketSigner(keyFixture(32, (byte) 0x11));
		byte[] signature = signer.sign(PAYLOAD);

		assertThat(signer.verify(PAYLOAD, null)).isFalse();
		assertThat(signer.verify(PAYLOAD, new byte[0])).isFalse();
		assertThat(signer.verify(PAYLOAD, Arrays.copyOf(signature, 31))).isFalse();
		assertThat(signer.verify(PAYLOAD, Arrays.copyOf(signature, 33))).isFalse();
	}

	@Test
	void copiesTheKeyBeforeTheCallerChangesItsArray() {
		byte[] suppliedKey = keyFixture(32, (byte) 0x11);
		StartTicketSigner signer = new StartTicketSigner(suppliedKey);
		StartTicketSigner reference = new StartTicketSigner(suppliedKey.clone());
		Arrays.fill(suppliedKey, (byte) 0x22);

		assertThat(signer.sign(PAYLOAD)).containsExactly(reference.sign(PAYLOAD));
	}

	@Test
	void rejectsMissingOrShortKeys() {
		assertThatThrownBy(() -> new StartTicketSigner(null))
				.isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> new StartTicketSigner(new byte[0]))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new StartTicketSigner(new byte[31]))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void keepsSuccessivePayloadsIndependent() {
		StartTicketSigner signer = new StartTicketSigner(keyFixture(32, (byte) 0x11));
		byte[] firstSignature = signer.sign(PAYLOAD);
		byte[] otherPayload = "another-fixture".getBytes(StandardCharsets.UTF_8);
		byte[] otherSignature = signer.sign(otherPayload);

		assertThat(signer.sign(PAYLOAD)).containsExactly(firstSignature);
		assertThat(signer.verify(otherPayload, otherSignature)).isTrue();
		assertThat(signer.verify(otherPayload, firstSignature)).isFalse();
	}

	private static byte[] keyFixture(int length, byte marker) {
		byte[] key = new byte[length];
		Arrays.fill(key, marker);
		return key;
	}
}
