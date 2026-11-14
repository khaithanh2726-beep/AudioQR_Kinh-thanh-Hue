# Chạy thử một địa điểm

Mốc này kiểm Admin lưu và áp dụng vùng, nghe thử MP3, ghi hồ sơ, áp dụng cặp tiếng Việt và bật địa điểm. Khách bắt đầu phiên, chọn vùng, đọc và nghe được bản đã áp dụng. Bản chấm vẫn cần 20 văn bản và 20 audio khớp nhau.

## Chuẩn bị môi trường riêng

Chạy các lệnh trên máy thử. Cần Java 21, Docker Desktop đang hoạt động, Node/npm theo [bộ phiên bản đã chốt](../../01-tong-quan/technical-inputs.md), và FFmpeg/ffprobe cho bước chuyển WAV. Kiểm cổng 33316, 8080 và 5173 đang rảnh trước khi khởi động.

Tại thư mục dự án, đặt mật khẩu MySQL riêng cho phiên terminal. Ví dụ này dùng image MySQL 8.4.13 như bộ test và chỉ mở cổng trên loopback. Tên container và volume dành riêng cho lần chạy thử.

```powershell
$root = (Get-Location).Path
$smoke = Join-Path $root 'docs/07-van-hanh/chay-thu'
$password = Read-Host 'Mật khẩu MySQL thử' -AsSecureString
$env:HUEQR_SMOKE_DB_PASSWORD = [Net.NetworkCredential]::new('', $password).Password
$env:MYSQL_PASSWORD = $env:HUEQR_SMOKE_DB_PASSWORD
docker run -d --name hueqr-smoke-mysql -p 127.0.0.1:33316:3306 --env MYSQL_RANDOM_ROOT_PASSWORD=yes --env MYSQL_DATABASE=hueqr_smoke --env MYSQL_USER=hueqr_smoke --env MYSQL_PASSWORD --mount type=volume,source=hueqr-smoke-mysql-data,target=/var/lib/mysql mysql:8.4.13
Remove-Item Env:MYSQL_PASSWORD
```

Đợi MySQL chấp nhận đăng nhập bằng `docker exec -it hueqr-smoke-mysql mysql -u hueqr_smoke -p hueqr_smoke`. Dùng một kho audio thử mới. Giữ nguyên khóa, epoch và đường dẫn trong cùng đợt thử, kể cả khi dừng rồi chạy lại backend.

```powershell
$bytes = New-Object byte[] 32
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()
$env:HUEQR_GUEST_SIGNING_KEY_BASE64 = [Convert]::ToBase64String($bytes)
$env:HUEQR_SERVICE_EPOCH = [guid]::NewGuid().ToString()
$env:HUEQR_AUDIO_ROOT = (New-Item -ItemType Directory -Path (Join-Path $env:TEMP ('hueqr-smoke-audio-' + [guid]::NewGuid()))).FullName
$env:HUEQR_SMOKE_LOCAL_AUDIO = 'false'
$env:SPRING_CONFIG_ADDITIONAL_LOCATION = ([uri](Join-Path $smoke 'application-smoke.properties')).AbsoluteUri
Set-Location (Join-Path $root 'backend')
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--spring.datasource.url=jdbc:mysql://127.0.0.1:33316/hueqr_smoke --spring.datasource.username=hueqr_smoke --spring.datasource.password=${HUEQR_SMOKE_DB_PASSWORD} --server.address=127.0.0.1 --server.port=8080'
```

[Cấu hình mẫu](application-smoke.properties) trỏ đến schema thử, tắt provider, cleaner và retention. Lượt đầu để chuyển đổi local tắt. Xác nhận Flyway đã áp V1–V12 vào `hueqr_smoke` và backend nghe trên 127.0.0.1:8080. Giữ biến môi trường chứa mật khẩu và khóa ngoài Git.

Ở terminal backend thứ hai, tạo classpath và sinh mã Hash cho mật khẩu Admin thử. Nhập mật khẩu tại prompt, sau đó dùng mã Hash có tiền tố thuật toán trong câu SQL bên dưới.

