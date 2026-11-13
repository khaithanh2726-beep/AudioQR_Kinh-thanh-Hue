# Bộ tài liệu đồ án

Bộ tài liệu mô tả yêu cầu, thiết kế, dữ liệu, giao diện, cách chạy và kết quả đánh giá của hệ thống thuyết minh QR Kinh thành Huế. Nội dung được tổ chức theo chủ đề; lịch sử ngắn gọn tách khỏi phần mô tả sản phẩm.

## Thứ tự đọc

1. [Tổng quan và hiện trạng](01-tong-quan/handoff.md), [phân công/checklist](01-tong-quan/roadmap.md).
2. [Quy trình nghiệp vụ](02-nghiep-vu/business-flow.md), [PRD](02-nghiep-vu/PRD.md), [quy tắc](02-nghiep-vu/decisions.md).
3. [Kiến trúc](03-ky-thuat/TDD.md), [mô hình dữ liệu](03-ky-thuat/data-model.md), [API/giao dịch](03-ky-thuat/api-transactions.md), [vòng đời/backup](03-ky-thuat/operations-design.md), [DFD](03-ky-thuat/dfd.md).
4. [Thiết kế giao diện](04-giao-dien/ui-design.md) và [bộ nội dung](05-noi-dung/README.md).
5. [Kết quả kiểm thử](06-kiem-thu/README.md), [cài đặt/đóng gói](07-van-hanh/dong-goi-va-ci.md), [sử dụng/trình diễn](07-van-hanh/tap-duot.md), [bảo trì/restore](07-van-hanh/bao-tri-va-sao-luu.md).

## Vai trò của từng nhóm

| Thư mục | Nội dung chính |
|---|---|
| 01-tong-quan | Hiện trạng, phân công, checklist và công nghệ đang dùng |
| 02-nghiep-vu | Business Flow, PRD, Use Case, quy tắc và giới hạn nghiệm thu |
| 03-ky-thuat | TDD, ERD, DFD, API/DTO, giao dịch và vận hành |
| 04-giao-dien | Các màn hình Khách/Admin và tài sản bản đồ/thiết kế |
| 05-noi-dung | Nguồn văn bản, bundle v02, hồ sơ vùng/cặp và trạng thái dữ liệu |
| 06-kiem-thu | Báo cáo tổng hợp, phạm vi bằng chứng và điều kiện chưa đạt |
| 07-van-hanh | Chuẩn bị môi trường, chạy, thao tác sử dụng và backup/restore |
| 99-luu-tru | Một bản lịch sử ngắn theo mốc, không lặp lại đặc tả |

## Phạm vi hiện tại

Mốc mã nguồn đã lưu trên main là `601c09a`, PR #15. Checklist hiện 62/89. Bản Ngọ Môn vi đã dùng tập dượt HTTPS; còn bộ dữ liệu/audio chưa duyệt đủ và các điều kiện vận hành cuối. [Hiện trạng](01-tong-quan/handoff.md) là nơi chính ghi các giới hạn này; đặc tả không được hiểu thành kết quả nghiệm thu.

Mã kiểm thử giữ trong backend/frontend. Log, XML, ảnh và JSON bằng chứng chi tiết đã được tách sang hồ sơ nội bộ riêng; các báo cáo tổng hợp dẫn tới [nguồn tại mốc đã lưu](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/tree/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu). Việc tách tài liệu không thay nghiệp vụ, phiên bản thư viện hoặc kết quả kiểm đã ghi nhận.
