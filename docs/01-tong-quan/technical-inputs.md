# Đầu vào thiết kế kỹ thuật

**Phiên bản nội dung:** 0.7; biên tập 08/11/2026. **Trạng thái:** TECH-01–05/TTS-01/DATA-01–05 và IN-P đã xác nhận đúng phạm vi; ngrok là phương án thử. Thiết kế và tương thích thực tế vẫn cần kiểm chứng.
**Mục đích:** tra nền tảng, phiên bản, TTS, môi trường, chính sách và đầu vào TDD. [Nguồn quyết định](../02-nghiep-vu/decisions.md), [PRD](../02-nghiep-vu/PRD.md), [UI](../04-giao-dien/ui-design.md), [roadmap](roadmap.md).
**Cách đọc bằng chứng:** cấu hình/mã nhìn thấy, đề xuất cũ, kết quả hồ sơ và kết quả dự án chính là các loại khác nhau. Nguồn dịch vụ giữ mốc khảo sát **03/11/2026**, chưa có lần kiểm chứng mới về giá/quota/API; không suy ra đã gọi TTS.
**Tiến độ thực tế:** xem [handoff](handoff.md#next-step) và [bằng chứng backend gần nhất](../03-ky-thuat/api-transactions.md#admin-place-test-result). V1–V4, API khách và phần quản trị đã có 168 test đạt trên MySQL tạm; chưa toàn môi trường/runtime, frontend hoặc nghiệm thu T02/T03/T05. Phương án cũ và hồ sơ demo nằm ở [lưu trữ](../99-luu-tru/technical-inputs-history.md).

<a id="inputs-confirmed"></a>

## 1. Đầu vào đã được người dùng xác nhận

| Nhóm | Xác nhận hiện hành | Giới hạn |
| --- | --- | --- |
| Hướng dẫn | Theo nhóm công việc; Git trình bày đầy đủ các bước điều kiện/chuẩn bị, staged diff, commit/push/kiểm kết quả. Phân công theo G02 và quy trình tại handoff. | Chỉ yêu cầu kết quả ở các mốc đối chiếu cần thiết hoặc khi có lỗi; dừng sau toàn mục checklist/issue có bằng chứng để rà soát và bàn giao. |
| Môn học | Chủ đề 2: BE Dev theo hướng 3 lớp bao gồm CD/CI. Backend phải thể hiện Controller → Service → Repository và bằng chứng CI/CD hoạt động; Frontend đầy đủ. | Yêu cầu học phần đã được xác nhận; chưa có rubric đầy đủ để đối chiếu độc lập. React Native/React.js của chủ đề 1 không thành bắt buộc chủ đề 2. Giữ Vue 3/TECH-01. |
| Mốc mục tiêu | Ngày 17/11/2026, tính từ 03/11; 14 ngày chênh lệch, 15 ngày lịch nếu tính cả hai đầu. | Mốc cá nhân, không hạn môn học; giờ thực hành/ngày chưa biết, không bảo đảm hoàn thành. |
| Máy | Intel Core i5-12450H, 8 nhân/12 luồng; RAM khoảng 16 GB; Windows/VS Code/PowerShell. | Thông tin người dùng cung cấp; đã có đầu ra Java/Docker/Compose/WSL/Engine Linux ở hướng dẫn thực hành. Windows build/ảo hóa/dung lượng và tổ hợp ứng dụng chưa có bằng chứng kiểm. |
| Thiết bị | IN-P02: khách Android/Chrome và iPhone/Safari; Admin Windows/Chrome máy tính/laptop, chuột/bàn phím. Nhóm có đủ Android/iPhone. | Ghi model/OS/browser build thực lúc kiểm; không dùng phiên bản minh họa cũ hoặc cam kết mọi thiết bị. Khách desktop/Admin mobile chưa bắt buộc. |
| Demo và chi phí | IN-P04 chọn THỬ ngrok Free, chấp nhận trang trung gian; laptop bật/có Internet; khách khác mạng, Admin bảo mật như đã chốt. | Chưa tài khoản/URL thực/kiểm thử/CI-CD; không mua hosting/domain/gói trả phí. Thay ưu tiên Funnel, không kế thừa Quick Tunnel demo. |
| Gemini | Người dùng xác nhận key hoạt động/còn quota/Free tier; chọn gemini-3.8-flash-tts + Charon, đã nghe sáu MP3 thử và hài lòng. | Không thu thập key hoặc chi tiết tài khoản. Google AI Plus không là bằng chứng quyền API. Không suy ra 20 cặp đã duyệt, quota đủ 20 lần hoặc quyền thực hiện lời gọi API. |

<a id="demo-survey"></a>

## 2. Bài học tham khảo từ demo

[Đối chiếu chỉ đọc và bằng chứng khảo sát ngày 03/11](../99-luu-tru/technical-inputs-history.md#demo-survey) được lưu riêng. Chỉ kế thừa bài học tách dữ liệu/file, nhận biết kết quả TTS và minh chứng CI/CD; không mang JWT, vùng tròn/Haversine, chính sách, tunnel hoặc task cũ vào repo chính. Quyết định hiện hành ở mục 1 và 3–8.

<a id="tts-research"></a>

## 3. TTS: model/giọng đã chốt, tích hợp còn cần xác nhận

**Không hỏi chọn lại model/giọng.** [TTS-01](../02-nghiep-vu/decisions.md#tts-01) chốt gemini-3.8-flash-tts/Charon; style rõ ràng, điềm tĩnh mới là hướng tham khảo để nghe thử, chưa coi chuỗi prompt cụ thể đã duyệt.

### 3.1. Nhà cung cấp công bố và điều chưa kiểm chứng

| Nội dung | Kết quả khảo sát chính thức ngày 03/11 | Trạng thái dự án chính |
| --- | --- | --- |
| Ngôn ngữ | Trang model liệt kê Vietnamese, English, French và Russian; giới hạn công bố 8.192 input/16.384 output token. [Nguồn model](https://ai.google.dev/gemini-api/docs/models/gemini-3.8-flash-tts). | Chưa thử bốn tiếng/tên Huế; token không đồng nghĩa số ký tự hoặc thời lượng bảo đảm. Không lấy ngưỡng byte demo làm giới hạn mới. |
| Đầu ra/giọng | Tài liệu có Charon; unary mặc định WAV 24 kHz, mono, PCM 16 bit có RIFF, không thêm header lần nữa. Metadata style tách lời thuyết minh. [Hướng dẫn](https://ai.google.dev/gemini-api/docs/speech-generation). | Chưa gọi API/đo file. Không tự nhận MP3 trực tiếp hoặc ghép chỉ dẫn thành câu đọc. |
| Miễn phí và phí | Bảng giá Standard của model có Free tier cho input/output; mức Paid hiện công bố input 0,50 USD/triệu text token, output 9 USD/triệu audio token tới 31/12/2026, giá sau đó khác. [Bảng giá](https://ai.google.dev/gemini-api/docs/pricing). | **Không bật billing/chi phí**, không suy từ Google AI Plus. Đây là giá công bố, không kiểm tra hóa đơn/quota tài khoản. |
| Quota/429 | Quota theo project/model/tier, không theo key; RPM/TPM/RPD và giới hạn thật xem AI Studio, không có suất miễn phí bảo đảm cho mọi project. [Rate limits](https://ai.google.dev/gemini-api/docs/rate-limits). | Key còn quota theo người dùng chưa chứng minh riêng model đủ 20 lượt. Chưa xác định chính xác nguyên nhân 429 demo; không tạo key để né quota. |
| Lưu và phục vụ lại | Guide minh họa tải byte âm thanh ra file; điều khoản không coi đầu ra độc quyền, việc sử dụng vẫn chịu trách nhiệm quyền nội dung. Free có thể dùng input/output cải thiện dịch vụ. [Điều khoản](https://ai.google.dev/gemini-api/terms). | Chỉ gửi bài cần tạo, không tên khách/phiên/thông tin cá nhân. Không copy audio demo. Quyền nguồn nội dung và duyệt theo D09 vẫn phải có. |

Đọc điều kiện sử dụng trước cung cấp API Client thực tế, gồm phạm vi khu vực/đối tượng truy cập; không diễn giải free API là được dùng vô điều kiện toàn cầu. Chưa kiểm chứng phạm vi pháp lý của một dịch vụ du lịch công khai; demo học phần không tự chứng minh đáp ứng điều khoản.

### 3.2. Hướng tích hợp đề xuất — chưa là TDD

- Java gọi REST **Interactions unary**, giữ adapter TTS riêng; không đưa Node.js thành Backend. Interactions hiện được Google công bố GA và hỗ trợ lưu hoặc `store=false`; đề xuất stateless vì không cần hội thoại, nhưng chưa coi tùy chọn đó xóa mọi log/provider training. [Nguồn Interactions](https://ai.google.dev/gemini-api/docs/interactions).
- Admin bấm tạo từ bản văn **đã lưu**, đóng băng mã bản/ngôn ngữ/model/voice; lưu yêu cầu nội bộ trước xử lý nền. Kiểm trạng thái yêu cầu cũ khi kết quả chưa rõ. Bốn trạng thái nghiệp vụ giữ nguyên, thêm mô tả kỹ thuật “chưa rõ kết quả” không thành trạng thái người dùng mới nếu chưa thiết kế.
- Không tự gửi lại khi 429/timeout. Lỗi xác định cho chủ động thử lại; trạng thái chưa rõ phải đối chiếu job/response đã lưu. Nếu `store=false` và không có phản hồi/ID để đối chiếu provider, không hứa truy hồi được hoặc gọi lại không bị tính thêm; cần thể hiện chưa rõ thay vì thành công giả.
- Không giữ transaction database mở suốt thời gian chờ provider. Tác vụ đã nhận tiếp tục khi Admin hết phiên/đóng trang; kết quả muộn sau xóa điểm không phục hồi đối tượng. Áp dụng cả cặp vẫn là thao tác riêng đủ hồ sơ; không tự phát hành.
- CI dùng fake adapter với WAV hợp lệ và các lỗi chủ động, không gọi Gemini thật bằng key. Một bài chạy thật sau này cần được người dùng giao phép thử riêng, không suy từ TTS-01.

### 3.3. IN-P03 — WAV/MP3 và FFmpeg đã được chọn

**Đã chọn theo TECH-03:** giữ WAV nguồn đúng bản còn tham chiếu; tạo **MP3 phục vụ khách**, FFmpeg/ffprobe trong Linux container. Bộ phiên bản mục 4 của 0.1 là mốc đã chọn; dòng FFmpeg **9.0.2** là đề xuất patch ngoài bảng 4, chưa được chọn riêng; tham khảo [trang phát hành](https://ffmpeg.org/download.html); bản build/codec và digest phải kiểm tra khi triển khai, chưa cài hoặc thử. ffprobe kiểm duration/codec/file không rỗng, **không thay nghe trọn bài**. Không chốt bitrate/dung lượng/thời gian chờ tùy ý thành AC.

Lý do: WAV công bố xấp xỉ 48 KB/giây, bài 2 phút khoảng 5,76 MB trước base64; MP3 phù hợp giảm tải 4G. Đây là phép tính minh họa theo định dạng, không đo bài thật. Đổi lại có bước chuyển đổi, quản lý file và test thêm. Phương án phục vụ WAV trực tiếp để tránh FFmpeg trước đây không còn là hướng hiện hành: IN-P03 đã chọn MP3/Linux container. Windows/VS Code/PowerShell vẫn giữ; hướng dẫn Docker Desktop/WSL khi thực hiện, chưa cài hoặc kiểm pipeline. Bitrate/giới hạn cụ thể không tự được duyệt.

### 3.4. Kế hoạch thử TTS — chưa thực hiện

| Bước | Dữ liệu/việc cần làm khi có quyền | Bằng chứng và điều kiện dừng |
| --- | --- | --- |
| 1 | Người dùng xem hàng quota riêng model trong AI Studio, chỉ cung cấp RPM/TPM/RPD và thời điểm; không gửi key/ảnh tài khoản nhạy cảm. | Đủ căn cứ lập nhịp gọi; thiếu quota/không truy cập model thì dừng, không bật billing. |
| 2 | Cung cấp một bài vi nháp và nguồn để rà soát; chuẩn bị ba bản en/fr/ru tương ứng hoặc mẫu đọc không khẳng định lịch sử. | Đã báo cáo có năm bản nháp, chưa xác minh bản lưu trong repo; không tự tái tạo. Mẫu thử không thành bài được duyệt. |
| 3 | Đề xuất bốn lần tạo nhỏ, tuần tự theo quota, cùng Charon; thử tên Ngọ Môn/Thái Hòa/Tử Cấm Thành/Cửu Đỉnh/Diên Thọ, chữ có dấu, số/năm và khoảng nghỉ. | Chỉ thực hiện sau lệnh cho phép; lỗi/quota/chưa rõ kết quả thì dừng, không tự retry. |
| 4 | Nghe trọn từng mẫu, ghi đọc sai/thiếu/lặp/cắt cụt/ngôn ngữ; người đủ năng lực rà en/fr/ru hoặc ghi giới hạn để người dùng duyệt từng bản theo D09. | Hài lòng sáu MP3 chưa bao phủ bản mới. Ghi hash/bản/ngôn ngữ và nhận xét, không “chuyên gia” bằng AI/dịch ngược. |
| 5 | Kiểm đường tạo → trạng thái → nghe thử → hồ sơ → áp dụng cặp → điện thoại/4G; so sánh lỗi và retry. | Phải có bằng chứng endpoint chạy, không chỉ chuyển file riêng. Bộ 20+20 chỉ hoàn tất khi từng bản đủ nguồn/duyệt/nghe trọn. |

<a id="version-proposal"></a>

## 4. IN-P01/IN-P02: bộ phiên bản đã chọn và ma trận thực tế

**Người dùng đã chọn nguyên bộ phiên bản mục 4 của bản 0.1 làm mốc triển khai theo TECH-03/IN-P01.** Giữ nguyên các mốc dưới đây. Cấu hình Boot 4.1.2/Java 21/Wrapper Maven 3.9.12 đã có trong backend; các mốc hồi quy 64–168 test được ghi theo thời điểm tại handoff và các nguồn kiểm chứng. Chưa kiểm tương thích toàn tổ hợp frontend/backend/vận hành. Sự không khả dụng/không tương thích phải có bằng chứng, ảnh hưởng và đề xuất điều chỉnh; không đổi âm thầm. Không lấy “đã chọn” hoặc “cài được” làm “nghiệp vụ đã đạt”.

| Thành phần | Mốc đã chọn | Căn cứ và giới hạn |
| --- | --- | --- |
| Frontend | Vue **3.5.44**, JavaScript/CSS; Vite **8.3.5**, plugin-vue **6.0.10** | [Vue stable](https://github.com/vuejs/core/releases), [Vite release](https://github.com/vitejs/vite/releases), [peer/engine plugin](https://raw.githubusercontent.com/vitejs/vite-plugin-vue/main/packages/plugin-vue/package.json) cho phép Vite 8/Vue 3.5. Không chọn Vue 3.6 RC. Chưa resolve lockfile/build. |
| Node/npm | Node **24.22.2 LTS**, npm đi kèm bản cài đó; ghi chính xác phiên bản npm lúc kiểm môi trường | [Node download](https://nodejs.org/en/download) có nhánh 24 LTS; không chọn chỉ theo chữ latest/Node Alpha. Node 24 thỏa engine Vite/plugin; không là Backend. |
| Bản đồ | Leaflet **1.9.4** + Geoman Free **2.20.4** | [Manifest Geoman có peer Leaflet ^1.2.0 và dev Leaflet 1.9.4](https://raw.githubusercontent.com/geoman-io/leaflet-geoman/v2.20.4/package.json), [Draw Mode](https://geoman.io/docs/leaflet/modes/draw-mode). Có cơ sở chọn cặp 1.x; chưa kiểm bấm/đóng/sửa đỉnh/chống tự cắt hoặc hành vi Lưu/Áp dụng thực. Không kéo Leaflet 2 vào cùng cặp chưa kiểm. |
| Backend | Java **21**, Spring Boot **4.1.2**, Maven **3.9.12** qua Wrapper | [Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html) cho Java 17–27/Maven ≥3.6.3; [mẫu Maven chính thức](https://docs.spring.io/spring-boot/tutorial/first-application/index.html) và [Wrapper](https://maven.apache.org/wrapper/). Đã biên dịch và kiểm thử phần backend hiện có; không suy tương thích toàn hệ thống hoặc frontend. |
| JPA/Security/Flyway/test | Theo **BOM Boot 4.1.2**, không tự pha phiên bản: Flyway core/mysql **12.4.0**, Testcontainers **2.0.5**, Connector/J **9.7.0** | [Managed versions](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html); Spring Data/Security/starter-test lấy cùng BOM. Đây là bộ quản lý phiên bản, không đồng nghĩa toàn bộ tổ hợp đã được dự án chạy thử. |
| OpenAPI | springdoc **3.2.0** | [Ma trận Springdoc](https://springdoc.org/#what-is-the-compatibility-matrix-of-springdoc-openapi-with-spring-boot): nhánh 3.1–3.2 cho Boot 4.1.x. Không kế thừa resource-server/JWT trong demo. |
| Database | MySQL **8.4.13 LTS**, cùng bản cho DEV và Testcontainers | [Release notes 8.4.13](https://dev.mysql.com/doc/relnotes/mysql/8.4/en/news-8-4-13.html) có ngày phát hành; trang tải có cache còn 8.4.11 nên cần kiểm tag/image/digest thực tế trước khóa TDD. [Flyway MySQL](https://documentation.red-gate.com/flyway/reference/database-driver-reference/mysql) yêu cầu module riêng; danh sách “Verified Versions” không liệt kê riêng 8.4, vì vậy migration/FK/transaction trên đúng 8.4.x vẫn là cổng kiểm tra, chưa khẳng định verified trọn tổ hợp. Nếu không lấy được tag thì báo, không âm thầm đổi DB/version. |
| Docker và audio | Docker Desktop stable + WSL 2/Linux containers/Compose v2; FFmpeg/ffprobe theo IN-P03 | [Docker Windows](https://docs.docker.com/desktop/setup/install/windows-install/) có điều kiện WSL/Windows/ảo hóa và tối thiểu 8 GB RAM. Máy 16 GB là cơ sở lập kế hoạch, không xác minh Windows build/quyền cài/dung lượng/hiệu năng. Chốt build Docker/Compose khi kiểm môi trường, không bịa số hiện đã cài. |

Khuyến nghị giữ BOM/lockfile, ghi image digest/build thật trước đưa bộ phiên bản vào TDD. Không cài/cập nhật ngay theo bảng; nếu nguồn phát hành thay đổi phải báo khác biệt, tránh đổi giữa sprint mà không có lý do.

**IN-P02 — phạm vi đã chốt, ma trận thiết bị thực chưa điền:** kiểm đầy đủ luồng chính/ngoại lệ, map/cuộn văn bản/audio-tua/ngôn ngữ/phiên và chức năng Admin đã duyệt trên các nền tảng dưới đây. “Chỉnh chu” là đủ hành vi trong phạm vi, không cam kết mọi thiết bị/browser.

| Mã kiểm tra | Vai trò/nền tảng đã chốt | Thiết bị và phiên bản thực | Trạng thái |
| --- | --- | --- | --- |
| ENV-KA | Khách Android/Chrome | Model/Android/Chrome build: ghi khi thực hiện | Có điện thoại theo người dùng; chưa kiểm phần mềm |
| ENV-KI | Khách iPhone/Safari | Model/iOS/Safari build: ghi khi thực hiện | Có điện thoại theo người dùng; chưa kiểm phần mềm |
| ENV-AW | Admin Windows/Chrome máy tính/laptop, chuột/bàn phím | Laptop đã mô tả; Windows/Chrome build ghi khi kiểm | Chưa kiểm phần mềm |

Các mốc Android 13/Chrome 142/iOS-Safari 18.6/Windows 11 trong 0.1 chỉ minh họa, **đã bị thay bằng ma trận thực này**, không yêu cầu hạ/cài phiên bản theo ví dụ. Không tự thêm Firefox/Edge/WebView/Admin mobile/khách desktop thành nghiệm thu bắt buộc. Baseline build của Vite không chứng minh audio/cookie/đa giác hoạt động; cần kiểm riêng từng nền tảng.

**Cổng kiểm chứng khi được giao triển khai:** (1) frontend resolve/build; (2) polygon draw/edit/preview đúng free API; (3) Boot/security session/CSRF/OpenAPI; (4) MySQL/Flyway/Testcontainers thật; (5) WAV→MP3 trong Linux container; (6) app chung HTTPS trên hai điện thoại qua 4G và CD. Không một cổng nào đã được chạy trong lượt khảo sát.

<a id="hosting-proposal"></a>

## 5. IN-P04: chọn THỬ ngrok Free, CD là đường riêng

**Hiện hành:** TECH-03/IN-P04 chọn thử ngrok Free thay ưu tiên Tailscale Funnel của 0.1; chưa có URL thật/đăng ký/agent hoặc bằng chứng đạt. Ngrok là đường vào ứng dụng trên laptop, không máy chủ hosting độc lập. Giữ khách từ Wi-Fi/4G khác mạng laptop và Admin session-cookie; không kế thừa demo chặn Admin public.

| Nội dung | Công bố chính thức kiểm 03/11/2026 | Ảnh hưởng / chưa kiểm chứng |
| --- | --- | --- |
| Domain | Dev domain tự cấp gắn tài khoản, không đổi chỉ vì restart agent; không được tự tùy biến trên Free. [Nguồn domain](https://ngrok.com/blog/free-static-domains-ngrok-users). | Ghi URL được cấp thực khi người dùng thực hiện, không chọn hậu tố theo ví dụ hoặc tạo QR giả. Domain giữ không có nghĩa app còn chạy. |
| Quota | Free: **1 GB dữ liệu outbound/tháng**, **20.000 HTTP request/tháng**, tối đa 3 endpoint online; nguồn limits còn nêu 4.000 request/phút. [Pricing/limits](https://ngrok.com/docs/pricing-limits). | Trần tài khoản, không số người/phiên. Audio/assets/API/Range cùng tiêu hao; quota còn lại phải xem thực trước demo, không tự nâng gói hoặc bypass hạn. |
| HTTPS/trang trung gian | HTTPS được cấp tự động; HTML trên Free có trang cảnh báo, sau Visit có cookie bỏ cảnh báo cho domain 7 ngày theo công bố. [Free plan](https://ngrok.com/docs/pricing-limits/free-plan-limits). | Người dùng chấp nhận để thử, chưa chứng minh QR liền mạch. Kiểm Chrome/Safari lần đầu/lần sau, khác mạng và audio; không yêu cầu extension hoặc coi trang ngrok là đăng nhập Admin. |
| Laptop/dữ liệu | App/MySQL/audio ở laptop, không phải ngrok lưu thay. | Laptop/Docker/app/agent phải bật/có Internet, 4G tiêu hao ở máy và khách. Mất điện/ngủ/mạng/quota có thể ngắt; không hứa 24/7 hoặc tự thêm offline. |

Ngrok docs mô tả refresh theo chu kỳ tháng; trang pricing/limits có câu đầu tháng, free-plan nói monthly billing cycle. Không dự đoán ngày reset tài khoản từ hai cách diễn đạt; ghi chu kỳ/usage thực khi người dùng kiểm dashboard. Đăng ký, cài đặt, mua domain/hosting/gói trả phí hoặc gọi API phải được giao và kiểm soát riêng.

### 5.1. Kênh CI/CD vào laptop — CD-L01 đã chọn, cần kiểm SSH/Docker

CI GitHub-hosted build frontend/Java/test MySQL và đóng gói bản gắn SHA/digest. CD phải đưa đúng bản đã đạt vào laptop, kiểm health/persistence/rollback và có bằng chứng run/SHA/bản phục vụ. URL ngrok chỉ phục vụ web, không tự có quyền triển khai, không tạo API web nhận lệnh shell.

**CD-L01 đã chọn TECH-05:** workflow GitHub-hosted dùng Tailscale **mạng riêng + SSH**, không Funnel, vào tài khoản triển khai giới hạn quyền trên laptop. Ngrok làm public web, mạng riêng chỉ cho workflow-to-laptop. [Tailscale Action](https://tailscale.com/docs/integrations/github/github-action) có node tạm; cần xem điều kiện gói/danh tính, Windows/WSL/SSH/Docker và quyền khi thiết kế chi tiết, chưa đăng ký/cài/kiểm thực; được chọn riêng theo TECH-05, không suy từ IN-P04.

Ưu điểm: không mở SSH/DB công khai hoặc chạy PR không tin cậy trên laptop. Đổi lại thêm khâu mạng riêng để hướng dẫn. Script pull artifact không thay kênh đã chọn. Hướng hiện hành Actions → Tailscale riêng → OpenSSH Windows → PowerShell; phải kiểm SSH và account/context Docker trước triển khai đầy đủ. Không mặc định self-hosted runner lâu dài repo public; [GitHub secure use](https://docs.github.com/en/actions/reference/security/secure-use) nêu rủi ro. Chi tiết tại [TDD 14](../03-ky-thuat/TDD.md#tdd-cicd).

### 5.2. Cổng kiểm thực trước coi demo đạt

- Cùng HTTPS origin frontend/API, Admin cookie/CSRF/proxy header đúng, không lộ DB/WAV nháp/key/actuator hoặc Swagger không kiểm soát.
- ENV-KA/ENV-KI truy cập QR từ khác mạng laptop, qua trang trung gian; map/cuộn/audio-tua/ngôn ngữ/expiry, đủ lỗi; Admin ENV-AW đủ chức năng.
- Restart agent kiểm đúng domain/QR; restart/redeploy app kiểm DB/audio còn; theo dõi usage 1 GB/20.000 request. Chưa đo bitrate/dung lượng nên chưa suy đủ bao nhiêu người nghe.
- CI/CD thật và restore riêng được kiểm theo TDD sau; ngrok chạy không chứng minh đã đạt. Nếu Free/4G không đáp ứng, báo bằng chứng/ảnh hưởng/phương án mới, không tự mua gói, đổi hosting hoặc giảm yêu cầu.

<a id="data-proposals"></a>

## 6. IN-P05: chính sách hiện hành và phần thiết kế còn mở

**Nguồn xác nhận:** [DATA-01](../02-nghiep-vu/decisions.md#data-01). Không kế thừa log/backup demo; không coi các đề xuất về nơi/nhịp/RPO-RTO trong 0.1 đã được duyệt toàn bộ.

| Mã nguồn giữ nguyên | Phần đã chốt | Điều kiện / việc còn thiết kế |
| --- | --- | --- |
| DATA-P01 | File hoàn chỉnh chưa từng áp dụng: chỉ tính dọn sau **24 giờ từ khi xác nhận không tham chiếu, không dùng chung, không tác vụ sử dụng**. Nháp còn tham chiếu không tự hết hạn vì chưa duyệt. | Kiểm lại trước xóa; chưa từng áp dụng thì không bịa last-unapply. Có tham chiếu mới thì không dùng mốc orphan cũ để xóa. |
| DATA-P02 | Workspace/file tạm: **1 giờ** khi tác vụ đã kết thúc, không worker sử dụng và đủ an toàn. | Đang chạy/chưa rõ kết quả không là rác. Mốc đủ điều kiện/lịch retry/worker và kết quả muộn cần TDD, không phục hồi điểm xóa. |
| DATA-P03 | Log kỹ thuật **7 ngày**, không secret/thông tin khách/nội dung nhạy cảm. | Fields/mục đích/quyền/công cụ/lịch và log provider/ngrok còn thiết kế/đối chiếu; không tự coi app xóa được log bên ngoài. |
| DATA-P04 | Mỗi backup **7 ngày từ tạo**, bảo vệ/dọn đúng hạn; restore môi trường riêng, lọc phiên/lượt quá hạn theo mốc gốc trước phục vụ. Người dùng chấp nhận bản sao có thể còn gần **37 ngày tính từ kết thúc/hết hạn gốc** (không phải cộng 37 ngày sau xóa vận hành). | DATA-02 cho backup cục bộ tách dữ liệu chạy, không bắt ngoài laptop; DATA-03 chốt DB + WAV/MP3 tham chiếu + manifest, cuối ngày có thay đổi/trước deploy-schema; path/quyền/công cụ/cách nhất quán còn thiết kế; mục tiêu RPO ≤24 giờ/RTO ≤2 giờ ở 0.1 vẫn là đề xuất, chưa được chọn hoặc đo đạt. |

Giữ D08 **30 ngày** từ kết thúc/hết hạn gốc, không từ audio phát hết/restore. File từng áp dụng vẫn theo A02.3: DB thành công, không tham chiếu **và** không dùng chung, bảo vệ **8 giờ từ ngừng áp dụng cuối cùng**; xóa DB khác dọn file. DATA-P01 không thay mốc này. Nghe tiếp không gia hạn phiên; TECH-04 chọn tải trọn MP3 cho tab trước phát, không cần tải tiếp A1 sau expiry. TDD hiện hành cụ thể hóa Blob/cleanup/late response/owner và kiểm browser, không tự đổi chính sách.

**DATA-02/DATA-03 hiện hành:** backup cục bộ tách dữ liệu chạy; DB + WAV/MP3 đang được tham chiếu + manifest cần phục hồi; cuối ngày có thay đổi và trước deploy/schema. Không bắt ổ ngoài/cứu mất toàn laptop. Path/công cụ/quyền/cách snapshot nhất quán/kiểm restore và RPO-RTO còn thiết kế/chưa đo; không hỏi lại phạm vi-nhịp. TDD 12 có ví dụ B còn trong hạn bảy ngày nhưng dữ liệu đã quá hạn gốc.

<a id="deadline-plan"></a>

## 7. Kế hoạch mục tiêu tới 17/11/2026

**Lịch dự kiến có điều kiện**, không hạn môn học/cam kết hoàn tất. Mốc 17/11 và cách lập lịch đã xác nhận, chưa có giờ/ngày được cam kết; phân công theo G02 và theo dõi quỹ giờ thực tế của các thành viên. Không trì hoãn phần thiết kế độc lập khi chưa có tổng quỹ giờ. Lịch theo công việc/người/phụ thuộc/kết quả/dự phòng hiện hành tại [roadmap mục 5](roadmap.md#target-1711).

Ước lượng ban đầu cho phần còn lại, có tính học/thực hành/kiểm tra phần mình, đơn vị **giờ người**, không phải kết quả đo:

| Task | Giờ dự kiến | Phụ thuộc/chú ý |
| --- | --- | --- |
| T01 còn lại | 20–28 | Chốt đầu vào, TDD từng phần, học cách kiểm môi trường và thiết kế. Tài liệu đã làm không tính lại từ đầu. |
| T02 | 8–12 | Cấu trúc/ràng buộc/migrations/seeds, sau TDD dữ liệu; không tính API hai lần. |
| T03 | 12–18 | Phiên khách, nội dung/lượt/quyền; cần T02/hợp đồng. |
| T04 | 18–26 | Vue/map/audio/mobile; học 5 công cụ mới và nối API, giữ toàn bộ ngoại lệ. |
| T05 | 22–32 | Admin/cặp/xóa-job-file/thống kê/QR và lỗi; cùng hợp đồng T07. |
| T06 | 24–34 | Toàn UI Admin/Q2/Q3/vẽ vùng/soạn/audio/phiên/QR. |
| T07 | 10–16 | Khảo sát đã làm một phần; tích hợp/thử thật/các lỗi chưa chạy. Quota có thể kéo dài ngày lịch. |
| T08 | 16–28 | Nhận bài, nguồn, dịch/duyệt/nghe 20 cặp; chưa bao gồm thời gian chờ người rà soát ngôn ngữ bên ngoài. |
| T09 | 16–24 | Tích hợp/thiết bị/4G/mốc thời gian/lỗi/restore, khác kiểm phần mình. |
| T10 | 10–18 | CI/CD, môi trường demo/persistence/rollback; không chỉ cuối kỳ. |
| T11 | 6–10 | Hướng dẫn và bằng chứng/bàn giao xuyên suốt. |
| **Tổng** | **162–246** | Chưa cộng chờ quota, người rà soát, cấp tài nguyên hoặc sự cố lớn. |

Lịch nhóm cần tính công việc có thể làm song song, phụ thuộc dữ liệu/API, thời gian rà soát nội dung, tích hợp và dự phòng. Quỹ giờ của từng thành viên được xác nhận khi theo dõi tiến độ; chưa có căn cứ cam kết chắc hoàn thành toàn phạm vi vào 17/11. Ưu tiên kiểm sớm phần rủi ro và cập nhật theo sản phẩm thực tế.

Lịch khoảng ngày chi tiết có **một vị trí hiện hành ở roadmap mục 5** để tránh hai lịch song song. Tổng 162–246 giờ người là ước lượng ban đầu, chưa phải giờ thực tế. Phân công/người commit theo G02; báo sớm trễ mốc và cập nhật theo sản phẩm đã kiểm, không giảm 20+20/CI-CD hoặc ngoại lệ.

### 7.1. Đầu vào TDD và cách tiếp tục triển khai

1. Phần kiến trúc: module nghiệp vụ trong một Backend, Controller nhận/kiểm DTO và trả HTTP; Service giữ nghiệp vụ/quyền/giao dịch; Repository truy cập dữ liệu. Không Controller gọi Repository trực tiếp. TTS/storage là adapter do Service điều phối; không thêm microservices/Redis/Kafka.
2. Phần dữ liệu/hợp đồng: định danh độc lập tên, phiên/lượt, đa giác, bản nháp/cặp áp dụng và snapshot job; DR/FR/UC/AC giữ truy vết. Xóa database khác file; không import schema/demo.
3. Phần bảo mật/phục hồi: session-cookie/CSRF, khách per-tab/8 giờ, Admin 30 phút/Q2/Q3; expiry nghe tiếp khách khác nghe thử Admin.
4. Phần audio/vận hành: adapter model đã chọn, file job, conversion, unknown/retry; chính sách được duyệt, CI/CD và môi trường/quota/matrix. Không viết đầy đủ trước khi đủ lựa chọn.
5. Mỗi bài hướng dẫn sau này: mục tiêu → vì sao → bước nhỏ → kết quả mong đợi → cách người dùng kiểm tra → lỗi thường gặp → câu hỏi ôn tập. Chưa thực hiện lệnh hoặc cài đặt trong tài liệu này.

Câu hỏi ôn tập gợi ý: Vì sao Controller không giữ giao dịch xóa? Vì sao session-cookie vẫn cần CSRF? Vì sao “TTS thành công” khác “cặp đã áp dụng”? Vì sao 429 không chứng minh hết tiền? Vì sao volume không thay backup và CI xanh không chứng minh CD? Vì sao restore không được làm sống lại dữ liệu đã quá D08?

<a id="new-choices"></a>

## 8. Trạng thái xác nhận hiện hành — không yêu cầu chọn lại

| Mã | Đã được người dùng xác nhận | Chưa kiểm/chưa quyết định |
| --- | --- | --- |
| IN-P01 | Nguyên bộ phiên bản mục 4 của 0.1 làm mốc triển khai | Backend đã build/test phần hiện có; frontend và tương thích toàn bộ chưa kiểm, thay đổi phải có căn cứ |
| IN-P02 | Android/Chrome, iPhone/Safari, Admin Windows/Chrome; đủ hai loại điện thoại; đầy đủ hành vi trong phạm vi | Model/OS/build thực điền khi kiểm, chưa có kết quả |
| IN-P03 | WAV còn tham chiếu + MP3 khách; FFmpeg/ffprobe Linux container, Windows vẫn giữ | Bitrate/ngưỡng/path/worker và pipeline thực |
| IN-P04 | **Chọn thử ngrok Free**, chấp nhận trang trung gian, URL khác mạng | Chưa đạt triển khai; URL/usage thực và CD-L01 riêng đã chọn TECH-05, chưa kiểm SSH/Docker/CD |
| IN-P05 / DATA-P01–P04 | DATA-01 và DATA-02: backup cục bộ tách dữ liệu chạy cho phiên bản nghiệm thu, không ổ ngoài bắt buộc; giữ hạn/restore | DATA-03 chốt phạm vi-nhịp; path/quyền/công cụ/RPO-RTO và kiểm restore chưa chọn/đo; không mục tiêu mất/hỏng laptop |
| Kế hoạch/G03 | Mốc cá nhân 17/11, lập lịch bằng giả định/G02 và nhắc nguyên văn checklist thật khi đủ bằng chứng | Lịch cụ thể dự kiến, không giờ cam kết/không task đã xong; không tự sửa GitHub |

[TDD hiện hành](../03-ky-thuat/TDD.md) giữ ERD/dữ liệu/tab/audio và dẫn [API/giao dịch hiện hành](../03-ky-thuat/api-transactions.md) mới dự thảo. TECH-04 chốt đóng gói demo/độc lập tab/full MP3; DATA-02 chốt phạm vi backup cục bộ. CD-L01 nay đã chọn TECH-05, hướng SSH Windows và xử lý deploy ở TDD 14; chưa kiểm SSH/Docker không chặn thiết kế ERD/API.

**Thiết kế hiện hành theo DATA-05/DATA-04:** [model 0.3](../03-ky-thuat/data-model.md), [API 0.4](../03-ky-thuat/api-transactions.md#api-dto), [operations 0.3](../03-ky-thuat/operations-design.md) dùng vé ký 24 giờ/ledger, không generation. [Thực hành 01](../03-ky-thuat/practice-01-start-session.md) giữ các mốc phiên khách và dẫn tới bằng chứng 168 test. Nền T10 đã push; hiện rà tài liệu trước T01 theo handoff. Ma trận V-START01.01–10 ghi trạng thái từng phần backend và các phần browser/runtime/restore còn thiếu. Không hỏi chọn lại stack/nhà cung cấp hoặc thay phiên bản âm thầm.

**Nguồn xác nhận mới:** [TECH-05](../02-nghiep-vu/decisions.md#tech-05)/[DATA-03](../02-nghiep-vu/decisions.md#data-03), bổ sung TECH-04/DATA-02. Phương án cũ ở lưu trữ được dẫn tại 9.1; chỉ dùng truy vết, không là yêu cầu hiện hành.

## 9. Hồ sơ nguồn và nhật ký

Mọi URL chính thức ở mục 3–5 được kiểm tra ngày 03/11/2026. Một số trang đổi URL về aistudio.google.com; không suy từ cache/search “latest” sang phiên bản đã cài. Nguồn Leaflet install truy cập không ổn định, cặp 1.9.4 được đối chiếu thêm qua manifest chính thức Geoman. Trang tải MySQL và release notes có khác biệt snapshot; phải kiểm tag thực khi được phép triển khai.

Nguồn/file/hash của lần khảo sát demo được giữ ở [hồ sơ khảo sát](../99-luu-tru/technical-inputs-history.md#demo-source-hashes), không đọc lại demo trong lượt chuẩn hóa.

### 9.1. Phương án cũ

[Nguyên bản 0.1 trước xác nhận](../99-luu-tru/technical-inputs-history.md#inputs-superseded) chỉ dùng truy vết, không là lựa chọn hiện hành.

### 9.2. Nhật ký



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-01-tong-quan-technical-inputs-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
