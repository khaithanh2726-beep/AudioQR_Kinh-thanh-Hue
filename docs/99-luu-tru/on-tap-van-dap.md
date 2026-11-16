# Ôn tập vấn đáp và thực hành

Tài liệu chuẩn bị nội bộ, đối chiếu mã nguồn tại mốc `45a41a0` ngày 15/11/2026. Đây là khung luyện tập, không phải đề thi hoặc kết quả nghiệm thu mới. Yêu cầu hiện hành vẫn lấy từ [PRD](../02-nghiep-vu/PRD.md), [quy tắc](../02-nghiep-vu/decisions.md) và [bàn giao](../01-tong-quan/handoff.md).

## 1. Nội dung cần nói được trong một phút

“Đồ án giúp khách quét một QR chung, nhập tên, chọn ngôn ngữ rồi chạm vùng trên bản đồ để đọc và nghe thuyết minh. Nhóm tách vai trò Khách và Admin. Admin quản lý vùng, phiên bản văn bản, audio và áp dụng đúng cặp đã được duyệt; Khách nhận bản đang áp dụng. Frontend dùng Vue, backend Spring Boot theo Controller → Service → Repository, dữ liệu MySQL/Flyway và audio ở kho file riêng. Bản phục vụ hiện có năm điểm, bốn ngôn ngữ, tổng cộng 20 cặp. Nhóm đã kiểm API, CI và backup/restore; kiểm ma trận thiết bị của bộ cập nhật là lượt cuối.”

Khi giới thiệu đóng góp, dùng G02: Thành T01/T03/T05/T07/T11; Hòa T02/T09/T10; Nghĩa T04/T06; T08 cả nhóm. Người trình bày cần giải thích được phần mình, luồng giao tiếp với phần còn lại và bằng chứng đã kiểm. Không suy người viết mã từ tài khoản tạo PR/push.

## 2. Kịch bản trình diễn 8–10 phút

Thời lượng là khung tập dượt, điều chỉnh theo yêu cầu giảng viên. Chuẩn bị/khởi động/tắt theo [hướng dẫn sử dụng](../07-van-hanh/tap-duot.md); không thực hiện triển khai hoặc backup giữa phần trình diễn.

| Bước | Thao tác | Ý cần giải thích |
|---|---|---|
| 1 — mở đầu | Giới thiệu bài toán, hai vai trò và sơ đồ ba lớp | QR dẫn tới hệ thống; vùng và cặp áp dụng do Admin quản lý |
| 2 — Khách | Quét QR/mở HTTPS, nhập tên, chọn tiếng Việt, Bắt đầu | Mở form chưa tạo phiên; Bắt đầu mới tạo phiên có hạn cố định |
| 3 — bản đồ | Chạm Ngọ Môn, chỉ ghim, tên điểm, văn bản và audio | Ghim là chỗ chạm; vùng lấy từ API, không phải vị trí GPS |
| 4 — audio | Phát/tạm dừng, tua, nghe lại khi phiên còn hiệu lực | MP3 tải trọn, kiểm Hash rồi phát từ Blob |
| 5 — ngôn ngữ | Đổi sang en/fr/ru, chọn thêm một điểm | Đúng cặp/ngôn ngữ; thiếu bản thì báo rõ, không tự chuyển sang tiếng Việt |
| 6 — phiên | Reload: giữ phiên/nội dung, audio không tự phát; sau đó Kết thúc | Reload không tự tạo phiên/lượt xem mới; kết thúc thu hồi quyền mở/tải tiếp |
| 7 — Admin | Đăng nhập, mở địa điểm/vùng và nội dung đang dùng | Chỉ rõ nháp, revision đã lưu, cặp đang áp dụng, tham chiếu duyệt |
| 8 — bảo vệ soạn thảo | Gõ thử một thay đổi chưa lưu rồi chuyển màn; chọn quay lại/bỏ thay đổi | Q2 bảo vệ bản soạn; không lưu/apply thay đổi thử lên bộ đang phục vụ |
| 9 — quản trị | Mở job TTS đã hoàn tất, nghe thử, mức đầy đủ, thống kê và QR | TTS thành công khác duyệt/áp dụng; số phiên khác lượt xem |
| 10 — kết luận | Mở CI, báo cáo kiểm và hướng dẫn backup/restore | Nêu kết quả đã đạt và phần chưa kiểm; không chạy restore lên bản phục vụ |

