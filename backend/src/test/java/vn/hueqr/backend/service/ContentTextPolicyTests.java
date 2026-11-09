package vn.hueqr.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentTextPolicyTests {
	private final ContentTextPolicy policy = new ContentTextPolicy();
	private final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

	@Test
	void supportsOnlyTheFourExactLanguageCodes() {
		for (String language : new String[]{"vi", "en", "fr", "ru"})
			assertThat(policy.language(language)).isEqualTo(language);
		for (String language : new String[]{null, "VI", " vi", "de", ""})
			assertThat(assertThrows(AdminCommandException.class, () -> policy.language(language)).status()).isEqualTo(422);
	}

	@Test
	void hashesTheExactUtf8TextWithoutTrimmingOrUnicodeNormalization() throws Exception {
		String text = "  Huế\nKinh thành 😀\r\n";
		assertThat(policy.bodyHash(text)).isEqualTo(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
		assertThat(policy.bodyHash("é")).isNotEqualTo(policy.bodyHash("e\u0301"));
		assertThat(policy.bodyHash("Huế")).isNotEqualTo(policy.bodyHash(" Huế"));
	}

	@Test
	void rejectsEmptyWhitespaceAndMalformedUnicodeButDoesNotInventATtsLengthLimit() {
		for (String body : new String[]{null, "", " \r\n\t", "\u00a0\u2003", "bad\ud800", "bad\udc00"})
			assertThat(assertThrows(AdminCommandException.class, () -> policy.bodyHash(body)).fields()).containsKey("body");
		assertThat(policy.bodyHash("x".repeat(100_000))).hasSize(32);
	}

	@Test
	void requiresOneStrongTypedEtagAndRejectsWildcardWeakListsOverflowAndWrongAbsentKinds() {
		String expected = policy.tag("slot", id, 1);
		for (String header : new String[]{"*", "W/" + expected, expected + "," + expected, "\"0\"",
				policy.tag("slot", id, "none"), policy.tag("slot", id, "01"), policy.tag("slot", id, "9223372036854775808")})
			assertThat(assertThrows(AdminCommandException.class, () -> policy.requireMatch(header, expected)).status()).isEqualTo(400);
		policy.requireMatch(expected, expected);
	}

	@Test
	void distinguishesMissingPreconditionsFromStaleVersionsAndDifferentObjects() {
		String expected = policy.tag("slot", id, 1);
		for (String header : new String[]{null, "", " "})
			assertThat(assertThrows(AdminCommandException.class, () -> policy.requireMatch(header, expected)).code()).isEqualTo("PRECONDITION_REQUIRED");
		for (String header : new String[]{policy.tag("slot", id, 0), policy.tag("place", id, 1), policy.tag("slot", UUID.randomUUID(), 1)})
			assertThat(assertThrows(AdminCommandException.class, () -> policy.requireMatch(header, expected)).status()).isEqualTo(412);
	}

	@Test
	void distinguishesAnAbsentReviewReferenceFromItsFirstPersistedVersionZero() {
		String absent = policy.tag("review", id, "none");
		policy.requireMatch(absent, absent);
		assertThat(assertThrows(AdminCommandException.class,
				() -> policy.requireMatch(absent, policy.tag("review", id, 0))).status()).isEqualTo(412);
	}

	@Test
	void textIdentifiersMustUseCanonicalLowercaseUuids() {
		UUID text = UUID.fromString("abcdef00-0000-0000-0000-000000000001");
		assertThat(policy.textId(text.toString(), "baseTextId")).isEqualTo(text);
		for (String value : new String[]{null, "", "bad", text.toString().toUpperCase(), "1-1-1-1-1"})
			assertThat(assertThrows(AdminCommandException.class, () -> policy.textId(value, "baseTextId")).fields()).containsKey("baseTextId");
	}
}
