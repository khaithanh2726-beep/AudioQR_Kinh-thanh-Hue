# Bảo trì, sao lưu và phục hồi có khóa bền

## Phạm vi

Công cụ dành cho bộ Compose riêng có database `hueqr`, schema từ V14 và ảnh ứng dụng đã tích hợp gate. `deploy/maintenance.mjs` nhận project phục vụ `audioqr-hue` cùng các project kiểm tra `hueqr-rehearsal-*` và `hueqr-ops-check-*`; đối chiếu nhãn hai container và MySQL không publish cổng. Khi đổi tên project, các volume đang có vẫn giữ tên Docker cũ và được khai báo external trong cấu hình Compose; không xóa hoặc tạo lại volume. Không suy từ việc có công cụ thành đã nghiệm thu trên môi trường phục vụ. [Hồ sơ kiểm](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/operations-guard-20261113/README.md) ghi lượt thực chạy.

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

Nếu app đã dừng, công cụ khởi động lại và đợi API trả 503 dưới gate đóng trước khi ghi RUNNING. Owner khác không mở được gate. Không xoay epoch/khóa ký ở chu trình backup thường. Khi lỗi, không tự mở gate trong finally; người vận hành kiểm nguyên nhân và quyết định mở lại. Bộ backup hết bảy ngày không được restore; bộ chạy định kỳ ở cuối tài liệu thực hiện dọn theo đường dẫn và manifest đã kiểm.

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