Chuẩn bị sẵn QR PNG, tab Khách, tab Admin, VS Code và trang CI. Mở đúng `http://localhost:18083` hoặc URL HTTPS hiện hành, không dùng preview cũ. Không chiếu tệp môi trường, runtime.json, mật khẩu, token hoặc khóa SSH. TTS thật chỉ trình diễn khi runtime/quota/quyền gọi đã sẵn sàng; nếu chưa, dùng job thật đã hoàn tất và giải thích điều kiện, không tạo job mới chỉ để minh họa nút bấm.

## 3. Câu hỏi nền tảng

### C01. QR chứa gì? Vì sao dùng một QR chung?

QR chứa URL trang bắt đầu, không chứa từng MP3 hoặc mật khẩu. Khách chọn địa điểm trên bản đồ nên cập nhật nội dung/cặp không đòi hỏi in lại QR khi URL giữ nguyên. URL đổi thì phải đồng bộ cấu hình/build và xuất QR lại. Mở [QrPanel.vue](../../frontend/src/admin/QrPanel.vue), tìm `QRCode`.

### C02. “Tự động” nằm ở đâu?

Khách chọn vùng để hệ thống lấy đúng bản thuyết minh đang áp dụng; Admin có pipeline tạo audio từ bản văn đã lưu. Hệ thống không gọi Gemini cho mỗi lượt khách và không tự phát hiện khách đến điểm bằng GPS. Chỉ hai luồng tại [App.vue](../../frontend/src/App.vue), tìm `select`, và [TtsJobService.java](../../backend/src/main/java/vn/hueqr/backend/service/TtsJobService.java), tìm `accept`.

### C03. Vì sao tách Controller, Service, Repository?

Controller nhận HTTP/DTO và trả kết quả; Service xử lý nghiệp vụ/giao dịch; Repository đọc ghi dữ liệu. Ví dụ Bắt đầu đi qua `GuestSessionController.start` → `GuestSessionService.start` → `GuestSessionRepository`. Tách lớp giúp thay luật nghiệp vụ hoặc truy vấn ở đúng nơi; không có nghĩa tự động đạt hiệu năng hoặc bảo mật.

### C04. Vue, Spring Boot và MySQL giao tiếp thế nào?

Vue gọi API JSON bằng HTTP; backend kiểm quyền và nghiệp vụ rồi truy cập MySQL qua Repository. Bản đóng gói phục vụ Vue static và API cùng origin. MP3 là phản hồi nhị phân có quyền tải, không phải chuỗi audio nhúng trong JSON. Xem [API](../03-ky-thuat/api-transactions.md) và [Dockerfile](../../deploy/Dockerfile).

### C05. Vì sao lưu audio ngoài database?

Database giữ metadata như storage key, Hash, kích thước, trạng thái và liên kết revision; WAV/MP3 nằm trong volume riêng. Backup phải chụp cả hai cùng checkpoint. Không gọi việc này là “database không liên quan audio”: FK, reference/claim và catalog quyết định file nào được giữ/dọn. Xem [FileAsset.java](../../backend/src/main/java/vn/hueqr/backend/entity/FileAsset.java) và [mô hình dữ liệu](../03-ky-thuat/data-model.md).

### C06. Flyway giải quyết việc gì?

Flyway quản lý thay đổi schema theo migration có thứ tự và lịch sử/checksum; không thay việc duyệt nội dung hoặc backup dữ liệu. Mở [db/migration](../../backend/src/main/resources/db/migration): V13 giữ chính xác tọa độ vùng, V14 bổ sung gate bảo trì. Không sửa migration đã áp dụng để làm lệch lịch sử môi trường.

