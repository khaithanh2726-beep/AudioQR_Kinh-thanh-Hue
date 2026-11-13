# Diễn tập đóng gói và backup/restore tách biệt

## Bộ kiểm gate V14

`maintenance-rehearsal.mjs` chỉ nhận bản mới do `deploy/prepare-rehearsal.mjs` tạo từ bộ provider đã dừng, dùng ảnh có V14, năm điểm ẩn/20 slot và một audio thật. Không dùng bản Ngọ Môn đã áp dụng/hiển thị. Chạy từ repo:

```powershell
node backend/src/test/fixtures/operations/maintenance-rehearsal.mjs '<runtime-thu-moi>' '<backup-moi>' '<restore-moi>' '<anh-V14>'
node --test backend/src/test/fixtures/operations/operations-backup.test.mjs
```

Ca kiểm tạo đúng một lease fixture để xác nhận chặn freeze, sai owner và không tháo theo tuổi; chỉ xóa lease fixture do chính ca tạo. Sau đó kiểm freeze/backup/resume giữ khóa và epoch; restore sang project mới, gate đóng, Hash audio đích và epoch mới. Bốn fixture phiên/lượt được thêm **vào đích restore**, lọc ba ca hết hạn và giữ nguyên mốc ca còn hạn. Browser chạy riêng bằng `operations-restore.spec.mjs`. Khi đạt công cụ giữ hai bộ thử để kiểm browser; dừng đúng các container ghi trong runtime sau kiểm. Khi lỗi giữ gói/volume và dừng các container thử đã biết, không dọn Docker toàn cục.

[Hướng dẫn công cụ V14](../../../../../docs/07-van-hanh/bao-tri-va-sao-luu.md) tách với các bộ offline cũ bên dưới. Các giới hạn của bộ offline không phủ định kết quả gate mới, và gate mới không tự nâng phạm vi bằng chứng cũ.

`url-gate-rehearsal.mjs` chỉ nhận project restore riêng, kiểm bản sửa URL rồi backup/restore lại với ảnh đúng ID. `scheduler-rehearsal.mjs '<runtime-restore-rieng>'` bật cleanup/retention trên đích restore, không bật TTS; thêm bốn phiên/lượt và hai asset synthetic riêng, kiểm gate chặn quét rồi xử lý sau mở. Không dùng hai asset đó làm audio thuyết minh. Cuối lượt dừng dịch vụ, khôi phục private.env và recreate app ở trạng thái dừng với cờ worker ban đầu. Không chạy bộ này trên bản Ngọ Môn đang phục vụ.

Các bộ offline cũ ở những mục bên dưới phục vụ kiểm thử, không có fence/drain của V14. Không dùng việc các script offline chạy đạt để tự bật backup hoặc scheduler live; kết quả gate mới được ghi riêng theo bộ kiểm và ảnh tương ứng.

## Diễn tập Compose và quay lại ảnh trước

`compose-rehearsal.mjs` nhận thư mục đầu ra mới ngoài repository và hai ảnh `hueqr-local:*` đã có trên máy, có ID khác nhau. Chạy trên Windows với Docker Desktop; không tải hoặc build ảnh trong lượt này.

```powershell
node backend/src/test/fixtures/operations/compose-rehearsal.mjs '<thu-muc-moi-ngoai-repo>' '<anh-moi>' '<anh-truoc>'
```

Công cụ tạo project `hueqr-compose-check-*`, database/kho audio riêng, cổng loopback còn trống và cấu hình riêng có ACL cho tài khoản chạy/SYSTEM. Provider, converter, cleaner và retention đều tắt. Các chặng kiểm gồm seed hai lần, restart hai dịch vụ, thay ảnh ứng dụng và quay lại ảnh trước; đối chiếu 5 điểm ẩn/20 slot/0 cặp, migration, volume, khóa/epoch, tệp checkpoint và hai trang web. Tệp checkpoint chỉ kiểm độ bền của volume, không thay bằng chứng phát MP3.

Chỉ dùng hai ảnh tương thích cùng schema để diễn tập này; không coi kết quả là cho phép hạ migration hoặc rollback mọi phiên bản. `report.json` không chứa credential. `private.env` chứa khóa/mật khẩu thử, giữ ngoài Git; không chia sẻ kết quả `docker inspect` hoặc `compose config` đầy đủ. Cuối lượt công cụ dừng đúng hai dịch vụ, giữ container/volume để rà, không gọi `down -v` hoặc prune. Nếu `servicesStopped` không đúng, dừng đúng project ghi trong báo cáo trước khi tiếp tục.

## Đầu vào

