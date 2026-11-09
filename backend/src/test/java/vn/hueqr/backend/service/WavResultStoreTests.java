package vn.hueqr.backend.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.support.WavFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WavResultStoreTests {
	@TempDir
	Path root;

	@Test
	void acceptsCompleteMono24kPcmAndCalculatesExactDurationAndChecksum() {
		byte[] wav = WavFixture.oneSecond();
		var info = WavPolicy.inspect(wav);
		assertThat(info.byteSize()).isEqualTo(48044);
		assertThat(info.duration()).isEqualByComparingTo("1");
		assertThat(info.checksum()).isEqualTo(WavPolicy.hash(wav));
	}

	@Test
	void rejectsTruncationOrTrailingBytesInsteadOfPretendingContainerIsComplete() {
		byte[] wav = WavFixture.oneSecond();
		assertThrows(IllegalArgumentException.class, () -> WavPolicy.inspect(Arrays.copyOf(wav, wav.length - 1)));
		assertThrows(IllegalArgumentException.class, () -> WavPolicy.inspect(Arrays.copyOf(wav, wav.length + 2)));
		assertThrows(IllegalArgumentException.class, () -> WavPolicy.inspect(new byte[0]));
	}

	@Test
	void rejectsWrongCodecChannelsRateAndFrameAlignment() {
		for (int offset : new int[]{20, 22, 24, 28, 32, 34}) {
			byte[] wav = WavFixture.oneSecond();
			wav[offset] ^= 1;
			assertThrows(IllegalArgumentException.class, () -> WavPolicy.inspect(wav));
		}
	}

	@Test
	void rejectsOversizedUnsignedChunkLengthsAndMissingAudio() {
		byte[] wav = WavFixture.oneSecond();
		ByteBuffer.wrap(wav).order(ByteOrder.LITTLE_ENDIAN).putInt(40, -1);
		assertThrows(IllegalArgumentException.class, () -> WavPolicy.inspect(wav));
		byte[] empty = Arrays.copyOf(WavFixture.oneSecond(), 44);
		ByteBuffer.wrap(empty).order(ByteOrder.LITTLE_ENDIAN).putInt(4, 36).putInt(40, 0);
		assertThrows(IllegalArgumentException.class, () -> WavPolicy.inspect(empty));
	}

	@Test
	void storesCompleteBytesWithoutAddingAnotherRiffHeaderAndRecoversOnlyMatchingManifest() throws Exception {
		var store = new WavResultStore(root, new ObjectMapper());
		UUID job = UUID.randomUUID();
		UUID token = UUID.randomUUID();
		var staged = store.save(job, token, "a".repeat(64), WavFixture.oneSecond());
		assertThat(Files.readAllBytes(root.resolve(staged.storageKey()))).isEqualTo(WavFixture.oneSecond());
		assertThat(store.recover(job, token, "a".repeat(64))).contains(staged);
		assertThat(store.recover(job, UUID.randomUUID(), "a".repeat(64))).isEmpty();
		assertThrows(IOException.class, () -> store.recover(job, token, "b".repeat(64)));
		assertThat(Files.readString(root.resolve(job + "-" + token + ".json"))).doesNotContain("body", "prompt", "apiKey");
	}

	@Test
	void refusesCorruptedOrMissingWavInsteadOfRegisteringReadyMetadata() throws Exception {
		var store = new WavResultStore(root, new ObjectMapper());
		UUID job = UUID.randomUUID();
		UUID token = UUID.randomUUID();
		var staged = store.save(job, token, "a".repeat(64), WavFixture.oneSecond());
		byte[] corrupt = WavFixture.oneSecond();
		corrupt[100] ^= 1;
		Files.write(root.resolve(staged.storageKey()), corrupt);
		assertThrows(IOException.class, () -> store.recover(job, token, "a".repeat(64)));
		Files.delete(root.resolve(staged.storageKey()));
		assertThrows(IOException.class, () -> store.recover(job, token, "a".repeat(64)));
	}

	@Test
	void manifestCannotEscapeRootOrLieAboutJobTokenHashSizeOrDuration() throws Exception {
		var mapper = new ObjectMapper();
		var store = new WavResultStore(root, mapper);
		UUID job = UUID.randomUUID();
		UUID token = UUID.randomUUID();
		var staged = store.save(job, token, "a".repeat(64), WavFixture.oneSecond());
		Path manifest = root.resolve(job + "-" + token + ".json");
		for (String field : new String[]{"jobId", "workerToken", "textHash", "storageKey", "byteSize", "duration"}) {
			var node = mapper.valueToTree(staged);
			var object = (tools.jackson.databind.node.ObjectNode) node;
			if (field.equals("byteSize") || field.equals("duration"))
				object.put(field, 2);
			else
				object.put(field, field.equals("storageKey") ? "../outside.wav" : UUID.randomUUID().toString());
			Files.write(manifest, mapper.writeValueAsBytes(object));
			assertThrows(IOException.class, () -> store.recover(job, token, "a".repeat(64)));
		}
	}

	@Test
	void invalidWavCreatesNoFilesAndManifestCanBeReadByANewStoreInstance() throws Exception {
		var store = new WavResultStore(root, new ObjectMapper());
		assertThrows(IllegalArgumentException.class, () -> store.save(UUID.randomUUID(), UUID.randomUUID(), "a".repeat(64), new byte[0]));
		try (var list = Files.list(root)) {
			assertThat(list.count()).isZero();
		}
		UUID job = UUID.randomUUID();
		UUID token = UUID.randomUUID();
		var staged = store.save(job, token, "a".repeat(64), WavFixture.oneSecond());
		assertThat(new WavResultStore(root, new ObjectMapper()).recover(job, token, "a".repeat(64))).contains(staged);
	}
}