## 4. Khách, bản đồ và thống kê

### C07. Phiên khách và tên khách có phải tài khoản đăng nhập không?

Không. Tên là tên hiển thị, có thể trùng; phiên được xác thực bằng credential riêng, backend giữ Hash và kiểm epoch. Vé Bắt đầu được ký, có hạn 24 giờ, phục vụ chống tạo lặp; phiên tham quan có hạn riêng 8 giờ từ lúc bắt đầu. Mở [GuestSessionService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestSessionService.java), tìm `start`, và [StartTicketService.java](../../backend/src/main/java/vn/hueqr/backend/service/StartTicketService.java).

### C08. Reload, tab mới và Duplicate khác nhau thế nào?

Thông tin tab giữ trong sessionStorage. Reload có thể khôi phục cùng phiên; tab mới có phiên độc lập. Duplicate có thể sao chép storage, nên còn cơ chế owner bằng Web Locks/BroadcastChannel và document nonce/sequence phía server; không chỉ dựa vào sessionStorage. Xem [owner.js](../../frontend/src/guest/owner.js), [App.vue](../../frontend/src/App.vue), tìm `restore`, và `GuestContentService.resume/fresh`.

### C09. Chạm vào vùng được xác định bằng cách nào?

Leaflet hiển thị nền OSM; polygon WGS84 lấy từ API. `inRing` kiểm điểm trong vòng bằng phép giao tia và kiểm biên; `contains` xét vòng ngoài/trừ lỗ. Một vùng thì chọn, nhiều vùng thì hiện lựa chọn, ngoài mọi vùng thì không chọn điểm mới. Mở [GuestMap.vue](../../frontend/src/components/GuestMap.vue), tìm `inRing`, `contains`, `map.on('click'`.

### C10. Ghim có thể hiện vị trí thật của khách không?

Ghim thể hiện tọa độ chạm đã chọn trong vùng, không phải tọa độ GPS của thiết bị. Chồng lấn thì chỉ chọn sau khi người dùng quyết định. Kéo/phóng bản đồ không có ý nghĩa khách đã mở địa điểm. Chỉ `paintPin/choose` trong `GuestMap.vue`; vùng chạm không phải ranh giới pháp lý đã khảo sát.

### C11. Một lượt xem được tính khi nào?

Sau khi đúng văn bản đã được hiển thị, frontend gửi view-ack; backend kiểm grant và ràng buộc opening để ghi tối đa một lượt cho lần mở đó. Quét QR, bắt đầu phiên, phát/tua/nghe lại không tự cộng lượt. Đổi tiếng/reload trong cùng lần mở không cộng; A → B → A có lần mở A mới. Mở [App.vue](../../frontend/src/App.vue), tìm `showContent`, rồi [GuestContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java), tìm `ack`.

### C12. Vì sao số phiên khác số lượt xem?

Một phiên có thể chưa chọn điểm, mở một điểm hoặc nhiều lần mở. Hai đại lượng được lưu/tổng hợp riêng. Khi chứng minh trên thống kê phải giữ cùng bộ lọc/ngày/múi giờ và chờ ACK, không so hai số ở hai phạm vi khác nhau. Xem [AdminStatisticsService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminStatisticsService.java) và [ReportsPanel.vue](../../frontend/src/admin/ReportsPanel.vue).

### C13. Đổi điểm nhanh hoặc phản hồi đến muộn có phát nhầm bài không?

Frontend dùng generation và hủy lượt tải cũ; server kiểm document nonce, selection sequence và opening hiện hành. Kết quả của lựa chọn cũ không được ghi đè lựa chọn mới. Idempotency key xử lý thử lại cùng thao tác, không thay vai trò của sequence. Xem `select/sendSelection` trong `App.vue`, `fresh/replay` trong `GuestContentService.java` và [playback.js](../../frontend/src/guest/playback.js).

