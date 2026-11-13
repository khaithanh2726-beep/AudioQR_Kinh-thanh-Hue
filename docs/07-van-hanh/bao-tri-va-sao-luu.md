# Bảo trì, sao lưu và phục hồi có khóa bền

## Phạm vi

Công cụ dành cho bộ Compose riêng có database `hueqr`, schema từ V14 và ảnh ứng dụng đã tích hợp gate. `deploy/maintenance.mjs` chỉ nhận runtime thuộc project `hueqr-rehearsal-*` hoặc `hueqr-ops-check-*`; đối chiếu nhãn hai container và MySQL không publish cổng. Không suy từ việc có công cụ thành đã nghiệm thu trên môi trường phục vụ. [Hồ sơ kiểm](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/operations-guard-20261113/README.md) ghi lượt thực chạy.

V14 tạo `ops_guard` và `ops_activity`. API và bốn scheduler lấy lease trong giao dịch ngắn: khóa chia sẻ hàng gate → kiểm RUNNING → ghi lease → commit. Lệnh QUIESCING tranh chấp cùng hàng nên không có việc mới lọt qua sau khi đóng gate. Lease tồn tại tới khi request/stream hoặc toàn lượt scheduler kết thúc. Timeout, tuổi lease và restart không tự tháo lease. HTTP bị chặn dùng mã đã có `503 SERVICE_UNAVAILABLE`, no-store; không đổi phiên thành hết hạn hoặc tự gửi lại lệnh. Không thêm endpoint HTTP vận hành.

Ảnh được rà ở mốc này là `hueqr-local:20261113-ops-guard-urls`, ID trong hồ sơ kiểm. Không chọn ảnh thử `20261113-ops-guard` trước đó để phục vụ: ảnh đó dùng URI thô và chưa chặn đường API mã hóa. Bản sửa nhận diện servletPath đã giải mã, kiểm cả đường thường/mã hóa ở trạng thái đóng và mở.

## Điều kiện trước thao tác

- Thực hiện ngoài buổi trình diễn. Người quản trị tự xử lý phần đang soạn; công cụ không lưu hoặc áp dụng thay Admin.
- Thư mục runtime, backup và restore ở ngoài repository, có ACL của tài khoản thực hiện và SYSTEM. Không chia sẻ private.env, runtime.json hoặc dump. Đích backup mới phải tách runtime; restore luôn tạo project/volume mới.
- Không có SQL/DDL thủ công, app ngoài quy trình hoặc công cụ khác ghi cùng database/kho audio. Gate không fence được người quản trị có quyền truy cập trực tiếp MySQL/Docker. Công cụ kiểm thêm kết nối MySQL và container dùng chung volume; nếu chưa rõ thì dừng.
- Dùng đúng ảnh V14 đã kiểm. Bản Ngọ Môn đã nâng V14 có snapshot/restore V13 trước migration và backup/restore V14 sau nâng. Công cụ gate V14 vẫn từ chối nguồn V13; hướng dẫn nâng riêng ở cuối tài liệu.

## Đóng nhận việc và sao lưu

Chạy từ thư mục repository bằng Node đã có. Không gửi token hoặc nội dung cấu hình ra chat/log công khai.

```powershell
$runtimeDirectory = '<thu-muc-runtime-rieng>'
$backupDirectory = '<thu-muc-backup-moi-ngoai-repo-va-runtime>'
$maintenanceOwner = [guid]::NewGuid().ToString()
node deploy/maintenance.mjs $runtimeDirectory status
node deploy/maintenance.mjs $runtimeDirectory quiesce $maintenanceOwner
node deploy/maintenance.mjs $runtimeDirectory status
```

Chờ activities/tasks/claims/deleting đều bằng 0. Công việc đã tiếp nhận giữ trạng thái thật; QUEUED không bị đổi thành FAILED và provider chưa rõ không tự được gọi lại. Nếu còn lease/claim, không xóa theo timeout để ép qua kiểm. Xác minh owner/process hoặc hoãn lần sao lưu; ca công cụ chưa hỗ trợ tự phục hồi mọi lease sau crash.

```powershell
node deploy/maintenance.mjs $runtimeDirectory freeze $maintenanceOwner
node deploy/maintenance.mjs $runtimeDirectory backup $maintenanceOwner $backupDirectory
```

`freeze` kiểm đã drain trước khi dừng app, giữ MySQL, kiểm lại task/claim và trạng thái dừng sạch rồi ghi FROZEN. `backup` chỉ chạy khi đúng owner, FROZEN, app đã dừng và không thấy writer khác. Nó dump database, đọc catalog cùng checkpoint từ MySQL nguồn, sao chép WAV/MP3 còn tham chiếu từ volume và kiểm từng kích thước/Hash. Catalog gồm audio revision, WAV nguồn của job và grant. File thiếu/sai Hash hoặc trạng thái thay đổi làm lần backup thất bại; không bỏ qua file để ghi COMPLETE.

