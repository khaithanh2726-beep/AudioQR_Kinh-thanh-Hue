package vn.hueqr.backend.repository;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import vn.hueqr.backend.entity.AudioRevision;
import vn.hueqr.backend.entity.AudioReviewReference;
import vn.hueqr.backend.entity.ContentPair;
import vn.hueqr.backend.entity.FileAsset;
import vn.hueqr.backend.entity.TextRevision;
import vn.hueqr.backend.support.AudioDataFixture;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static vn.hueqr.backend.support.AudioDataFixture.NOW;
import static vn.hueqr.backend.support.AudioDataFixture.bytes;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class AudioPairRepositoryTests {
	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private EntityManager entities;

	@Autowired
	private AudioRevisionRepository audios;

	@Autowired
	private AudioReviewReferenceRepository reviews;

	@Autowired
	private ContentPairRepository pairs;

	@Autowired
	private ContentSlotRepository slots;

	@Autowired
	private TextRevisionRepository texts;

	@Autowired
	private FileAssetRepository files;

	@Test
	void reloadsAudioPairAndUnicodeReviewWithCorrectRelationshipsAndMicroseconds() {
		var source = new AudioDataFixture(jdbc).insertSource("vi");
		var text = texts.findById(source.text()).orElseThrow();
		var slot = slots.findById(source.slot()).orElseThrow();
		var audio = audios.saveAndFlush(new AudioRevision(UUID.randomUUID(), text, source.job(), source.wav(), source.mp3(), NOW));
		var pair = pairs.saveAndFlush(new ContentPair(UUID.randomUUID(), slot, text.getId(), audio.getId(), NOW));
		UUID account = UUID.randomUUID();
		jdbc.update("INSERT INTO admin_account (id, login_name, password_hash, enabled) VALUES (?, ?, 'Fixture', TRUE)", bytes(account), account.toString());
		reviews.saveAndFlush(new AudioReviewReference(audio.getId(), "Hồ sơ nghe trọn bài, mục 2", "Chờ rà tên riêng", account, NOW));
		entities.clear();

		var loaded = audios.findById(audio.getId()).orElseThrow();
		assertThat(loaded.getTextRevision().getId()).isEqualTo(source.text());
		assertThat(loaded.getTextRevision().getSlot().getLanguage()).isEqualTo("vi");
		assertThat(loaded.getJobId()).isEqualTo(source.job());
		assertThat(loaded.getWavFileId()).isEqualTo(source.wav());
		assertThat(loaded.getMp3FileId()).isEqualTo(source.mp3());
		assertThat(loaded.getCreatedAt()).isEqualTo(NOW);
		var loadedPair = pairs.findByIdAndSlot_Id(pair.getId(), source.slot()).orElseThrow();
		assertThat(loadedPair.getTextRevisionId()).isEqualTo(source.text());
		assertThat(loadedPair.getAudioRevisionId()).isEqualTo(audio.getId());
		assertThat(loadedPair.getSlot().getId()).isEqualTo(source.slot());
		assertThat(loadedPair.getCreatedAt()).isEqualTo(NOW);
		assertThat(pairs.findByIdAndSlot_Id(pair.getId(), UUID.randomUUID())).isEmpty();
		var review = reviews.findById(audio.getId()).orElseThrow();
		assertThat(review.getLocator()).isEqualTo("Hồ sơ nghe trọn bài, mục 2");
		assertThat(review.getLimitations()).isEqualTo("Chờ rà tên riêng");
		assertThat(review.getRecordedBy()).isEqualTo(account);
		assertThat(review.getRecordedAt()).isEqualTo(NOW);
		assertThat(review.getVersion()).isZero();
		assertThat(slots.findById(source.slot()).orElseThrow().getActivePairId()).isNull();
	}

	@Test
	void savingANewDraftKeepsTheAppliedPairAndItsOldTextUnchanged() {
		var data = new AudioDataFixture(jdbc);
		var source = data.insertSource("vi");
		UUID pair = data.insertPair(source.slot(), source.text(), data.insertAudio(source));
		jdbc.update("UPDATE content_slot SET active_pair_id = ? WHERE id = ?", bytes(pair), bytes(source.slot()));
		var slot = slots.findById(source.slot()).orElseThrow();
		Long oldVersion = slot.getVersion();
		var next = texts.saveAndFlush(new TextRevision(UUID.randomUUID(), slot, "Bản sửa chưa áp dụng", new byte[32], NOW));
		slot.setDraftTextId(next.getId());
		slots.saveAndFlush(slot);
		entities.clear();
		var loaded = slots.findById(source.slot()).orElseThrow();
		assertThat(loaded.getActivePairId()).isEqualTo(pair);
		assertThat(loaded.getDraftTextId()).isEqualTo(next.getId());
		assertThat(loaded.getVersion()).isEqualTo(oldVersion + 1);
		assertThat(pairs.findById(pair).orElseThrow().getTextRevisionId()).isEqualTo(source.text());
		assertThat(texts.findById(source.text()).orElseThrow().getBody()).isEqualTo("Bản đã lưu");
		assertThat(reviews.count()).isZero();
	}

	@Test
	void wavAndMp3MetadataRoundTripWithoutSharingMutableHashArrays() {
		byte[] hash = new byte[32];
		hash[0] = 1;
		UUID wavId = UUID.randomUUID();
		UUID mp3Id = UUID.randomUUID();
		files.saveAndFlush(new FileAsset(wavId, wavId + ".wav", hash, 48044, new BigDecimal("1.123456789"), NOW));
		files.saveAndFlush(FileAsset.mp3(mp3Id, mp3Id + ".mp3", hash, 12345, new BigDecimal("1.123456789"), NOW));
		hash[0] = 9;
		entities.clear();
		var wav = files.findById(wavId).orElseThrow();
		var mp3 = files.findById(mp3Id).orElseThrow();
		assertThat(wav.getFormat()).isEqualTo("WAV");
		assertThat(mp3.getFormat()).isEqualTo("MP3");
		assertThat(mp3.getState()).isEqualTo("READY");
		assertThat(mp3.getDuration()).isEqualByComparingTo("1.123456789");
		assertThat(mp3.getChecksum()[0]).isEqualTo((byte) 1);
		mp3.getChecksum()[0] = 7;
		assertThat(mp3.getChecksum()[0]).isEqualTo((byte) 1);
	}

	@Test
	void updatingAStaleReviewCannotOverwriteTheNewerReference() {
		var data = new AudioDataFixture(jdbc);
		UUID audio = data.insertAudio(data.insertSource("vi"));
		UUID account = UUID.randomUUID();
		jdbc.update("INSERT INTO admin_account (id, login_name, password_hash, enabled) VALUES (?, ?, 'Fixture', TRUE)", bytes(account), account.toString());
		var review = reviews.saveAndFlush(new AudioReviewReference(audio, "Hồ sơ cũ", null, account, NOW));
		// Mô phỏng giao dịch khác đã sửa hồ sơ trước khi bản đang giữ được lưu lại
		jdbc.update("UPDATE audio_review_ref SET locator = 'Hồ sơ mới', version = version + 1 WHERE audio_revision_id = ?", bytes(audio));
		review.update("Bản cũ ghi đè", null, account, NOW);
		assertThrows(ObjectOptimisticLockingFailureException.class, () -> reviews.saveAndFlush(review));
		assertThat(jdbc.queryForObject("SELECT locator FROM audio_review_ref WHERE audio_revision_id = ?", String.class, bytes(audio))).isEqualTo("Hồ sơ mới");
	}
}