### C14. Vì sao tải trọn MP3 và kiểm Hash trước khi phát?

Đây là phương án đã chọn để quản lý đúng bytes/revision và quyền nghe tiếp ở cuối phiên. Frontend so SHA256 rồi tạo Blob URL. Đánh đổi là phải chờ tải và dùng bộ nhớ; hệ thống không được mô tả như streaming hoặc offline đầy đủ. Xem `load` trong `playback.js` và [GuestAudioService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestAudioService.java).

### C15. Hết 8 giờ có tắt audio ngay không?

Phiên hết hạn không được mở bài/tải mới hoặc gia hạn bởi việc nghe. Ngoại lệ chỉ cho bài đã tải trọn và đang phát đúng mốc: nghe tiếp, tạm dừng/tiếp tục; không tua/nghe lại. Reload/đóng tab làm mất ngoại lệ này. Cảnh báo xuất hiện ở 5 phút cuối. Dùng báo cáo ca clock/expiry để giải thích, không sửa giờ máy hoặc hạn database phục vụ nhằm demo.

## 5. Admin, nhất quán và TTS

### C16. Nháp, revision và cặp đang áp dụng khác nhau thế nào?

Nội dung đang gõ chưa được lưu khác revision văn bản đã lưu; `content_slot` theo địa điểm/ngôn ngữ trỏ tới nháp và cặp áp dụng riêng. Khách đọc `active_pair_id`, không tự đọc bản Admin mới sửa. Áp dụng nguyên cặp tránh văn bản mới đi với giọng đọc của bản cũ. Xem [ContentSlot.java](../../backend/src/main/java/vn/hueqr/backend/entity/ContentSlot.java), [AdminContentService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminContentService.java) và [AdminPairService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminPairService.java).

### C17. Khi áp dụng cặp, backend kiểm những gì?

Kiểm slot/version, văn bản đúng slot, audio đúng text revision, Hash văn bản/MP3, file sẵn sàng và version hồ sơ duyệt. Con trỏ áp dụng và metadata liên quan thay đổi trong giao dịch; dữ liệu thiếu/sai không được ghi nửa cặp. Mở `AdminPairService.apply` và [ContentPairReadiness.java](../../backend/src/main/java/vn/hueqr/backend/service/ContentPairReadiness.java).

### C18. ETag và idempotency key có phải cùng một thứ không?

ETag/If-Match chống ghi dựa trên phiên bản cũ; idempotency key nhận diện một thao tác để kiểm/replay kết quả khi mất phản hồi. Cùng key nhưng payload/điều kiện khác bị từ chối. Không tạo key mới để né một kết quả chưa rõ. Xem [AdminCommandService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminCommandService.java), tìm `execute/lookup/fingerprint`, và [workspace.js](../../frontend/src/admin/workspace.js).

### C19. Transaction có bao gồm cuộc gọi Gemini hoặc xóa file không?

Không có một transaction MySQL bao trùm dịch vụ ngoài và filesystem. Nhóm dùng trạng thái job, owner/token, file metadata, reference/claim và bước xử lý riêng. Ví dụ áp dụng cặp nằm trong giao dịch; worker gọi provider và dọn bytes có quy trình tách. Không trả lời rằng rollback database sẽ tự hoàn tác mọi cuộc gọi/file đã xóa. Xem [TtsDispatchService.java](../../backend/src/main/java/vn/hueqr/backend/service/TtsDispatchService.java) và [FileCleanupService.java](../../backend/src/main/java/vn/hueqr/backend/service/FileCleanupService.java).

### C20. Q2/Q3 bảo vệ điều gì?

Q2 giữ tạm bản soạn theo tab/tài khoản, chặn hoặc xác nhận trước bỏ thay đổi; không tự lưu/apply sau reload/đăng nhập lại. Q3 tính 30 phút không có tương tác hợp lệ; polling không tự gia hạn. Frontend che/dừng nghe thử khi khóa, backend vẫn kiểm phiên. Mở `workspace.js` và [AdminSessionService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminSessionService.java), tìm `ACTIVITIES/activeState/activity`.