Chưa tự giải quyết lease/claim do crash thiếu bằng chứng owner hoặc chuyển phục vụ sang bản restore. CD từ xa đã có lượt thực thi đạt; công cụ lịch backup/dọn được mô tả bên dưới. Không thay thế kiểm đủ 20 cặp, toàn ma trận Android/iPhone và HTTPS/QR. [Thiết kế vận hành](../03-ky-thuat/operations-design.md#ops-backup) tiếp tục là nguồn chính sách.

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

## Backup theo lịch và quản lý log

### Cấu hình và đăng ký

Kho vận hành của bản Ngọ Môn: `%USERPROFILE%\AudioQR-Operations`; config chỉ chứa đường dẫn/project/chính sách, log và bộ công cụ ghim ở cùng kho. Backup định kỳ dùng chung `%USERPROFILE%\AudioQR-CD\backups` với CD, tách khỏi database/audio live. Kho có ACL tài khoản thực hiện và SYSTEM. Không sao chép private.env, khóa hoặc dump vào Git.

Với môi trường mới, khởi tạo một lần; công cụ từ chối ghi đè kho đã có:

```powershell
node deploy/init-operations.mjs '<runtime-V14>' '<kho-van-hanh-moi>' '<backup-root-da-bao-ve>'
node deploy/scheduled-operations.mjs '<kho-van-hanh>\config.json' check
powershell.exe -NoProfile -ExecutionPolicy Bypass -File deploy/register-operations-task.ps1 -ConfigPath '<kho-van-hanh>\config.json' -DailyAt '23:00'
```

Tham số backup-root có thể bỏ để tạo thư mục backups mới dưới kho vận hành. Lịch mặc định 23:00 theo giờ Windows, chạy mỗi ngày cả khi không có thay đổi để không bỏ sót DDL/cập nhật file; không thay yêu cầu backup trước deploy/schema. Task `AudioQR-Operations-<project>` dùng tài khoản hiện tại, Interactive/Limited, không lưu mật khẩu, không chạy bù khi mở máy, không đánh thức máy. Máy/tài khoản phải đang hoạt động và Docker Linux Engine phải sẵn sàng. Lượt bỏ lỡ phải được người vận hành chạy bù ngoài buổi trình diễn. Không cam kết backup khi laptop tắt hoặc đã đăng xuất.

Khi đăng ký, bốn công cụ cần thiết được chép sang `tools/<Hash-bộ-công-cụ>/deploy` trong kho riêng và kiểm SHA256; task dùng bản ghim này thay vì phụ thuộc nhánh Git đang mở. Không tự cập nhật công cụ ghim khi pull/đổi nhánh. Nếu cần nâng công cụ, tắt lịch, chờ lượt hiện tại kết thúc, xuất XML task cũ, rà Hash rồi gỡ/đăng ký lại đúng task có kiểm soát. Bộ mới có thư mục Hash riêng, không ghi đè bộ cũ; script từ chối ghi đè task đang tồn tại hoặc tệp ghim khác Hash.

### Chu trình và xử lý thất bại

`scheduled-operations.mjs` kiểm runtime/project và các thư mục không lồng nhau hoặc có junction/symlink; lấy khóa một lượt trong log-root. Sau đó kiểm gate RUNNING → quiesce → drain → freeze → backup → đọc lại Hash → resume → kiểm HTTP 200 → dọn backup. Mỗi lần có tên backup UUID mới và hạn bảy ngày từ lúc tạo. Báo cáo JSON không ghi tên khách, nội dung, token, mật khẩu hoặc stderr của tiến trình con.

Hai lượt không chạy chồng; gặp gate của owner khác thì dừng. Nếu ứng dụng đã dừng, lịch không tự khởi động lại nó. Backup lỗi hoặc không drain được không tự mở gate, không dọn backup cũ. Báo cáo FAILED có operationId/stage trong kho riêng. Khóa `.run-lock` còn sau crash cần đối chiếu PID/process và gate trước xử lý; không tháo khóa/lease chỉ vì quá giờ. Không dùng Stop-ScheduledTask hoặc kill tiến trình để thay cho quy trình drain.

Chỉ dọn thư mục `backup-*` trực tiếp trong root đã cấu hình sau khi bản mới còn hạn và đủ Hash. Bản hết hạn phải có manifest COMPLETE loại quiesced-backup, hạn đúng bảy ngày, đúng tập database/audio/manifest và bytes khớp. Xóa từng tệp đã đối chiếu rồi xóa thư mục rỗng; không recursive delete hoặc đi vào link. Bản dở, hỏng, có tệp lạ hay loại V13 được ghi review và giữ để người vận hành xử lý; tuyệt đối không dùng chúng như backup còn hiệu lực. Cần theo dõi review để dọn dữ liệu quá hạn có kiểm soát, không âm thầm kéo hạn nguồn.

```powershell
$taskName = 'AudioQR-Operations-audioqr-hue'
Get-ScheduledTaskInfo -TaskName $taskName
Start-ScheduledTask -TaskName $taskName # Ngoài trình diễn; có gián đoạn phục vụ.
```

Sau lượt chạy, kiểm LastTaskResult=0 và báo cáo mới nhất trong `AudioQR-Operations\logs` có SUCCESS, backupComplete/reopened=true. Restore định kỳ vẫn dùng `restore-backup.mjs` với đúng ảnh trong manifest vào kho/project mới; COMPLETE không tự đồng nghĩa restore đã đạt. Khi có lỗi, kiểm stage, gate và owner trước resume; không chạy lại để ép mở.

### Log và dung lượng

| Loại | Chính sách và phạm vi |
|---|---|
| Báo cáo chạy lịch | Tệp `operations-<UUID>.json`, giữ bảy ngày từ finishedAt; dọn ở đầu mỗi lượt. Chỉ nhận đúng loại scheduled-operations; tệp lạ/hỏng/link giữ để rà. Không có lượt chạy thì dọn vật lý có thể trễ, không đặt lại timestamp. |
| Console ứng dụng/MySQL do Docker quản lý | Driver local, max-size=10m, max-file=3 cho từng container; Docker tự xoay. Đây là giới hạn dung lượng, không cam kết xóa theo tuổi bảy ngày. Không sửa/xóa trực tiếp tệp daemon hoặc dọn volume để giảm log. |
| Báo cáo nghiệm thu và phục hồi | Bằng chứng đã chọn tách sang hồ sơ nội bộ; recovery-private/config/owner đang cần dùng không phải log rác để xóa theo tên. |
| GitHub/ngrok/Tailscale/provider | Log hạ tầng ngoài phạm vi app tự xóa; theo quyền và chính sách của từng dịch vụ. Không ghi token hoặc thông tin khách vào log chia sẻ. |

Đặt giới hạn Docker trong compose chỉ có hiệu lực với container được tạo lại. Công cụ sau backup/quiesce/freeze rồi tạo lại database/application bằng cùng ảnh/volume, kiểm schema/khóa/health trước mở; không tự nâng phiên bản:

```powershell
node deploy/apply-log-limits.mjs '<runtime-V14>' '<backup-moi>'
```

Thực hiện ngoài buổi trình diễn. Nếu lỗi, giữ gate và báo cáo log-policy trong runtime cho người vận hành; không tự clear owner. Việc giới hạn buffer Docker không phải bằng chứng mọi log hạ tầng có TTL bảy ngày.

## Chuyển bộ nội dung v02 đã duyệt sang bản phục vụ

Ba công cụ `import-approved-content.mjs`, `publish-approved-content.mjs` và `content-admin-client.mjs` phục vụ một lần chuyển 19 cặp v02, không phải API tải audio công khai. Chúng đọc [hồ sơ duyệt](../05-noi-dung/duyet-19-cap-v02-20261114.json), khóa đúng revision/Hash và giữ cặp Ngọ Môn vi cũ. Không thay database phục vụ bằng database tạo nội dung vì sẽ mất lịch sử khách/thống kê.

1. Nguồn là bản phục hồi riêng của snapshot nội dung đủ 40 tệp, còn hạn, provider và mọi worker tắt. Hai ứng dụng phải dừng, DB chạy, gate FROZEN, không còn activity/task/claim. Bản đích phải có backup mới cùng owner/checkpoint và 19 slot trống; có chỉnh sửa hoặc ID trùng thì dừng để đối chiếu.
2. Import giữ khóa ngoại, nhập file/text/job/audio cùng dấu vết instance chuyển đổi đã STOPPED; không tạo kết quả TTS giả. Chép và đọc lại 38 tệp mới, kiểm Hash, dùng transaction cho bản ghi/slot và đối chiếu Hash dữ liệu cũ. Không ghi trực tiếp cặp, review hoặc visibility.
3. Sau kết quả `IMPORTED_FROZEN`, người vận hành mở lại đúng owner rồi chạy publisher. Công cụ đăng nhập Admin loopback, lưu/đọc lại Hash/áp dụng bốn vùng, ghi hồ sơ duyệt và áp dụng 19 cặp bằng ETag/command window/receipt. Chỉ bật địa điểm sau khi đủ cặp. Các receipt/SQL/log riêng phải nằm ngoài Git, có ACL bảo vệ.

```powershell
node deploy/import-approved-content.mjs '<runtime-nguon-rieng>' '<backup-40-tep>' '<runtime-dich>' '<owner-UUID>' '<backup-dich-truoc-nhap>' '<thu-muc-bao-cao-moi>'
node deploy/maintenance.mjs '<runtime-dich>' resume '<owner-UUID>'
node deploy/publish-approved-content.mjs '<runtime-dich>' '<runtime-co-tai-khoan-Admin>' '<thu-muc-ledger-moi>'
node backend/src/test/fixtures/smoke/verify-published-content.mjs '<runtime-dich>' '<bao-cao-rieng.json>'
```

Chỉ resume sau khi importer báo đạt. `FAILED_CLOSED` giữ gate đóng; `IMPORT_OUTCOME_REQUIRES_RECONCILIATION` phải đối chiếu transaction/bản ghi/file trước thao tác tiếp. Publisher lưu key PENDING trước gửi; mất phản hồi hoặc receipt lỗi không tự thử ghi lại. Đọc ledger và tra receipt hiện có theo hợp đồng Admin trước khi quyết định; không chạy lại bằng thư mục/key mới để né xung đột. File đã chép nhưng transaction chưa hoàn tất không được tự xóa hoặc gắn sang revision khác.

Bộ kiểm API tạo và kết thúc bốn phiên kỹ thuật, lấy đủ 20 văn bản/MP3, so Hash và thử replay cùng key. Nó không gửi view-ack vì không hiển thị văn bản cho khách, không tạo bằng chứng đọc/nghe trên điện thoại. Sau chuyển phải backup mới và thử restore đúng ảnh/schema sang kho riêng; giữ hạn/epoch theo quy trình đã nêu.
