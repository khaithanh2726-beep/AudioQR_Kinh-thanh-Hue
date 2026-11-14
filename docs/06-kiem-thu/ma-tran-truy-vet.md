# Ma trận truy vết yêu cầu, ca kiểm và bằng chứng

Đối chiếu PRD hiện hành: **23 FR, 14 UC, 50 AC, 6 NFR**, không đổi nội dung hoặc mã tiêu chí. Có ca kiểm/code không tự đồng nghĩa đạt toàn bộ tiêu chí. `Kỹ thuật có bằng chứng` có thể là unit/integration/fixture; `Còn nghiệm thu` giữ các điều kiện thiết bị, nội dung hoặc lỗi chưa đủ. Các số 530 backend/71 frontend là lượt trước; đợt mới có 11 ca Chrome riêng và 29 ca vận hành, không cộng thành một lượt full.

Nguồn bằng chứng: [Kết quả 14/11](README.md#acceptance-final-result); [CI/CD thật](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664); hồ sơ nội bộ nghiệm thu 14/11 lưu log/ảnh/JSON. [Tra cứu file/dòng](../07-van-hanh/tra-cuu-chuc-nang.md) chỉ vị trí ba lớp. Kiểm model/OS/browser thật được thực hiện sau mốc lưu phiên bản; không ghi viewport Chrome thành Android/iPhone.

## Yêu cầu chức năng

| FR | Trách nhiệm | Ca kiểm nguồn | Bằng chứng và giới hạn |
|---|---|---|---|
| [FR-K01](../02-nghiep-vu/PRD.md) | Bắt đầu/chuẩn hóa tên/chống tạo trùng | [GuestSessionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestSessionApiTests.java#L37) | lost-start-response; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K02](../02-nghiep-vu/PRD.md) | Phiên theo tab, vé ký và hạn 8 giờ | [GuestSessionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestSessionApiTests.java#L37) | tabs-reload; duplicate-owner; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K03](../02-nghiep-vu/PRD.md) | Chạm vùng, ghim, chồng lấn/kéo/phóng | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52) | tabs-reload; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K04](../02-nghiep-vu/PRD.md) | Văn bản đúng cặp/ngôn ngữ và thiếu nội dung | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52) | audio-error-retry; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K05](../02-nghiep-vu/PRD.md) | Tải trọn MP3/Hash, Blob, điều khiển audio | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52) | audio-error-retry; expiry-warning-tail; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K06](../02-nghiep-vu/PRD.md) | Đổi ngôn ngữ và bỏ response cũ | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52) | frontend/tests/guest-transport.test.mjs; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K07](../02-nghiep-vu/PRD.md) | Khôi phục cùng phiên/mốc nghe, không auto phát | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52) | tabs-reload; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K08](../02-nghiep-vu/PRD.md) | Lỗi mạng/thử lại/đối chiếu kết quả chưa rõ | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52) | lost-selection-response; offline-reload-recovery; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K09](../02-nghiep-vu/PRD.md) | Cảnh báo, hết hạn, nghe tiếp và kết thúc | [GuestSessionEndApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestSessionEndApiTests.java#L39) | expiry-warning-tail; tabs-reload; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-K10](../02-nghiep-vu/PRD.md) | ACK và lượt xem đúng lần mở | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52) | lost-selection-response; tabs-reload; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A01](../02-nghiep-vu/PRD.md) | Đăng nhập/đăng xuất, Q3 và hết phiên Admin | [AdminAuthApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminAuthApiTests.java#L40) | admin-401-q2; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A02](../02-nghiep-vu/PRD.md) | CRUD địa điểm, gate bật hiển thị | [AdminPlaceApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminPlaceApiTests.java#L55) | backend controller tests; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A03](../02-nghiep-vu/PRD.md) | Vẽ/xem trước/lưu/áp dụng vùng riêng | [AdminRegionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminRegionApiTests.java#L64) | frontend/tests/admin-editors.test.mjs; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A04](../02-nghiep-vu/PRD.md) | Nháp và cặp đang dùng/Q2 theo điểm-tiếng | [AdminContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminContentApiTests.java#L69) | admin-q2-reload; admin-401-q2; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A05](../02-nghiep-vu/PRD.md) | Tạo/Check/Retry và trạng thái TTS | [AdminTtsApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminTtsApiTests.java#L72) | provider thật và ledger nội dung; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A06](../02-nghiep-vu/PRD.md) | Gắn kết quả đúng revision, không hồi sinh điểm | [TtsWorkerIntegrationTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/TtsWorkerIntegrationTests.java#L84) | provider thật và ledger nội dung; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A07](../02-nghiep-vu/PRD.md) | Hồ sơ duyệt và áp dụng nguyên cặp | [AdminPairApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminPairApiTests.java#L73) | bộ Ngọ Môn đã áp dụng; bộ mới chờ D09; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A08](../02-nghiep-vu/PRD.md) | Xóa giao dịch, FK và lịch sử điểm | [AdminPlaceDeletionTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminPlaceDeletionTests.java#L48) | backend controller tests; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A09](../02-nghiep-vu/PRD.md) | Dọn file độc lập DB, ref/claim và hạn bảo vệ | [LifecycleCleanupTests.java](../../backend/src/test/java/vn/hueqr/backend/service/LifecycleCleanupTests.java#L42) | scheduler riêng; không thay bằng xóa DB; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A10](../02-nghiep-vu/PRD.md) | QR/URL, PNG và bản in chung | [QR HTTPS](../../frontend/tests/e2e/public-rehearsal.spec.mjs) | frontend/tests/e2e/public-rehearsal.spec.mjs; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A11](../02-nghiep-vu/PRD.md) | Danh sách/chi tiết phiên và thống kê | [AdminGuestSessionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminGuestSessionApiTests.java#L43) | admin-statistics-filters; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-A12](../02-nghiep-vu/PRD.md) | Mức đầy đủ 5 × 4; phân biệt đã tạo/áp dụng | [AdminContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminContentApiTests.java#L69) | 20 cặp được chủ đồ án duyệt dùng học phần; API thật kiểm 20/20 Hash; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |
| [FR-D01](../02-nghiep-vu/PRD.md) | Dọn phiên/lượt theo hạn gốc 30 ngày | [GuestRetentionTests.java](../../backend/src/test/java/vn/hueqr/backend/service/GuestRetentionTests.java#L47) | scheduler riêng và restore retention; [Kết quả 14/11](README.md#acceptance-final-result). Dữ liệu/thiết bị còn theo AC bên dưới. |

## Use Case

| UC | Chức năng/case liên quan | Trạng thái phạm vi |
|---|---|---|
| UC-K01 — Truy cập và bắt đầu tham quan | FR-K01, FR-K02 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-K02 — Chọn vùng và nhận thuyết minh | FR-K03, FR-K04, FR-K10 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-K03 — Điều khiển audio và đổi ngôn ngữ | FR-K05, FR-K06 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-K04 — Tải lại tab hoặc phục hồi sau lỗi mạng | FR-K02, FR-K07, FR-K08 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-K05 — Cảnh báo, kết thúc, hết hạn và bắt đầu lại | FR-K02, FR-K09 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A01 — Đăng nhập, đăng xuất và hết phiên Admin | FR-A01 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A02 — Quản lý địa điểm, vẽ và áp dụng vùng | FR-A02, FR-A03 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A03 — Soạn và lưu nháp văn bản/bản dịch | FR-A04 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A04 — Tạo audio và theo dõi tác vụ | FR-A05, FR-A06 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A05 — Hồ sơ duyệt và áp dụng cả cặp văn bản/audio | FR-A06, FR-A07 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A06 — Xóa địa điểm, giữ lịch sử và dọn file | FR-A08, FR-A09 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A07 — Xem, tải và in QR chung | FR-A10 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-A08 — Xem mức đầy đủ, danh sách phiên và thống kê | FR-A11, FR-A12 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |
| UC-D01 — Tự xóa dữ liệu hết hạn lưu | FR-D01 | Có kiểm theo nhóm; chỉ khép khi tất cả AC liên quan và điều kiện môi trường đạt. |

## Tiêu chí nghiệm thu

| AC | FR liên quan | Phép kiểm/bằng chứng và phần chưa khép |
|---|---|---|
| [AC-K01](../02-nghiep-vu/PRD.md) | FR-K01, FR-A10 | QR/URL/PNG đã kiểm; thiết bị/bản in thực chờ lượt riêng. |
| [AC-K02](../02-nghiep-vu/PRD.md) | FR-K01 | GuestInputPolicyTests/GuestSessionApiTests: chuẩn hóa/Unicode/độ dài. |
| [AC-K03](../02-nghiep-vu/PRD.md) | FR-K01, FR-K02 | lost-start-response: mất response sau commit vẫn một phiên/cùng key. |
| [AC-K04](../02-nghiep-vu/PRD.md) | FR-K02 | tabs-reload/duplicate-owner: cùng phiên khi reload, hai tab độc lập. |
| [AC-K05](../02-nghiep-vu/PRD.md) | FR-K03 | tabs-reload + GuestContentApiTests: chọn/đọc/ACK thật. |
| [AC-K06](../02-nghiep-vu/PRD.md) | FR-K03, FR-K05 | GuestContentApiTests + playback.test.mjs; còn kiểm đủ hai điểm có audio trên thiết bị. |
| [AC-K07](../02-nghiep-vu/PRD.md) | FR-K03 | map-pin/map-preview ca tương tác tại mốc trước; tile là fixture. |
| [AC-K08](../02-nghiep-vu/PRD.md) | FR-K03, FR-K05 | delayedSelectionAndLeaveCannotOverwriteTheNewerSelection; playback unit. |
| [AC-K09](../02-nghiep-vu/PRD.md) | FR-K03, FR-K05 | GuestMap/map-pin kiểm chồng lấn/Hủy bằng fixture; cần thiết bị thật. |
| [AC-K10](../02-nghiep-vu/PRD.md) | FR-K04, FR-K06 | missingTranslationHasNoGrantNoFallbackAndNoViewUntilTheFirstAvailableLanguage. |
| [AC-K11](../02-nghiep-vu/PRD.md) | FR-K04, FR-K05 | audio-error-retry: văn bản còn, tải lại MP3 không cộng lượt. |
| [AC-K12](../02-nghiep-vu/PRD.md) | FR-K05 | playback.test.mjs + tabs-reload/expiry-warning-tail; còn mẫu máy. |
| [AC-K13](../02-nghiep-vu/PRD.md) | FR-K03, FR-K05 | samePlaceAndLanguageChangesKeepOneOpeningAndOneView; playback unit. |
| [AC-K14](../02-nghiep-vu/PRD.md) | FR-K06 | GuestContentApiTests language/sequence + messages/transport unit; bộ 20 cặp đã áp dụng, API thật kiểm bốn ngôn ngữ; kiểm giao diện/thiết bị cuối còn riêng. |
| [AC-K15](../02-nghiep-vu/PRD.md) | FR-K02, FR-K07 | tabs-reload: giữ id, pause, một view; không đo chính xác từng giây. |
| [AC-K16](../02-nghiep-vu/PRD.md) | FR-K05, FR-K08 | offline-reload-recovery/audio-error-retry: lỗi API thật bị chặn mạng rồi phục hồi. |
| [AC-K17](../02-nghiep-vu/PRD.md) | FR-K09 | expiry-warning-tail: timestamp phiên fixture + clock browser; không chờ 8 giờ thật. |
| [AC-K18](../02-nghiep-vu/PRD.md) | FR-K02, FR-K05, FR-K09 | expiry-warning-tail: khóa tua/map/tiếng, chỉ pause/resume bài đang phát; còn các biến thể thiết bị. |
| [AC-K19](../02-nghiep-vu/PRD.md) | FR-K02, FR-K09 | expiry-warning-tail reload mất ngoại lệ; GuestSessionApiTests trạng thái mất/hết hạn. |
| [AC-K20](../02-nghiep-vu/PRD.md) | FR-K02, FR-K09 | tabs-reload kết thúc bằng UI; GuestSessionEndApiTests kiểm replay/error. |
| [AC-K21](../02-nghiep-vu/PRD.md) | FR-K10 | lost-selection-response/tabs-reload: 0→1 view, retry/reload không cộng. |
| [AC-K22](../02-nghiep-vu/PRD.md) | FR-K10 | GuestContentApiTests: thiếu văn bản không grant/view, ACK có bản thành công mới ghi. |
| [AC-A01](../02-nghiep-vu/PRD.md) | FR-A01 | AdminAuthApiTests/AdminAuthHttpTests và login Chrome; Q3 dùng MutableClock trong test. |
| [AC-A02](../02-nghiep-vu/PRD.md) | FR-A01, FR-A02, FR-A03, FR-A04, FR-A07 | admin-q2-reload/admin-401-q2: bản văn chưa lưu; vùng/tham chiếu và khác tài khoản còn cần rà đủ. |
| [AC-A03](../02-nghiep-vu/PRD.md) | FR-A02 | AdminPlaceApiTests/DisplayNamePolicy: create/edit/ẩn/trùng tên/version. |
| [AC-A04](../02-nghiep-vu/PRD.md) | FR-A02 | AdminPlaceApiTests/AdminPairApiTests: gate đủ vùng/cặp vi, không tự bật. |
| [AC-A05](../02-nghiep-vu/PRD.md) | FR-A03 | AdminRegionApiTests + admin-editors.test.mjs; thao tác vẽ có bằng chứng mốc trước. |
| [AC-A06](../02-nghiep-vu/PRD.md) | FR-A03 | AdminRegionApiTests: save khác apply; Q2 vùng trên toàn thiết bị chưa khép. |
| [AC-A07](../02-nghiep-vu/PRD.md) | FR-A04 | AdminContentApiTests + admin-q2-reload; chưa sửa cặp phục vụ trong lượt này. |
| [AC-A08](../02-nghiep-vu/PRD.md) | FR-A05 | AdminTtsApiTests + 19 lượt TTS thật qua API; không đồng nghĩa duyệt nội dung. |
| [AC-A09](../02-nghiep-vu/PRD.md) | FR-A05 | AdminTtsApiTests/TtsWorkerIntegrationTests: accepted/phase/failure, quota được rà. |
| [AC-A10](../02-nghiep-vu/PRD.md) | FR-A05 | AdminTtsApiTests/recovery: receipt và retry rõ; không retry outcome chưa rõ. |
| [AC-A11](../02-nghiep-vu/PRD.md) | FR-A06 | TtsWorkerIntegrationTests: snapshot V1/target version, kết quả gắn đúng revision. |
| [AC-A12](../02-nghiep-vu/PRD.md) | FR-A06 | TtsWorkerIntegrationTests/restart; chưa kiểm mọi crash/lease thiếu owner. |
| [AC-A13](../02-nghiep-vu/PRD.md) | FR-A01, FR-A05, FR-A06 | AdminAudioPreviewTests/StreamTests + browser restore; hồ sơ chủ đồ án duyệt 19 bài có giới hạn; không suy đã nghe trọn từ phép kiểm tự động. |
| [AC-A14](../02-nghiep-vu/PRD.md) | FR-A07 | AdminPairApiTests kiểm tham chiếu/gate; hồ sơ D09 bộ mới vẫn chờ nghe. |
| [AC-A15](../02-nghiep-vu/PRD.md) | FR-A06, FR-A07 | AdminPairApiTests atomic apply/ETag/rollback; thực phục vụ 20 cặp; 19 cặp mới áp dụng qua API/ETag/review, Ngọ Môn vi được giữ. |
| [AC-A16](../02-nghiep-vu/PRD.md) | FR-A02, FR-A08 | AdminPlaceDeletionTests: TX/FK/rollback và receipt; UI xóa cần kiểm ma trận thiết bị. |
| [AC-A17](../02-nghiep-vu/PRD.md) | FR-A08, FR-A11 | AdminPlaceDeletionTests + AdminGuestSessionApiTests giữ snapshot điểm cũ. |
| [AC-A18](../02-nghiep-vu/PRD.md) | FR-A06, FR-A08 | AdminPlaceDeletionTests/TtsWorkerIntegrationTests không hồi sinh target đã xóa. |
| [AC-A19](../02-nghiep-vu/PRD.md) | FR-A09 | LifecycleCleanupTests: DB rollback không làm unlink file. |
| [AC-A20](../02-nghiep-vu/PRD.md) | FR-A09 | LifecycleCleanupTests: ref/use/claim + boundary tám giờ; scheduler riêng đã kiểm. |
| [AC-A21](../02-nghiep-vu/PRD.md) | FR-A09 | LifecycleCleanupTests: lỗi unlink giữ trạng thái/thử lại, không báo xóa giả. |
| [AC-A22](../02-nghiep-vu/PRD.md) | FR-A10 | public-rehearsal.spec.mjs: URL/PNG/print CSS; chưa bản in/QR đủ mẫu máy. |
| [AC-A23](../02-nghiep-vu/PRD.md) | FR-A11, FR-K10 | admin-statistics-filters + AdminGuestSessionApiTests: ngày/count/Asia-Saigon. |
| [AC-A24](../02-nghiep-vu/PRD.md) | FR-A11 | AdminGuestSessionApiTests/ReportsPanel; còn toàn ma trận modal/lỗi/thiết bị. |
| [AC-A25](../02-nghiep-vu/PRD.md) | FR-A12 | AdminContentApiTests + CompletenessPanel; audio kỹ thuật khác cặp áp dụng. |
| [AC-A26](../02-nghiep-vu/PRD.md) | FR-A12, FR-A07 | Đủ 20 văn bản/20 audio, hồ sơ chủ đồ án duyệt 19 cặp mới cho học phần; đã áp dụng và API kiểm Hash 20/20. Kiểm thiết bị cuối không thay bằng API. |
| [AC-D01](../02-nghiep-vu/PRD.md) | FR-D01 | GuestRetentionTests và scheduler trên restore riêng: xóa vật lý theo anchor gốc. |
| [AC-D02](../02-nghiep-vu/PRD.md) | FR-D01, FR-A11 | GuestSessionSchemaTests/AdminGuestSessionApiTests: dữ liệu tối thiểu/quyền; log hạ tầng riêng. |

## Yêu cầu phi chức năng

| NFR | Đối chiếu | Giới hạn |
|---|---|---|
| [NFR-01](../02-nghiep-vu/PRD.md) | Dùng trên điện thoại: đầu trang gọn; bản đồ/tên điểm/audio vẫn thấy khi chỉ box văn bản cuộn; audio ngoài/trên box.… | Chrome nhiều viewport ở mốc trước; đợt này desktop thật, mẫu máy/OS/build và accessibility đầy đủ còn riêng. |
| [NFR-02](../02-nghiep-vu/PRD.md) | Tính nhất quán/toàn vẹn: một audio khách mỗi tab, lựa chọn mới nhất thắng/không lượt lặp; áp dụng cặp không để lộ n… | GuestContent/AdminPair/deletion/retention API + retry thật sau commit; không suy mọi crash đã đạt. |
| [NFR-03](../02-nghiep-vu/PRD.md) | Quản trị cần phiên hợp lệ; 30 phút Q3 độc lập khách. Hết phiên dừng nghe thử/che nội dung, đăng nhập lại không tự p… | AdminAuthApiTests MutableClock cho Q3, admin-401-q2 cho UI; chưa chờ 30 phút thật trên tất cả thiết bị. |
| [NFR-04](../02-nghiep-vu/PRD.md) | Phục hồi/báo đúng trạng thái: không báo lưu/xóa/tạo thành công giả. Q2 bảo vệ ba loại nội dung qua hết phiên/lưu lỗ… | 10 ca Chrome gồm Q2/reload/lỗi mạng/mất response; còn Q2 vùng/tham chiếu và toàn lỗi chưa rõ. |
| [NFR-05](../02-nghiep-vu/PRD.md) | Đa ngôn ngữ/nội dung có căn cứ: bộ chọn chung cho giao diện khách và nội dung/audio, tên khách nguyên vẹn; nút biểu… | 20 cặp được duyệt dùng học phần và UI vi/en/fr/ru; giữ giới hạn phát âm/thẩm định chuyên gia và kiểm thiết bị cuối. |
| [NFR-06](../02-nghiep-vu/PRD.md) | Hoạt động trực tuyến, lỗi không làm mất lựa chọn/nội dung đã có; giữ phần audio vẫn phát được nhưng không hứa hoạt … | Có network-fault/Blob/expiry fixture; không thử offline service hoặc cam kết chạy khi laptop/ngrok tắt. |

## Cách cập nhật

Khi sửa mã, rà file/dòng và ca thực sự tác động; lưu build/schema/môi trường, số ca và kết luận vào báo cáo kiểm. Không tích issue hoặc ghi toàn AC đạt chỉ từ bảng này. Khi có kết quả thiết bị/nội dung, cập nhật đúng hàng còn mở và hồ sơ gốc; không thay fixture bằng nhãn chạy thật.
