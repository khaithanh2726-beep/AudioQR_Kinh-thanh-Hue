# Đóng gói và kiểm tra tự động

Ứng dụng dùng Vue và Spring Boot chung origin. Docker build đóng gói hai trang khách/Admin vào static resources của backend. MySQL và kho audio nằm trên hai volume riêng.

Đã kiểm build ảnh Linux, FFmpeg/ffprobe, đường dẫn Unicode, ứng dụng chung origin và Compose restart/update/rollback trong môi trường tách biệt. HTTPS/QR Ngọ Môn vi đã có lượt chạy thử; bản phục vụ đã nâng V14 có kiểm trước/sau, còn 20 cặp và nghiệm thu vận hành cuối. Phạm vi và bằng chứng tại [báo cáo kiểm thử](../06-kiem-thu/README.md).

## Cấu hình

Sao chép mẫu `deploy/.env.example` ra tệp cấu hình riêng ngoài repository. Đặt mật khẩu database, khóa ký khách ít nhất 32 byte dưới dạng Base64 và UUID epoch. Giữ các giá trị ổn định trong cùng môi trường. Cấp khóa TTS cho môi trường được phép gọi provider.

`HUEQR_PUBLIC_START_URL` là URL HTTPS thật của trang bắt đầu và được nhận lúc build frontend. Trang Admin hiển thị URL chỉ đọc. Khi URL thay đổi, build lại ảnh ứng dụng và kiểm quét QR.

Riêng bản tập dượt ngrok free có `HUEQR_NGROK_SKIP_BROWSER_WARNING=true` lúc build để API Khách cùng origin nhận JSON/audio thay trang cảnh báo. Mặc định tắt; không đổi cơ chế credential hoặc gửi cookie cho Khách. Cách chạy và QR tại [hướng dẫn tập dượt](tap-duot.md).

`HUEQR_TTS_ENABLED` bật lời gọi provider. `HUEQR_TTS_LOCAL_ENABLED` trong Compose bật phần chuyển WAV đã có thành MP3. Cả hai mặc định tắt; cleaner và retention cũng chỉ bật bằng các biến cấu hình riêng sau khi kiểm điều kiện vận hành. Không dùng khóa hoặc database của dự án khác khi sao chép cấu hình.

`HUEQR_IMAGE` chọn tag ảnh, mặc định `hueqr:local`. Khi chuẩn bị phát hành, ghi tag/ID ảnh và SHA nguồn cụ thể vào hồ sơ. Chọn ảnh cũ bằng biến này chỉ phù hợp khi đã xác nhận tương thích schema; không hạ hoặc sửa migration đã áp dụng. [Công cụ Compose riêng](../../backend/src/test/fixtures/operations/README.md) kiểm độ bền volume qua bốn chặng, giữ cấu hình/credential ngoài repository.

Từ thư mục repository, khi được giao chạy môi trường đóng gói, dùng lệnh sau với đường dẫn tệp cấu hình đã chuẩn bị.

```powershell
docker compose --env-file '<tệp cấu hình riêng>' -f deploy/compose.yaml config --quiet
docker compose --env-file '<tệp cấu hình riêng>' -f deploy/compose.yaml build
docker compose --env-file '<tệp cấu hình riêng>' -f deploy/compose.yaml up -d
```

MySQL chỉ được nối từ mạng Compose. Backend mặc định mở cổng 8080 trên loopback. HTTPS được cấp bởi đường phục vụ đã cấu hình, giữ cookie Secure/HttpOnly/SameSite và cùng origin cho trang web với API.

Ảnh Linux xây FFmpeg/ffprobe 9.0.2 từ nguồn, hỗ trợ WAV PCM 16-bit mono 24 kHz và MP3 bằng libmp3lame. Cần kiểm build ảnh, chuyển đổi, giải mã và đường dẫn Unicode trên Linux trước khi nghiệm thu môi trường đóng gói.

Lượt ngày 11/11 đã đạt các kiểm media trên Linux nêu trên; giữ yêu cầu kiểm lại khi thay công cụ, ảnh hoặc profile audio. Công cụ diễn tập và hướng dẫn chạy lại tại [fixture operations](../../backend/src/test/fixtures/operations/README.md).

## CI