### C21. Pipeline tạo audio chạy thế nào?

Admin lưu văn bản → tạo job gắn revision/Hash/ngôn ngữ/model/giọng → worker gọi Gemini nhận WAV → FFmpeg chuyển MP3 → kiểm/đăng ký file và audio revision → Admin nghe/rà, ghi hồ sơ rồi áp dụng cặp. Khách không chờ tạo audio theo từng lần mở. Xem `TtsJobService.accept`, [TtsWorker.java](../../backend/src/main/java/vn/hueqr/backend/service/TtsWorker.java), [LocalAudioService.java](../../backend/src/main/java/vn/hueqr/backend/service/LocalAudioService.java).

### C22. Timeout TTS có được bấm Retry liên tục không?

Không: timeout có thể là đã gửi nhưng chưa biết kết quả. Trạng thái UNCERTAIN chỉ cho đối chiếu phù hợp, không tự gọi lại provider. Lỗi local có WAV hợp lệ có thể Retry chuyển đổi tại chỗ; lỗi xác định trước khi có WAV khác với lỗi nguồn WAV không hợp lệ. Mở [TtsRecoveryService.java](../../backend/src/main/java/vn/hueqr/backend/service/TtsRecoveryService.java), tìm `check/retry`, và DTO `retryPolicy` do `TtsJobService.response` tạo.

### C23. TTS thành công có chứng minh nội dung đúng không?

Không. Job thành công chứng minh có đầu ra kỹ thuật; Hash/giải mã chứng minh tính toàn vẹn, không chứng minh lịch sử hoặc phát âm. Bộ hiện tại có hồ sơ chủ đồ án chấp thuận dùng học phần, có giới hạn chuyên gia/bản ngữ và ghi chú Thái Hòa fr. Xem [hồ sơ 19 cặp](../05-noi-dung/duyet-19-cap-v02-20261114.json) và [ma trận nội dung](../05-noi-dung/doi-chieu-van-ban-audio.md).

### C24. Xóa địa điểm có xóa ngay mọi MP3 không?

Xóa dữ liệu riêng và dọn bytes là hai bước. Lịch sử, snapshot, cặp/grant/tham chiếu đang được bảo vệ và claim tải phải được xét theo quy tắc. Cleanup file có trạng thái/điều kiện riêng; không xóa thư mục audio trực tiếp để minh họa. Xem [AdminPlaceDeletionService.java](../../backend/src/main/java/vn/hueqr/backend/service/AdminPlaceDeletionService.java), `FileCleanupService` và [GuestRetentionService.java](../../backend/src/main/java/vn/hueqr/backend/service/GuestRetentionService.java).

## 6. Vận hành, kiểm thử và giới hạn

### C25. CI khác CD thế nào trong chính dự án này?

CI chạy kiểm/backend/frontend/build trên GitHub. Job deploy chỉ chạy khi workflow_dispatch có SHA đúng và kiểm đạt; push/merge main không tự đồng nghĩa đã chuyển ảnh phục vụ. CD đã có lượt thật qua Tailscale/OpenSSH/receiver. Xem [ci.yml](../../.github/workflows/ci.yml) và [cd-receive.mjs](../../deploy/cd-receive.mjs).

### C26. Vì sao không chỉ backup database?

DB chứa liên kết tới bytes WAV/MP3, nên thiếu audio thì restore chưa đầy đủ. Quy trình đóng nhận việc, chờ drain, dừng app/freeze rồi backup DB + file còn tham chiếu + manifest Hash cùng checkpoint. Restore vào schema/volume riêng, kiểm bytes, áp retention theo mốc gốc, cấp epoch mới và giữ gate đóng trước rà. Xem [bảo trì](../07-van-hanh/bao-tri-va-sao-luu.md).

### C27. Backup tự chạy được bảo đảm cả khi tắt laptop không?

