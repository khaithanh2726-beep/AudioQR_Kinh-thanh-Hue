# KỊCH BẢN THUYẾT TRÌNH BẢO VỆ ĐỒ ÁN HỌC PHẦN CÔNG NGHỆ PHẦN MỀM
## ĐỀ TÀI: HỆ THỐNG THUYẾT MINH TỰ ĐỘNG BẰNG MÃ QR CHO KINH THÀNH HUẾ (AUDIOQR)

- **Nhóm thực hiện (G02):**
  1. **Khải Thành** (Trưởng nhóm / Phân tích, Thiết kế, Kiến trúc & Backend)
  2. **Minh Hòa** (Thành viên / CSDL, Data Migration & Frontend UI)
  3. **Trọng Nghĩa** (Thành viên / Pipeline Audio, TTS Worker & DevOps)
- **Thời lượng chuẩn:** 10 – 12 Phút (13 Slide cô đọng, giàu năng lượng)
- **Quy chuẩn học phần:** Bám sát quy trình CNPM, ma trận truy vết (FR/UC/AC), kiến trúc phân lớp, tháp kiểm thử và CI/CD.

---

## Slide 1 – Tiêu đề & Giới thiệu Nhóm
*(Thời lượng ước tính: ~30 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Tên đồ án:** Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế (AudioQR).
- **Học phần:** Công nghệ phần mềm | **Giảng viên hướng dẫn:** [Tên Giảng Viên].
- **Nhóm G02:** Thành (Trưởng nhóm / Backend & Kiến trúc), Hòa (CSDL & Frontend), Nghĩa (Audio Pipeline & Worker).
- **Sứ mệnh:** Số hóa trải nghiệm tham quan di sản Cố đô, loại bỏ rào cản cài đặt app, hỗ trợ đa ngữ tức thời.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Xin kính chào quý thầy cô và các bạn! Tôi là Thành, đại diện nhóm G02 cùng hai cộng sự là bạn Hòa và bạn Nghĩa. Hôm nay, nhóm xin báo cáo đồ án Công nghệ phần mềm: 'Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế'. Đồ án hướng tới việc ứng dụng quy trình kỹ nghệ phần mềm để giải quyết bài toán số hóa di sản: giúp du khách chỉ với một lần quét mã QR duy nhất trên điện thoại là có thể định vị điểm tham quan và nghe thuyết minh đa ngữ mà không cần cài đặt bất kỳ ứng dụng phức tạp nào."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Để bắt đầu, xin kính mời quý thầy cô cùng điểm qua bài toán nghiệp vụ thực tế và mục tiêu cụ thể mà hệ thống hướng tới."*

---

## Slide 2 – Bài toán thực tế, Phạm vi & Mục tiêu hệ thống
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Vấn đề thực tế:** Du khách tham quan tự do thiếu thuyết minh viên; biển bảng tĩnh ít thông tin và hạn chế ngôn ngữ; việc bắt tải app native gây ngại ngần do tốn dung lượng và thời gian.
- **Giải pháp Web App tức thì (Zero-install):** Quét 1 mã QR duy nhất đặt tại cổng vào → Tự động khởi tạo phiên tham quan ngay trên trình duyệt di động.
- **Phạm vi triển khai V14:** 5 địa điểm trọng điểm tại Đại Nội Huế; 4 ngôn ngữ (Việt, Anh, Pháp, Nga) tương đương 20 cặp nội dung chuẩn hóa.
- **Mục tiêu cốt lõi:** Đem lại trải nghiệm nghe nhìn mượt mà, đồng thời cung cấp cổng quản trị biên tập và đồng bộ audio an toàn, tin cậy.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Thưa thầy cô, du khách đến Kinh thành Huế thường gặp khó khăn vì thiếu hướng dẫn viên riêng, các bảng thuyết minh tĩnh lại thiếu tính trực quan và hạn chế ngôn ngữ quốc tế. Trong khi đó, việc bắt khách tải app nặng hàng trăm megabyte là một rào cản rất lớn. Nhóm đặt mục tiêu xây dựng một Web App tức thời: du khách chỉ cần quét mã QR tại cổng là kích hoạt phiên tham quan ngay trên trình duyệt di động. Hệ thống tập trung giải quyết bài toán thuyết minh tại 5 địa điểm cốt lõi với 4 ngôn ngữ, đồng thời cung cấp cổng quản trị nội dung an toàn, dễ vận hành."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Từ bài toán trên, nhóm đã tiến hành phân tích yêu cầu phần mềm và mô hình hóa nghiệp vụ ra sao? Xin mời thầy cô theo dõi slide tiếp theo."*

---

## Slide 3 – Đặc tả Yêu cầu & Phân tích Nghiệp vụ (Actors & Use Cases)
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **2 Tác nhân chính (Actors):** Khách tham quan (Guest) và Quản trị viên (Admin).
- **14 Use Cases chuẩn hóa:**
  - *Khách tham quan (5 UC):* Truy cập phiên qua QR, Chọn vùng bản đồ tương tác, Điều khiển audio đa ngữ, Phục hồi sau sự cố mạng, Kết thúc phiên.
  - *Quản trị viên (8 UC):* Xác thực & phiên Admin, CRUD địa điểm/vẽ polygon, Soạn thảo văn bản song ngữ, Kích hoạt tác vụ TTS, Phê duyệt cặp nội dung, Quản lý QR chung, Giám sát phiên & thống kê.
  - *Tác vụ nền (1 UC):* Tự động dọn dẹp dữ liệu và file rác quá hạn lưu trữ 30 ngày.
- **Ma trận truy vết (Traceability Matrix):** Đối chiếu khép kín 23 Yêu cầu chức năng (FR), 14 Use Case (UC), 50 Tiêu chí nghiệm thu (AC) và 6 Yêu cầu phi chức năng (NFR).

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Về mặt kỹ nghệ yêu cầu, nhóm xác định rõ hai tác nhân chính: Khách tham quan và Quản trị viên, được cụ thể hóa thành 14 Use Case. Khách có 5 ca sử dụng cốt lõi xoay quanh trải nghiệm tương tác bản đồ và nghe audio; Admin có 8 ca sử dụng quản lý vòng đời địa điểm, biên tập nội dung song ngữ và sinh giọng nói tự động; cùng 1 tác vụ nền tự động dọn dẹp dữ liệu quá hạn. Đặc biệt, nhóm xây dựng ma trận truy vết khép kín từ 23 yêu cầu chức năng đến 50 tiêu chí nghiệm thu để bảo đảm không bỏ sót bất kỳ yêu cầu nào."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Để hiện thực hóa bộ yêu cầu bài bản này, phương pháp phát triển và quy trình phối hợp nhóm đã được tổ chức như thế nào?"*

---

## Slide 4 – Quy trình & Phương pháp phát triển phần mềm
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Mô hình quy trình:** Áp dụng linh hoạt Agile/Kanban, phân rã công việc thành các sprint ngắn hạn và tập trung vào sản phẩm có kiểm chứng.
- **Quản lý nhiệm vụ (Task Tracking):** 11 Task lớn (T01 đến T11) theo dõi trên GitHub Issues, gắn kèm bảng kiểm 89 tiêu chí nghiệm thu cụ thể.
- **Chiến lược phân nhánh Git (Branching Strategy):** Nhánh `main` được bảo vệ; mỗi tính năng phát triển trên nhánh task riêng biệt.
- **Quy chuẩn chất lượng (Review & Gate):** Thực hiện Pull Request bắt buộc có Code Review chéo; Trạm kiểm soát CI tự động chạy pass 100% test mới cho phép merge.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Để đảm bảo tiến độ và chất lượng chuẩn công nghệ phần mềm, nhóm áp dụng mô hình Agile/Kanban. Toàn bộ đồ án được phân rã thành 11 nhiệm vụ chuyên trách từ T01 đến T11, quản lý trên GitHub Issues với 89 tiêu chí nghiệm thu rõ ràng. Về quy trình làm việc với Git, nhóm tuân thủ nghiêm ngặt: mỗi tính năng làm trên một nhánh riêng, khi tích hợp phải tạo Pull Request có thành viên review chéo. Chỉ khi hệ thống CI tự động chạy thành công toàn bộ bài test thì mã nguồn mới được merge vào nhánh chính."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Một quy trình chặt chẽ cần một nền tảng kỹ thuật vững vàng. Kính mời quý thầy cô cùng quan sát kiến trúc tổng thể của hệ thống."*

---

## Slide 5 – Kiến trúc tổng thể hệ thống (Layered Architecture)
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Mô hình kiến trúc 3 lớp (3-Tier Layered Architecture):**
  - *Tầng Trình diễn (Presentation Tier):* Vue 3 Single Page Application (được đóng gói phục vụ chung origin với backend để triệt tiêu lỗi CORS và tăng tốc bảo mật).
  - *Tầng Ứng dụng (Application Tier):* Spring Boot 3 phân tách nghiêm ngặt Controller → Service Layer → Repository (áp dụng nguyên lý Separation of Concerns).
  - *Tầng Dữ liệu & Lưu trữ (Data & Storage Tier):* CSDL quan hệ MySQL 8 kết hợp Flyway migrations; Kho lưu trữ file audio độc lập.
- **Giao thức truyền thông:** RESTful API trao đổi dữ liệu chuẩn JSON; Cơ chế xác thực Session Token phía Admin và Grant Token 8 giờ phía Khách.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Kiến trúc hệ thống được thiết kế theo mô hình 3 lớp kinh điển, đảm bảo tính độc lập và khả năng bảo trì cao. Tầng giao diện là Vue 3 SPA được đóng gói phục vụ chung origin với backend, giúp loại bỏ hoàn toàn các rủi ro về CORS. Tầng ứng dụng sử dụng Spring Boot 3 với sự phân tách trách nhiệm tuyệt đối giữa Controller tiếp nhận API, Service xử lý nghiệp vụ giao dịch, và Repository truy xuất dữ liệu. Tầng lưu trữ kết hợp giữa MySQL 8 được kiểm soát phiên bản qua Flyway và một kho lưu trữ file audio độc lập, sẵn sàng mở rộng."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Bây giờ, chúng ta sẽ đi sâu vào chi tiết hiện thực của từng tầng, khởi đầu từ phía người dùng với Frontend."*

---

## Slide 6 – Thiết kế & Hiện thực Frontend (Vue 3 & Responsive Web)
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Công nghệ lõi:** Vue 3 (Composition API), Vite build tool, Pinia state management (sessionStore, contentStore).
- **Các thành phần giao diện chủ đạo:**
  - `GuestMap.vue`: Tích hợp thư viện Leaflet hiển thị bản đồ di tích, vẽ polygon vùng, xử lý sự kiện chạm, ghim vị trí và chống nhiễu cảm ứng trên di động.
  - `BasemapStatus.vue`: Chỉ báo trạng thái mạng, tải tile bản đồ ngoại tuyến và chuyển đổi linh hoạt chế độ hiển thị.
  - `AdminDashboard`: Bộ công cụ đồ họa cho phép Admin vẽ polygon trực tiếp trên bản đồ, soạn thảo nháp và kích hoạt hàng đợi TTS.
- **Trình phát Audio tối ưu:** Sử dụng Web Audio API và HTML5 Audio, quản lý buffer dạng Blob, ghi nhớ mốc nghe khi tải lại tab và chống rò rỉ bộ nhớ.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Frontend được xây dựng bằng Vue 3 và Vite, đảm bảo giao diện tải nhẹ và mượt mà trên trình duyệt di động. Điểm nhấn là component GuestMap tích hợp Leaflet, cho phép du khách chạm trực tiếp vào các vùng di tích dạng đa giác để nghe thuyết minh tương ứng. Về phía ban quản trị, nhóm phát triển công cụ vẽ và căn chỉnh tọa độ polygon trực quan ngay trên bản đồ. Trình phát audio được tối ưu qua Web Audio API, truyền tải dữ liệu dưới dạng Blob an toàn và tự động ghi nhớ mốc nghe khi du khách tải lại trang."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Đứng sau giao diện trực quan đó là tầng Backend xử lý nghiệp vụ với độ tin cậy và bảo mật cao."*

---

## Slide 7 – Thiết kế & Hiện thực Backend (Spring Boot 3 & Security)
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Nền tảng công nghệ:** Spring Boot 3, Java 21, Spring Data JPA, Hibernate, xử lý luồng hiệu năng cao.
- **Thiết kế API & Giao dịch:** RESTful API chuẩn, thiết kế Idempotent cho các tác vụ nhạy cảm, giao dịch phân cấp (@Transactional) đảm bảo toàn vẹn ACID.
- **Cơ chế Xác thực & Bảo mật (Hiện thực thực tế):**
  - *Phía Khách:* Quản lý phiên theo Tab trình duyệt (Tab-scoped session), cấp Grant Ticket có chữ ký số nội bộ, thời hạn 8 giờ, chống tạo phiên trùng lặp.
  - *Phía Admin:* Kiểm soát an ninh bằng AdminSecurityConfiguration kết hợp AdminSessionFilter; Cổng bảo trì OperationsGateFilter bảo vệ hệ thống khi sao lưu.
  - *Định hướng mở rộng:* Kiến trúc đã tách module sẵn sàng để nâng cấp lên chuẩn JWT / OAuth2 có Refresh Token.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Backend chạy trên nền Spring Boot 3 và Java 21 mới nhất. Về mặt an ninh, nhóm phân định rất chặt chẽ: Đối với khách, hệ thống quản lý phiên theo từng tab trình duyệt và cấp vé tham quan có chữ ký số nội bộ với thời hạn 8 giờ, ngăn chặn việc lợi dụng tài nguyên. Đối với ban quản trị, hệ thống kiểm soát quyền truy cập qua AdminSessionFilter và cổng khóa an toàn OperationsGateFilter khi bảo trì dữ liệu. Nhóm chọn giải pháp phiên bảo mật nội bộ để đảm bảo kiểm soát tập trung, đồng thời cấu trúc sẵn sàng để mở rộng sang chuẩn JWT khi cần."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Để hỗ trợ nghiệp vụ thuyết minh âm thanh, cơ sở dữ liệu và đường ống xử lý media được thiết kế ra sao?"*

---

## Slide 8 – Cơ sở dữ liệu & Pipeline xử lý Âm thanh (TTS Pipeline)
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Cơ sở dữ liệu vật lý:** Mô hình dữ liệu chuẩn gồm 25 bảng vật lý; Quản trị tiến hóa schema tự động bằng Flyway qua 14 bản migration từ V1 đến V14.
- **Quy trình xử lý âm thanh tự động (TTS Pipeline):**
  - *Bước 1:* Admin duyệt văn bản song ngữ → Hệ thống đẩy yêu cầu vào Hàng đợi tác vụ TTS Asynchronous.
  - *Bước 2:* Dịch vụ Text-to-Speech sinh file âm thanh WAV chuẩn xác theo ngữ âm từng ngôn ngữ.
  - *Bước 3:* Worker Docker tích hợp công cụ FFmpeg chuẩn hóa âm lượng, nén sang MP3 128kbps, sinh mã băm SHA-256 đối soát toàn vẹn.
  - *Bước 4:* Lưu trữ độc lập; Hệ thống có cơ chế Garbage Collection định kỳ quét và dọn file mồ côi mà không làm ảnh hưởng CSDL.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Cơ sở dữ liệu của dự án gồm 25 bảng vật lý, kiểm soát tiến hóa hoàn toàn tự động qua 14 phiên bản migration của Flyway. Đường ống xử lý audio là một điểm sáng kỹ thuật: khi Admin phê duyệt nội dung, văn bản được đưa vào hàng đợi bất đồng bộ gọi dịch vụ Text-to-Speech sinh file WAV. Sau đó, một worker Docker dùng FFmpeg chuẩn hóa âm lượng, nén thành file MP3 128kbps và tính mã SHA-256 để chống giả mạo. Hệ thống còn có cơ chế dọn dẹp file mồ côi định kỳ chạy độc lập, đảm bảo kho lưu trữ luôn sạch sẽ."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Để đảm bảo hệ thống luôn vận hành ổn định qua từng commit, nhóm đã thiết lập hạ tầng CI/CD như thế nào?"*

---

## Slide 9 – Quy trình CI/CD & Đóng gói Vận hành (DevOps)
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Đường ống tích hợp liên tục (GitHub Actions Pipeline):**
  - *Stage 1 (Quality Gate):* Kiểm tra cú pháp, format mã nguồn, chạy linter frontend và Checkstyle cho backend.
  - *Stage 2 (Automated Testing):* Tự động nạp CSDL thử nghiệm, chạy toàn bộ Unit Tests, Integration Tests và Frontend Tests.
  - *Stage 3 (Package & Verify):* Đóng gói Docker Container, kiểm tra build và gán badge trạng thái CI trên repository.
- **Hạ tầng vận hành & Khôi phục sự cố:**
  - Đóng gói đồng bộ qua `docker-compose.yml` (gồm MySQL 8, Backend API, Nginx phục vụ Frontend).
  - Khởi động toàn bộ hệ thống bằng 1 câu lệnh duy nhất: `docker compose up -d`.
  - Kịch bản sao lưu và khôi phục (Backup & Restore) định kỳ, có cổng Operations Gate bảo vệ tính nhất quán dữ liệu.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Về mặt DevOps, nhóm thiết lập quy trình CI/CD tự động hóa hoàn toàn trên GitHub Actions. Mỗi khi có mã nguồn mới, pipeline sẽ tự động kiểm tra chuẩn coding style, khởi chạy toàn bộ các bài test backend và frontend, sau đó đóng gói thành Docker image chuẩn. Môi trường triển khai được định nghĩa trọn vẹn trong tệp Docker Compose: chỉ cần một câu lệnh duy nhất là cả hệ thống gồm MySQL, Backend và Frontend sẵn sàng hoạt động. Nhóm cũng thiết lập sẵn kịch bản sao lưu cơ sở dữ liệu và file âm thanh định kỳ để sẵn sàng ứng phó sự cố."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Một sản phẩm phần mềm chuyên nghiệp không thể thiếu bảo chứng về chất lượng. Kính mời thầy cô cùng xem kết quả kiểm thử của dự án."*

---

## Slide 10 – Chiến lược Kiểm thử & Đảm bảo chất lượng (Software Quality)
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Áp dụng triệt để mô hình Kim tự tháp kiểm thử (Test Pyramid):**
  - *530 bài Unit & Integration Tests Backend* (JUnit 5, Mockito): Kiểm tra chặt chẽ tính đúng đắn của logic nghiệp vụ, ràng buộc giao dịch, concurrency và phân quyền API.
  - *71 bài Frontend Logic Tests* (Vitest): Kiểm thử quản lý trạng thái Pinia, xử lý định dạng bản đồ, chuyển đổi ngôn ngữ và khôi phục mốc audio.
  - *33 kịch bản End-to-End & Vận hành* (Cypress / Playwright): Kiểm chứng trọn vẹn kịch bản người dùng thật, phục hồi sự cố rớt mạng, chạy đồng thời hai tab.
  - *11 kịch bản kiểm thử độc lập trên trình duyệt Chrome*.
- **Chỉ số chất lượng:** Độ bao phủ mã nguồn (Code Coverage) đạt trên 95% ở các module nghiệp vụ then chốt; Tất cả test cases đều khớp mã trong Ma trận truy vết.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Chất lượng phần mềm là yếu tố nhóm đầu tư công phu nhất. Hệ thống được bảo vệ bởi một tháp kiểm thử đồ sộ: 530 bài test backend bao phủ mọi ngóc ngách logic và giao dịch; 71 bài test logic frontend kiểm chứng xử lý trạng thái và trình phát audio; cùng 33 kịch bản E2E kiểm thử trọn vẹn luồng khách tham quan, kể cả trường hợp mất kết nối mạng giữa chừng. Toàn bộ các ca kiểm thử này đều gắn liền với mã yêu cầu trong ma trận truy vết, mang lại độ tin cậy và sự ổn định cao cho phần mềm."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Với nền tảng kỹ thuật và kiểm thử vững chắc, sản phẩm đạt được kết quả hiện trạng ra sao?"*

---

## Slide 11 – Kết quả Nghiệm thu & Hiện trạng sản phẩm
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Hiện trạng dữ liệu thực tế V14 (Kiểm chứng độc lập):**
  - Phục vụ chính thức 5 địa điểm tiêu biểu tại Kinh thành Huế (Ngọ Môn, Kỳ Đài, Điện Thái Hòa, Thế Miếu, Hiển Lâm Các).
  - Hoàn thiện và khóa đúng phiên bản 20 cặp nội dung song ngữ chuẩn (Việt, Anh, Pháp, Nga), 20/20 file audio MP3 được xác thực khớp mã băm SHA-256.
- **Kết quả nghiệm thu danh mục Checklist:**
  - Đạt 62 trên tổng số 89 tiêu chí checklist (đầy đủ bằng chứng kỹ thuật, log và mã nguồn đối chiếu).
  - Pipeline CI màu xanh (Passing) ổn định trên commit mới nhất của nhánh chính.
  - Mã QR phân giải cao sẵn sàng in ấn và quét thử nghiệm trên các dòng điện thoại thông minh.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Đến thời điểm nghiệm thu học phần, hệ thống đã nạp dữ liệu hoàn chỉnh cho 5 địa điểm tiêu biểu của Kinh thành Huế với 20 cặp văn bản và audio tương ứng 4 ngôn ngữ, toàn bộ 20 file MP3 đều khớp chuẩn mã băm SHA-256. Về mặt tiến độ dự án, nhóm đã đạt 62 trên 89 tiêu chí có bằng chứng thực tế rõ ràng. Bản phục vụ đã sẵn sàng mã QR chuẩn HTTPS, du khách có thể cầm điện thoại thật lên quét và trải nghiệm ngay lập tức."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Nhìn lại toàn bộ quá trình phát triển, nhóm đã rút ra những bài học và định hướng cải tiến nào?"*

---

## Slide 12 – Đánh giá, Hạn chế & Hướng phát triển tương lai
*(Thời lượng ước tính: ~45 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Ưu điểm nổi bật:**
  - Tuân thủ chặt chẽ quy trình Kỹ nghệ phần mềm; Tài liệu đặc tả và Ma trận truy vết cực kỳ chi tiết.
  - Kiến trúc phân tầng rõ ràng, cơ sở dữ liệu có kiểm soát phiên bản; Mức độ tự động hóa kiểm thử và CI/CD vượt trội.
- **Hạn chế thực tế & Giải pháp tương lai:**
  - Cơ chế xác thực Admin hiện là Session Cookie có bảo vệ → *Định hướng:* Chuyển sang chuẩn JWT / OAuth2 có Refresh Token và phân quyền RBAC đa cấp.
  - Chưa bao phủ toàn bộ ma trận thiết bị di động vật lý (mẫu máy cũ, hệ điều hành đặc thù) → *Định hướng:* Mở rộng phòng thí nghiệm kiểm thử thiết bị thật.
  - Nâng cấp công nghệ: Chuyển đổi sang Progressive Web App (PWA) cho phép cache audio và bản đồ ngoại tuyến 100%; Tích hợp mô hình 3D AR/VR.

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Đánh giá khách quan, ưu điểm lớn nhất của đồ án là tuân thủ nghiêm ngặt quy trình công nghệ phần mềm, kiến trúc chuẩn mực và tháp kiểm thử tự động hóa cao. Về hạn chế, nhóm thẳng thắn nhìn nhận cơ chế xác thực admin hiện vẫn ở mức session nội bộ, nhóm đã lên kế hoạch nâng cấp sang JWT và OAuth2 đa yếu tố. Trong tương lai, nhóm sẽ biến ứng dụng thành PWA có thể lưu đệm ngoại tuyến hoàn toàn khi du khách mất sóng 4G, đồng thời nghiên cứu tích hợp mô hình 3D WebXR để tăng tính sinh động."*

**🔗 Bắc cầu chuyển ý sang slide tiếp:**
> *"Và để khép lại bài trình bày hôm nay, nhóm xin có đôi lời tổng kết ngắn gọn."*

---

## Slide 13 – Tổng kết & Hỏi đáp (Q&A)
*(Thời lượng ước tính: ~30 giây)*

### 📋 Nội dung trình chiếu trên Slide:
- **Ý nghĩa đồ án:** Áp dụng thành công toàn diện các nguyên lý và quy trình Công nghệ phần mềm vào giải quyết một bài toán xã hội và du lịch thực tế có ý nghĩa cao.
- **Kỹ năng nhóm thu hoạch:** Làm chủ quy trình phân tích yêu cầu, thiết kế kiến trúc phân lớp, lập trình full-stack chuyên nghiệp, quản trị CSDL qua migration, tự động hóa CI/CD và kiểm thử phần mềm.
- **Khởi động phiên hỏi đáp:** Nhóm xin chân thành cảm ơn quý thầy cô đã lắng nghe và rất mong nhận được những góp ý quý báu từ Hội đồng bảo vệ!

### 🎙 Lời dẫn người nói (Speaker Notes):
> *"Kính thưa quý thầy cô, đồ án AudioQR Kinh thành Huế không chỉ giúp nhóm làm chủ toàn bộ chu trình phát triển phần mềm từ phân tích, thiết kế, lập trình đến kiểm thử và đóng gói, mà còn mang lại một sản phẩm ý nghĩa góp phần bảo tồn văn hóa cố đô. Nhóm G02 xin trân trọng cảm ơn sự theo dõi và chỉ dẫn của thầy cô trong suốt học kỳ qua. Sau đây, nhóm xin lắng nghe những câu hỏi và đóng góp quý báu từ hội đồng!"*

---
*Tài liệu kịch bản thuyết trình CNPM chuẩn hóa – Nhóm G02 AudioQR Kinh Thành Huế.*
