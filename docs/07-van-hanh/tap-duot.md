# Bản tập dượt riêng

Mốc HTTPS Ngọ Môn: trang bắt đầu `https://kiwi-situated-outboard.ngrok-free.dev/`, Admin tại `/admin.html`. [QR chung](qr-tap-duot.png) mở trang bắt đầu, không tự tạo phiên hoặc chọn điểm. Hiện chỉ bật Ngọ Môn vi theo [hồ sơ bộ đã rà](../05-noi-dung/duyet-ngo-mon-tap-duot-20261112.md); các điểm/ngôn ngữ khác chưa đủ hồ sơ/cặp. Đây là bản tập dượt một điểm, chưa nghiệm thu toàn bộ 20 cặp và vận hành.

Bản tập dượt dùng trang Khách và Admin thật, database/volume riêng, tài khoản quản trị và khóa runtime riêng. Nó tách khỏi preview 18082 và các lượt E2E. Chuẩn bị dữ liệu không tự duyệt văn bản, vùng hoặc audio. Chỉ mở địa điểm sau khi nhóm xác nhận đúng bản và lưu hồ sơ qua Admin.

## Chuẩn bị từ lượt provider đã kiểm

`deploy/prepare-rehearsal.mjs` chỉ nhận thư mục của lượt kiểm một bài provider thật đã đạt, gồm `provider-runtime.json`, `provider-result.json`, `provider-verification.json` và hai tệp WAV/MP3 ngoài repository. Cả hai container nguồn phải đang dừng. Công cụ từ chối nguồn có phiên khách, cặp/điểm hiển thị, hồ sơ duyệt, claim/task hoặc trạng thái khác bộ nguồn một job đã xác định.

Yêu cầu: Windows, Node 24.22.2, Java 21 trong PATH, Docker Desktop; phụ thuộc Spring Security crypto 7.1.2/commons-logging 1.3.6 đã resolve trong Maven local. Không tự cài công cụ hoặc đổi phụ thuộc. Ảnh Linux được build từ `deploy/Dockerfile`, mặc định `VITE_MAP_PREVIEW=false`; cổng loopback 18083 phải rảnh.

```powershell
docker build --pull=false --build-arg VITE_MAP_PREVIEW=false -f deploy/Dockerfile -t hueqr-local:20261112-rehearsal .
node deploy/prepare-rehearsal.mjs '<runtime-provider-da-kiem>' '<thu-muc-moi-ngoai-repo>' 'hueqr-local:20261112-rehearsal'
```

Công cụ chỉ bật MySQL nguồn để đọc và dump bộ thử đứng yên, sau đó dừng lại; đây không phải công cụ backup live hoặc thay thế OPS_GUARD. Đích mới có project `hueqr-rehearsal-*`, schema/user `hueqr`, hai volume mới, không publish MySQL. Chỉ nhập vào schema trống. Hai file audio được đối chiếu kích thước/Hash trước và sau sao chép, giữ revision/job thật cùng lịch sử; không tạo job hoặc kết quả provider giả.

Tài khoản nguồn bị vô hiệu hóa trên **đích**, giữ lịch sử; tạo `hue-admin` có mật khẩu BCrypt ngẫu nhiên. Khóa ký và epoch mới, cấu hình lưu ổn định trong `private.env`; thông tin đăng nhập ở `runtime.json`. Cả hai nằm trong thư mục ngoài repository có ACL cho tài khoản chạy và SYSTEM. Không chép vào Git, tài liệu hoặc chat. `preparation.json` chỉ chứa metadata/kết quả được phép chia sẻ.

Ứng dụng khởi động tại `http://localhost:18083`, Admin tại `/admin.html`. Provider, converter, cleaner và retention đều tắt. `/map-preview.html` phải trả 404; bản xem vùng đề xuất giữ riêng tại 18082. Nếu lỗi, công cụ dừng dịch vụ đích đã tạo, giữ dữ liệu/báo cáo để rà; không chạy lại với cùng thư mục đầu ra, không xóa volume hoặc sửa nguồn để vượt guard.

## Rà trước khi mở điểm

Đăng nhập bằng thông tin ở tệp riêng. Đối chiếu năm điểm/20 slot, bản Ngọ Môn vi và audio Gemini đã giữ nguyên Hash. Các điểm vẫn ẩn và chưa có cặp. Nghe trọn audio, rà bản văn và vùng đề xuất; ghi tham chiếu/giới hạn theo xác nhận thực tế của nhóm. Lưu/áp dụng vùng, ghi hồ sơ văn bản/audio, áp dụng cặp và bật đúng điểm theo hợp đồng hiện hành. Không dùng `smoke-review` hoặc tên ca kiểm làm bằng chứng duyệt.

Chạy kiểm sẵn có khi chưa áp dụng dữ liệu:

```powershell
$env:HUEQR_REHEARSAL_RUNTIME = '<thu-muc-runtime-tap-duot>'
$env:HUEQR_E2E_URL = 'http://localhost:18083'
$env:HUEQR_E2E_OUTPUT = '<thu-muc-ket-qua-moi-ngoai-repo>'
# Chạy tại frontend, dùng npm.cmd nếu PowerShell chặn npm.ps1
npm.cmd run test:e2e -- rehearsal.spec.mjs
```