Không. Task 23:00 dùng tài khoản đang đăng nhập và Docker sẵn sàng, không chạy bù khi mở máy. Đã có trigger thử tự chạy đạt và restore 40 tệp; lượt 23:00 gặp môi trường không sẵn sàng đã thất bại ở preflight, sau đó chạy bù thủ công đạt. Không xóa báo cáo lỗi hoặc gọi lượt thủ công là lượt tự nhiên đạt. Log/báo cáo chi tiết nằm ngoài bộ chấm.

### C28. Nhóm đã kiểm được gì? Còn gì chưa đủ?

CI main `45a41a0` [run 43626995965](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43626995965) đạt 530 backend, 71 logic frontend, 33 vận hành và Vue build. API đủ 20 cặp/Hash; Chrome riêng 11/11; backup/restore đủ 40 tệp. Các lượt này không cộng thành một full lớn hơn. Kiểm thiết bị bộ cập nhật và người nhận thực hiện hướng dẫn là lượt cuối; checklist 62/89 chưa đồng nghĩa đạt 100% tiêu chí.

### C29. Nếu hỏi hệ thống đã tối ưu/chịu tải lớn chưa?

Nêu phần có căn cứ: audio tạo trước và dùng lại, giữ revision/Hash, tránh tạo lặp và có giới hạn log/retention. Không khẳng định đã tối ưu tuyệt đối, đạt RPO/RTO hay chịu được một số khách đồng thời khi chưa đo. Hạn chế hiện tại gồm tải trọn MP3, phụ thuộc mạng/OSM/ngrok/provider và môi trường laptop; đề xuất cải tiến phải tách với chức năng đã triển khai.

### C30. Nhóm dùng AI như thế nào và chịu trách nhiệm ra sao?

Trình bày đúng vai trò thực tế của AI trong hỗ trợ phân tích, mã, rà soát hoặc bản dịch; thành viên phải tự giải thích luồng, sửa phần mình và đối chiếu bằng kiểm thử. TTS/ASR/AI không thay phê duyệt nội dung hoặc nghiệm thu thiết bị. Không gán danh tính commit, tỷ lệ đóng góp hoặc mức kiểm chứng chỉ từ công cụ/tài khoản.

### C31. Backend bảo vệ Admin và quyền tải audio thế nào?

Admin dùng Spring Security với PasswordEncoder, phiên máy chủ và CSRF cho luồng quản trị; các API quản trị còn kiểm phiên/phiên bản/điều kiện nghiệp vụ. Khách dùng credential và grant thuộc đúng phiên, không được tải tùy ý bằng đường dẫn file vật lý. Hash audio kiểm toàn vẹn, không thay cơ chế xác thực hoặc HTTPS. Mở [AdminSecurityConfiguration.java](../../backend/src/main/java/vn/hueqr/backend/config/AdminSecurityConfiguration.java), `AdminSessionFilter` và `GuestAudioService`; không tuyên bố đã kiểm an ninh toàn diện chỉ từ các cơ chế này.

### C32. Nếu phải chỉ quan hệ dữ liệu của bài đang phát?

Đi theo `place` → `content_slot` theo tiếng → `active_pair_id` → `content_pair` → `text_revision/audio_revision` → `file_asset`. `draft_text_id` là con trỏ khác. Chỉ trong Entity/migration trước, rồi có thể dùng truy vấn chỉ đọc dưới đây trên môi trường được phép; đây là ví dụ để luyện, không phải kết quả đã chạy ở lượt soạn tài liệu.

```sql
SELECT p.display_name, s.language,
       HEX(t.id) AS text_revision, HEX(a.id) AS audio_revision,
       f.format, f.byte_size, HEX(f.checksum) AS mp3_sha256
FROM place p
JOIN content_slot s ON s.place_id = p.id
JOIN content_pair cp ON cp.id = s.active_pair_id AND cp.slot_id = s.id
JOIN text_revision t ON t.id = cp.text_revision_id AND t.slot_id = s.id
JOIN audio_revision a ON a.id = cp.audio_revision_id AND a.text_revision_id = t.id
JOIN file_asset f ON f.id = a.mp3_file_id
WHERE p.visible = 1
ORDER BY p.display_name, s.language;
```

