# Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế

Đồ án học phần xây dựng hệ thống để khách truy cập qua một mã QR chung, chọn vùng trên bản đồ và đọc hoặc nghe thuyết minh. Admin quản lý địa điểm, vùng, nội dung, audio, phiên tham quan và thống kê.

Nhóm thực hiện gồm **Thành, Hòa và Nghĩa**. Phân công trách nhiệm được quy định tại [G02](docs/02-nghiep-vu/decisions.md#g02); quy trình làm việc trên ba máy riêng, nhánh task, bàn giao và Pull Request được trình bày trong [kế hoạch commit](docs/01-tong-quan/commit-plan-2026-11-05.md).

- **Repository:** [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue).
- **Quản lý công việc:** [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1).
- **Tài liệu:** bắt đầu tại [mục lục](docs/README.md), tra yêu cầu tại [PRD](docs/02-nghiep-vu/PRD.md) và trạng thái bàn giao tại [handoff](docs/01-tong-quan/handoff.md).

**Trạng thái ngày 11/11/2026.** Backend có V1–V12, quản trị, phiên/nội dung khách, hàng đợi TTS, chuyển WAV thành MP3 và xử lý vòng đời; lượt full mới đạt 520/520, BUILD SUCCESS. Frontend khách/Admin đã có nguồn, 60 ca logic đạt và build Vue thành công. Hai kịch bản Chrome với API/MySQL riêng đã kiểm áp dụng cặp, đọc/phát MP3 thật, thống kê và lỗi mạng audio. WAV tiếng Việt được tạo bằng Gemini thật; worker chuyển MP3 từ WAV đưa vào bằng fixture kiểm thử. Chi tiết và giới hạn tại [hồ sơ chạy local](docs/06-kiem-thu/local-20261111/README.md). Bộ nội dung, vùng, 20 audio được duyệt, thiết bị thật, HTTPS/QR, CI/CD và backup/restore vẫn cần hoàn thiện/kiểm trước nghiệm thu. Có mã nguồn hoặc kiểm local không tự hoàn tất issue.

**Đóng gói và restore riêng:** đã build ảnh Linux phục vụ Vue/API chung origin, kiểm FFmpeg/ffprobe và đường dẫn Unicode, diễn tập phục hồi 12 audio khớp Hash cùng database, lọc retention và kiểm Chrome đăng nhập/đọc/phát sau restore với epoch mới. [Hồ sơ đóng gói](docs/06-kiem-thu/packaging-20261111/README.md) phân biệt diễn tập offline với OPS_GUARD/drain và backup live chưa triển khai. Môi trường kiểm đã dừng, dữ liệu được giữ để rà.

**Bản thử giao diện:** đã sửa chữ tiếng Việt, bố cục dọc, ô văn bản cuộn riêng và nút Hủy. [Hồ sơ UI](docs/06-kiem-thu/guest-ui-20261111/README.md) ghi 7/7 ca Chrome và quan sát VS Code. Dữ liệu E2E cũ còn lưới/tam giác trùng nhau, đang được giữ ở trạng thái dừng tại cổng 18081; chưa dùng làm bộ demo chuẩn.

**Mốc hiện hành:** [bản xem trước bản đồ](docs/06-kiem-thu/map-preview-20261111/README.md) đang mở riêng ở `http://127.0.0.1:18082/map-preview.html`, có nền OSM offline/năm vùng đề xuất và database mới 5 điểm/20 slot/5 bản vi nháp. Chưa có hồ sơ/cặp áp dụng hoặc bật điểm; không tự coi vùng được duyệt. Sửa lỗi trạng thái Mức đầy đủ đã có kiểm lỗi/thử lại/dữ liệu cũ. [Theo dõi issue](docs/01-tong-quan/issue-follow-up-2026-11-11.md) ghi phần còn mở theo G02; QR/HTTPS, audio được duyệt, thiết bị và vận hành vẫn cần hoàn thiện.

Backlog hiện hành gồm **11 task, 89 ô checklist; bản local có 20 dấu tích, mốc GitHub đã đối chiếu có 18** tại [roadmap](docs/01-tong-quan/roadmap.md). Các dấu tích không biểu thị phần trăm hoàn thành đồ án. Trạng thái Git và các mốc chưa có bằng chứng được theo dõi tại handoff.

## Cấu trúc thực tế

```text
AudioQR_Kinh-thanh-Hue/
├── README.md
├── .gitignore
├── .git/                         # chỉ ghi nhận hiện diện, không thao tác
├── frontend/                     # Vue khách/Admin, kiểm logic và browser
├── deploy/                       # Dockerfile, Compose và cấu hình mẫu
├── .github/workflows/            # CI đã soạn, chưa chạy GitHub
├── backend/                      # Spring Boot ba lớp, V1–V12
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
    ├── 05-noi-dung/              # văn bản, bản dịch nháp, đối chiếu audio
    ├── 06-kiem-thu/              # bằng chứng và giới hạn kiểm
    ├── 07-van-hanh/              # môi trường, chạy thử, đóng gói
    └── 99-luu-tru/               # lịch sử, căn cứ và phương án cũ
```

## Phạm vi triển khai và nghiệm thu

- Frontend khách/Admin dùng bộ phiên bản đã chốt; backend giữ [package chia lớp](docs/03-ky-thuat/TDD.md#tdd-packages). Dockerfile đóng gói Vue cùng backend đã build và kiểm trên Linux; Compose update/rollback và triển khai public chưa nghiệm thu.
- docs/05-noi-dung/: bài thuyết minh, bản dịch, nguồn và hồ sơ duyệt; docs/06-kiem-thu/: kết quả/bằng chứng, mã kiểm thử đặt cùng frontend/backend; docs/07-van-hanh/: hướng dẫn chạy và triển khai.
- [Đóng gói và CI](docs/07-van-hanh/dong-goi-va-ci.md) mô tả cấu hình đã có và điều kiện CD còn thiếu. `.env.example` không chứa secret; tệp runtime và khóa giữ ngoài repository.
- Vị trí audio/file tải lên/dữ liệu thực, đường dẫn backup và chi tiết thực thi tiếp tục ở TDD; không mặc định đưa vào Git. .gitignore gốc có phạm vi toàn repo.

Các hướng đã chọn: Vue 3/Vite, Leaflet/Geoman Free; Java 21/Spring Boot/Maven Wrapper; MySQL/JPA/Flyway; Admin session/cookie; Docker và GitHub Actions. Chi tiết/mốc phiên bản tại [technical-inputs](docs/01-tong-quan/technical-inputs.md#version-proposal), căn cứ tại [decisions](docs/02-nghiep-vu/decisions.md). Đã chọn khác với đã kiểm chứng tương thích hoặc đã triển khai.

## Lịch sử biên tập



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](docs/99-luu-tru/document-history.md#official-20261107-readme-root-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