Ca này kiểm form Khách, trang preview vắng, đăng nhập Admin, tải/Hash/phát audio đã bảo quản và gate chưa có hồ sơ. Không gọi TTS, không duyệt/áp dụng/bật điểm và không chứng minh QR hoặc OSM thật. Nó dành cho trạng thái chuẩn bị ban đầu; sau khi cấu hình URL/áp dụng dữ liệu phải dùng ca kiểm phù hợp trạng thái mới.

## HTTPS và QR cho buổi tập

Cài/liên kết ngrok theo [hướng dẫn](dong-goi-va-ci.md#chuẩn-bị-ngrok-trên-windows). Khi đã kiểm đúng app 18083 và dữ liệu được phép phục vụ, dùng `ngrok http 18083`. Không mở tunnel vào preview hoặc database thử. Giữ token ngoài repository và chat.

Sau khi có URL HTTPS thật, đặt `HUEQR_PUBLIC_START_URL` trong cấu hình riêng, build ảnh mới nhận URL đó qua `VITE_PUBLIC_START_URL`, rồi cập nhật **chỉ dịch vụ application** của đúng project. Giữ database/audio, mật khẩu, epoch và khóa ký. Kiểm cookie Secure/HttpOnly/SameSite và CSRF qua HTTPS trước phát QR. Admin → QR chung → tải PNG/in; quét trên Android/Chrome và iPhone/Safari từ mạng ngoài laptop.

Với ngrok free, đặt `HUEQR_NGROK_SKIP_BROWSER_WARNING=true` khi build bản tập dượt; Docker chuyển thành `VITE_NGROK_SKIP_BROWSER_WARNING`. Client Khách chỉ thêm header công khai `ngrok-skip-browser-warning: 1` cho API cùng origin HTTPS khớp URL đã cấu hình, vẫn dùng `credentials: omit` và header credential riêng. Không gửi cookie Admin cho API Khách. Mặc định cờ tắt cho các môi trường khác. Cấu hình này xử lý trường hợp ngrok trả trang cảnh báo thay JSON cho request không có cookie, theo [hướng dẫn client headers của ngrok](https://ngrok.com/docs/pricing-limits/free-plan-limits#using-headers). Trang HTML đầu tiên vẫn có thể hiện Visit Site; người dùng xác nhận để vào form.

Một điểm vi là mốc tập dượt ban đầu. Bộ chấm vẫn yêu cầu 20 cặp được duyệt, các ca thiết bị, vòng đời và vận hành/CD theo [điều kiện nghiệm thu](nghiem-thu-van-hanh.md). Cấu hình ngrok/QR không tự giải quyết kết nối tới tile OSM trên máy khách; cần kiểm nền thật riêng.

## Dừng và khởi động lại

Máy ngừng chạy hoặc Docker/ngrok dừng sẽ làm URL không phục vụ được. Khi bắt đầu buổi tập, mở Docker Desktop rồi dùng đúng cấu hình đã chuẩn bị:

```powershell
$rehearsalDirectory = '<thu-muc-runtime-tap-duot>'
$rehearsalState = Get-Content (Join-Path $rehearsalDirectory 'runtime.json') -Raw | ConvertFrom-Json
docker compose -p $rehearsalState.project --env-file $rehearsalState.configPath -f deploy/compose.yaml -f (Join-Path $rehearsalDirectory 'limits.yaml') up -d --no-build --pull never
ngrok http 18083 --inspect=false
```

Giữ cửa sổ ngrok hoạt động, đối chiếu dòng Forwarding đúng URL trên QR. Nếu URL thay đổi, cập nhật/build và xuất QR mới trước buổi tập. Không gửi token hoặc toàn bộ tệp runtime cho người xem demo. Kiểm một lượt Khách qua mạng ngoài laptop trước khi trình diễn; mật khẩu Admin chỉ cấp cho người quản trị nhóm.

Đọc `project` và `configPath` từ `runtime.json`. Từ thư mục repo, dùng đúng project, `--env-file`, `deploy/compose.yaml` và `limits.yaml` đã tạo. `docker compose ... stop` dừng hai dịch vụ; `docker compose ... up -d --no-build --pull never` dùng lại ảnh/cấu hình/volume. Không dùng `down -v`, prune, tạo lại epoch hoặc chạy bootstrap lần nữa chỉ để khởi động lại.

Môi trường này chưa có OPS_GUARD/drain/backup live đã nghiệm thu. Giữ worker tắt và không cập nhật trong lúc người dùng đang thao tác; không coi dừng container là bằng chứng provider đã hủy hoặc mọi file writer đã kết thúc. Các bước bảo trì đầy đủ vẫn theo thiết kế vận hành.

V13 sửa lưu tọa độ vùng thành JSON nguyên dạng trong LONGTEXT có CHECK. Không rollback ảnh trước V13 trên schema đã nâng chỉ vì app khởi động lỗi; bản sao riêng trước cập nhật giữ ngoài repo, cần đối chiếu kế hoạch restore và tương thích schema. Vùng cũ bị JSON làm lệch chữ số không tự được sửa: lưu lại hình gốc đã duyệt thành revision mới, giữ lịch sử và receipt cũ. [Mô hình dữ liệu](../03-ky-thuat/data-model.md#model-region-precision) ghi chi tiết.