## 7. Bài thực hành khi được yêu cầu tại chỗ

| Yêu cầu có thể gặp | Thực hiện và kết quả cần quan sát |
|---|---|
| Chỉ luồng Bắt đầu | VS Code mở Controller → `GuestSessionService.start` → Repository → migration; trình duyệt Network xem ticket và sessions, không chiếu credential/header bí mật |
| Chứng minh chống đếm trùng | Tạo phiên trình diễn; mở A, phát/tua/đổi tiếng/reload; kiểm thống kê cùng bộ lọc. Sau đó A → B → A để giải thích opening mới; chờ ACK trước đối chiếu |
| Chứng minh nháp không lên Khách | Chỉ sửa chưa lưu trên bản phục vụ rồi bỏ thay đổi; bài lưu nháp/apply thực hiện trên bản tập dượt riêng, Khách vẫn đọc cặp cũ đến khi áp dụng |
| Chứng minh Q2 | Soạn tạm, chuyển màn để thấy xác nhận, quay lại/reload theo hướng dẫn; không để bản soạn trình diễn tự ghi vào 20 cặp đang dùng |
| Giải thích lỗi mạng | Dùng DevTools trên tab trình diễn riêng rồi bật mạng lại; xem thông báo/thử lại đúng thao tác. Không ngắt ngrok/Docker của mọi người để giả lập lỗi một khách |
| Chỉ kiểm phiên bản | Tìm `If-Match`, `requireMatch`, `VERSION_CONFLICT`; dùng ca kiểm có sẵn để minh họa hai thao tác cạnh tranh, không ghi SQL sửa version của bản phục vụ |
| Chỉ giới hạn phiên | Mở test có Clock/expiry và kết quả đã lưu; không chờ giả hoặc sửa giờ hệ thống để nói đã qua 8 giờ/30 phút thật |
| Sửa một lỗi nhỏ | Nêu file/hàm đúng lớp, dự đoán ảnh hưởng, làm ở bản riêng, chạy phép kiểm liên quan rồi giải thích diff. Không sửa migration đã áp dụng hoặc bấm deploy giữa buổi |
| Thử backup/restore | Trình bày manifest, báo cáo Hash và quy trình; nếu phải chạy thật, dùng thời gian/môi trường riêng, tránh quiesce bản phục vụ đang có người dùng |

Các clone kiểm trước đã được dọn ngày 15/11. Khi cần bài tập có ghi dữ liệu, chuẩn bị lại môi trường riêng từ backup còn hạn bằng công cụ restore; không chạy runtime.json cũ của container đã xóa. Không có phép thực hành nào trong tài liệu này được tính là đã chạy chỉ vì đã soạn hướng dẫn.

## 8. Cách luyện

Lượt 1: trả lời C01–C06 trong 20–40 giây/câu, không nhìn đáp án. Lượt 2: giải thích một chuỗi nghiệp vụ bằng dữ liệu thật, chỉ được đúng Service và Repository. Lượt 3: thực hiện kịch bản mục 2 và một bài mục 7 trên môi trường phù hợp; ghi câu chưa trả lời được để học lại. Với Thành, ưu tiên C03/C07/C11/C16–C24/C26 và tự chỉ mã T03/T05/T07; các thành viên khác ưu tiên đúng G02.

Mỗi câu dùng ba ý: quyết định nghiệp vụ → cách mã hiện thực → bằng chứng hoặc giới hạn. Khi chưa có số đo/kết quả, trả lời “chưa kiểm phần đó” và nêu phép kiểm cần làm. [Tra chức năng → file/dòng](../07-van-hanh/tra-cuu-chuc-nang.md) là bảng tra nhanh, không chép lại toàn bộ vào bộ ôn.
