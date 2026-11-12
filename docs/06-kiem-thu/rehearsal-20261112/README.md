# Chuẩn bị bản tập dượt — 12/11/2026

## Đầu ra

Đã dựng bản tập dượt riêng tại `http://localhost:18083` và `/admin.html`, project `hueqr-rehearsal-15d2f5c0`. Preview 18082 được giữ riêng. Ảnh `hueqr-local:20261112-rehearsal` được build từ nguồn hiện có; sửa cấu hình Vite để bản thường không đóng gói trang preview. Nguồn mới chưa commit/push, ghi nhận bằng SHA-256 trong [result.json](result.json); không coi đây là ảnh đã phát hành từ GitHub CI.

Database/kho audio mới tách khỏi nguồn kiểm provider, không dùng database đang vận hành. Có 5 điểm/20 slot, một văn bản vi và một audio Ngọ Môn từ job Gemini thật đã kiểm; chưa có cặp, vùng áp dụng hoặc điểm hiển thị. Tài khoản cũ bị vô hiệu hóa trên bản sao; tạo `hue-admin` với mật khẩu riêng. Khóa ký/epoch mới, giữ ổn định qua restart. Provider/converter/cleaner/retention tắt; không gọi Gemini hoặc tạo hồ sơ duyệt trong lượt này.

Thông tin đăng nhập/cấu hình nằm ngoài repository trong thư mục LocalAppData có ACL riêng. Hồ sơ công khai chỉ chứa metadata/Hash, không chép `runtime.json`, `private.env`, `admin-login.txt`, dump hoặc audio. Tái lập theo [hướng dẫn tập dượt](../../07-van-hanh/tap-duot.md).

## Sửa và kiểm thực chạy

| Phần | Kết quả |
|---|---|
| Vite `VITE_MAP_PREVIEW=false` | Build đạt, chỉ có hai HTML Khách/Admin; không có `map-preview.html` |
| Vite `VITE_MAP_PREVIEW=true` | Build đạt, vẫn có trang preview |
| Logic frontend | 65/65 đạt, không bỏ qua |
| Docker | Build lại ảnh đầy đủ với preview tắt; giữ Node 24.22.2, Maven 3.9.12, Java 21, FFmpeg 9.0.2 và phiên bản package/lockfile |
| Chuẩn bị dữ liệu | Chỉ import vào schema trống, giữ revision/job thật; WAV/MP3 khớp kích thước/Hash trước và sau copy |
| Chrome trước restart | 2/2 ca đạt: form Khách/preview 404; Admin đăng nhập, đọc đúng văn bản, tải/Hash/phát MP3, gate áp dụng còn khóa, QR báo chưa có URL |
| Restart | Giữ image/volume, khóa/epoch, số đếm dữ liệu và Hash hai file; Admin cũ bị khóa, Admin mới còn hoạt động |
| Chrome sau restart | Chạy lại cùng hai ca, 2/2 đạt; đây là bốn lượt thực thi của hai ca, không phải bốn ca độc lập |
| Media trong ảnh mới | Probe/giải mã trọn WAV/MP3 đạt; encode lại WAV qua đúng argv của encoder, MP3 mono 24 kHz/93.740 byte, Hash trùng MP3 đã có; không đăng ký bản encode kiểm thành nội dung |

[Form Khách](guest-ready.png) và [Admin nghe thử](admin-audio-ready.png) là ảnh Chrome headless trên bản tập dượt. Ca browser dùng tile kiểm và không ghi duyệt/áp dụng/bật điểm; có 0 business write từ Admin trong ca nghe thử. Thử phát chỉ chứng minh bắt đầu phát, không thay bước con người nghe trọn/duyệt.

## Chẩn đoán OSM riêng

Một GET bằng curl tới một tile vẫn lỗi kết nối. Một lần tải ảnh cùng tile trong Chrome từ origin localhost đạt 256×256, có đúng một request; không pan/zoom, quét vùng hoặc tải tile hàng loạt. Vì vậy lỗi curl không chứng minh Chrome không tải được tile. Đây chưa phải bằng chứng toàn bản đồ hoặc Android/iPhone hiển thị ổn định. Giữ [chính sách OSM](https://operations.osmfoundation.org/policies/tiles/) và các ca hồi quy bản đồ dùng fixture; không ghi tile kiểm là nền thật.

## Giới hạn và điểm tiếp tục

Chưa có ngrok/URL HTTPS trong môi trường đã kiểm; chưa tạo QR public, chưa quét bằng điện thoại. Chờ duyệt đúng bản văn, audio và vùng Ngọ Môn để ghi hồ sơ qua Admin, áp dụng cặp và bật điểm. T08 giữ nguyên sáu file nháp chưa commit. Các cặp/ngôn ngữ khác, thiết bị thật, OPS_GUARD/drain/scheduler/backup live và Tailscale/OpenSSH/CD vẫn còn mở.

Không thay hợp đồng API, migration, mã backend, main, issue/Project hoặc checklist 89 ô/20 dấu tích. Không dùng computer use, cài công cụ hệ thống hoặc gọi provider. Hai dịch vụ tập dượt tiếp tục chạy loopback để nhóm rà; nguồn provider đã dừng. Chưa mở tunnel hoặc tuyên bố hoàn thành toàn đồ án.
