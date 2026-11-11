# Diễn tập đóng gói và backup/restore tách biệt

Các công cụ tại đây phục vụ kiểm thử, không phải quy trình backup môi trường đang phục vụ. Thiết kế BKP-P02 yêu cầu OPS_GUARD và drain có bằng chứng; nguồn hiện chưa có gate đó. Không dùng việc script chạy đạt để tự bật backup hoặc scheduler live.

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