```powershell
$classpathFile = Join-Path $env:TEMP ('hueqr-smoke-classpath-' + [guid]::NewGuid() + '.txt')
.\mvnw.cmd dependency:build-classpath "-Dmdep.outputFile=$classpathFile"
$classpath = (Get-Content $classpathFile -Raw).Trim()
java --class-path $classpath src\test\fixtures\smoke\SmokePassword.java
```

Chạy SQL trong MySQL thử sau khi Flyway tạo bảng. Dùng tên tài khoản `smoke-admin` vì fixture bên dưới dùng tên này.

```sql
INSERT INTO admin_account (id, login_name, password_hash, enabled)
VALUES (UUID_TO_BIN(UUID()), 'smoke-admin', '<mã Hash vừa sinh>', TRUE);
```

Ở terminal frontend, chạy `npm install`, `npm test`, `npm run build`, rồi `npm run dev` từ thư mục `frontend`. Vào `http://localhost:5173/admin.html` và `http://localhost:5173/`. Kiểm thực tế cookie Secure và CSRF qua proxy cùng origin. Nếu trình duyệt không giữ cookie Secure tại localhost, dùng HTTPS loopback cho frontend thử. Giữ nguyên các thuộc tính bảo vệ cookie.

## Chuẩn bị đúng một cặp

1. Đăng nhập Admin và tạo một địa điểm ẩn. Vẽ hoặc nhập polygon thử, xem trước, lưu và áp dụng đúng revision vùng. Dùng tọa độ được nhóm cho phép hoặc polygon ghi rõ là dữ liệu kiểm thử. Lưu một văn bản tiếng Việt trong số 20 bản và ghi lại mã revision. Thử bật địa điểm ở bước này để kiểm gate thiếu cặp.
2. Thu một WAV đọc đúng bản đã lưu, hoặc dùng WAV hợp lệ đã có. Chuẩn hóa bằng `ffmpeg -i "<tệp nguồn>" -ac 1 -ar 24000 -c:a pcm_s16le "<tệp WAV thử>"`. Kiểm nghe và đối chiếu nội dung. Backend cần WAV RIFF PCM signed 16-bit, mono, 24 kHz.
3. Dừng backend thử. Sao chép WAV vào kho thử với tên mới `<UUID>.wav`. Tính Hash SHA-256, số byte và thời lượng từ số mẫu của WAV đó. Ví dụ dưới đây in các biến SQL cho fixture. Thay hai giá trị đầu bằng đường dẫn WAV và mã revision từ Admin.

```powershell
$sourceWav = '<tệp WAV thử>'
$textRevisionId = [guid]'<mã revision tiếng Việt>'
$wavId = [guid]::NewGuid()
$wav = Join-Path $env:HUEQR_AUDIO_ROOT "$wavId.wav"
Copy-Item -LiteralPath $sourceWav -Destination $wav
$metadata = ffprobe -v error -select_streams a:0 -show_entries stream=codec_name,channels,sample_rate,duration_ts,time_base -of json $wav | ConvertFrom-Json
$stream = $metadata.streams[0]
if ($stream.codec_name -ne 'pcm_s16le' -or $stream.channels -ne 1 -or $stream.sample_rate -ne '24000' -or $stream.time_base -ne '1/24000' -or [long]$stream.duration_ts -le 0) {
    throw 'WAV không đúng profile chạy thử.'
}
$duration = [Math]::Round(([decimal]$stream.duration_ts / 24000), 9, [MidpointRounding]::AwayFromZero)
$seconds = $duration.ToString([Globalization.CultureInfo]::InvariantCulture)
$hash = (Get-FileHash -LiteralPath $wav -Algorithm SHA256).Hash.ToLowerInvariant()
$size = (Get-Item -LiteralPath $wav).Length
"SET @smoke_text_id = '$textRevisionId', @smoke_wav_id = '$wavId', @smoke_wav_hash = '$hash', @smoke_wav_size = $size, @smoke_wav_duration = $seconds;"
```