Workflow `.github/workflows/ci.yml` chạy kiểm frontend và backend cho pull request, các nhánh task và khi kích hoạt thủ công. Frontend dùng lockfile, chạy test và build. Backend dùng Java 21, Maven Wrapper và Testcontainers MySQL, xuất báo cáo Surefire.

Job Node kiểm thêm manifest backup bằng năm ca độc lập. [CI PR #15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43406790912) đạt 530/530 backend, 71/71 logic frontend, 5/5 manifest và build. Bộ kiểm manifest V14 mở rộng đạt 8/8 cục bộ là lượt khác. Artifact gồm Surefire, JAR và frontend dist; đó là mốc CI trước khi bổ sung CD. Workflow hiện có deploy chủ động bằng ảnh truyền qua SSH, không publish registry; các job kiểm không gọi provider hoặc dùng database phục vụ.

T10 `5ef599c` đã push và [CI đạt](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43469439941). Bộ kiểm cục bộ mới mở rộng thành 20 ca (5 manifest, 12 integrity V13/V14, 3 đọc runtime-env); workflow bổ sung các suite này, kết quả GitHub của mốc bổ sung được ghi riêng khi chạy.

Chạy workflow trên GitHub cần các file nguồn tương ứng đã được đưa lên nhánh. Lượt chạy cục bộ và lượt chạy GitHub được ghi nhận riêng trong hồ sơ kiểm thử.

## HTTPS/ngrok khi diễn tập

Ngrok đã được liên kết trên máy diễn tập. Token giữ trong cấu hình riêng của tài khoản, không ghi vào hồ sơ. Khi đổi máy, cấu hình lại công cụ theo tài liệu nhà cung cấp trước khi mở đường phục vụ.

Chỉ mở agent tới đúng cổng ứng dụng đã rà theo [hướng dẫn tập dượt](tap-duot.md). URL HTTPS phải khớp HUEQR_PUBLIC_START_URL và QR; giữ agent/laptop hoạt động, kiểm lại từ điện thoại sau mỗi lần khởi động. Ngrok không phục vụ database hoặc môi trường restore chưa nghiệm thu.



## Bàn giao CD

Kiểm điều kiện máy bằng công cụ chỉ đọc:

```powershell
node deploy/check-host.mjs
node deploy/check-host.mjs --require-cd
```

Công cụ báo Node, Docker/Compose, Linux Engine và trạng thái dịch vụ Windows OpenSSH/Tailscale; không cài, đăng nhập, mở cổng hoặc khởi động dịch vụ. Cờ `--require-cd` trả mã lỗi khi thiếu điều kiện máy. Trường kiểm từ xa của công cụ chỉ đọc không thay kết quả diễn tập riêng. Máy đích đã có Tailscale/OpenSSH chạy; đăng nhập bằng khóa, ghim host key, Docker context qua SSH và SFTP/receiver trên restore riêng đã đạt. [Lượt deploy thật từ GitHub runner](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) đã đạt, runtime được đọc lại sau chuyển.

Đường CD đã chọn là GitHub Actions qua Tailscale riêng đến OpenSSH trên Windows, chạy PowerShell với context Docker Desktop của tài khoản triển khai. Cần cấu hình danh tính Tailscale, máy đích, SSH host key, khóa SSH, tài khoản và thư mục triển khai trước khi tạo bước phát hành thực thi.

Các phép kiểm bắt buộc gồm SSH vào đúng máy, chạy được Docker/Compose dưới đúng tài khoản, giữ volume qua cập nhật ảnh, đổi phiên bản có thể quay lại và URL HTTPS truy cập được từ thiết bị ngoài mạng. Kết quả các phép kiểm được bổ sung khi môi trường đích được cấp.

### Chuẩn bị máy và tài khoản runner

Kho riêng ở `%USERPROFILE%/AudioQR-CD` có ACL cho tài khoản triển khai/SYSTEM, gồm `config.json`, cặp khóa `actions_ed25519`, host key công khai, inbox và backup. `config.json` ghi đường dẫn Node, runtime hiện hành, backupRoot, tài khoản SSH và địa chỉ Tailscale; không đưa vào repository. Khóa riêng chỉ lưu trong kho riêng/GitHub Secrets, không chép vào source hoặc artifact.

Khởi tạo dưới đúng tài khoản Windows chạy Docker Desktop; chỉ chạy khi thư mục CD chưa tồn tại. Công cụ từ chối ghi đè kho/khóa đã có:

```powershell
node deploy/init-cd.mjs '<runtime-V14-co-publicStartUrl>' '<IPv4-Tailscale>'
```

Với tài khoản Windows thuộc nhóm Administrators và chạy Docker Desktop, người quản trị mở PowerShell Administrator, chạy script sau sau khi đã chuẩn bị cấu hình/cặp khóa:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File deploy/prepare-ssh.ps1 -CdDirectory '<thu-muc-AudioQR-CD>'
```

Script không cài OpenSSH, không đọc khóa riêng hoặc xóa khóa cũ; thêm khóa công khai, giữ bản trước, đặt ACL của administrators_authorized_keys, khởi động sshd và giới hạn rule TCP 22 vào dải Tailscale 100.64.0.0/10. Windows có thể hiển thị cùng dải bằng subnet mask 255.192.0.0. Không mở port router hoặc Tailscale Funnel. Kiểm SSH thực với StrictHostKeyChecking và host key lấy từ máy đích; không dùng trust-on-first-use thay đối chiếu khóa.

Tailscale OAuth client dùng Auth Keys: Write và tag `tag:hueqr-ci`; cấu hình tag/quyền trước khi dùng runner. [Policy mẫu](../../deploy/tailscale-policy.example.json) dành cho tailnet mới chỉ có chính sách mặc định; tailnet đã tùy chỉnh phải hợp nhất đúng phạm vi, không ghi đè toàn bộ. Dùng [hướng dẫn OAuth](https://tailscale.com/docs/features/oauth-clients#setting-up-an-oauth-client) và [GitHub Action của Tailscale](https://tailscale.com/docs/integrations/github/github-action) làm nguồn cấu hình nhà cung cấp.

| Loại GitHub Actions | Tên | Giá trị |
|---|---|---|
| Variable | HUEQR_PUBLIC_START_URL | URL HTTPS trang bắt đầu, khớp runtime |
| Variable | HUEQR_CD_HOST | IPv4 Tailscale của máy đích |
| Variable | HUEQR_CD_USER | Tài khoản Windows đã kiểm Docker qua SSH |
| Variable | HUEQR_CD_KNOWN_HOSTS | Dòng known_hosts đã ghim khóa máy chủ |
| Variable | HUEQR_TS_OAUTH_CLIENT_ID | OAuth Client ID của Tailscale |
| Secret | HUEQR_TS_OAUTH_SECRET | OAuth Client secret |
| Secret | HUEQR_CD_SSH_KEY | Nội dung khóa riêng CD, giữ đầy đủ các dòng |

### Chạy job CD chủ động

Workflow `ci.yml` có input `deploy_source_sha`. Push/PR hoặc input rỗng chỉ build/kiểm, không deploy. Chỉ workflow_dispatch trên T10/main với SHA đầy đủ đúng commit của lượt chạy mới mở job deploy; job này phải chờ cả frontend/backend đạt. Hai lượt deploy không chạy chồng và lượt dispatch không bị hủy tự động khi có lượt khác.

Job build ảnh Linux gắn SHA, tính Hash tar/JAR/bộ công cụ, kết nối Tailscale rồi chuyển bằng SFTP qua SSH ghim host key. Máy đích kiểm Hash trước khi nạp ảnh, đối chiếu SHA nhãn và Hash JAR sau nạp, gọi update-image với backup trước chuyển. Runtime config/khóa và database/audio không đi qua artifact. Các file trung chuyển tar/JAR được bỏ sau deploy đạt; giữ báo cáo/Hash/ảnh và backup theo chính sách riêng.

Lượt SFTP/receiver riêng và [workflow CD thật](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) đều đã đạt, được ghi riêng. Run 43486032664 triển khai nguồn 134ac0d sau backend 530/530, frontend 71/71 và 20 ca backup/cấu hình; target đọc lại đúng SHA/ảnh, V14, volume/khóa/epoch và hai Hash audio giữ nguyên, local/HTTPS 200. GitHub giữ artifact deployment-result; báo cáo máy đích và log chi tiết được bảo quản riêng. Lượt CD này không thay kiểm toàn ma trận thiết bị, dữ liệu mẫu hoặc lịch backup/log.
