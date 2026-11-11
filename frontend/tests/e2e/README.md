# Chạy kiểm trình duyệt với API thật

Kết quả tạm của Playwright mặc định nằm tại `%TEMP%/hueqr-playwright/audioqr-kinh-thanh-hue`, không sinh thêm thư mục trong frontend. Đặt `HUEQR_E2E_OUTPUT` ngoài repository cho từng lượt cần giữ hoặc chạy song song. Mã ca kiểm vẫn ở `frontend/tests`; hồ sơ được chọn lọc và bỏ secret tại `docs/06-kiem-thu`.

Hai kịch bản tạo dữ liệu thử thật, dùng SQL fixture chỉ chấp nhận schema/tài khoản `hueqr_smoke`. Chỉ chạy trong môi trường riêng theo [hướng dẫn chạy thử](../../../docs/07-van-hanh/chay-thu/README.md), không dùng database đang vận hành. Bộ kiểm không gọi Gemini; cần WAV tiếng Việt Ngọ Môn đã chuẩn bị, RIFF PCM 16-bit mono 24 kHz, khớp bài trong tài liệu.

Backend cần local-audio worker bật và đọc được kho audio riêng. Khởi động Vite với `HUEQR_BACKEND_URL` trỏ backend thử và cổng 15173. Tạo Admin `smoke-admin`. Đặt `runtime.json` ngoài repository, UTF-8, gồm:

```json
{
  "container": "hueqr-e2e-ten-rieng",
  "dbPassword": "<mat-khau-MySQL-thu>",
  "adminPassword": "<mat-khau-Admin-thu>",
  "audioRoot": "C:/duong-dan-kho-audio-thu"
}
```

Tên container phải bắt đầu `hueqr-e2e-`. Mật khẩu không ghi vào log, tài liệu kết quả hoặc Git. Tên container và đường dẫn kho phải khớp đúng backend thử; backend không được trỏ vào môi trường khác.

Trong thư mục frontend:

```powershell
$env:HUEQR_E2E_RUNTIME = '<thu-muc-chua-runtime.json>'
$env:HUEQR_E2E_WAV = '<tep-WAV-Ngo-Mon>'
$env:HUEQR_E2E_OUTPUT = '<thu-muc-ket-qua-moi>'
$env:HUEQR_E2E_URL = 'http://localhost:15173'
$env:HUEQR_BROWSER_PATH = 'C:/Program Files/Google/Chrome/Application/chrome.exe'
npm run test:e2e
```

Chrome dùng bản đã cài sẵn; không tải browser tự động. Không cấu hình runtime thì các ca bị bỏ qua, không được ghi là đạt. Giữ thư mục kết quả riêng cho mỗi lượt để không xóa ảnh lỗi trước. Ảnh và `e2e-result.json` nằm trong thư mục runtime; sao chép có chọn lọc sang hồ sơ kiểm, không chép `runtime.json` hoặc cấu hình chứa khóa.

Kịch bản tạo địa điểm/tài khoản khách có tên riêng, không xóa dữ liệu cũ. Sau kiểm, dừng đúng tiến trình/container thử do lượt đó tạo. Không dùng thao tác dọn toàn Docker hoặc reset/clean repository.
