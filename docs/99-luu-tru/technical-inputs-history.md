# Lịch sử đầu vào kỹ thuật và khảo sát demo

**Lưu trữ ngày:** 05/11/2026. **Mục đích:** Giữ hồ sơ khảo sát 03/11, file/hash đọc khi đó và phương án 0.1 bị thay. Không khảo sát mới, không kế thừa demo hoặc coi nguồn dịch vụ đã được kiểm lại.
**Nguồn hiện hành:** [mở tài liệu chính](../01-tong-quan/technical-inputs.md); kiểm phiên bản/trạng thái trước dùng. Bản lưu không là nguồn triển khai song song hoặc phê duyệt mới. [Mục lục lưu trữ](README.md).

## Mục lục phần lưu

- [demo-survey-source](#demo-survey-source) — từ docs/01-tong-quan/technical-inputs.md
- [inputs-superseded](#inputs-superseded) — từ docs/01-tong-quan/technical-inputs.md
- [demo-source-hashes](#demo-source-hashes) — từ docs/01-tong-quan/technical-inputs.md

<a id="demo-survey-source"></a>

## Phần lưu: demo-survey-source

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="demo-survey"></a>

<a id="2-a--đối-chiếu-demo-với-repo-chính"></a>

## 2. A — Đối chiếu demo với repo chính

Đã đọc có lọc dòng nhạy cảm: README, pom.xml, application.yaml, compose.yaml, ci.yml, deploy.ps1/sh; TTS/converter/publish/storage; PROGRESS, TDD_APPROVAL và hồ sơ nội dung 28/10. README/TDD dài chỉ đọc phần liên quan, không rà toàn repo/demo hoặc lịch sử Git. Không mở file audio/runtime/database để thẩm định lại.

| Hạng mục và nguồn local | Phân loại / điều thấy được | Bài học cho repo chính / giới hạn |
| --- | --- | --- |
| [pom.xml demo](../../../Thuyet-minh-tu-dong-QR-demo/pom.xml), [compose](../../../Thuyet-minh-tu-dong-QR-demo/compose.yaml) | (a) Java 21, Boot 4.1.1, springdoc 3.2.0; image MySQL 8.4.11; có Flyway, JPA, Testcontainers và volume DB/audio. | Học cách tách image với dữ liệu, quản lý phiên bản. Không copy pom/compose/volume/DB, không lấy phiên bản demo làm bộ mặc định mới. |
| [application.yaml](../../../Thuyet-minh-tu-dong-QR-demo/src/main/resources/application.yaml) và [GeminiTtsClient](../../../Thuyet-minh-tu-dong-QR-demo/src/main/java/vn/hue/qrguide/service/GeminiTtsClient.java) | (a) mặc định model 3.8 Flash TTS/Charon, style hướng dẫn bảo tàng rõ, ấm/điềm tĩnh; REST Interactions, metadata style riêng, kiểm WAV, phân loại 429/5xx/timeout. | Cấu trúc metadata và kiểm định dạng phù hợp hướng tài liệu hiện hành. Các giới hạn byte/90 giây trong demo không tự thành điều kiện repo chính. Không copy client/key. |
| [AudioConverterService](../../../Thuyet-minh-tu-dong-QR-demo/src/main/java/vn/hue/qrguide/service/AudioConverterService.java), [AudioPublishService](../../../Thuyet-minh-tu-dong-QR-demo/src/main/java/vn/hue/qrguide/service/AudioPublishService.java) | (a) converter chặn OS không phải Linux; có FFmpeg/ffprobe, kiểm codec/duration, publish dựa snapshot/hash; (d) chưa chạy lại. | Nếu chọn MP3, đề xuất pipeline Linux container; không coi chuyển sáu file Windows riêng là endpoint chạy được. Học snapshot/đối chiếu kết quả muộn, nhưng viết theo áp dụng cả cặp của dự án chính. |
| [PROGRESS](<../../../Thuyet-minh-tu-dong-QR- demo - docs, diagram, .md/PROGRESS.md>), [hồ sơ 28/10](<../../../Thuyet-minh-tu-dong-QR- demo - docs, diagram, .md/issue-evidence/T09/CONTENT_APPROVAL_2026-10-28.md>) | (c) một mẫu vi được nhận xét tốt; tổng sáu WAV sau đó 429 và dừng; hồ sơ ghi chuyển sáu MP3, công bố thử; chưa đủ nghe duyệt 20 cặp. Có nhiều snapshot test khác nhau, một số chưa bao phủ source cuối. | Dùng làm bằng chứng hồ sơ demo có giới hạn, không đóng task repo chính. Bổ sung mới của người dùng: đã nghe cả sáu MP3 và hài lòng giọng; vẫn không phê duyệt sẵn nội dung/audio 20 cặp. |
| [README demo](../../../Thuyet-minh-tu-dong-QR-demo/README.md), [TDD](<../../../Thuyet-minh-tu-dong-QR- demo - docs, diagram, .md/TDD.md>) | (a/b) chọn điểm bằng tọa độ/radius/Haversine, JWT và quy tắc xóa khác; TDD_APPROVAL chấp thuận chỉ trong phạm vi demo ở thời điểm đó. | **Phải thay đổi:** đa giác khép; session/cookie Admin; CRUD/xóa điều phối và giữ lịch sử; cặp văn bản/audio; phiên khách/Admin, Q2/Q3 và 8 giờ/30 ngày của repo chính. Không mang T01–T24 sang T01–T11. |
| [ci.yml](../../../Thuyet-minh-tu-dong-QR-demo/.github/workflows/ci.yml), [deploy.ps1](../../../Thuyet-minh-tu-dong-QR-demo/deploy.ps1), [deploy.sh](../../../Thuyet-minh-tu-dong-QR-demo/deploy.sh) | (a) CI khai báo build/test Java 21 và upload báo cáo; script deploy hiện throw/exit ngay vì khóa triển khai. (c) README/TDD nói CD có kiểm soát nhưng đây không là log CD thành công. | Không coi có YAML/script là CI/CD hoạt động. Repo chính cần run URL/SHA, báo cáo test, artifact và bằng chứng triển khai/health/giữ dữ liệu/rollback. |
| PROGRESS/TDD về tunnel | (c) hồ sơ ghi Quick Tunnel tạm, URL đổi, chỉ route khách; Admin công khai bị chặn, chưa đầy đủ kiểm thiết bị. | **Chỉ rút kinh nghiệm**, không kế thừa hoặc đề nghị duyệt lại phương án này. Nhu cầu URL công khai của repo chính còn giữ; phương án mới ở mục 5. |
| TDD về log/backup/lịch cũ | (b) log 14 ngày, backup 7 bản ngày +4 bản tuần và RPO/RTO đề xuất; lịch/task cũ. | Không áp dụng cho repo chính. Mục 6 đề xuất chính sách mới và roadmap mục 5 đề xuất lịch mới; chưa được duyệt. |

Không xác nhận thẩm định lịch sử/bản dịch từ bảng duyệt text demo; không import code, dữ liệu, audio, secret hoặc lịch sử. Các test/log được PROGRESS nêu là **kết quả hồ sơ ghi nhận**, chưa được tái hiện hoặc chuyển thành bằng chứng repo chính.

<a id="inputs-superseded"></a>

## Phần lưu: inputs-superseded

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="91-dấu-vết-01-trước-xác-nhận--không-áp-dụng-như-hiện-hành"></a>

### 9.1. Dấu vết 0.1 trước xác nhận — KHÔNG ÁP DỤNG NHƯ HIỆN HÀNH

TECH-03/DATA-01 đã thay trạng thái đề xuất đúng phần; giữ trích đoạn sau để nhận biết phương án bị thay, không giữ hai phương án hiện hành. Các số phiên bản mục 4 giữ nguyên. Phần lịch cũ được hợp nhất ở roadmap với nhật ký, không tạo tiến độ giả.

<details>
<summary>Ma trận minh họa, phương án H1/H2/H3 và DATA-P của bản 0.1</summary>

```markdown
**IN-P02 — ma trận trình duyệt dự kiến, chưa là tiêu chí bắt buộc:** khách Android 13+ với Chrome 142+; khách iOS 18.6 với Safari 18.6; Admin Windows 11 với Chrome 142+. Chốt một Android và một iPhone thật cùng một laptop, ghi đúng OS/build/trình duyệt thực khi vào T09; nếu máy có phiên bản mới hơn thì dùng phiên bản đó và ghi nhận lại, không yêu cầu hạ phiên bản. Chrome desktop/Android và Safari iOS đều phải kiểm riêng audio/tua/đổi tiếng/expiry/box/map. Edge là mở rộng tùy chọn, không tự thêm Firefox/Samsung Internet/WebView hoặc mọi iOS làm bắt buộc.

Lý do chọn phạm vi hẹp có hai họ trình duyệt: [Vite production baseline](https://vite.dev/guide/build.html) cho Chrome ≥111/Safari ≥16.4 nhưng đó chỉ là nền JS/CSS, **không chứng minh** audio tự phát/tua/cookie/đa giác trên thiết bị. Chọn phạm vi kiểm thử cao hơn baseline không thay cho test thực; user chưa cung cấp model/build thiết bị nên ma trận còn cần điều chỉnh theo máy sẵn có.

## 5. B — IN-P04: triển khai miễn phí và CI/CD, đề xuất mới

Không kế thừa URL, whitelist chỉ-khách hoặc Cloudflare Quick Tunnel demo; không đề nghị duyệt lại topology đó. **Nhu cầu URL công khai của repo chính giữ nguyên**. Hosting cung cấp máy/lưu trữ chạy ứng dụng; tunnel/Funnel chỉ cung cấp đường vào máy đang chạy — không biến laptop thành dịch vụ cloud có bảo đảm hoạt động.

| Phương án mới | URL/HTTPS, MySQL/audio, Admin và CI/CD | Đánh giá cho mục tiêu 17/11 |
| --- | --- | --- |
| **H1 — khuyến nghị cho trình diễn miễn phí:** Linux containers trên laptop, đường công khai mới bằng **Tailscale Funnel** | [Funnel](https://tailscale.com/kb/1223/funnel) có HTTPS, tên miền ts.net, giới hạn băng thông không tự chỉnh và đang beta; [Personal](https://tailscale.com/pricing) công bố 0 USD. DB/audio ở volume laptop, không mất chỉ vì rebuild app. Dự kiến phục vụ cả khách và trang đăng nhập Admin cùng origin; API Admin vẫn Spring Security/session-cookie, không public DB/tệp riêng/actuator. | Không cần mua domain/VPS; phải do người dùng đăng ký/cài/cho phép sau khi chọn, laptop không ngủ và 4G hoạt động. Không bảo đảm 24/7, URL không bất biến nếu đổi máy/tailnet/tên. Phải kiểm tải audio/Range/cookie/4G thật; cần xem điều kiện Personal theo mục đích sử dụng. Chưa chọn hoặc triển khai. |
| **H2 — hosting thật có điều kiện:** Oracle Always Free VM + volume, ứng dụng/MySQL/audio trên VM | [OCI Always Free](https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm) có tài nguyên miễn phí nhưng có thể thiếu capacity/thu hồi VM nhàn rỗi; [Free Tier](https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier.htm) tách tài nguyên Always Free với trial. Cần tài khoản/xác minh thanh toán, firewall/SSH/HTTPS và kiểm CPU architecture của image; chưa có domain/URL được cấp. | Tách khỏi laptop/mạng 4G máy chủ, có thể đáp ứng CI/CD; đổi lại nhiều thao tác vận hành và không bảo đảm được cấp VM kịp. Không đăng ký hoặc bật trả phí. Chỉ chọn nếu người dùng có thể nhận tài nguyên đủ và chấp nhận điều kiện; không dùng làm đường găng hai tuần khi chưa có VM. |
| **H3 — Render Free** | [Render](https://render.com/docs/free) ngủ sau 15 phút không traffic, filesystem tạm và free không có persistent disk. Một web service không tự giải quyết MySQL/audio bền vững; cần dịch vụ lưu trữ khác hoặc trả phí. Admin web có thể truy cập khi được bảo vệ, nhưng việc đó không khắc phục persistence. | **Không khuyến nghị làm toàn bộ hệ thống với ngân sách 0**. Không thay MySQL bằng Postgres để hợp gói miễn phí; không ghi mất file sau redeploy là đã đáp ứng. Không hứa “hosting miễn phí hoàn chỉnh”. |

### 5.1. Hướng CI/CD mới để đưa vào TDD nếu chọn H1/H2

Đề xuất CI trên GitHub-hosted runner: build Vue, build/test Java/MySQL/Testcontainers, fixture TTS, gói artifact/image đúng commit SHA. CD có thao tác chủ động chọn bản CI đã đạt, rồi workflow tự triển khai vào môi trường demo, healthcheck và kiểm giữ DB/audio; thất bại giữ/quay về bản app trước phù hợp migration. Không tự rollback database bằng deploy code. Ghi run URL, SHA/digest, báo cáo test, deployment/health và kết quả rollback.

Với H1, đề xuất CD kết nối **riêng** từ GitHub-hosted runner đến máy đích qua Tailscale + SSH được giới hạn quyền; Funnel chỉ cho web, không mở SSH/MySQL ra Internet. [Tailscale GitHub Action](https://tailscale.com/kb/1276/tailscale-github-action) hỗ trợ node tạm và quy tắc truy cập; cấu hình Windows/WSL/SSH/OAuth thực tế chưa kiểm. Với H2 có thể dùng SSH đến VM theo quyền đã duyệt. Không đặt self-hosted runner lâu dài trên laptop repo public làm mặc định: [GitHub cảnh báo runner loại này dễ bị code PR không tin cậy xâm nhập](https://docs.github.com/en/actions/reference/security/secure-use).

Nút phê duyệt triển khai không thay cho tự động triển khai sau đó; có build/test hoặc file deploy chưa đủ chứng minh CD. Nhóm cần giải thích pipeline và đưa bằng chứng bản thật. [GitHub environments](https://docs.github.com/en/actions/reference/workflows-and-actions/deployments-and-environments) là nguồn cho bước kiểm soát môi trường; chưa tạo environment/secret/workflow. Cách kích hoạt/tối thiểu quyền/SHA pin là phần TDD, không là cấu hình đã duyệt ở đây.

### 5.2. Các điểm phải kiểm trước dùng đi chấm

- URL thật một origin HTTPS, trang khách và đăng nhập Admin, session/CSRF/proxy headers; không lộ DB, private audio root, API key hoặc Swagger không kiểm soát.
- Máy 16 GB: đề xuất chạy tuần tự test integration với DEV, không chạy nhiều MySQL/build song song không cần thiết; ước lượng dành 6–8 GB tổng cho Linux containers/WSL và giữ RAM cho Windows/IDE là giả định, **chưa cấu hình hoặc đo**, không là NFR bắt buộc.
- Đổi mạng 4G/restart app và restart đường vào: kiểm URL, QR, cookie/audio và persistence thật. URL đổi phải lấy lại đúng QR, không thêm dịch vụ chuyển hướng trái A05.
- Video/log là bằng chứng bổ sung, không thay URL công khai hoạt động nếu đó là điều kiện demo. Nếu 4G đứt, chẩn đoán nội bộ không được ghi thành đã nghiệm thu online; không tự thêm offline.
- Trường hợp H1 không đạt kiểm tra mạng hoặc rubric yêu cầu hosting độc lập laptop: phải báo và chọn H2 hoặc ngân sách được duyệt riêng, không đổi lời mô tả để coi tunnel là hosting.

## 6. C — Chính sách dữ liệu/file đề xuất mới, chờ duyệt

Không lấy log 14 ngày/7 bản ngày +4 bản tuần/RPO-RTO demo làm căn cứ đã chốt. Các giá trị ở bảng này là **đề xuất mới**, có thể duyệt/sửa theo từng dòng; không ghi vào AC bắt buộc trước xác nhận.

| Mã | Khuyến nghị chính | Lý do, điều kiện giữ/xóa và ảnh hưởng |
| --- | --- | --- |
| DATA-P01 — file hoàn chỉnh chưa từng áp dụng | Nếu còn được bản nháp/audio/hồ sơ công việc tham chiếu thì giữ. Chỉ sau khi bỏ liên kết có kiểm soát, không dùng chung và không tác vụ đang dùng, đánh dấu orphan; đề xuất dọn sau **24 giờ từ lúc xác nhận không còn tham chiếu**. | Cho cơ hội đối chiếu/lưu lại trong ngày; không tự hết hạn bản nháp chỉ vì chưa duyệt. Không bịa mốc “ngừng áp dụng” cho file chưa áp dụng, không thay 8 giờ của file từng dùng. Số lượng/dung lượng cần theo dõi khi triển khai. |
| DATA-P02 — file tạm và kết quả TTS muộn | File tạm của job đang chạy không dọn. Job đã kết thúc/hủy liên kết và không worker dùng: đề xuất dọn workspace sau **1 giờ**; kết quả hoàn chỉnh đến muộn của điểm đã xóa/bản không còn đích áp dụng đưa vào orphan DATA-P01, không phục hồi đối tượng hoặc tự áp dụng. | Tách output hoàn chỉnh khỏi file hỏng/tạm; kiểm tham chiếu/worker trước xóa. Không hứa nhà cung cấp hủy được; dọn lỗi giữ chờ/thử lại. Mốc job terminal và retry worker cần TDD, không gọi xóa bản ghi là đã xóa file. |
| DATA-P03 — log | Đề xuất log chẩn đoán/vận hành và an ninh tối thiểu **7 ngày từ phát sinh**, xoay dung lượng theo máy ở TDD. Ghi thời điểm, mức lỗi, route mẫu, mã lỗi, correlation ID; sự kiện Admin cần truy vết chỉ dùng mã tài khoản, không nội dung soạn. | Không log tên khách, tọa độ, text/prompt/audio, cookie/session credential, mật khẩu/key, raw query/body hoặc raw IP mặc định. Chỉ Thành/Hòa phục vụ xử lý lỗi theo nhiệm vụ được giao xem qua vận hành; không thêm trang log Admin hoặc lịch sử audio. Stacktrace phải lọc dữ liệu nhạy cảm. Log nền tảng/provider cần kiểm riêng, không tuyên bố xóa được hết bằng cấu hình app. |
| DATA-P04 — backup | Đề xuất snapshot **DB + file audio còn tham chiếu + manifest phiên bản/hash** nhất quán cuối ngày có thay đổi và trước deploy/migration; giữ mỗi snapshot **7 ngày từ tạo**, không tạo bộ bản tuần giữ dài hơn. Mã nguồn/tài liệu theo repo riêng, secret không nhét trong gói chia sẻ. | Cất bản mã hóa ở thiết bị/ổ khác được người dùng chọn, quyền Thành/Hòa theo nhiệm vụ; chưa có nơi thì chưa bảo vệ hỏng laptop. Không chỉ backup DB mà quên file. Đích **RPO ≤24 giờ, RTO ≤2 giờ** chỉ là mục tiêu đề xuất, phải diễn tập restore trước cam kết. |
| DATA-P04 — phiên/lượt trong backup và phục hồi | Đề xuất backup bao gồm phiên/lượt đang được giữ tại thời điểm chụp; snapshot tự hết hạn 7 ngày. Restore vào vùng riêng, đối chiếu manifest, loại phiên/lượt đã quá D08 **trước** cho truy cập rồi mới chuyển phục vụ. | D08 vẫn xóa nghiệp vụ sau 30 ngày từ kết thúc/hết hạn, không từ lúc audio hết. Snapshot cũ có thể giữ bản sao dữ liệu tới khoảng **30 + 7 ngày** tính từ kết thúc/hết hạn: đây là ảnh hưởng cần người dùng duyệt rõ, không gọi là đã giải quyết D08 cho mọi bản sao. Nếu không chấp nhận khoảng đuôi này, cần chọn loại trừ/ẩn danh phiên-lượt trong backup và chấp nhận không phục hồi thống kê lịch sử; không tự chọn thay. |

**Giữ nguyên đã chốt:** file từng áp dụng chỉ đủ điều kiện dọn sau database thành công, không tham chiếu/dùng chung và hết khoảng bảo vệ **8 giờ từ ngừng áp dụng cuối cùng**; phiên/lượt theo **D08 30 ngày**. Nghe tiếp khách không gia hạn phiên hoặc đổi mốc 30 ngày. Không dùng cùng một TTL cho workspace, orphan, log, backup và nghiệp vụ.

**Phụ thuộc cần TDD giải quyết, không đổi chính sách ngầm:** bài khách đang nghe có thể pause/resume sau hết hạn; file từng dùng có thể tới hạn dọn trong khi tab còn cần dữ liệu. Cần đánh giá tải đủ bài trong tab so với tham chiếu phục vụ tạm, Range/buffer, tab nền và việc không biết chắc đóng trang. Không tự đặt hạn nghe mới, kéo bảo vệ file vô hạn hoặc tuyên bố mọi mạng/browser phát liên tục. Nếu giải pháp buộc thay chính sách 8 giờ thì trình riêng; đây là cổng cần xử lý trước chốt phần phục vụ file, không chặn mọi phần TDD.

Các điều kiện giữ/xóa/thời hạn/quyền là **chính sách cần người dùng xác nhận**; nơi lưu, lịch job, transaction kiểm lại tham chiếu, mã hóa, khóa/lease, dung lượng xoay log và công cụ backup là cách làm trong TDD. Không coi quyền xem log vận hành là phân quyền nghiệp vụ Admin mới.
```

</details>

<a id="demo-source-hashes"></a>

## Phần lưu: demo-source-hashes

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

Các nguồn demo ở mục 2 được đọc từ đúng hai thư mục người dùng chỉ định; dòng nhạy cảm được lọc trước khi đưa vào kết quả. Không sao chép nội dung/code/audio/secret sang repo chính. Hash dưới đây chỉ để nhận diện file đọc, không chứng minh tính đúng hoặc phần mềm chạy:

| File local đã đọc | SHA-256 |
| --- | --- |
| Repo demo/README.md (phần liên quan) | 0AC75DC0262AEDA67697ECA73122EC5AC271D2A256CCD07C805E24642E1EBA25 |
| Repo demo/pom.xml  | FFE82FF6183BEC60D3F2F9069CE18A0D5B14F3F5DF44DB4823D14286C14B8B51 |
| Repo demo/src/main/resources/application.yaml  | C977F62760E08BD3F52320D4FB3DF83EAC8BD833866460387B44118573BAC33D |
| Repo demo/compose.yaml  | 31657B3559814171A263A5201DA44BE9707896EE21436BE98578F20AAA6E2A39 |
| Repo demo/.github/workflows/ci.yml  | 822F216D3D6C2FE8C919C5773624427C22AD247BDEA838E965DE40431339225E |
| Repo demo/deploy.ps1  | 2BDADAD73B3D4DEB4F34BF0AB66583102B42D169244896BF95B10C0ABA721E86 |
| Repo demo/deploy.sh  | BE3EB22E63A12E9A3268121B1BE0302F919E05773CACE59D66EDB057989C3CCC |
| Repo demo/src/main/java/vn/hue/qrguide/service/GeminiTtsClient.java  | 0CC14D2781A1F716A75943A022259BEBB30A8BEBAF41E1514B11D9F29CD3F0D3 |
| Repo demo/src/main/java/vn/hue/qrguide/service/AudioConverterService.java  | 78D116CDF66D042F3AC28D40C4EA1010F0E748715EE6F20640D3A5CB703F0E48 |
| Repo demo/src/main/java/vn/hue/qrguide/service/AudioPublishService.java  | 38A48A2ADAD4FF7CFC2E61CDDB87F9CE1117E9600543EFE841C0FDA34C5C7777 |
| Repo demo/src/main/java/vn/hue/qrguide/service/AudioStorageService.java  | DF3ECA95B49ED346887C29CBDD102F4DB715225011697118C0E9EBAB22F0177B |
| Tài liệu demo/TDD.md (phần liên quan) | 8BB01198F11202F4CEFF58C0D58950BACA04AAD280E3EF035953A471F9F4D339 |
| Tài liệu demo/TDD_APPROVAL.md  | EABEAD8F33FDF07ADD52387C0E3F84B72379F1E31DA7C48A987AAEB6CD794767 |
| Tài liệu demo/PROGRESS.md  | F6D1D304ADB870DD5CBA3BBAE11DF1CA112E7917A9C7F400A1EC335F2E244747 |
| Tài liệu demo/issue-evidence/T09/CONTENT_APPROVAL_2026-10-28.md  | 982FF7D231407D2CEEF03111E0887EB504ACBBAA0FD644272A897863F6ED381E |

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 03/11/2026 | Tạo khảo sát 0.1; ghi đầu vào TECH-02/TTS-01, đối chiếu demo chỉ đọc, nguồn chính thức, bộ phiên bản/môi trường/policy và lịch mục tiêu mới. Đã điều chỉnh theo bổ sung người dùng: model/Charon đã chốt, không kế thừa tunnel/log/backup/lịch demo. | Phương án IN-P/DATA-P và lịch khả thi còn chờ chọn; chưa gọi API, thử phần mềm, tạo TDD hoặc thực hiện kỹ thuật/Git/GitHub. |