`manifest.json` ghi schema/checksum Flyway, ảnh nguồn, checkpoint, UTC, Hash dump và từng file, hạn đúng bảy ngày. COMPLETE nghĩa là đã kiểm bản sao, chưa có nghĩa restore đã đạt. Bản thất bại giữ FAILED.json để rà và dùng đích mới cho lần sau. Thông số thời gian trong manifest đo bước backup; không coi là tổng downtime hoặc cam kết RTO.

Các lệnh thay đổi trạng thái giữ khóa thư mục nguyên tử `/tmp/hueqr-operations-lock` tại chính container database tới khi kết thúc. Vì vậy hai cấu hình runtime cùng trỏ một DB, hoặc hai lệnh cùng owner, cũng không chạy chồng backup/resume. Status chỉ đọc. Khóa còn sau crash không tự được tháo theo tuổi; phải đối chiếu tiến trình công cụ trước xử lý thủ công. Không xóa khóa của một lượt đang chạy. Trước ghi dump/audio, công cụ kiểm dung lượng khả dụng so với bytes dump thực và tổng catalog; không cam kết dung lượng đĩa không đổi bởi tiến trình ngoài.

Sau đối chiếu kết quả mới mở lại:

```powershell
node deploy/maintenance.mjs $runtimeDirectory resume $maintenanceOwner
```

Nếu app đã dừng, công cụ khởi động lại và đợi API trả 503 dưới gate đóng trước khi ghi RUNNING. Owner khác không mở được gate. Không xoay epoch/khóa ký ở chu trình backup thường. Khi lỗi, không tự mở gate trong finally; người vận hành kiểm nguyên nhân và quyết định mở lại. Bộ backup hết bảy ngày không được restore; việc dọn bản hết hạn phải theo đường dẫn đã kiểm, chưa có lịch dọn tự động trong công cụ này.

## Restore tách biệt

```powershell
$restoreDirectory = '<thu-muc-runtime-moi-ngoai-repo>'
node deploy/restore-backup.mjs $backupDirectory $restoreDirectory '<anh-hueqr-local-dung-ID-nguon>'
```

Restore kiểm hạn/Hash trước tạo database và trước import; yêu cầu ID ảnh khớp snapshot, không tự nâng hoặc hạ schema. Nó tạo schema/volume mới, đối chiếu Flyway/catalog và bytes đọc lại từ volume đích; từ chối snapshot còn activity/task/claim chưa đối chiếu. Không xóa claim hoặc sửa phase job cho qua kiểm.

Trước khởi động app, lọc phiên/lượt quá hạn theo anchor gốc, thu hồi context/grant cũ, cấp epoch/khóa mới và giữ gate FROZEN với owner mới. Không sửa started/expires/endedAt của phiên được giữ. Bốn worker vẫn tắt. Công cụ kiểm app khởi động với API bị chặn, ghi RESTORE_VERIFIED_CLOSED rồi dừng đúng bộ restore. Trạng thái này chưa xác nhận UI/âm thanh, provider chưa rõ, toàn vòng đời file hoặc quyền cũ đã được kiểm thực qua mọi endpoint.

Đọc runtime riêng để biết project, URL loopback, configPath và maintenanceToken. Khởi động đúng database bằng Compose, sau đó dùng `maintenance.mjs ... resume ...` để kiểm bản phục hồi trong môi trường riêng. Rà job chưa terminal và nhu cầu bật worker trước khi phục vụ; không bật provider/cleaner chỉ vì import đạt. Không mở tunnel vào bản restore chưa được rà. Cách cấp tài khoản Admin tiếp tục theo quy trình riêng; không đưa mật khẩu vào báo cáo.

Nếu đã mở gate và sau đó dừng dịch vụ bình thường, dùng Compose với đúng config để khởi động lại; không gọi resume bằng owner cũ khi mode đã RUNNING. Công cụ restore ép các worker tắt trong môi trường Compose, kể cả shell đang có cờ bật; việc bật lại cần mốc kiểm vận hành riêng.

## Giới hạn còn mở