4. Dùng tài khoản MySQL `hueqr_smoke` trong schema `hueqr_smoke`. Chạy dòng `SET` vừa sinh rồi chạy [fixture WAV](../../../backend/src/test/fixtures/smoke/stage-smoke-wav.sql) trong cùng kết nối. Terminal MySQL chạy trong container nên cần dán nội dung SQL hoặc chép tệp vào container trước khi dùng lệnh `source`. Fixture chỉ nhận nháp vi của điểm ẩn và chưa có job. Kết quả phải trả một mã job. `NO_FIXTURE` yêu cầu đối chiếu schema, tài khoản, revision và các biến đã nhập. Nếu mysql báo lỗi, rollback hoặc thoát kết nối trước khi thử lại.
5. Đặt `$env:HUEQR_SMOKE_LOCAL_AUDIO = 'true'` trong terminal backend, giữ các biến của lượt đầu rồi chạy lại Maven. Cấp `HUEQR_FFMPEG_PATH` và `HUEQR_FFPROBE_PATH` nếu executable nằm ngoài PATH. Chờ job đạt `SUCCEEDED` và mở lại trang Nội dung. Có thể đối chiếu bằng `GET /api/v1/admin/tts-jobs/{jobId}` trong phiên Admin hợp lệ. Nếu thất bại, ghi phase và mã lỗi. V12 tạo MP3 và đăng ký audio theo đúng revision của WAV thử.
6. Chọn audio, tải để nghe thử, ghi hồ sơ văn bản và audio theo bằng chứng thực tế. Áp dụng cặp, sau đó bật địa điểm. Mở trang khách và thực hiện các ca dưới đây.

## Ca kiểm và bằng chứng cần lưu

| Ca | Thao tác | Kết quả mong đợi |
|---|---|---|
| SM-01 | Lưu và áp dụng vùng trên địa điểm ẩn, chưa có cặp vi. Bấm bật. | Backend từ chối bật và giao diện báo thiếu cặp vi. Khách tải bản đồ chưa thấy điểm. |
| SM-02 | Chuyển WAV thử qua V12. Admin chọn audio, tải nghe thử, ghi hai hồ sơ và áp dụng cặp. | Job `SUCCEEDED` có đúng text/audio revision. MP3 phát được. Sau khi áp dụng, điểm vẫn ẩn cho đến thao tác bật. |
| SM-03 | Bật điểm. Khách mở trang, bắt đầu phiên vi và chọn vùng. | Hiện đúng văn bản, MP3 tải đủ và phát được, chỉ có một lượt cho lần mở đủ điều kiện ACK. Dùng nút Phát nếu trình duyệt chặn tự phát. |
| SM-04 | Trong phiên thử mới, dùng DevTools chặn riêng yêu cầu `/content-grants/{id}/audio` sau khi điểm đã sẵn sàng. | Văn bản vẫn đọc được, giao diện báo lỗi audio. Bỏ chặn và thử lại audio còn hạn mà không mở lại điểm. |
| SM-05 | Đổi bản văn sau khi cặp cũ đã áp dụng. Đối chiếu bản nháp, audio và phía khách. | Tham chiếu cũ thuộc bản cũ. Audio cũ không được gắn vào nháp mới. Khách vẫn nhận cặp đã áp dụng. |

Ghi thời điểm, phiên bản tool, kết quả `npm test`/build, mã place/region/text/audio/job và ảnh hoặc log phù hợp của từng ca. Ghi trạng thái chưa chạy khi còn thiếu điều kiện. Dùng [bảng đối chiếu 20 bản](../../05-noi-dung/doi-chieu-van-ban-audio.md) cho lần mở rộng tiếp theo.

[Bằng chứng chuẩn bị nguồn](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/chay-thu-mot-dia-diem.json) ghi 54 ca logic và kiểm cú pháp tại mốc trước. [Hồ sơ chạy local ngày 11/11](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/local-20261111/README.md) ghi lượt npm/build/Chrome với API và MP3 thật, có thao tác tương ứng SM-01 đến SM-05. Đọc phạm vi assertion trong hồ sơ; không coi hai kịch bản tự động là nghiệm thu toàn bộ ma trận hoặc bộ 20 cặp. Thiết bị thật vẫn cần bổ sung bằng chứng.
