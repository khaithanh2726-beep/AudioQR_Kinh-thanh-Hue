# Bảo trì, sao lưu và phục hồi có khóa bền

## Phạm vi

Công cụ dành cho bộ Compose riêng có database `hueqr`, schema từ V14 và ảnh ứng dụng đã tích hợp gate. `deploy/maintenance.mjs` chỉ nhận runtime thuộc project `hueqr-rehearsal-*` hoặc `hueqr-ops-check-*`; đối chiếu nhãn hai container và MySQL không publish cổng. Không suy từ việc có công cụ thành đã nghiệm thu trên môi trường phục vụ. [Hồ sơ kiểm](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/operations-guard-20261113/README.md) ghi lượt thực chạy.

V14 tạo `ops_guard` và `ops_activity`. API và bốn scheduler lấy lease trong giao dịch ngắn: khóa chia sẻ hàng gate → kiểm RUNNING → ghi lease → commit. Lệnh QUIESCING tranh chấp cùng hàng nên không có việc mới lọt qua sau khi đóng gate. Lease tồn tại tới khi request/stream hoặc toàn lượt scheduler kết thúc. Timeout, tuổi lease và restart không tự tháo lease. HTTP bị chặn dùng mã đã có `503 SERVICE_UNAVAILABLE`, no-store; không đổi phiên thành hết hạn hoặc tự gửi lại lệnh. Không thêm endpoint HTTP vận hành.

Ảnh được rà ở mốc này là `hueqr-local:20261113-ops-guard-urls`, ID trong hồ sơ kiểm. Không chọn ảnh thử `20261113-ops-guard` trước đó để phục vụ: ảnh đó dùng URI thô và chưa chặn đường API mã hóa. Bản sửa nhận diện servletPath đã giải mã, kiểm cả đường thường/mã hóa ở trạng thái đóng và mở.

## Điều kiện trước thao tác

- Thực hiện ngoài buổi trình diễn. Người quản trị tự xử lý phần đang soạn; công cụ không lưu hoặc áp dụng thay Admin.
- Thư mục runtime, backup và restore ở ngoài repository, có ACL của tài khoản thực hiện và SYSTEM. Không chia sẻ private.env, runtime.json hoặc dump. Đích backup mới phải tách runtime; restore luôn tạo project/volume mới.
- Không có SQL/DDL thủ công, app ngoài quy trình hoặc công cụ khác ghi cùng database/kho audio. Gate không fence được người quản trị có quyền truy cập trực tiếp MySQL/Docker. Công cụ kiểm thêm kết nối MySQL và container dùng chung volume; nếu chưa rõ thì dừng.
- Dùng đúng ảnh V14 đã kiểm. Chưa áp dụng công cụ lên bản tập dượt V13 đang giữ dữ liệu Ngọ Môn. Backup/đổi schema của bản đó cần mốc cập nhật riêng và bản sao trước migration.

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