Chưa tự giải quyết lease/claim do crash thiếu bằng chứng owner, triển khai từ xa, lịch backup/dọn backup, hay chuyển phục vụ sang bản restore. Chưa thay thế kiểm 20 cặp, Android/iPhone, HTTPS/QR và CD-L01. [Thiết kế vận hành](../03-ky-thuat/operations-design.md#ops-backup) tiếp tục là nguồn chính sách.

## Chuẩn bị chuyển bản phục vụ V13 sang V14

Gate V14 không tồn tại trên V13; không chạy `maintenance.mjs` hoặc tự thêm hàng gate để vượt kiểm trên bản cũ. Thực hiện theo các mốc:

1. Ghi danh tính project/container, ID ảnh, URL, Flyway, volume database/audio và Hash cấu hình. Giữ secret ở kho riêng; xác nhận không có writer dùng chung volume hoặc kết nối database ngoài quy trình.
2. Chọn thời gian ngừng phục vụ; dừng ứng dụng/worker V13 có kiểm soát. Xác minh tiến trình đã dừng và không còn task/claim chưa đối chiếu; không xóa marker theo tuổi. MySQL tiếp tục hoạt động để lấy bản sao nhất quán. Nếu không đủ căn cứ dừng sạch, giữ môi trường đóng và xử lý nguyên nhân trước.
3. Sao lưu trước migration: dump database, catalog và toàn bộ WAV/MP3 còn tham chiếu; kiểm Hash, quyền, dung lượng và hạn bảy ngày. Bản sao V13 là loại offline trước nâng cấp, không gắn nhãn backup gate V14. Dùng `deploy/offline-v13-backup.mjs` cho đúng loại snapshot offline; công cụ gate V14 không thực hiện bước này.
4. Phục hồi snapshot V13 vào database/kho audio mới bằng đúng ảnh V13, kiểm dữ liệu/âm thanh và lọc hạn theo mốc gốc. Sau đó diễn tập nâng bản sao lên V14 với provider/cleaner/retention tắt, chỉ mở loopback; kiểm migration, gate, API/Admin/Khách và Hash dữ liệu.
5. Chỉ chuyển bản phục vụ sau khi diễn tập đạt và mốc được rà. Build ảnh V14 có đúng URL HTTPS/cấu hình frontend; không dùng ảnh thử thiếu cấu hình public chỉ vì đã đạt kiểm gate. Kiểm lại QR/đăng nhập/đọc/nghe sau chuyển.
6. Khi migration đã áp dụng, không mặc định chạy lại ảnh V13 trên schema V14. Phương án khôi phục là snapshot/ảnh V13 đã kiểm ở môi trường riêng, rồi chuyển đường phục vụ có kiểm soát; không hạ hoặc sửa migration.

Restore nội dung V14 ở môi trường riêng không thay phép kiểm snapshot V13 trước nâng cấp. Khóa/epoch giữ qua restart/cập nhật thường; phục hồi backup thật cấp khóa/epoch mới theo REC-P01.


## Công cụ V13 và cập nhật ảnh V14

Với nguồn V13 đã dừng app/worker và MySQL đang chạy, dùng đích mới ngoài repository/runtime:

```powershell
node deploy/offline-v13-backup.mjs '<runtime-V13>' '<backup-moi>'
node deploy/restore-backup.mjs '<backup-moi>' '<restore-moi>' '<anh-V13-dung-ID-snapshot>'
node backend/src/test/fixtures/operations/upgrade-v13-rehearsal.mjs '<restore-V13-rieng>' '<anh-V14>'
```

Snapshot kiểm đầy đủ migrations V1–V13 thành công, app đã dừng, zero task/claim/job chưa đối chiếu và không writer/container dùng chung volume. Restore tạo project mới, khóa/epoch mới và chỉ mở loopback; V13 không có gate nên trả trạng thái RESTORE_V13_VERIFIED_PRIVATE. Không sửa manifest thành backup gate V14. Không chạy lại bước V13 trên bản Ngọ Môn hiện đã ở V14.

Cập nhật ảnh V14 cùng schema dùng công cụ sau. Ảnh phải có nhãn `org.opencontainers.image.revision` khớp SHA đã rà/đối chiếu CI, TTS và converter đang tắt; thư mục backup phải mới, ở ngoài runtime/repository:

```powershell
node deploy/update-image.mjs '<runtime-V14>' '<anh-moi>' '<SHA-nguon-40-ky-tu>' '<backup-moi>'
```

Công cụ quiesce/drain/freeze, backup, đổi ảnh trong khi gate đóng, kiểm health/schema/volume/khóa rồi mở lại. Không tự nâng migration qua công cụ cập nhật cùng schema. Nếu ảnh mới lỗi, chỉ thử quay ảnh cũ khi schema không đổi và gate vẫn thuộc owner; không ép xóa lease hoặc hạ migration. Hồ sơ khôi phục và bản cấu hình trước nằm trong thư mục image-update của runtime, có thông tin riêng cần bảo vệ. Nếu không thể quay lại an toàn, đối chiếu owner/gate và snapshot trước khi thao tác tiếp.

`runtime-env.mjs` bỏ qua BOM/dòng trống/comment và nhận giá trị cụ thể, không thực thi hoặc tự nội suy secret. Cấu hình dùng biểu thức chưa giải quyết bị từ chối; không đưa kết quả đọc private.env vào log. Các bộ kiểm tại backend/src/test/fixtures/operations giữ riêng với bằng chứng runtime.
