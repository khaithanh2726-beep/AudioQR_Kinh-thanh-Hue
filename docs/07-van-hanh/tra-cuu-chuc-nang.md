# Tra cứu chức năng, mã nguồn và sơ đồ

Bảng dùng khi trình bày hoặc rà mã. Liên kết dẫn tới tệp và dòng tại mốc 14/11/2026; trong VS Code có thể mở tệp rồi Ctrl+G tới số dòng. Khi mã thay đổi cần rà lại vị trí. Mã lớp/chức năng không tự chứng minh đã nghiệm thu toàn bộ. [Ma trận yêu cầu và bằng chứng](../06-kiem-thu/ma-tran-truy-vet.md) ghi giới hạn từng nhóm.

Luồng dữ liệu chính: Vue → Controller → Service → Repository → MySQL. Controller nhận HTTP/DTO, Service điều phối nghiệp vụ/giao dịch, Repository truy cập dữ liệu. QR được tạo phía frontend từ URL cấu hình; worker/scheduler/CLI có điểm vào riêng, không tạo Controller giả.

Tra nhanh: [sơ đồ theo câu hỏi](#tra-cuu-so-do), [chức năng → mã nguồn](#tra-cuu-ma-nguon), [điểm vào bổ sung](#diem-vao-bo-sung), [nguồn ca kiểm](#nguon-ca-kiem). Bộ ôn tập riêng tại [99-luu-tru](../99-luu-tru/on-tap-van-dap.md).

<a id="tra-cuu-so-do"></a>

## Sơ đồ theo câu hỏi vấn đáp

Liên kết “mục nguồn” mở đúng phần có sơ đồ và giải thích. SVG dùng xem/phóng to; Mermaid hoặc draw.io là nguồn chỉnh sửa. Bảng chỉ dẫn tới nguồn chính, không sao chép hình để tránh lệch phiên bản.

| Khi được hỏi | Sơ đồ / mục nguồn | Hình hoặc nguồn chỉnh sửa | Điểm cần giải thích |
|---|---|---|---|
| Hệ thống có ai dùng, làm được gì? | [Use Case tổng quan — PRD mục 5](../02-nghiep-vu/PRD.md#use-case-overview) | [SVG](../02-nghiep-vu/use-case-overview.svg), [draw.io](../02-nghiep-vu/use-case-overview.drawio) | 14 UC: 5 Khách, 8 Admin, 1 dọn dữ liệu; không phải 14 bảng SQL |
| Kiến trúc ba lớp và các thành phần chạy ở đâu? | [Kiến trúc tổng thể — TDD mục 2](../03-ky-thuat/TDD.md#tdd-overview) | Mermaid trong mục nguồn | Vue cùng origin với API; Controller → Service → Repository → MySQL; worker và file có vai trò riêng |
| Một thao tác Admin đi qua ba lớp thế nào? | [Tuần tự áp dụng cặp — TDD mục 4](../03-ky-thuat/TDD.md#tdd-layers) | Mermaid trong mục nguồn | AdminPairService điều phối, Repository truy cập dữ liệu; chỉ trả thành công sau giao dịch |
| Database có bao nhiêu bảng, quan hệ nào là FK? | [ERD vật lý đầy đủ](../03-ky-thuat/erd-vat-ly.md) | [SVG đầy đủ](../03-ky-thuat/erd-vat-ly.svg), Mermaid trong mục nguồn | 25 bảng = 24 ứng dụng + Flyway; 36 FK; phân biệt FK tổ hợp, pointer và snapshot logic |
| Địa điểm/vùng/văn bản/audio đang dùng liên kết ra sao? | [ERD nhóm nội dung — mô hình mục 2](../03-ky-thuat/data-model.md#model-content) | Mermaid trong mục nguồn | Place → slot theo ngôn ngữ → cặp; văn bản/audio phải đúng revision; nháp khác áp dụng |
| Phiên, lần mở, quyền tải và lượt xem khác nhau thế nào? | [ERD nhóm Khách — mô hình mục 3](../03-ky-thuat/data-model.md#model-guest) | Mermaid trong mục nguồn | Session/opening/grant/view khác đơn vị; ACK chống đếm lặp, snapshot giữ lịch sử |
| Window, receipt, job và file/claim dùng làm gì? | [ERD nhóm lệnh/TTS/file — mô hình mục 4](../03-ky-thuat/data-model.md#model-operations) | Mermaid trong mục nguồn | Receipt gắn thao tác; file claim bảo vệ lúc sử dụng; cạnh retry logic không phải FK SQL |
| Dữ liệu vào/ra toàn hệ thống gồm gì? | [DFD mức 0 — ngữ cảnh](../03-ky-thuat/dfd.md#dfd-context) | Mermaid trong mục nguồn | Khách/Admin/Gemini và biên hệ thống; mũi tên mang dữ liệu, không phải thứ tự thực hiện |
| Các xử lý chính trao đổi với kho nào? | [DFD mức 1](../03-ky-thuat/dfd.md#dfd-level1) | [SVG mức 1](../03-ky-thuat/dfd-level1.svg) | 6 tiến trình chính, 68 luồng; ký hiệu kho lặp chỉ giúp bố trí |
| Vì sao DFD có ít kho hơn số bảng ERD? | [Kho DFD và bảng ánh xạ](../03-ky-thuat/dfd.md#dfd-stores), [danh mục vật lý](../03-ky-thuat/erd-vat-ly.md) | Bảng trong hai mục nguồn | D1–D5 gộp 24 bảng ứng dụng; F1/F2 là file, T1 là tab, không cộng thành bảng |
| Bắt đầu phiên và chống tạo trùng xử lý thế nào? | [DFD mức 2 — Bắt đầu](../03-ky-thuat/dfd.md#dfd-start) | Mermaid trong mục nguồn | Xác minh vé/quyền → đối chiếu ledger → ghi phiên; vé 24 giờ khác phiên 8 giờ |
| Yêu cầu tạo audio đi từ Admin tới Gemini và kho file thế nào? | [DFD mức 2 — TTS](../03-ky-thuat/dfd.md#dfd-tts) | Mermaid trong mục nguồn | Snapshot bản đã lưu, job/receipt, gọi ngoài giao dịch dài, kết quả kỹ thuật không tự áp dụng |
| Một cặp được áp dụng nhất quán thế nào? | [DFD mức 2 — áp dụng cặp](../03-ky-thuat/dfd.md#dfd-pair) | Mermaid trong mục nguồn | Đúng quyền/version/revision/Hash/hồ sơ; cặp + receipt + mốc file trong giao dịch |
| Mất phản hồi sau Apply thì làm sao biết đã lưu chưa? | [Sequence mất response — API mục 6.3](../03-ky-thuat/api-transactions.md#api-pair-sequence) | Mermaid trong mục nguồn | Chưa rõ kết quả khác thất bại; giữ key, tra receipt trước thử lại |
| Các trạng thái TTS và Check/Retry chuyển thế nào? | [State machine TTS — vận hành mục 2](../03-ky-thuat/operations-design.md#ops-jobs) | Mermaid trong mục nguồn | UNCERTAIN không tự gọi provider lại; Retry local dùng WAV hợp lệ/cùng job, khác attempt provider mới |
| Từ văn bản tới MP3 và nội dung Khách có những bước nào? | [Pipeline audio — TDD mục 11](../03-ky-thuat/TDD.md#tdd-audio) | Mermaid trong mục nguồn | WAV → kiểm/chuyển MP3 → nghe/duyệt → áp dụng nguyên cặp; không tạo audio theo mỗi lượt khách |
| CI/CD triển khai tới laptop ra sao? | [Sơ đồ CI/CD — TDD mục 14](../03-ky-thuat/TDD.md#tdd-cicd) | Mermaid trong mục nguồn | CI kiểm/build; CD chủ động chọn SHA; push/main không tự đồng nghĩa đã chuyển bản phục vụ |

Khi trình bày một chức năng: mở sơ đồ để giải thích luồng, dùng bảng mã nguồn bên dưới chỉ Controller/Service/Repository, rồi mở nguồn ca kiểm tương ứng. Với backup/restore, Q2/Q3 hoặc thao tác bản đồ, đọc [quy trình bảo trì](bao-tri-va-sao-luu.md), [TDD](../03-ky-thuat/TDD.md) và [UI](../04-giao-dien/ui-design.md); không gọi bảng quy trình hoặc hình bố cục là ERD/sequence nếu tài liệu chưa có loại hình đó.

ERD đầy đủ cần phóng to; trình bày từng nhóm khi cần đọc chi tiết, không yêu cầu mọi hình chứa đủ 25 bảng. Các sơ đồ lịch sử không thay sơ đồ hiện hành; tra [chỉ mục history](../99-luu-tru/chi-muc-history.md) khi cần giải thích phương án cũ.

<a id="tra-cuu-ma-nguon"></a>

## Chức năng và vị trí mã nguồn

| Mã/chức năng | Vue | Controller | Service | Repository |
|---|---|---|---|---|
| FR-K01 — Bắt đầu/chuẩn hóa tên/chống tạo trùng | [App.vue](../../frontend/src/App.vue#L155) | [GuestSessionController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestSessionController.java#L17) | [GuestSessionService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestSessionService.java#L21) | [GuestSessionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestSessionRepository.java#L15) |
| FR-K02 — Phiên theo tab, vé ký và hạn 8 giờ | [owner.js](../../frontend/src/guest/owner.js#L2) | [GuestSessionController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestSessionController.java#L17) | [GuestSessionService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestSessionService.java#L21) | [GuestSessionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestSessionRepository.java#L15) |
| FR-K03 — Chạm vùng, ghim, chồng lấn/kéo/phóng | [GuestMap.vue](../../frontend/src/components/GuestMap.vue#L58) | [GuestContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java#L16) | [GuestContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java#L32) | [GuestContentRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestContentRepository.java#L17) |
| FR-K04 — Văn bản đúng cặp/ngôn ngữ và thiếu nội dung | [App.vue](../../frontend/src/App.vue#L187) | [GuestContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java#L16) | [GuestContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java#L32) | [GuestContentRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestContentRepository.java#L17) |
| FR-K05 — Tải trọn MP3/Hash, Blob, điều khiển audio | [playback.js](../../frontend/src/guest/playback.js#L1) | [GuestContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java#L16) | [GuestAudioService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestAudioService.java#L25) | [FileAssetRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/FileAssetRepository.java#L13) |
| FR-K06 — Đổi ngôn ngữ và bỏ response cũ | [App.vue](../../frontend/src/App.vue#L248) | [GuestContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java#L16) | [GuestContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java#L32) | [GuestContentRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestContentRepository.java#L17) |
| FR-K07 — Khôi phục cùng phiên/mốc nghe, không auto phát | [App.vue](../../frontend/src/App.vue#L133) | [GuestContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java#L16) | [GuestContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java#L32) | [GuestContentRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestContentRepository.java#L17) |
| FR-K08 — Lỗi mạng/thử lại/đối chiếu kết quả chưa rõ | [api.js](../../frontend/src/guest/api.js#L1) | [GuestContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java#L16) | [GuestContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java#L32) | [GuestContentRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestContentRepository.java#L17) |
| FR-K09 — Cảnh báo, hết hạn, nghe tiếp và kết thúc | [App.vue](../../frontend/src/App.vue#L68) | [GuestSessionController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestSessionController.java#L17) | [GuestSessionService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestSessionService.java#L21) | [GuestSessionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestSessionRepository.java#L15) |
| FR-K10 — ACK và lượt xem đúng lần mở | [App.vue](../../frontend/src/App.vue#L187) | [GuestContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java#L16) | [GuestContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java#L32) | [PlaceViewRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/PlaceViewRepository.java#L9) |
| FR-A01 — Đăng nhập/đăng xuất, Q3 và hết phiên Admin | [AdminApp.vue](../../frontend/src/admin/AdminApp.vue#L56) | [AdminAuthController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminAuthController.java#L22) | [AdminAuthService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminAuthService.java#L19) | [AdminAccountRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/AdminAccountRepository.java#L9) |
| FR-A02 — CRUD địa điểm, gate bật hiển thị | [AdminApp.vue](../../frontend/src/admin/AdminApp.vue#L91) | [AdminPlaceController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminPlaceController.java#L30) | [AdminPlaceService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminPlaceService.java#L22) | [PlaceRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/PlaceRepository.java#L13) |
| FR-A03 — Vẽ/xem trước/lưu/áp dụng vùng riêng | [RegionEditor.vue](../../frontend/src/admin/RegionEditor.vue#L29) | [AdminRegionController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminRegionController.java#L23) | [AdminRegionService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminRegionService.java#L23) | [RegionRevisionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/RegionRevisionRepository.java#L9) |
| FR-A04 — Nháp và cặp đang dùng/Q2 theo điểm-tiếng | [ContentEditor.vue](../../frontend/src/admin/ContentEditor.vue#L37) | [AdminContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminContentController.java#L26) | [AdminContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminContentService.java#L35) | [ContentSlotRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/ContentSlotRepository.java#L13) |
| FR-A05 — Tạo/Check/Retry và trạng thái TTS | [TtsPanel.vue](../../frontend/src/admin/TtsPanel.vue#L1) | [AdminTtsController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminTtsController.java#L23) | [TtsJobService.java](../../backend/src/main/java/vn/hueqr/backend/service/TtsJobService.java#L24) | [TtsJobRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/TtsJobRepository.java#L13) |
| FR-A06 — Gắn kết quả đúng revision, không hồi sinh điểm | [TtsPanel.vue](../../frontend/src/admin/TtsPanel.vue#L1) | [AdminTtsController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminTtsController.java#L23) | [TtsResultService.java](../../backend/src/main/java/vn/hueqr/backend/service/TtsResultService.java#L22) | [TtsJobRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/TtsJobRepository.java#L13) |
| FR-A07 — Hồ sơ duyệt và áp dụng nguyên cặp | [ContentEditor.vue](../../frontend/src/admin/ContentEditor.vue#L16) | [AdminPairController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminPairController.java#L24) | [AdminPairService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminPairService.java#L39) | [ContentPairRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/ContentPairRepository.java#L11) |
| FR-A08 — Xóa giao dịch, FK và lịch sử điểm | [AdminApp.vue](../../frontend/src/admin/AdminApp.vue#L101) | [AdminPlaceController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminPlaceController.java#L30) | [AdminPlaceDeletionService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminPlaceDeletionService.java#L17) | [PlaceDeletionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/PlaceDeletionRepository.java#L14) |
| FR-A09 — Dọn file độc lập DB, ref/claim và hạn bảo vệ | [ContentEditor.vue](../../frontend/src/admin/ContentEditor.vue#L1) | [AdminPlaceController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminPlaceController.java#L30) | [FileCleanupService.java](../../backend/src/main/java/vn/hueqr/backend/service/FileCleanupService.java#L18) | [LifecycleRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/LifecycleRepository.java#L17) |
| FR-A10 — QR/URL, PNG và bản in chung | [QrPanel.vue](../../frontend/src/admin/QrPanel.vue#L8) | Không có HTTP riêng | QR phía client | Không lưu QR trong DB |
| FR-A11 — Danh sách/chi tiết phiên và thống kê | [ReportsPanel.vue](../../frontend/src/admin/ReportsPanel.vue#L18) | [AdminGuestSessionController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminGuestSessionController.java#L18) | [AdminGuestSessionService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminGuestSessionService.java#L24) | [GuestSessionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestSessionRepository.java#L15) |
| FR-A12 — Mức đầy đủ 5 × 4; phân biệt đã tạo/áp dụng | [CompletenessPanel.vue](../../frontend/src/admin/CompletenessPanel.vue#L1) | [AdminContentController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminContentController.java#L26) | [AdminContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminContentService.java#L35) | [ContentSlotRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/ContentSlotRepository.java#L13) |
| FR-D01 — Dọn phiên/lượt theo hạn gốc 30 ngày | [ReportsPanel.vue](../../frontend/src/admin/ReportsPanel.vue#L1) | Không có HTTP riêng | [GuestRetentionService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestRetentionService.java#L15) | [GuestRetentionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestRetentionRepository.java#L17) |

<a id="diem-vao-bo-sung"></a>

## Điểm vào bổ sung

| Phần việc | Điểm vào | Phần phối hợp |
|---|---|---|
| Xử lý WAV→MP3/Retry local | [LocalAudioService.java](../../backend/src/main/java/vn/hueqr/backend/service/LocalAudioService.java#L21) | [LocalAudioRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/LocalAudioRepository.java#L15) |
| Gọi Gemini theo snapshot | [GeminiTtsProvider.java](../../backend/src/main/java/vn/hueqr/backend/service/GeminiTtsProvider.java#L16) | [TtsWorker.java](../../backend/src/main/java/vn/hueqr/backend/service/TtsWorker.java#L7) |
| Thống kê theo Asia/Saigon | [AdminStatisticsController.java](../../backend/src/main/java/vn/hueqr/backend/controller/AdminStatisticsController.java#L16) | [AdminStatisticsService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminStatisticsService.java#L28) |
| Owner/claim và gate bảo trì | [RuntimeOwnerService.java](../../backend/src/main/java/vn/hueqr/backend/service/RuntimeOwnerService.java#L22) | [OperationsGateService.java](../../backend/src/main/java/vn/hueqr/backend/service/OperationsGateService.java#L12) |
| Q2/Q3/receipt ở Vue | [workspace.js](../../frontend/src/admin/workspace.js#L5) | [api.js](../../frontend/src/admin/api.js#L1) |
| Q3 phía server và lọc quyền Admin | [AdminSessionService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminSessionService.java#L19) | [AdminSessionFilter.java](../../backend/src/main/java/vn/hueqr/backend/config/AdminSessionFilter.java#L16) |
| Scheduler dọn file/metadata | [LifecycleCleanupRunner.java](../../backend/src/main/java/vn/hueqr/backend/service/LifecycleCleanupRunner.java#L12) | [GuestRetentionRunner.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestRetentionRunner.java#L14) |
| Nền bản đồ thực OSM và fallback | [basemap.js](../../frontend/src/map/basemap.js#L1) | [local-basemap.js](../../frontend/src/map/local-basemap.js#L1) |
| Backup/restore/lịch | [scheduled-operations.mjs](../../deploy/scheduled-operations.mjs#L1) | [restore-backup.mjs](../../deploy/restore-backup.mjs#L1) |
| CD từ GitHub tới máy Windows | [ci.yml](../../.github/workflows/ci.yml#L1) | [cd-receive.mjs](../../deploy/cd-receive.mjs#L1) |

<a id="nguon-ca-kiem"></a>

## Từ màn hình đến phép kiểm

| Nhóm | Nguồn ca kiểm |
|---|---|
| FR-K01 | [GuestSessionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestSessionApiTests.java#L37); lost-start-response |
| FR-K02 | [GuestSessionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestSessionApiTests.java#L37); tabs-reload; duplicate-owner |
| FR-K03 | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52); tabs-reload |
| FR-K04 | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52); audio-error-retry |
| FR-K05 | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52); audio-error-retry; expiry-warning-tail |
| FR-K06 | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52); frontend/tests/guest-transport.test.mjs |
| FR-K07 | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52); tabs-reload |
| FR-K08 | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52); lost-selection-response; offline-reload-recovery |
| FR-K09 | [GuestSessionEndApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestSessionEndApiTests.java#L39); expiry-warning-tail; tabs-reload |
| FR-K10 | [GuestContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java#L52); lost-selection-response; tabs-reload |
| FR-A01 | [AdminAuthApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminAuthApiTests.java#L40); admin-401-q2 |
| FR-A02 | [AdminPlaceApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminPlaceApiTests.java#L55); backend controller tests |
| FR-A03 | [AdminRegionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminRegionApiTests.java#L64); frontend/tests/admin-editors.test.mjs |
| FR-A04 | [AdminContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminContentApiTests.java#L69); admin-q2-reload; admin-401-q2 |
| FR-A05 | [AdminTtsApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminTtsApiTests.java#L72); provider thật và ledger nội dung |
| FR-A06 | [TtsWorkerIntegrationTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/TtsWorkerIntegrationTests.java#L84); provider thật và ledger nội dung |
| FR-A07 | [AdminPairApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminPairApiTests.java#L73); 20 cặp đã được duyệt dùng học phần và áp dụng qua API; giữ giới hạn hồ sơ nội dung |
| FR-A08 | [AdminPlaceDeletionTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminPlaceDeletionTests.java#L48); backend controller tests |
| FR-A09 | [LifecycleCleanupTests.java](../../backend/src/test/java/vn/hueqr/backend/service/LifecycleCleanupTests.java#L42); scheduler riêng; không thay bằng xóa DB |
| FR-A10 | [QR/HTTPS](../../frontend/tests/e2e/public-rehearsal.spec.mjs); frontend/tests/e2e/public-rehearsal.spec.mjs |
| FR-A11 | [AdminGuestSessionApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminGuestSessionApiTests.java#L43); admin-statistics-filters |
| FR-A12 | [AdminContentApiTests.java](../../backend/src/test/java/vn/hueqr/backend/controller/AdminContentApiTests.java#L69); 20 cặp được duyệt dùng học phần và API Khách kiểm Hash 20/20 |
| FR-D01 | [GuestRetentionTests.java](../../backend/src/test/java/vn/hueqr/backend/service/GuestRetentionTests.java#L47); scheduler riêng và restore retention |

Công cụ nhập dữ liệu đã duyệt: [import-approved-content.mjs](../../deploy/import-approved-content.mjs). Bước ghi hồ sơ, áp dụng vùng/cặp và bật hiển thị qua API: [publish-approved-content.mjs](../../deploy/publish-approved-content.mjs); khóa/receipt và kết quả chưa rõ: [content-admin-client.mjs](../../deploy/content-admin-client.mjs). Bộ kiểm đủ 20 bài: [verify-published-content.mjs](../../backend/src/test/fixtures/smoke/verify-published-content.mjs). Đây là công cụ vận hành, không thêm hợp đồng API.

Các migrations nằm tại [db/migration](../../backend/src/main/resources/db/migration); cấu hình ứng dụng tại [application.properties](../../backend/src/main/resources/application.properties). Đọc [API/giao dịch](../03-ky-thuat/api-transactions.md), [dữ liệu](../03-ky-thuat/data-model.md) và [quy trình vận hành](bao-tri-va-sao-luu.md) để xem hợp đồng/giới hạn. Credential, khóa và audio không nằm trong tài liệu tra cứu.