- Node 24.22.2, Docker đang hoạt động, ảnh `mysql:8.4.13` và nguồn chạy thử riêng theo `frontend/tests/e2e/README.md`.
- Thư mục runtime chứa `runtime.json` của container `hueqr-e2e-*`, schema/tài khoản `hueqr_smoke`, mật khẩu thử và đường dẫn audio. Không nhận môi trường đang vận hành.
- Dừng backend/worker trước chạy. Công cụ từ chối khi còn tiến trình backend Java, task/claim, file DELETING, job chưa terminal hoặc kết nối khác của tài khoản DB. Đây là kiểm hỗ trợ trong môi trường thử được kiểm soát; không fence được writer mới như OPS_GUARD.
- Thư mục đầu ra mới ngoài repository và kho audio nguồn, nằm trong thư mục cha đã giới hạn ACL cho tài khoản thực hiện. Gói chứa dump, Hash credential và mật khẩu thử trong `private-runtime.json`; giữ riêng, không đưa lên Git hoặc hồ sơ chia sẻ.

## Kiểm và diễn tập

Từ thư mục gốc dự án:

```powershell
node --test backend/src/test/fixtures/operations/backup-manifest.test.mjs
node backend/src/test/fixtures/operations/backup-restore.mjs '<thu-muc-runtime-nguon>' '<thu-muc-dau-ra-moi>'
```

Công cụ chỉ đọc database/kho audio nguồn. Tạo dump InnoDB và catalog audio đang được tham chiếu, kiểm kích thước/Hash, ghi hạn bảy ngày; từ chối gói hỏng/hết hạn trước import. Snapshot chỉ nhận job terminal, zero claim/task. Dữ liệu chưa terminal phải được đối chiếu bằng chặng vận hành khác, không sửa phase để vượt kiểm.

Restore vào container `hueqr-restore-*` mới không publish cổng. Đối chiếu Flyway, catalog và số lượng với snapshot; sau đó thêm bốn phiên/lượt fixture vào **đích restore** để kiểm retention. Ba ca quá hạn bị xóa, ca còn hạn được giữ cùng mốc gốc. Do đó số phiên/lượt cuối có thể nhiều hơn snapshot một dòng, không coi là lỗi đối chiếu import. `restore-filter.sql` thu hồi context cũ, không đổi started/expires/endedAt của phiên được giữ. Epoch mới và khóa runtime giữ riêng ngoài gói manifest.

Kết quả `report.json` là diễn tập offline. Container restore được dừng trong `finally`; công cụ không xóa container, volume, dữ liệu nguồn hoặc gói kết quả. Khi thất bại giữ `FAILED.txt` và dùng thư mục đầu ra mới cho lượt tiếp theo. File lock chỉ bảo vệ hai lượt công cụ cùng runtime, không chặn ứng dụng live.

## Kiểm media Linux và ứng dụng sau restore

Build `deploy/Dockerfile` với tag `hueqr-local:20261111` trước. WAV đầu vào `vi-01.wav` phải là bản Ngọ Môn thật đã kiểm profile, giữ ngoài repository.

```powershell
node backend/src/test/fixtures/operations/linux-media.mjs '<thu-muc-WAV>' '<thu-muc-ket-qua-media>'
node backend/src/test/fixtures/operations/packaged-runtime.mjs '<thu-muc-dau-ra-restore>' '<thu-muc-runtime-nguon>'
```

Kiểm media chạy container không có mạng, tài khoản không phải root; chuyển WAV ra pipe MP3, probe và giải mã toàn tệp có đường dẫn Unicode. `packaged-runtime.mjs` khởi động database restore và ứng dụng riêng trên **127.0.0.1:18081**, provider/worker/cleaner/retention đều tắt. Nó thêm một phiên fixture còn hạn với epoch cũ để kiểm HTTP từ chối, không sửa dữ liệu nguồn. Không dùng cổng này làm môi trường công khai.

Tại `frontend`, đặt `HUEQR_PACKAGED_RUNTIME` tới thư mục restore, `HUEQR_E2E_URL=http://localhost:18081`, `HUEQR_E2E_OUTPUT` tới thư mục kết quả mới; chạy `npm run test:e2e -- packaged.spec.mjs`. Chrome dùng bản cài sẵn theo cấu hình Playwright. Không đặt `HUEQR_E2E_RUNTIME` của suite tạo dữ liệu nếu chỉ chạy ca packaged.

Sau kiểm, đọc `browser-runtime.json` để xác định đúng hai container thử rồi dừng chúng bằng tên đã đối chiếu; giữ kết quả và dữ liệu để rà. Không chạy lệnh dọn toàn Docker. Nếu khởi động ứng dụng lỗi, container thử được giữ để kiểm log và cần dừng trước chạy lại. Không chép `browser-runtime.json`, `private-runtime.json` hoặc `database.sql` vào hồ sơ chung.
