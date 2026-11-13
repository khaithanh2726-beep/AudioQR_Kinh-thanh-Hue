package vn.hueqr.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import vn.hueqr.backend.entity.AudioRevision;
import vn.hueqr.backend.entity.ContentSlot;
import vn.hueqr.backend.entity.FileAsset;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.TextRevision;
import vn.hueqr.backend.repository.AudioRevisionRepository;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.FileReadClaimRepository;
import vn.hueqr.backend.repository.PlaceRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminAudioPreviewStreamTests {
	@TempDir
	Path root;

	private final UUID aid = UUID.randomUUID();
	private final UUID fid = UUID.randomUUID();
	private final UUID owner = UUID.randomUUID();
	private final byte[] bytes = "Fixture-stream-bytes".getBytes(java.nio.charset.StandardCharsets.UTF_8);
	private final Clock clock = Clock.fixed(Instant.parse("2026-11-10T00:00:00Z"), ZoneOffset.UTC);
	private AudioRevisionRepository audios;
	private PlaceRepository places;
	private FileAssetRepository files;
	private FileReadClaimRepository claims;
	private RuntimeOwnerService owners;
	private RuntimeOwnerService.Task task;
	private AdminAudioPreviewService preview;

	@BeforeEach
	void setUp() throws IOException {
		audios = mock(AudioRevisionRepository.class);
		places = mock(PlaceRepository.class);
		files = mock(FileAssetRepository.class);
		claims = mock(FileReadClaimRepository.class);
		owners = mock(RuntimeOwnerService.class);
		task = mock(RuntimeOwnerService.Task.class);
		var manager = mock(PlatformTransactionManager.class);
		when(manager.getTransaction(any())).thenAnswer(call -> new SimpleTransactionStatus());
		var place = new Place(UUID.randomUUID(), "Fixture", clock.instant());
		var slot = new ContentSlot(UUID.randomUUID(), place, "vi");
		var text = new TextRevision(UUID.randomUUID(), slot, "Fixture", new byte[32], clock.instant());
		var audio = new AudioRevision(aid, text, UUID.randomUUID(), UUID.randomUUID(), fid, clock.instant());
		when(audios.findPlaceIdById(aid)).thenReturn(java.util.Optional.of(place.getId()));
		when(places.findByIdForUpdate(place.getId())).thenReturn(java.util.Optional.of(place));
		when(audios.findById(aid)).thenReturn(java.util.Optional.of(audio));
		when(files.findByIdForUpdate(fid)).thenReturn(java.util.Optional.of(file(fid + ".mp3")));
		when(owners.begin("READ", aid)).thenReturn(task);
		when(task.instanceId()).thenReturn(owner);
		Files.write(root.resolve(fid + ".mp3"), bytes);
		preview = new AdminAudioPreviewService(audios, places, files, claims, owners, clock, root.toString(), manager);
	}

	@Test
	void realStreamTransfersExactBytesAndKeepsClaimUntilActualClose() throws Exception {
		var checks = new AtomicInteger();
		var download = preview.download(aid.toString(), checks::incrementAndGet);
		var stream = download.resource().getInputStream();
		try {
			assertThat(stream.readAllBytes()).isEqualTo(bytes);
			assertThat(download.checksum()).isEqualTo(WavPolicy.hash(bytes));
			verify(claims).claim(any(), eq(fid), eq(aid), eq(owner), eq(clock.instant()));
			verify(claims, never()).release(any());
			assertThat(checks.get()).isGreaterThanOrEqualTo(3);
		}
		finally {
			stream.close();
		}
		verify(claims).release(any());
		verify(task).close();
	}

	@FunctionalInterface
	private interface ReadOperation {
		void read(InputStream stream) throws IOException;
	}

	@Test
	void everyTransferPathChecksPermissionAndCannotBypassExpiry() throws Exception {
		// Kiểm cả các hàm InputStream dùng nội bộ read, tránh một đường tải bỏ qua lớp kiểm quyền
		List<ReadOperation> reads = List.of(InputStream::read, s -> s.read(new byte[4]), s -> s.read(new byte[4], 1, 2),
				s -> s.readNBytes(4), InputStream::readAllBytes, s -> s.transferTo(new ByteArrayOutputStream()), s -> s.skip(1), InputStream::available);
		for (var operation : reads) {
			var permitted = new AtomicBoolean(true);
			try (var stream = preview.download(aid.toString(), () -> {
				if (!permitted.get())
					throw new AdminSessionException("ADMIN_SESSION_EXPIRED");
			}).resource().getInputStream()) {
				assertThat(stream.read()).isEqualTo(bytes[0]);
				permitted.set(false);
				assertThatThrownBy(() -> operation.read(stream)).isInstanceOf(IOException.class).hasMessage("ADMIN_PREVIEW_FORBIDDEN");
			}
		}
		verify(claims, times(reads.size())).release(any());
		verify(task, times(reads.size())).close();
	}

	@Test
	void repeatedCloseReleasesOnlyOnceAndClosedStreamCannotBeRead() throws Exception {
		var stream = preview.download(aid.toString(), () -> {}).resource().getInputStream();
		stream.close();
		stream.close();
		assertThatThrownBy(stream::read).isInstanceOf(IOException.class).hasMessage("ADMIN_PREVIEW_CLOSED");
		verify(claims).release(any());
		verify(task).close();
	}

	@Test
	void deniedAccessDoesNotLookUpPrivateAudioOrOpenAClaim() {
		assertThatThrownBy(() -> preview.download(aid.toString(), () -> {
			throw new AdminSessionException("ADMIN_SESSION_EXPIRED");
		})).isInstanceOf(AdminSessionException.class);
		verifyNoInteractions(audios, places, files, claims, owners);
	}

	@Test
	void expiryAfterFileLockDoesNotOpenAStreamOrAReadScope() {
		var checks = new AtomicInteger();
		assertThatThrownBy(() -> preview.download(aid.toString(), () -> {
			if (checks.incrementAndGet() == 2)
				throw new AdminSessionException("ADMIN_SESSION_EXPIRED");
		})).isInstanceOf(AdminSessionException.class);
		verify(files).findByIdForUpdate(fid);
		verifyNoInteractions(claims, owners);
	}

	@Test
	void nonCanonicalPathAndWrongSizeCannotExposeBytesOrCreateAClaim() throws Exception {
		when(files.findByIdForUpdate(fid)).thenReturn(java.util.Optional.of(file("../outside.mp3")));
		assertThatThrownBy(() -> preview.download(aid.toString(), () -> {})).hasMessage("MEDIA_NOT_READY");
		when(files.findByIdForUpdate(fid)).thenReturn(java.util.Optional.of(file(fid + ".mp3")));
		Files.write(root.resolve(fid + ".mp3"), new byte[]{1});
		assertThatThrownBy(() -> preview.download(aid.toString(), () -> {})).hasMessage("MEDIA_NOT_READY");
		verifyNoInteractions(claims, owners);
	}

	@Test
	void claimFailureClosesTheOpenedRuntimeScope() {
		doThrow(new IllegalStateException("CLAIM_FAILED")).when(claims).claim(any(), any(), any(), any(), any());
		assertThatThrownBy(() -> preview.download(aid.toString(), () -> {})).hasMessage("CLAIM_FAILED");
		verify(task).close();
		// Repository/transaction được giả ở lớp này, rollback DB được kiểm bằng bộ MySQL riêng
		verify(claims, never()).release(any());
	}

	@Test
	void releasingClaimFailureStillClosesTheRuntimeScope() throws Exception {
		var stream = preview.download(aid.toString(), () -> {}).resource().getInputStream();
		doThrow(new IllegalStateException("RELEASE_FAILED")).when(claims).release(any());
		assertThatThrownBy(stream::close).hasMessage("RELEASE_FAILED");
		verify(task).close();
		stream.close();
		verify(task).close();
	}

	private FileAsset file(String key) {
		return FileAsset.mp3(fid, key, java.util.HexFormat.of().parseHex(WavPolicy.hash(bytes)), bytes.length, java.math.BigDecimal.ONE, clock.instant());
	}
}
