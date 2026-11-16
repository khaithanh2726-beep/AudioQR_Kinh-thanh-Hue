# Chạy kiểm trình duyệt với API thật

Kết quả tạm của Playwright mặc định nằm tại `%TEMP%/hueqr-playwright/audioqr-kinh-thanh-hue`, không sinh thêm thư mục trong frontend. Đặt `HUEQR_E2E_OUTPUT` ngoài repository cho từng lượt cần giữ hoặc chạy song song. Mã ca kiểm vẫn ở `frontend/tests`; hồ sơ được chọn lọc và bỏ secret tại `docs/06-kiem-thu`.

Hai kịch bản tạo dữ liệu thử thật, dùng SQL fixture chỉ chấp nhận schema/tài khoản `hueqr_smoke`. Chỉ chạy trong môi trường riêng theo [hướng dẫn chạy thử](../../../docs/99-luu-tru/chay-thu-mot-diem.md), không dùng database đang vận hành. Bộ kiểm không gọi Gemini; cần WAV tiếng Việt Ngọ Môn đã chuẩn bị, RIFF PCM 16-bit mono 24 kHz, khớp bài trong tài liệu.

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

## Kiểm bản tập dượt đã chuẩn bị

`rehearsal.spec.mjs` nhận `HUEQR_REHEARSAL_RUNTIME` và URL đúng bộ `hueqr-rehearsal-*`, chỉ dùng bản ban đầu chưa duyệt/áp dụng/cấu hình QR. Hai ca kiểm form Khách/preview 404, đăng nhập Admin, Hash bản văn/MP3, thử phát và gate chưa có hồ sơ. Không tạo TTS hoặc thay nội dung nghiệp vụ. [Hướng dẫn tập dượt](../../../docs/07-van-hanh/tap-duot.md) ghi cách chuẩn bị và thực chạy; credential giữ ngoài repo. Sau khi có HTTPS/cặp phục vụ, dùng ca kiểm tương ứng, không sửa dữ liệu để ép bộ ban đầu đạt.

## Kiểm HTTPS sau khi áp dụng Ngọ Môn

`public-rehearsal.spec.mjs` nhận `HUEQR_PUBLIC_REHEARSAL_RUNTIME` trỏ tới runtime riêng có project `hueqr-rehearsal-*`, tài khoản và `publicStartUrl` HTTPS. Đặt `HUEQR_E2E_URL` cùng URL, chọn Chrome đã cài rồi chạy riêng `npm run test:e2e -- public-rehearsal.spec.mjs`. Chỉ dùng bộ một cặp vi đã áp dụng; không chạy bộ `rehearsal.spec.mjs` ban đầu trên trạng thái này. Kịch bản có tạo/kết thúc phiên khách và đăng nhập Admin, không tạo TTS hoặc thay cặp.

Hai ca kiểm tải QR/in CSS, nhận map JSON không cookie, đọc/nghe MP3 thật và kết thúc. Tile là fixture; không thay bằng chứng quét QR/thiết bị hoặc mạng thật. Chọn thư mục kết quả mới, không công bố runtime chứa khóa. [Kết quả mốc HTTPS](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/https-ngo-mon-20261112/README.md).

## Kiểm Admin sau restore V14

`operations-restore.spec.mjs` nhận `HUEQR_OPS_RESTORE_RUNTIME` tới thư mục restore riêng có `browser-private.json` và `restore-result.json` do bộ diễn tập vận hành tạo. Đặt `HUEQR_E2E_URL` đúng URL loopback của bộ đó, `HUEQR_E2E_OUTPUT` tới đích mới; chạy riêng `npm.cmd run test:e2e -- operations-restore.spec.mjs`. Chỉ nhận project `hueqr-ops-check-*`. Ca kiểm đăng nhập Admin, đọc bản văn và phát đúng Hash MP3 từ volume phục hồi, không tạo TTS/duyệt/áp dụng. Tile dùng fixture, không thay kiểm thiết bị thật hoặc HTTPS. Không chia sẻ browser-private.json.

## Kiểm một lượt provider thật

`provider.spec.mjs` chỉ chạy khi đặt `HUEQR_ALLOW_PROVIDER_TEST=one-vi` và `HUEQR_PROVIDER_RUNTIME` tới thư mục riêng có `provider-runtime.json`. Cấu hình này gồm `project` có tiền tố `hueqr-compose-check-`, `url` là origin localhost, `providerEnabled=true`, `adminLogin`, `adminPassword`; giữ tệp ngoài repository và giới hạn ACL. Backend phải thuộc đúng project thử, đã có năm điểm ẩn/20 slot, tài khoản Admin thử, khóa Gemini được phép sử dụng và hai worker provider/local bật; cleaner/retention tắt. Không dùng runtime đang phục vụ.

Đặt `HUEQR_E2E_URL` đúng `url` và chạy riêng `npm run test:e2e -- provider.spec.mjs`. Ca này lưu bài Ngọ Môn vi, bấm Tạo một lần, theo dõi job thật, tải và kiểm Hash MP3, thử phát trong Chrome. Nền bản đồ vẫn dùng tile kiểm; không gọi OSM. Không ghi hồ sơ duyệt, áp dụng cặp hoặc bật địa điểm. Kết quả provider lỗi/quota/chưa rõ được ghi trong `provider-result.json` và ca kiểm báo lỗi; không retry tự động. `provider-attempt.lock` được giữ cả khi lỗi để tránh lần chạy lại vô tình phát sinh thêm lời gọi; chỉ chuẩn bị lượt mới sau khi rà job cũ và quyền/quota. Dừng worker thử sau kiểm, giữ audio và bằng chứng riêng để nghe duyệt.
