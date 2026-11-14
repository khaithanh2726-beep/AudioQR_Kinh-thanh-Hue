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

Mã nguồn được lưu theo các nhánh task và tích hợp qua [main](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/tree/main), giữ lịch sử tác giả G02. Backend có migrations V1–V14, API Khách/Admin, TTS hàng đợi, WAV→MP3, vòng đời dữ liệu/file và gate bảo trì. Frontend có trang Khách, Admin và QR theo URL cấu hình.

Bản phục vụ đã bật **năm địa điểm và 20 cặp văn bản/audio vi/en/fr/ru**. Ngọ Môn vi giữ hồ sơ cũ; 19 cặp mới được chủ đồ án duyệt dùng học phần, khóa đúng revision/Hash. Bản sao và API phục vụ đều kiểm 20/20 bài/MP3 khớp Hash. [Hồ sơ nội dung](docs/05-noi-dung/README.md) giữ phạm vi duyệt và giới hạn chuyên gia/phát âm.

[CI 1bab9a6](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43598895644) đạt 530 backend, 71 logic frontend, 33 ca vận hành và Vue build. Chrome riêng đạt 11/11 ca. Lịch backup/log, CD và restore được ghi tại [bàn giao](docs/01-tong-quan/handoff.md); nguồn Vue/Java và ảnh ứng dụng phục vụ giữ nguyên trong mốc bổ sung công cụ/dữ liệu này.

Kiểm thiết bị của bộ cập nhật và người nhận thực hiện lại hướng dẫn là lượt cuối. Android/iPhone từng dùng Ngọ Môn cơ bản không thay toàn ma trận mẫu máy/OS/browser. Checklist thực tế giữ 62/89; chưa tự tích thêm issue/Project từ việc commit hoặc merge.

## Cấu trúc

```text
frontend/       Giao diện Vue và kiểm thử frontend
backend/        Spring Boot ba lớp, migrations và kiểm thử backend
deploy/         Dockerfile, Compose, cấu hình mẫu và CLI vận hành
.github/        Workflow CI
docs/           Bộ tài liệu mô tả, sử dụng và đánh giá sản phẩm
```

Tài khoản, khóa, database, WAV/MP3 và snapshot runtime được lưu ngoài repository. Báo cáo/log/ảnh kiểm thử chi tiết cùng nguyên bản tài liệu dài được gom vào hồ sơ nội bộ riêng; bộ chấm giữ báo cáo tổng hợp và liên kết nguồn kiểm chứng. Cách tổ chức này không xóa mã kiểm thử hoặc thay đổi chức năng.
