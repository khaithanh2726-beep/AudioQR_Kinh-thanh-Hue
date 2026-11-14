# Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế

Đồ án học phần xây dựng hệ thống để khách quét một mã QR chung, bắt đầu phiên trong tab, chọn vùng trên bản đồ và đọc/nghe thuyết minh. Admin quản lý địa điểm, vùng, văn bản/audio, cặp nội dung, phiên tham quan và thống kê.

Nhóm thực hiện: **Thành, Hòa và Nghĩa**. Kiến trúc: **Vue → Spring Boot Controller → Service → Repository → MySQL/Flyway**. Vue được đóng gói cùng backend để frontend/API dùng chung origin; audio được lưu ở kho file riêng.

- [Mục lục tài liệu chấm](docs/README.md)
- [Yêu cầu hệ thống](docs/02-nghiep-vu/PRD.md) · [Quy trình nghiệp vụ](docs/02-nghiep-vu/business-flow.md)
- [Thiết kế kỹ thuật](docs/03-ky-thuat/TDD.md) · [Thiết kế giao diện](docs/04-giao-dien/ui-design.md)
- [Cài đặt và chạy](docs/07-van-hanh/dong-goi-va-ci.md) · [Sử dụng và trình diễn](docs/07-van-hanh/tap-duot.md)
- [Kết quả kiểm thử](docs/06-kiem-thu/README.md) · [Hiện trạng bàn giao](docs/01-tong-quan/handoff.md)
- [Tra chức năng → file/dòng](docs/07-van-hanh/tra-cuu-chuc-nang.md) · [Ma trận truy vết](docs/06-kiem-thu/ma-tran-truy-vet.md)

## Sản phẩm và hiện trạng

Mã nguồn đã tích hợp vào main qua [PR #15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/pull/15), mốc `601c09a`. Backend có migrations V1–V14, API Khách/Admin, TTS hàng đợi, chuyển WAV→MP3, vòng đời dữ liệu/file và gate bảo trì. Frontend có trang Khách, trang Admin và QR theo URL cấu hình.

Đã có lượt full backend 530/530 đạt; 71/71 logic frontend, build và các lượt Chrome/API/MP3 riêng có bằng chứng. Người kiểm đã báo sử dụng Ngọ Môn vi thành công trên Android và iPhone. Checklist được đối chiếu 62/89; số ô không biểu thị phần trăm hoàn thành đồ án.

Bản tập dượt phục vụ Ngọ Môn tiếng Việt ở schema V14; đã kiểm snapshot/restore V13 trước nâng, backup/restore V14 và giữ nguyên vùng/cặp/audio khi chuyển. Hai scheduler dọn dữ liệu đã bật; TTS vẫn tắt; CD GitHub đã đạt. Backup Windows lúc 23:00 đã đăng ký và chạy thử qua Task Scheduler đạt, có restore riêng; báo cáo vận hành giữ bảy ngày, Docker giới hạn log 10 MB × 3 tệp/container. Bốn vùng khác và 19 audio mới ở môi trường nội dung riêng: đã đủ 20 văn bản/20 audio kỹ thuật, backup/restore 40 WAV/MP3 đạt. D09/nghe duyệt bộ mới và phát âm Thái Hòa fr còn chờ. Bộ 20 cặp và toàn bộ nghiệm thu chưa hoàn tất.

## Cấu trúc

```text
frontend/       Giao diện Vue và kiểm thử frontend
backend/        Spring Boot ba lớp, migrations và kiểm thử backend
deploy/         Dockerfile, Compose, cấu hình mẫu và CLI vận hành
.github/        Workflow CI
docs/           Bộ tài liệu mô tả, sử dụng và đánh giá sản phẩm
```

Tài khoản, khóa, database, WAV/MP3 và snapshot runtime được lưu ngoài repository. Báo cáo/log/ảnh kiểm thử chi tiết cùng nguyên bản tài liệu dài được gom vào hồ sơ nội bộ riêng; bộ chấm giữ báo cáo tổng hợp và liên kết nguồn kiểm chứng. Cách tổ chức này không xóa mã kiểm thử hoặc thay đổi chức năng.
