# Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế

Đồ án học phần xây dựng hệ thống để khách truy cập qua một mã QR chung, chọn vùng trên bản đồ và đọc hoặc nghe thuyết minh. Admin quản lý địa điểm, vùng, nội dung, audio, phiên tham quan và thống kê.

Nhóm thực hiện gồm **Thành, Hòa và Nghĩa**. Phân công trách nhiệm được quy định tại [G02](docs/02-nghiep-vu/decisions.md#g02); quy trình làm việc trên ba máy riêng, nhánh task, bàn giao và Pull Request được trình bày trong [kế hoạch commit](docs/01-tong-quan/commit-plan-2026-11-05.md).

- **Repository:** [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue).
- **Quản lý công việc:** [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1).
- **Tài liệu:** bắt đầu tại [mục lục](docs/README.md), tra yêu cầu tại [PRD](docs/02-nghiep-vu/PRD.md) và trạng thái bàn giao tại [handoff](docs/01-tong-quan/handoff.md).

**Trạng thái ngày 08/11/2026.** Nghiệp vụ và phạm vi yêu cầu đã rõ được phê duyệt, các hướng UI/công nghệ được xác nhận theo từng phần. Tài liệu kỹ thuật còn các nội dung dự thảo. Backend đã có V1–V4, K11/K01/K02/K04, xác thực Admin A01–A05, cửa sổ lệnh/receipt/tạo điểm ẩn A31/A06/A08 và phần đọc/sửa tên A07/A09, đạt 168 test hồi quy với MySQL tạm và HTTP localhost. Toàn T02/T03/T05, điều kiện bật hiện/vùng/cặp/xóa, grant/nội dung/audio, frontend, CI/CD và triển khai/backup/restore thực tế còn cần kiểm chứng. [Bằng chứng ngày 08/11](docs/03-ky-thuat/api-transactions.md#admin-place-test-result)

Backlog hiện hành gồm **11 task, 89 ô checklist và 18 dấu tích có căn cứ** tại [roadmap](docs/01-tong-quan/roadmap.md). Các dấu tích không biểu thị phần trăm hoàn thành đồ án. Trạng thái Git và các mốc chưa có bằng chứng được theo dõi tại handoff.

## Cấu trúc thực tế

```text
AudioQR_Kinh-thanh-Hue/
├── README.md
├── .gitignore
├── .git/                         # chỉ ghi nhận hiện diện, không thao tác
├── backend/                      # khung Spring Boot, guest_session và vé/API Bắt đầu đã có test
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   ├── .mvn/wrapper/
│   ├── src/main/
│   ├── src/test/
│   ├── HELP.md                   # tham khảo sinh sẵn; không nguồn yêu cầu dự án
│   ├── .gitignore
│   └── .gitattributes
└── docs/
    ├── README.md                 # tìm nhanh, bản đồ tài liệu, danh mục sơ đồ
    ├── 01-tong-quan/             # đầu vào, roadmap, bàn giao
    ├── 02-nghiep-vu/             # Business Flow, PRD, decisions, kết luận rà soát
    ├── 03-ky-thuat/              # TDD, mô hình, API, DFD, vận hành, thực hành
    ├── 04-giao-dien/             # UI hiện hành và tài sản/nguồn bản đồ
    └── 99-luu-tru/               # lịch sử, căn cứ và phương án cũ
```

## Khung định hướng — chưa tạo hoặc triển khai

- frontend/ dự kiến có README, src, public, tests và mẫu môi trường theo công nghệ đã chọn. Backend hiện có V1 guest_session, V2 place/region_revision, V3 admin_account, V4 admin_command_window/operation_receipt, API khách và phần quản trị đã nêu ở trạng thái, theo [package chia lớp](docs/03-ky-thuat/TDD.md#tdd-packages). Toàn schema, dữ liệu khởi tạo và các API khác vẫn chưa hoàn tất.
- docs/05-noi-dung/: bài thuyết minh, bản dịch, nguồn và hồ sơ duyệt; docs/06-kiem-thu/: kết quả/bằng chứng, mã kiểm thử đặt cùng frontend/backend; docs/07-van-hanh/: hướng dẫn chạy và triển khai.
- deploy/ và .github/workflows/ dành cho cấu hình thực thi khi được giao. Mẫu .env.example chỉ tạo khi cần; không chứa secret. Không tạo hàng loạt thư mục/file trống để đủ cây.
- Vị trí audio/file tải lên/dữ liệu thực, đường dẫn backup và chi tiết thực thi tiếp tục ở TDD; không mặc định đưa vào Git. .gitignore gốc có phạm vi toàn repo.

Các hướng đã chọn: Vue 3/Vite, Leaflet/Geoman Free; Java 21/Spring Boot/Maven Wrapper; MySQL/JPA/Flyway; Admin session/cookie; Docker và GitHub Actions. Chi tiết/mốc phiên bản tại [technical-inputs](docs/01-tong-quan/technical-inputs.md#version-proposal), căn cứ tại [decisions](docs/02-nghiep-vu/decisions.md). Đã chọn khác với đã kiểm chứng tương thích hoặc đã triển khai.

## Lịch sử biên tập



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](docs/99-luu-tru/document-history.md#official-20261107-readme-root-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
