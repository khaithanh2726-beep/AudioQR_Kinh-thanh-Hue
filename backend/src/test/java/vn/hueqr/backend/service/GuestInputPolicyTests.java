package vn.hueqr.backend.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuestInputPolicyTests {
	private final GuestInputPolicy input = new GuestInputPolicy();

	@Test
	void normalizesSpacesButKeepsUnicodeNumbersAndPunctuation() {
		assertThat(input.displayName("  An\u00a0  Bình 123!  ")).isEqualTo("An Bình 123!");
	}

	@Test
	void countsGraphemesInsteadOfUtf16Units() {
		assertThat(input.displayName("a\u0301".repeat(50))).isEqualTo("a\u0301".repeat(50));
		assertThat(input.displayName("👨‍👩‍👧‍👦".repeat(50))).isEqualTo("👨‍👩‍👧‍👦".repeat(50));
		assertThatThrownBy(() -> input.displayName("a\u0301".repeat(51))).isInstanceOf(GuestSessionException.class);
	}

	@Test
	void rejectsRawControlsBeforeWhitespaceNormalization() {
		for (String bad : new String[]{"An\nBình", "An\tBình", "An\rBình", "An\u2028Bình", "An\u0000Bình", "\ud800"}) {
			assertThatThrownBy(() -> input.displayName(bad)).isInstanceOf(GuestSessionException.class);
		}
	}

	@Test
	void rejectsMissingBlankAndLongNames() {
		assertThatThrownBy(() -> input.displayName(null)).isInstanceOf(GuestSessionException.class);
		assertThatThrownBy(() -> input.displayName(" \u00a0 ")).isInstanceOf(GuestSessionException.class);
		assertThatThrownBy(() -> input.displayName("a".repeat(51))).isInstanceOf(GuestSessionException.class);
	}

	@Test
	void onlyAllowsFourExactLanguageCodes() {
		for (String language : new String[]{"vi", "en", "fr", "ru"})
			assertThat(input.language(language)).isEqualTo(language);

		for (String language : new String[]{"VI", "de", "", " vi"}) {
			assertThatThrownBy(() -> input.language(language)).isInstanceOf(GuestSessionException.class);
		}
	}

	@Test
	void validatesCanonicalCredentialsAndRequestKeys() {
		String credential = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32]);

		assertThat(input.credentialHash(credential)).hasSize(32);
		assertThatThrownBy(() -> input.credentialHash(credential + "=")).isInstanceOf(GuestSessionException.class);
		assertThatThrownBy(() -> input.credentialHash(null)).isInstanceOf(GuestSessionException.class);
		assertThatThrownBy(() -> input.requestKey(null)).isInstanceOf(GuestSessionException.class);
		assertThatThrownBy(() -> input.requestKey("1-1-1-1-1")).isInstanceOf(GuestSessionException.class);
	}
}

