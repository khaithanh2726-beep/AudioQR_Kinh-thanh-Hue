# Thực hành 01 — Chuẩn bị nền dữ liệu và Bắt đầu phiên

**Phiên bản hướng dẫn:** 0.1; cập nhật điểm dừng 08/11/2026. **Trạng thái:** dự thảo hướng dẫn, đã tiếp tục thực hành theo yêu cầu người dùng; áp dụng [quy ước tại handoff mục 7](../01-tong-quan/handoff.md#guidance-protocol).
**Mục đích:** hướng dẫn triển khai và kiểm chứng lát cắt nền dữ liệu/Bắt đầu theo phân công G02. Các chặng và câu hỏi ôn tập hỗ trợ nhóm hiểu và giải thích thiết kế; không coi một lát cắt là toàn T01/T02/T03 hoàn tất.
**Quy trình triển khai:** phân công theo G02, bàn giao các nhóm nền theo commit-plan; triển khai và kiểm chứng trong môi trường tách biệt, dừng sau toàn mục checklist hoặc issue có bằng chứng để rà soát và chuẩn bị commit. Các chặng file/unit test dưới đây phục vụ truy vết kỹ thuật, không thay điểm hoàn thành của checklist.
**Đọc trước:** [DATA-05/LIFE-P01](../02-nghiep-vu/decisions.md#data-05), [TDD](TDD.md), [model](data-model.md#model-guest), [API/DTO](api-transactions.md#api-dto), [protocol](operations-design.md#ops-start-simple) và [ma trận kiểm](operations-design.md#ops-start-checks). Đọc mục tiêu trước, tra [điểm dừng](#practice-stop) để tiếp tục; [bằng chứng chi tiết](#practice-results) đặt sau nội dung hướng dẫn.

Mục lục: [1. Mục tiêu/lát cắt](#practice-slice) · [2. Môi trường](#practice-env) · [3. Các chặng/điểm dừng](#practice-stages) · [4. Cách kiểm/lỗi](#practice-evidence) · [5. Ôn tập](#practice-recap) · [Phụ lục A: bằng chứng theo thời điểm](#practice-results) · [Nhật ký](#practice-history).

<a id="practice-slice"></a>

## 1. Lát cắt Bắt đầu — đã có API backend, chưa frontend/DEV bền vững

Lát cắt gồm **API-K11 cấp vé, API-K01 Bắt đầu/replay, API-K02 xem trạng thái** với GUEST_SESSION lưu bền và kiểm backend/database. Không phục vụ nội dung chưa duyệt; bản đồ/audio/TTS/Admin/QR và CI/CD vẫn các chặng sau, không bị loại khỏi đồ án.

```mermaid
sequenceDiagram
    participant U as Tab hoặc client test
    participant C as Controller
    participant S as Service
    participant R as Repository
    participant D as MySQL
    U->>C: K11, quyền riêng tab
    C->>S: Cấp vé ký 24 giờ
    S-->>C: Vé đã ký
    C-->>U: Vé/requestId/issuedAt/ticketExpiresAt
    U->>C: K01, vé + tên/tiếng + cùng quyền
    C->>S: DTO hợp lệ
    S->>S: Kiểm signature/epoch/binding
    S->>R: Tra requestId
    R->>D: Tìm ledger session
    D-->>R: Có hoặc chưa có
    R-->>S: Ledger tương ứng
    S->>S: Có thì đối chiếu; chưa có kiểm vé còn hạn
    S->>R: Insert nếu được phép, TX/UNIQUE
    R->>D: Session + key/fingerprint/ticket metadata
    D-->>R: Commit hoặc xung đột
    R-->>S: Kết quả lưu/đọc
    S-->>C: Session/mốc gốc
    C-->>U: HTTP/DTO theo kết quả
```

Sơ đồ chỉ giải thích trách nhiệm, không network response database trả thẳng cho khách: Controller là nơi nhận DTO/trả HTTP; Service giữ quy tắc/TX; Repository truy vấn/ghi MySQL. Cấp vé không tạo session; Bắt đầu thành công mới tạo.

<a id="practice-env"></a>

## 2. Điều kiện môi trường và cách kiểm

Phần thực hành đầu tiên đã có **đầu ra do người dùng cung cấp** tại mục A.1–A.2: Java/Javac, Docker CLI/Compose/WSL và Docker Engine Linux; kết quả Wrapper bổ sung ở mục A.3. Các kết quả MySQL/build/test về sau nằm tại mục A.4 trở đi; [mốc API hiện tại](#practice-start-api) ghi riêng, chưa toàn tổ hợp dự án hoặc DEV bền vững. Không cài, bật service hoặc đổi phiên bản chỉ vì tên xuất hiện trong bảng; container/database kiểm thử tách biệt theo [quyền hiện hành](../01-tong-quan/handoff.md#guidance-protocol). Windows/VS Code/PowerShell giữ theo TECH-02; mốc đã chọn ở [technical-inputs mục 4](../01-tong-quan/technical-inputs.md#version-proposal). Nếu anchor nguồn thay đổi, đọc đúng mục 4 qua mục lục, không tự thay mốc.

Bảng sau ghi các lệnh kiểm môi trường để tra cứu hoặc tái hiện khi cần. Các kết quả đã có được lưu ở phụ lục A; chỉ kiểm lại khi có thay đổi hoặc lý do kỹ thuật. Nếu tới bước sau Windows mời cài/cập nhật thì ghi thông báo, chưa tự chấp nhận thay đổi.

| Lệnh bạn tự chạy | Để biết gì | Đối chiếu / nếu lỗi |
| --- | --- | --- |
| java -version | Runtime Java thực được PATH chọn | Java 21 theo TECH-01; JRE khác bản hoặc không tìm thấy thì ghi lại, chưa tự đổi |
| javac -version | Có JDK/compiler, không chỉ runtime | Cùng Java 21; thiếu javac thì chưa sẵn sàng build backend |
| docker --version | Docker CLI có mặt | Có CLI không chứng minh Docker Desktop/engine chạy |
| docker compose version | Compose plugin | Tài liệu mốc ghi Compose v2; máy báo v5.5.1. Ghi nhận khác số major và đối chiếu nguồn ở A.1, chưa tự đổi/hạ phiên bản hoặc đánh dấu tương thích |
| wsl --status | Trạng thái WSL trên Windows | Chưa có/không đủ quyền/đòi cài thì ghi thông báo, không dùng bước này để tự cài Linux |

Không cần kiểm Maven global để thay Wrapper: backend và các script Wrapper đã có; distributionUrl trỏ Maven 3.9.12 và đã có kết quả chạy đúng mốc ở mục A.3. Frontend chưa tạo. Không lấy cài được Java/Docker hoặc xem được phiên bản Maven làm TDD đã tương thích. Không chạy project demo để có kết quả thay thế.

<a id="practice-env-results"></a>

<a id="practice-stages"></a>

## 3. Các chặng triển khai và điểm dừng

Các bước đã có bằng chứng được giữ để hiểu/tái hiện khi cần, không là yêu cầu chạy lại. Kiểm chứng tuân thủ quyền tại handoff; người vận hành trực tiếp thực hiện Git/database đang dùng/TTS/triển khai. Điểm tiếp tục hiện tại ở mục 3.1.

| Chặng | Bạn sẽ làm gì / vì sao | Cách nhận biết xong / giới hạn |
| --- | --- | --- |
| P01-ENV | Kiểm công cụ bằng mục 2, giải thích JDK/Wrapper/Compose/WSL; xử lý từng lỗi trước khi đi tiếp | Có bảng kết quả thật và mốc đúng; mốc này chỉ kiểm công cụ; kết quả build/test được ghi riêng ở phụ lục A |
| P01-BASE | Chuẩn bị backend skeleton, Wrapper đúng mốc, Spring Web/JPA/Flyway/MySQL/test theo BOM; cấu hình DEV/secret qua hướng dẫn riêng | Build/test theo quyền tại handoff; chạy môi trường phục vụ do người dùng thực hiện; không tự thêm JWT/Redis hoặc copy demo |
| P01-DATA | Mapping GUEST_SESSION + migration đầu và Repository tìm startRequestKey; DB UNIQUE thực | DB tái tạo được trên môi trường test, constraint được kiểm thật; không chỉ chú thích unique trên DTO |
| P01-TICKET | Tạo/check vé ký theo protocol, clock kiểm thử và serviceEpoch runtime bền | Vé không đọc secret từ client; issuedAt+24 giờ, signature/binding đúng, không start_generation/context. Không log vé/quyền/khóa |
| P01-START | Service lookup → compare fingerprint hoặc validate first-create expiry → TX insert; xử lý unique collision và rollback/read mới | Hai lệnh cùng key một session; khác body không sửa phiên. Controller không gọi Repository để bypass |
| P01-HTTP | K11/K01/K02 trả DTO/mã lỗi theo API; giữ hai timestamp ticket/session rõ | Request thành công/invalid/expired/lookup cũ được quan sát; chưa frontend thực hoặc hai tab browser đã đạt |
| P01-CHECK | Thực hiện V-START01.01–10 phù hợp từng lớp; backend trước, browser khi có form | Ghi từng ca đạt/chưa đạt/chưa chạy, không tự đánh toàn AC/T02/T03 hoàn tất |

<a id="practice-initializr"></a>
<a id="practice-wrapper-pin"></a>
<a id="practice-stop"></a>

### 3.1. Điểm dừng backend và chuẩn bị tiếp tục commit

- Java/Javac 21.0.12.1; Docker CLI/Engine 29.8.1; Compose 5.5.1; WSL mặc định 2; Engine OSType=linux: theo hồ sơ kiểm môi trường đã ghi nhận.
- Backend đã có ở đúng cấp repo; pom.xml chọn Boot 4.1.2/Java 21. Các file được giữ nguyên, không tạo lại/giải nén lại.
- Đã đọc backend/.mvn/wrapper/maven-wrapper.properties: distributionUrl trỏ **Maven 3.9.12**. Wrapper chạy đúng mốc và Maven test skeleton đạt theo mục A.3/1.5.
- TestcontainersConfiguration.java dùng **mysql:8.4.13**; image đã tải và MySQL đã chạy/kết nối trong test theo mục A.4/1.5.
- Đã biên dịch/test contextLoads(), chạy HTTP cùng MySQL tạm, áp dụng V1 và 4 test ràng buộc đạt theo mục A.8–A.9. Entity/Repository và 2 test mapping sau sắp xếp package theo lớp đạt trong bộ 7 test tại [mục A.10](#practice-mapping-test-result), tiếp tục đạt trong bộ mới.
- K11/K01/K02 và Service/ticket/binding/ledger/TX đã có [64 test ngày 05/11](#practice-start-api-result). K04 đạt [88 test lúc 04:16:40+07 ngày 08/11](#practice-end-session-result), gồm 24 ca mới. Nền địa điểm/vùng V2 đạt 104 test lúc 17:02:17+07, [bằng chứng](data-model.md#place-region-test-result). Nền xác thực đạt 122 test lúc 17:45:42+07, window/receipt/tạo điểm ẩn đạt 147 test lúc 18:20:20+07. Mốc mới là [168 test cùng đọc/sửa tên địa điểm](api-transactions.md#admin-place-test-result), điểm rà ở handoff mục 2.9. Toàn T02/T03/T05, frontend/DEV bền vững/restart thật/D08/restore còn cần kiểm chứng.

[Lịch sử tạo ZIP, đối chiếu và lệnh giải nén/chỉnh Wrapper trước đây](../99-luu-tru/document-history.md#practice-skeleton-history) được giữ theo thời điểm. Package thực tế theo [TDD 3.1](TDD.md#tdd-packages). Snapshot Git trước lần tạo lại repository tại handoff mục 2.2 được giữ riêng với mốc T10 mới. Đã tiếp tục đến đọc/sửa tên địa điểm với If-Match, đạt [168 test lúc 20:16:49+07 ngày 08/11](api-transactions.md#admin-place-test-result). Điểm rà backend ở [handoff mục 2.9](../01-tong-quan/handoff.md#admin-place-result), tiếp theo dự kiến là lưu/đọc/áp dụng bản vùng A12–A14. Nền T10 đã commit/push theo [mục 2.11](../01-tong-quan/handoff.md#current-t10-commit-result); commit-plan đã khôi phục, nhóm tài liệu đang được rà trước T01 theo [mục 2.12](../01-tong-quan/handoff.md#document-review-result).

### 3.2. Nền dữ liệu, vé ký và API — thực hiện tăng dần

Các chặng dưới đây giữ hướng dẫn và kết quả tại thời điểm triển khai. Những câu như “chưa kiểm” ở bước chuẩn bị phản ánh mốc đó; trạng thái hiện tại xem mục 3.1 và bằng chứng cuối file. Không chạy lại một chặng chỉ vì đang đọc hướng dẫn cũ, không dùng lệnh của chặng này thay quy trình commit hiện hành.

GUEST_SESSION phần Bắt đầu cần: id, start_request_key UNIQUE, credential_hash UNIQUE, service_epoch, start_fingerprint, start_ticket_issued_at, start_ticket_expires_at, start_ticket_digest, display_name, initial_language, started_at, expires_at, ended_at nullable. Mapping UUID/UTC và validation theo model; không lấy tên UNIQUE hoặc nhận client startedAt.

Mô hình đầy đủ còn current opening/nonce/lượt/grant. Migration tăng dần phải bổ sung quan hệ/FK khi bảng đích được tạo ở chặng tương ứng; **không tạo FK tới bảng chưa có**, không bỏ vĩnh viễn FK đã thiết kế hoặc tạo dữ liệu giả để chặn build. Lưu rõ phần còn thiếu của mô hình, không gọi migration phần Bắt đầu là toàn T02.

ServiceEpoch/khóa ký giữ trong runtime được bảo vệ ngoài repo theo thiết kế; chưa có bằng chứng cấu hình môi trường hoặc key thật được áp dụng. Quy trình restore epoch của REC-P01 vẫn cần chặng kiểm riêng.

<a id="practice-first-migration"></a>

#### 3.2.1. Nhiệm vụ nhỏ — tạo bảng guest_session bằng Flyway

**Trạng thái:** V1 đã có theo thiết kế; kết quả áp dụng version 1/test đạt tại [mục A.8](#practice-v1-result). Không sửa V1 trong lượt viết test ràng buộc tiếp theo. Phần lệnh/cách kiểm dưới đây giữ để tra cứu bước đã đạt, không yêu cầu chạy lại chỉ để lặp bằng chứng.

**Căn cứ:** [mô hình phiên](data-model.md#model-guest), [protocol Bắt đầu](operations-design.md#ops-start-simple), phạm vi 13 cột ở mục 3.2. Flyway dùng `src/main/resources/db/migration` với tên `V<version>__<description>.sql` theo [Spring Boot — Database Initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway), kiểm 05/11/2026. Đây là migration ứng dụng, không đặt trong target hoặc src/test/resources.

**Mapping vật lý cho lát cắt này — lựa chọn triển khai, chưa chạy kiểm:** id/request key là UUID lưu BINARY(16); credential hash/fingerprint/ticket digest dùng SHA-256 lưu 32 byte BINARY(32), không lưu credential/vé thô. service_epoch chọn UUID dạng chuỗi chuẩn 36 ký tự do runtime cấp/giữ bền; thời gian UTC DATETIME(6), do ứng dụng chịu trách nhiệm chuyển UTC, kiểu DATETIME không tự làm việc này. display_name dùng LONGTEXT/utf8mb4 để giới hạn ký tự của cột không bị dùng thay quy tắc 1–50 grapheme tại Service. Cột ngôn ngữ ASCII phân biệt hoa/thường và CHECK chỉ nhận vi/en/fr/ru. PK/UK ở DB là bước đầu; chưa kiểm duplicate/concurrency. Lựa chọn hash/epoch này phải dùng nhất quán khi viết Entity/ticket sau, không thay quy tắc phiên/vé/retention.

**SQL thực tế:** [V1__create_guest_session.sql](../../backend/src/main/resources/db/migration/V1__create_guest_session.sql) là nguồn nội dung migration hiện tại; đã tạo đúng mẫu đã hướng dẫn, UTF-8. Bỏ bản SQL chép trong hướng dẫn để tránh hai bản lệch nhau; phần giải thích mapping ở trên vẫn áp dụng. Bạn mở file để đọc trước chạy, không cần tự tạo/chép lại.

Không thêm FK tới opening chưa tồn tại, cột current_language/nonce/seq ở lát cắt tương tác sau; không bỏ chúng khỏi mô hình đầy đủ. Chưa có insert/seed, Entity hoặc API trong nhiệm vụ này. Quy tắc tên, chữ ký/fingerprint, thứ tự lookup, vé 24 giờ/phiên 8 giờ và endedAt hợp lệ vẫn phải thực hiện/kiểm riêng, không tuyên bố DDL trên đã bảo đảm chúng.

Bạn chạy `.\mvnw.cmd test` tại `backend`, Docker Engine vẫn hoạt động. Lệnh nạp context và áp dụng migration trên MySQL do Testcontainers quản lý; kết quả mong đợi Flyway tìm/áp dụng migration version 1 (guest session), không còn No migrations found; test 1/0/0/0 và BUILD SUCCESS. Kết quả này chỉ chứng minh migration được áp dụng khi nạp context, chưa chứng minh UK/CHECK xử lý dữ liệu sai/concurrency.

**Gửi lại:** đoạn log Flyway validate/migrate/schema version và kết quả test/BUILD; nếu lỗi gửi khối lỗi và Caused by liên quan. Nếu log vẫn 0 migration, đối chiếu file đã lưu/được copy từ src/main/resources, đúng tên V1 với hai dấu gạch dưới và đuôi .sql. Chưa chạy Flyway clean/repair, đổi phiên bản hay dùng ddl-auto=update để vượt lỗi.

**Ôn tập:** vì sao UNIQUE ở database vẫn cần dù sau này Service có bước kiểm requestId đã tồn tại?

<a id="practice-schema-tests"></a>

#### 3.2.2. Nhiệm vụ nhỏ — kiểm ràng buộc bằng dữ liệu thật trong MySQL test

**Trạng thái:** [GuestSessionSchemaTests.java](../../backend/src/test/java/vn/hueqr/backend/schema/GuestSessionSchemaTests.java), đủ 4 ca đạt trước sắp xếp package theo [mục A.9](#practice-schema-test-result), tiếp tục đạt trong bộ 7 test sau sắp xếp tại [mục A.10](#practice-mapping-test-result). Dùng JdbcTemplate gửi INSERT/SELECT xuống MySQL 8.4.13; riêng lớp này chưa kiểm mapping Entity/Repository. Lệnh/cách kiểm bên dưới giữ cho tra cứu giai đoạn 5 test, không phải yêu cầu chạy lại.

| Test mới | Điều cần chứng minh khi chạy |
| --- | --- |
| allowsIndependentSessionsWithTheSameVietnameseDisplayName | Hai phiên khác ID/request key/credential hash được phép cùng tên; đọc lại đúng tên Unicode |
| rejectsDuplicateStartRequestKey | INSERT thứ hai giữ request key nhưng đổi ID/hash bị đúng UK start_request_key chặn |
| rejectsDuplicateCredentialHash | INSERT thứ hai đổi ID/request key nhưng giữ hash bị đúng UK credential_hash chặn |
| rejectsLanguageOutsideTheSupportedSet | `de` vừa đúng độ dài cột nhưng ngoài vi/en/fr/ru, phải bị CHECK ngôn ngữ chặn |

Các test lỗi kiểm cả mã lỗi MySQL và tên ràng buộc để lỗi SQL/kết nối hoặc nhầm PRIMARY KEY không bị tính là đạt: **1062** cho duplicate key, **3819** cho CHECK theo [MySQL 8.4 Error Reference](https://dev.mysql.com/doc/mysql-errors/8.4/en/server-error-reference.html). Hash 32 byte trong fixture là dữ liệu tổng hợp, không phải credential thật hoặc phần cài đặt SHA-256. Timestamp fixture theo UTC quy ước, chưa kiểm mapping thời gian Entity. Dữ liệu chỉ được INSERT khi người dùng chạy test.

Lớp test dùng `@Transactional`: mỗi ca có giao dịch và mặc định rollback sau test theo [Spring TestContext](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html); không phụ thuộc thứ tự ca. Sau INSERT lỗi không truy vấn tiếp trong test đó. Đây là kiểm ràng buộc tuần tự ở DB, chưa là kiểm concurrent/replay/TX của Service, chưa V-START01.01–10 hoàn tất. Hai nguồn kỹ thuật trên đối chiếu ngày 05/11/2026.

**Bạn chạy:** `.\mvnw.cmd test` tại backend, Docker Engine vẫn hoạt động. Mong đợi 4 test mới và contextLoads() cũ: **tổng 5 test, 0 failures/errors/skipped, BUILD SUCCESS**. Nếu khác số test, kiểm log tên hai lớp và báo cáo Surefire thay vì suy tất cả đã chạy. Chỉ lặp test skeleton vì bộ kiểm đã thay đổi, không yêu cầu pull/chạy HTTP lại.

**Gửi lại:** phần kết quả của GuestSessionSchemaTests/BackendApplicationTests và tổng test/BUILD; nếu lỗi gửi tên ca, lỗi assertion/SQL cùng Caused by. Chờ kết quả trước bước Entity/Repository.

**Ôn tập:** vì sao ca ngôn ngữ sai dùng `de` thay vì một chuỗi dài hơn hai ký tự?

<a id="practice-jpa-mapping"></a>

#### 3.2.3. Nhiệm vụ nhỏ — Entity/Repository và kiểm đọc lại dữ liệu

**Trạng thái:** đã biên dịch/chạy sau sắp xếp package theo [mục 3.2.4](#practice-package-layout); hai test Repository đạt trong bộ 7 test theo [bằng chứng mục A.10](#practice-mapping-test-result). Mapping và migration được giữ nguyên trong bước tổ chức package. File thực tế:

- [GuestSession.java](../../backend/src/main/java/vn/hueqr/backend/entity/GuestSession.java): `@Entity` ánh xạ đúng 13 cột V1, field access; constructor rỗng protected cho JPA, constructor nhận dữ liệu do Service chuẩn bị. UUID/hashes BINARY, epoch/ngôn ngữ CHAR, các mốc `Instant` qua JDBC TIMESTAMP/DATETIME(6); byte[] được sao chép khi nhận/trả. Chưa xác minh vé hoặc tính hạn trong Entity, không dùng Entity làm DTO HTTP.
- [GuestSessionRepository.java](../../backend/src/main/java/vn/hueqr/backend/repository/GuestSessionRepository.java): JpaRepository và `findByStartRequestKey(UUID)` trả Optional. Spring Data tạo query từ tên thuộc tính theo [query methods](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html); chưa lookup/replay Service hoặc giải quyết unique race.
- [GuestSessionRepositoryTests.java](../../backend/src/test/java/vn/hueqr/backend/repository/GuestSessionRepositoryTests.java): 2 ca tích hợp có rollback. Ca đầu saveAndFlush, clear persistence context rồi tra request key, đối chiếu UUID/hash/tên/tiếng/tất cả timestamp/endedAt null; đọc started_at bằng JDBC LocalDateTime và so giá trị UTC để tránh round-trip Java che lỗi múi giờ. Ca hai tra key chưa có trả Optional.empty. Fixture Instant có microsecond; hash là dữ liệu tổng hợp, không credential thật.

**Cấu hình đã sửa:** application.properties giữ app name/OSIV false, thêm `spring.jpa.hibernate.ddl-auto=validate` và `spring.jpa.properties.hibernate.jdbc.time_zone=UTC`. Flyway tạo/sửa schema; Hibernate chỉ đối chiếu mapping khi nạp context. `@JdbcTypeCode(TIMESTAMP)` cùng JDBC time_zone=UTC dùng đường đọc/ghi timestamp rõ múi giờ cho DATETIME(6), kiểu cột tự nó không chứa múi giờ. Căn cứ [Hibernate 7.4 — JDBC timezone](https://docs.hibernate.org/orm/7.4/userguide/html_single/#jdbc-time-zone), [Spring Boot — database initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html), đối chiếu 05/11/2026; không đổi phiên bản thư viện theo bản trang tài liệu. Cấu hình/mapping đã chạy đạt trong phạm vi bộ test tại mục A.10.

**Đã kiểm:** bộ 7 test sau sắp xếp tại mục A.10 đạt; lệnh kiểm hồi quy khi có thay đổi là `.\mvnw.cmd clean test` tại backend theo mục 3.2.4, giữ Docker Engine hoạt động. Scan được 1 JPA Repository, Flyway v1, context với Hibernate validate nạp thành công. Test dùng các mốc fixture đúng 24 giờ/8 giờ nhưng chưa chứng minh Service tự thực hiện đúng các quy tắc đó. Không yêu cầu chạy lại lúc này hoặc sửa V1.

**Phản hồi đã nhận:** kết quả tổng test/BUILD và báo cáo Surefire đối chiếu khớp tại mục A.10. Khi có lần kiểm sau bị lỗi schema-validation, UTC assertion hoặc query method, gửi tên ca/khối lỗi và Caused by; chưa dùng ddl-auto=update để vượt lỗi.

**Ôn tập:** vì sao phải flush rồi clear trước đọc lại, và vì sao còn kiểm giá trị UTC trực tiếp trong database?

<a id="practice-package-layout"></a>

#### 3.2.4. Sắp xếp package chia theo lớp trước — đã kiểm, 7 test ĐẠT

**Cấu trúc package hiện hành:** chia theo lớp trước, được xác nhận ngày 05/11/2026. [Quy tắc, cây thực tế và phần dự kiến tại TDD mục 3.1](TDD.md#tdd-packages). Entity nằm trong entity, Repository trong repository, hai lớp test trong repository và schema trực tiếp dưới package gốc vn.hueqr.backend; package/import đã được cập nhật. Không còn package guestsession bao ngoài. Không thay mapping, logic/assertion/fixture của test hoặc migration V1, không tạo thư mục/lớp rỗng.

[TestcontainersConfiguration.java](../../backend/src/test/java/vn/hueqr/backend/support/TestcontainersConfiguration.java) đã ở support và là **lớp public** từ lượt trước, giữ nguyên để import từ các package mới và TestBackendApplication. Phương thức Bean giữ nguyên; BackendApplicationTests và TestBackendApplication vẫn ở package gốc, import cấu hình support. Test JUnit vẫn package-private. Spring Boot configuration vẫn ở gốc vn.hueqr.backend, chưa thêm scan hoặc đổi cơ chế khởi động. Test Service/Controller bổ sung sau trong service/controller; test nhiều thực thể nhóm theo mục đích, không theo từng thực thể bắt buộc.

**Bằng chứng:** bộ 5 test trước thay đổi package giữ ở mục A.9. Sau thay đổi, người dùng gửi BUILD SUCCESS ngày 05/11/2026 lúc 13:35:16+07:00; Surefire TXT/XML khớp đủ 7 ca, gồm 2 ca Repository tại [mục A.10](#practice-mapping-test-result). Các tên package trong log/báo cáo lịch sử giữ đúng mốc cũ.

**Lệnh kiểm hồi quy, đã có kết quả, không yêu cầu chạy lại lúc này:** `.\mvnw.cmd clean test` tại backend, Docker Engine hoạt động. `clean` xóa đầu ra biên dịch cũ trong target trước compile để tránh lớp/test package cũ còn lại sau di chuyển; không xóa database hay image Docker. Đã xác nhận scan 1 JPA Repository, schema v1, context nạp với validate thành công và:

| Lớp test theo package mới | Số ca đã đạt |
| --- | --- |
| vn.hueqr.backend.BackendApplicationTests | 1 |
| vn.hueqr.backend.schema.GuestSessionSchemaTests | 4 |
| vn.hueqr.backend.repository.GuestSessionRepositoryTests | 2 |

Tổng **7 test, 0 failures/errors/skipped, BUILD SUCCESS**, theo bằng chứng mục A.10. Kết quả đã được đối chiếu với phạm vi T02, chưa đủ điều kiện tích thêm toàn ô checklist; kết quả không xác nhận Service/ticket/API hoặc toàn issue đã hoàn tất.

**Ôn tập:** vì sao cấu hình dùng chung cần public, còn lớp test JUnit có thể giữ package-private?

<a id="practice-ticket-time"></a>

#### 3.2.5. P01-TICKET — bước 1: Clock và quy tắc thời gian vé, đã kiểm ĐẠT

**Trạng thái:** lớp test được biên dịch và chạy riêng ngày 05/11/2026: **8 ca đạt, BUILD SUCCESS lúc 13:57:40+07:00**, Surefire TXT/XML khớp tại [mục A.11](#practice-ticket-time-result). Bộ 7 test ở mục A.10 là lần chạy riêng trước bước này. Nhiệm vụ tách quy tắc thời gian để kiểm mốc chính xác, trước khi triển khai payload/chữ ký, credential binding, khóa ký/epoch bền vững và API.

**Căn cứ:** [DATA-05](../02-nghiep-vu/decisions.md#data-05), [operations A.1](operations-design.md#ops-start-simple), [API 9.1–9.2](api-transactions.md#api-dto). Vé có hạn đúng 24 giờ từ issuedAt, không issuedAt ở tương lai; đúng ticketExpiresAt đã hết hạn cho lần tạo đầu. Tra ledger đã có vẫn có thể trả kết quả gốc sau khi vé hết hạn, nếu đủ signature/quyền/epoch/fingerprint. Không thay hạn phiên 8 giờ hoặc chính sách D08.

**File đã thêm:**

- [StartTicketTimePolicy.java](../../backend/src/main/java/vn/hueqr/backend/service/StartTicketTimePolicy.java): lớp Java trong service, nhận Clock qua constructor; chưa đăng ký Spring bean. `issueTimes()` lấy clock phía server, cắt phần nhỏ hơn microsecond cho khớp DATETIME(6) trước khi sau này ký/lưu, trả cặp issuedAt/ticketExpiresAt đúng 24 giờ. `TicketTimes` là record bất biến nội bộ, không phải DTO API hoặc bằng chứng vé hợp lệ.
- `validateTimes()` kiểm đúng khoảng 24 giờ và issuedAt không tương lai, **không từ chối chỉ vì vé hết hạn**. `isExpiredForFirstCreation()` dùng một mốc now trong mỗi lần gọi, kiểm các điều kiện trên rồi trả true khi now >= ticketExpiresAt; sau này chỉ gọi ở nhánh chưa có ledger, sau kiểm signature/quyền/epoch. Các ngoại lệ thời gian chưa ánh xạ sang HTTP; lớp này không xác minh chữ ký/quyền, không đọc/ghi session và không tự quyết định được tạo phiên.
- [StartTicketTimePolicyTests.java](../../backend/src/test/java/vn/hueqr/backend/service/StartTicketTimePolicyTests.java): 8 unit test dùng Clock.fixed; không nạp Spring, không kết nối database hoặc Docker.

**Kiến thức mới:** truyền Clock vào thay vì gọi giờ hệ thống rải rác giúp kiểm đúng biên 24 giờ mà không đợi hoặc sửa giờ máy. Instant biểu diễn mốc thời gian, không đổi chỉ vì clock dùng múi giờ Việt Nam. record giữ cặp mốc bất biến. Căn cứ [Java 21 Clock](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Clock.html) và [Instant](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Instant.html), đối chiếu 05/11/2026; cách cắt microsecond là lựa chọn triển khai khớp V1 hiện có, không thay migration.

**Lệnh đã có kết quả tại backend, giữ để tra cứu, không yêu cầu chạy lại lúc này:**

```powershell
Set-Location -LiteralPath 'C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue\backend'
.\mvnw.cmd "-Dtest=StartTicketTimePolicyTests" test
```

Lệnh chọn chạy riêng lớp mới; Maven vẫn biên dịch mã nguồn/test liên quan, nhưng các test MySQL không được chọn chạy. Không cần Docker cho lượt unit test này. Không chạy full suite hoặc pull image để kiểm phần thời gian.

| Ca kiểm | Kết quả mong đợi |
| --- | --- |
| Cấp mốc từ clock có nanosecond | issuedAt cắt về microsecond, ticketExpiresAt đúng +24 giờ |
| Cùng instant, clock UTC và Asia/Ho_Chi_Minh | Cùng cặp mốc, không lệch 7 giờ |
| Trước hạn 1 microsecond | Chưa hết hạn cho lần tạo đầu |
| Đúng hạn | Đã hết hạn cho lần tạo đầu |
| Sau hạn 1 microsecond | Đã hết hạn cho lần tạo đầu |
| Vé cũ nhưng cặp mốc hợp lệ | validateTimes không chặn chỉ vì expiry; nhánh tạo đầu vẫn báo expired, chưa kiểm replay thật |
| issuedAt tương lai | Bị từ chối ở cả kiểm thời gian và nhánh kiểm tạo đầu |
| Khoảng hạn ngắn/dài hơn 24 giờ | Bị từ chối |

**Đã nhận:** kết quả lớp `vn.hueqr.backend.service.StartTicketTimePolicyTests` và tổng **8 test, 0 failures/errors/skipped, BUILD SUCCESS** tại mục A.11. Đây là 8 ca được chọn, không phải bộ 7 ca trước được chạy lại hoặc một lần chạy tổng 15 ca. Khi kiểm hồi quy sau này có lỗi, gửi tên ca và khối compile/assertion liên quan.

Bước thời gian đã có bằng chứng; tiếp tục bộ ký ở mục 3.2.6. Chưa hoàn thành toàn P01-TICKET hoặc các ca tích hợp V-START01; không nhắc tích checklist tổng hợp chỉ từ unit test thời gian. Giữ dependency, V1, Entity/Repository, cấu hình chạy và test cũ; chưa sinh khóa/epoch.

**Ôn tập:** vì sao không được dùng kiểm hết hạn vé để chặn mọi yêu cầu trước khi tra ledger? Clock.fixed giúp kiểm điều gì mà chờ thời gian thật khó kiểm chính xác?

<a id="practice-ticket-signer"></a>

#### 3.2.6. P01-TICKET — bước 2: bộ ký HmacSHA256, đã kiểm ĐẠT

**Trạng thái:** lớp test đã được biên dịch và chạy riêng, **9 ca đạt, BUILD SUCCESS lúc 14:07:51+07:00**, Surefire TXT/XML khớp tại [mục A.12](#practice-ticket-signer-result). Căn cứ [operations A.1](operations-design.md#ops-start-simple): payload do server định nghĩa, HmacSHA256 cố định, khóa runtime được bảo vệ/giữ qua restart; không lấy thuật toán từ client hoặc đổi Admin sang JWT.

**File đã thêm:**

- [StartTicketSigner.java](../../backend/src/main/java/vn/hueqr/backend/service/StartTicketSigner.java): lớp Java trong service, constructor nhận khóa byte[] và sao chép để bên gọi không sửa khóa nội bộ; lựa chọn kỹ thuật yêu cầu khóa ít nhất 32 byte, chữ ký đầy đủ 32 byte. Kiểm độ dài không chứng minh khóa có entropy tốt; khóa ngẫu nhiên thật/cách lưu bền sẽ triển khai ở bước cấu hình riêng.
- `sign(payload)` dùng JCA Mac với HmacSHA256, tạo Mac riêng mỗi lần gọi vì đối tượng xử lý có trạng thái. `verify(payload, signature)` từ chối tag thiếu/sai độ dài rồi dùng MessageDigest.isEqual đối chiếu tag đầy đủ. Không sinh khóa ngầm, không getter/log key, chưa Spring bean hoặc endpoint ký payload tùy ý từ client.
- [StartTicketSignerTests.java](../../backend/src/test/java/vn/hueqr/backend/service/StartTicketSignerTests.java): 9 unit test; khóa là fixture tổng hợp công khai, không dùng cho runtime. Một ca so sánh trực tiếp với **RFC 4231 mục 4.7, Test Case 6** thay vì chỉ ký rồi tự kiểm cùng một implementation. Các ca khác kiểm cùng key qua hai instance, payload/signature bị sửa, sai key, tag thiếu/ngắn/dài, sao chép key đầu vào, từ chối key thiếu/ngắn và các payload liên tiếp không nhiễm trạng thái.

**Kiến thức:** HMAC kiểm toàn vẹn bằng khóa bí mật, không mã hóa payload hoặc tự chứng minh quyền tab. Nguồn đối chiếu 05/11/2026: [Java 21 Mac](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/Mac.html), [MessageDigest.isEqual](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/MessageDigest.html#isEqual(byte%5B%5D,byte%5B%5D)), [vector RFC 4231](https://www.rfc-editor.org/rfc/rfc4231.txt). Cách dùng JCA là triển khai kỹ thuật theo hướng đã ghi, không thay nghiệp vụ.

**Phạm vi bước 2:** chỉ ký/kiểm mảng byte, chưa nối Clock, requestId, credentialHash, serviceEpoch hoặc expiry/ledger, chưa kiểm restart/restore thực, chưa cấp vé K11 hoặc tạo phiên K01. Định dạng/encoding/version được triển khai tiếp ở mục 3.2.7, chờ kiểm riêng. Hai instance cùng key trong unit test không chứng minh khóa đã được lưu bền. Giữ dependency, file thời gian/test cũ, V1, mapping và cấu hình chạy.

**Lệnh đã có kết quả tại backend, giữ để tra cứu, không yêu cầu chạy lại lúc này:**

```powershell
Set-Location -LiteralPath 'C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue\backend'
.\mvnw.cmd "-Dtest=StartTicketSignerTests" test
```

Không cần Docker; lệnh đã chạy riêng **9 test, 0 failures/errors/skipped, BUILD SUCCESS** theo mục A.12. Không cộng với báo cáo cũ để nhận là full suite đã chạy. Khi kiểm hồi quy sau này có lỗi, gửi tên ca và khối compile/assertion liên quan. Không gửi khóa runtime; fixture không cần cấu hình khóa thật.

Bộ ký đã có bằng chứng; tiếp tục codec định dạng tại mục 3.2.7. Chưa có ô checklist tổng hợp mới đủ căn cứ để nhắc tích.

**Ôn tập:** vì sao một test ký rồi tự kiểm vẫn có thể bỏ sót lỗi, và vector có kết quả chuẩn giúp gì? HMAC có che nội dung payload khỏi người cầm vé không?

<a id="practice-ticket-codec"></a>

#### 3.2.7. P01-TICKET — bước 3: ghép định dạng vé/version và HMAC, đã kiểm ĐẠT

**Trạng thái kiểm chứng:** **14 ca đạt, BUILD SUCCESS lúc 14:32:52+07:00**, bằng chứng [mục A.13](#practice-ticket-codec-result). Đã rà comment tiếng Việt, giữ logic/assertion. Mục tiêu nhỏ: ghép các field đã chuẩn bị ở server thành chuỗi vé rồi đọc lại sau kiểm toàn vẹn. [Định dạng v1 và quy tắc biểu diễn tại operations](operations-design.md#ops-start-ticket-format) là vị trí chính, không sửa yêu cầu DATA-05/K11/K01.

**File đã thêm:**

- [StartTicketCodec.java](../../backend/src/main/java/vn/hueqr/backend/service/StartTicketCodec.java): nhận StartTicketSigner qua constructor; encode thành hai phần Base64URL không padding, decodeAndVerify kiểm HMAC trước khi trả field. Payload nội bộ chứa requestId, credentialHash 32 byte, serviceEpoch và TicketTimes; sao chép hash khi nhận/trả và che toString. Chưa DTO HTTP, Spring bean hoặc tạo khóa/epoch thật.
- [StartTicketCodecTests.java](../../backend/src/test/java/vn/hueqr/backend/service/StartTicketCodecTests.java): 14 ca kiểm format đã mô tả, round-trip/microsecond, sửa payload/tag/sai khóa, envelope/encoding sai, padding/bit dư, version lạ, field không theo dạng chuẩn/thiếu-thừa/sai kiểu, giữ mốc cũ cho lookup, bảo vệ hash và từ chối độ dài/precision sai. Fixture công khai, không quyền/key thật.

**Giải thích:** Base64URL giúp biểu diễn bytes thành chuỗi; chữ ký HMAC mới kiểm toàn vẹn. Serialize/deserialize phải chỉ có một cách biểu diễn được chấp nhận, tránh hai chuỗi cùng nội dung nhưng khác digest khi lưu ledger sau này. Codec chỉ kiểm định dạng/toàn vẹn, chưa so credential thật, request key hoặc epoch hiện hành. Cặp mốc được giữ nguyên cả khi đã cũ; validateTimes/nhánh expiry và ledger sẽ được lớp điều phối gọi theo thứ tự thiết kế ở bước tiếp theo.

**Lệnh kiểm codec tại backend, dùng để tra cứu hoặc tái hiện khi cần:**

```powershell
Set-Location -LiteralPath 'C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue\backend'
.\mvnw.cmd "-Dtest=StartTicketCodecTests" test
```

Đã đạt riêng **14 test, 0 failures/errors/skipped, BUILD SUCCESS**, mã thoát 0; không cần Docker hoặc khóa thật. Surefire khớp theo mục A.13. Không ghép các báo cáo cũ thành một lần full suite, không yêu cầu chạy lại khi chưa có lý do mới.

Mốc codec đã có kết quả đạt trong phạm vi riêng; mốc nhánh sau đó được ghi nhận trong hồ sơ lịch sử, phần cấp/xác minh kết hợp và API được triển khai tại [mục 3.2.8](#practice-start-api). Kết quả riêng codec không chứng minh toàn P01-TICKET, restart/restore thực hoặc toàn ô T03 (#3).

**Ôn tập:** nếu hai chuỗi khác nhau giải mã ra cùng bytes, vì sao việc lưu digest vé cần quy tắc biểu diễn thống nhất? Codec kiểm chữ ký đúng đã đủ chứng minh người gửi có credential của tab chưa?

<a id="practice-start-api"></a>

#### 3.2.8. API/ticket/Service/Repository và lookup/expiry hai nhánh

**Mục được chọn:** “Có API/ticket/Service/Repository đúng ba lớp và lookup/expiry hai nhánh.” Đây là mục kỹ thuật làm nền cho [ô đầu T03](../01-tong-quan/roadmap.md#t03), chưa đồng nghĩa toàn ô đã hoàn tất. T03 do Thành phụ trách theo G02. Trạng thái commit/push được theo dõi tại [handoff](../01-tong-quan/handoff.md#next-step); kết quả API/test không chứng minh mã nguồn đã commit hoặc push.

**Đã triển khai trong phạm vi này:**

- K11 `POST /api/v1/guest/start-ticket`: body rỗng, cấp requestId/vé ký 24 giờ ràng buộc credential/epoch; không ghi phiên.
- K01 `POST /api/v1/guest/sessions`: nhận StartRequest và hai header, chuẩn hóa tên/kiểm tiếng; xác minh chữ ký/key/credential/epoch rồi tra ledger. Có ledger đúng fingerprint/digest/mốc thì trả 200 cùng phiên; không có chỉ insert trước hạn vé và trả 201. Phiên có hạn cố định 8 giờ từ startedAt server.
- K02 `GET /api/v1/guest/sessions/{sid}`: credential/epoch đúng mới trả ACTIVE/EXPIRED/ENDED và mốc gốc; không gia hạn hoặc lộ tên phiên khác. ENDED trong test là fixture DB tạm, chưa API K04 kết thúc phiên.
- GuestSessionController chỉ gọi StartTicketService/GuestSessionService; GuestSessionService điều phối Repository và TransactionTemplate. UNIQUE race được bắt sau rollback rồi tra lại trong transaction mới; không query trong TX đã lỗi, không sinh key khác. V1, Entity và Repository giữ nguyên.
- GuestInputPolicy kiểm raw control/newline trước normalize, rút gọn khoảng trắng Unicode; đếm 1–50 cụm ký tự hiển thị bằng `Pattern` với `\X`, cho phép dấu/khoảng trắng/ký hiệu; ngôn ngữ đúng vi/en/fr/ru. SHA-256 fingerprint ghép dữ liệu có độ dài để không nhập nhằng, so hash bằng MessageDigest.isEqual.
- DTO che toString của vé/request/phiên; response `Cache-Control: no-store`. Lỗi có code/message/fieldErrors/retryable/correlationId, không phản chiếu vé/credential hoặc tên người khác. JSON/path sai cấu trúc trả 400, dữ liệu không hợp lệ 422; sai quyền 401, epoch 403, key/expiry conflict 409.

**Lựa chọn kỹ thuật và cấu hình:** credential là 32 byte ngẫu nhiên do client đúng tab tạo, truyền Base64URL chuẩn không padding (43 ký tự); backend lưu SHA-256 của bytes, không credential thô. Test dùng fixture công khai, chưa chứng minh Web Crypto/owner/reload/duplicate trên browser. Cấu hình ứng dụng đọc `HUEQR_GUEST_SIGNING_KEY_BASE64` (Base64 chuẩn có padding, khóa tối thiểu 32 byte) và `HUEQR_SERVICE_EPOCH` (UUID chuẩn chữ thường). Không có giá trị fallback hoặc tự sinh mới mỗi boot; thiếu/sai thì không khởi động. Cấp/lưu khóa thật, giữ cấu hình qua restart và xoay epoch khi restore thật thuộc thao tác của người vận hành; chưa có bằng chứng thực hiện trên môi trường phục vụ. Khóa/epoch phải ở cấu hình bảo vệ ngoài repo và ngoài gói backup; xem [operations](operations-design.md#ops-start-runtime).

**Các test mới:** GuestInputPolicyTests; GuestRuntimeConfigurationTests; GuestSessionApiTests qua MockMvc → Service → JPA → MySQL 8.4.13 Testcontainers. GuestSessionTestClock chỉ trong src/test; không endpoint chỉnh giờ public. Cấu hình support dùng DynamicPropertyRegistrar cấp khóa/epoch giả riêng cho context import nó. Dựng lại context với cùng cấu hình kiểm vé cũ vẫn hợp lệ, đổi epoch từ chối; đây chưa là restart/restore toàn ứng dụng + ledger bền. Fixture xóa ledger sau 31 ngày chỉ kiểm nhánh vé cũ không tạo lại, chưa tác vụ D08.

**Cách kiểm hồi quy** tại thư mục backend, khi có thay đổi cần kiểm; chỉ thực hiện khi có thay đổi hoặc lý do kỹ thuật mới:

```powershell
Set-Location -LiteralPath 'C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue\backend'
.\mvnw.cmd clean test
```

Docker Engine cần hoạt động; các test chỉ dùng MySQL tạm. Không cần đặt khóa thật để chạy test. Bằng chứng chạy đầy đủ và giới hạn được ghi tại [mục A.14](#practice-start-api-result).

**Căn cứ kỹ thuật đã đối chiếu 05/11/2026:** [Java 21 Pattern](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html) cho `\X`; [Spring DynamicPropertyRegistrar](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/dynamic-property-sources.html) cho thuộc tính test; [Spring Boot 4.1 MockMvc](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/webmvc/test/autoconfigure/package-summary.html) cho package kiểm HTTP. Hợp đồng nghiệp vụ theo [API 9.1–9.2](api-transactions.md#api-dto) và [LIFE-P01](operations-design.md#ops-start-simple).

**Ôn tập:** vì sao kiểm chữ ký/quyền trước nhưng chỉ chặn expiry tạo lần đầu sau tra ledger? Vì sao không được query tiếp ngay trong giao dịch đã lỗi UNIQUE?

<a id="practice-end-session"></a>

#### 3.2.9. K04 — kết thúc phiên khách

Thành phụ trách T03 theo G02. Mục này triển khai backend theo API-K04/TX-K04, làm nền cho ô tạo/kết thúc/kiểm trạng thái phiên. Bằng chứng mới tại [A.15](#practice-end-session-result).

- `POST /api/v1/guest/sessions/{id}/end` nhận body rỗng, `X-Guest-Credential` và `Idempotency-Key` là UUID chuẩn chữ thường. Body có dữ liệu hoặc key thiếu/sai trả 422, path sai UUID trả 400.
- Service khóa bản ghi phiên trong giao dịch, kiểm mã Hash credential và epoch trước khi đọc lại kết quả đã kết thúc. Phiên thiếu hoặc sai quyền trả 401, không trả dữ liệu phiên khác.
- Phiên active được ghi `endedAt` bằng giờ server sau khi lấy khóa, cắt đến micro giây để khớp DATETIME(6). Giữ nguyên `startedAt`, `expiresAt` và thời hạn 8 giờ. Phiên đã tới hạn nhưng chưa kết thúc trả 409 `SESSION_EXPIRED`, không ghi `endedAt` muộn.
- Lựa chọn triển khai cho thao tác kết thúc có body rỗng là dùng `endedAt` đã commit làm kết quả duy nhất của phiên. Gửi lại với key hợp lệ, kể cả key khác hoặc sau hạn phiên, trả cùng mốc với `replayed=true` sau kiểm quyền. Chưa cần thêm bảng receipt hoặc cột end key cho thao tác này.
- Hai request kết thúc đồng thời nhận cùng mốc kết thúc, một kết quả đầu và một replay. Kết thúc một phiên không thay phiên khác dù cùng tên. Response giữ `Cache-Control: no-store`.

Đã thêm `GuestSessionEndTests` gồm 12 unit test và `GuestSessionEndApiTests` gồm 12 test MockMvc → Service → JPA → MySQL tạm. Các ca kiểm thời điểm sát/đúng/sau hạn, quyền, dữ liệu đầu vào, replay và đồng thời. Unit test khóa dùng mock, bằng chứng giao dịch/database nằm ở test tích hợp.

K04 hiện có phần kết thúc phiên. Grant chưa dùng, nội dung/audio, D08, frontend/owner tab và các phần còn lại của TX-K04 vẫn cần triển khai theo phụ thuộc. Toàn ô T03 giữ trạng thái chưa tích.

### 3.3. Ranh giới trách nhiệm ba lớp và cách trình bày

| Phần | Nhiệm vụ | Lỗi cần tránh |
| --- | --- | --- |
| Controller/DTO | Nhận vé/tên/tiếng/credential, kiểm cấu trúc và trả HTTP đúng trạng thái | Tin id/status/clock client hoặc đưa secret vào URL/log |
| Service/ticket validator | Check chữ ký/quyền/epoch; đọc kết quả đã tạo trước nhánh expiry; normalize/so fingerprint/TX | Từ chối mọi vé expired trước lookup, cấp vé khác cho pending, đổi session gốc |
| Repository/MySQL | Tìm requestId, persist đúng một session, UK/FK theo migration | “if không có thì insert” không UNIQUE; catch lỗi TX rồi query tiếp trong TX hỏng |
| Client/tab sau này | Giữ đúng vé/key/quyền khi pending, refresh vé chưa gửi nếu quá hạn, xác nhận phiên mới đúng lúc | Dùng cookie chung/tên cho hai tab; tự tạo lại hoặc đổi quyền sau timeout |

<a id="practice-evidence"></a>

## 4. Cách kiểm, trạng thái từng phần và lỗi thường gặp

Phần backend có bằng chứng tại [mục A.14](#practice-start-api-result) và [A.15](#practice-end-session-result). Các ca [operations 5.1](operations-design.md#ops-start-checks) còn giới hạn riêng. Clock/ledger dùng fixture trong test, không sửa giờ máy, TTL production hoặc gọi endpoint chỉnh giờ public. Browser, cleaner D08 và restart/restore thực vẫn chưa kiểm.

| Nhóm quan sát | Bằng chứng khi thực hiện |
| --- | --- |
| Tạo/replay | Hai response cùng sessionId/startedAt/expiresAt, DB chỉ một row/key; không chỉ screenshot nút |
| Khác nội dung/quyền | 409/401/403 phù hợp, không record mới/không sửa tên/tiếng gốc, không lộ tên phiên khác |
| Vé expired nhưng ledger có | Signature/binding/epoch/fingerprint đúng trả gốc; ví dụ session vẫn active do bắt đầu gần cuối cửa sổ vé |
| Vé expired/ledger không | Không insert; giữ form và chủ động Bắt đầu mới khi cần, không nói sai phiên đã hết hạn |
| Restart | Key/epoch/ledger giữ, vé cũ xác minh được; không kéo lại đồng hồ |
| Restore | Môi trường riêng, lọc D08, epoch cũ bị chặn; chưa thể đánh chỉ từ test restart |
| Hai tab | Backend hai credential chỉ kiểm quyền dữ liệu; owner/reload/duplicate phải kiểm thật trên nền tảng đã chọn sau khi có frontend |
| Purge | Session/view thực bị xóa đúng mốc, replay vé cũ không tạo lại; không generation/hook/tombstone thêm |
| Nhật ký kết quả | Đường dẫn bằng chứng/schema/app build/model thiết bị được ghi khi thực hiện; không token/key/tên thật trong báo cáo chia sẻ |

Lỗi thường gặp: Java runtime khác compiler; Docker CLI có nhưng engine không chạy; Wrapper chưa tồn tại mà gọi build; MySQL/Flyway thiếu module/khác mốc; default expiry validator chặn lookup; thiếu UNIQUE gây hai phiên; nhầm 24 giờ vé với 8 giờ phiên; đổi khóa ký mỗi restart làm kết quả cũ không tra được. Mỗi lỗi cần lấy thông báo thật, xác định lớp rồi xử lý từng phần, không đổi yêu cầu để hợp thức hóa.

### Theo dõi phạm vi thực hành — không thay checklist GitHub

| Phần | Trạng thái theo bằng chứng | Phần còn lại |
| --- | --- | --- |
| Công cụ và môi trường | Đã có Java/Wrapper/Docker Engine/MySQL tạm | DEV bền vững và môi trường phục vụ chưa kiểm |
| Skeleton, V1 và mapping | Đã có bằng chứng, vẫn đạt trong bộ 64 test | Toàn schema/seeds/FK thuộc T02 chưa xong |
| API/ticket/ba lớp/lookup hai nhánh và kết thúc | K11/K01/K02/K04 đạt trong bộ 88 test ngày 08/11 | Chưa toàn ô T03, grant/nội dung/audio và browser |
| UNIQUE/concurrent/replay/khác payload | Có ca kiểm MySQL tạm trong bộ 64 test | Không tự thay kết luận browser/mạng thật hoặc toàn nghiệm thu |
| Clock/24 giờ/ledger/restart/restore | Có fixture kiểm thời gian, dựng lại context và ledger removal | Chưa runtime key/ledger bền, tác vụ D08, restart/restore thật hoặc owner browser |
| Bằng chứng và bàn giao | Đã lưu phạm vi/lần chạy và đồng bộ handoff | Tiếp tục cập nhật theo công việc, không tự tích GitHub |

<a id="practice-recap"></a>

## 5. Ôn tập và bàn giao

1. Vì sao kiểm “requestId chưa có” trong Service chưa đủ nếu database không UNIQUE?
2. Vé hết hạn nhưng session đã tạo còn hạn có thể xảy ra thế nào, và vì sao không được tạo lại?
3. Tại sao sau dọn ledger D08 không cần start_generation nữa?
4. 24 giờ vé, 24 giờ window Admin, 8 giờ guest và 30 phút Admin khác nhau ở đâu?
5. Vì sao restart thường phải giữ epoch/khóa ký, còn restore thật thu hồi quyền?
6. Phần nào chứng minh ba lớp, phần nào cần database/browser thực mới kết luận được?

Bàn giao đọc đúng phiên bản ở docs/README, model/API/operations hiện hành; giữ code T01–T11/G02, đối chiếu cùng phiên bản và nguồn bằng chứng. Sau mốc Git hiện tại, chọn mục checklist khả thi theo phụ thuộc và tiếp tục theo handoff, không yêu cầu duyệt lại LIFE-P01 hoặc dùng việc có tài liệu để tích “Hoàn thiện TDD”/đóng issue.


<a id="practice-results"></a>

**Phạm vi hồ sơ kiểm chứng:** các kết quả dưới đây chỉ có hiệu lực trong phạm vi và thời điểm đã ghi nhận. Task và mapping hiện hành tại [roadmap](../01-tong-quan/roadmap.md#t01); trước bàn giao cần đối chiếu trạng thái Git, không suy từ kết quả test hoặc một ghi nhận kỹ thuật.

## Phụ lục A. Tổng hợp kết quả kiểm chứng

Bảng này tổng hợp các kết quả đã ghi nhận theo thời điểm. [Hồ sơ kiểm chứng nguyên văn](../99-luu-tru/document-history.md#official-20261107-practice-evidence) giữ lệnh, log, người thực hiện được ghi nhận, đối chiếu báo cáo và giới hạn của từng lần chạy. Không cộng các lần chạy riêng thành một lần hồi quy hoặc suy kết quả của một lần sang toàn bộ hệ thống.

| Mốc | Kết quả đã ghi nhận | Giới hạn |
| --- | --- | --- |
| A.1 — môi trường, 04/11/2026 | Java/Javac 21.0.12.1, Docker CLI 29.8.1, Compose 5.5.1, WSL mặc định 2 | Chưa có đủ Windows build, dung lượng hoặc bằng chứng tích hợp toàn môi trường |
| A.2 — Docker Engine | Engine 29.8.1, OSType Linux | Không chứng minh ứng dụng/CI-CD đã chạy |
| A.3 — Maven Wrapper, 05/11/2026 | Maven 3.9.12, Java 21.0.12.1 | Kết quả công cụ không thay bằng chứng nghiệp vụ |
| A.4 — image MySQL | Đã tải mysql:8.4.13; digest được giữ trong hồ sơ | Tải image không chứng minh database phục vụ bền vững |
| A.5 — skeleton, 01:23:00 +07:00 ngày 05/11 | 1 test đạt, 0 failures/errors/skipped; BUILD SUCCESS; MySQL tạm kết nối | Chưa migration hoặc API nghiệp vụ |
| A.6 — open-in-view, 01:32:54 +07:00 | 1 test đạt; BUILD SUCCESS; open-in-view=false | Chưa toàn nghiệp vụ hoặc runtime thật |
| A.7 — HTTP, 09:41:53 +07:00 | Tomcat 8080 khởi động, MySQL tạm kết nối; HTTP 404 và kết thúc lệnh đã xác nhận | Chưa API nghiệp vụ; chưa có bằng chứng mọi container được dọn sau dừng |
| A.8 — V1, 10:26:20 +07:00 | V1 áp dụng trên MySQL test, 1 test đạt; BUILD SUCCESS | Chưa toàn schema hoặc ràng buộc/concurrency |
| A.9 — ràng buộc, 10:51:11 +07:00 | 4 ca schema trong bộ tổng 5 test đạt; BUILD SUCCESS | Chưa đủ toàn T02 |
| A.10 — mapping, 13:35:16 +07:00 | Bộ tổng 7 test đạt, gồm 2 ca Repository; validate/UTC và package chia lớp | Chưa runtime bền vững, toàn T02 hoặc API |
| A.11 — thời gian, 13:57:40 +07:00 | 8 unit test đạt; BUILD SUCCESS | Chưa chữ ký/ledger/API |
| A.12 — HMAC, 14:07:51 +07:00 | 9 unit test đạt; BUILD SUCCESS, có RFC 4231 case 6 | Chưa vé hoàn chỉnh hoặc restart/restore |
| A.13 — codec, 14:32:52 +07:00 | 14 unit test đạt, mã thoát 0; BUILD SUCCESS | Chỉ lớp codec, chưa Docker/MySQL hoặc runtime thật |
| A.14 — hồi quy, 16:15:34 +07:00 | 64 test đạt, 0 failures/errors/skipped; BUILD SUCCESS, mã thoát 0; 9 báo cáo Surefire khớp | Chưa K04, browser/owner/Blob, runtime thật/restart/D08/backup/restore hoặc toàn T03 |

Các thời điểm A.6–A.14 thuộc ngày 05/11/2026. Các báo cáo trong target là file sinh, có thể được thay bởi lần chạy sau; không đưa vào Git hoặc coi là bằng chứng hiện trạng mới.

### Thành phần bộ hồi quy 64 test

| Lớp test | Số ca đạt |
| --- | ---: |
| BackendApplicationTests | 1 |
| GuestRuntimeConfigurationTests | 6 |
| GuestSessionApiTests | 14 |
| GuestSessionRepositoryTests | 2 |
| GuestSessionSchemaTests | 4 |
| GuestInputPolicyTests | 6 |
| StartTicketCodecTests | 14 |
| StartTicketSignerTests | 9 |
| StartTicketTimePolicyTests | 8 |

<a id="practice-docker-engine"></a>
<a id="practice-mysql-pull"></a>
<a id="practice-skeleton-test"></a>
<a id="practice-osiv-test"></a>
<a id="practice-http-partial"></a>
<a id="practice-v1-result"></a>
<a id="practice-schema-test-result"></a>
<a id="practice-mapping-test-result"></a>
<a id="practice-ticket-time-result"></a>
<a id="practice-ticket-signer-result"></a>
<a id="practice-ticket-codec-result"></a>
<a id="practice-start-api-result"></a>

Các liên kết đến mốc A.2–A.14 được duy trì để tra cứu bảng tổng hợp này; nội dung đầy đủ nằm trong hồ sơ kiểm chứng nêu trên.

<a id="practice-end-session-result"></a>

### A.15. K04 và hồi quy ngày 08/11/2026

Kiểm thử trên bản sao backend tách biệt tại `C:\Users\khait\AppData\Local\Temp\hueqr-session-end-e80eac98c9824d3fa30bfee26728ea00\backend`. Dùng Maven Wrapper 3.9.12, Java 21.0.12.1 và MySQL 8.4.13 Testcontainers. Không dùng database phục vụ hoặc khóa thật. Người vận hành đã bật Docker Desktop, kết nối Engine Linux 29.8.1 được xác nhận trước lần chạy đầy đủ.

| Lần chạy | Kết quả | Giới hạn |
| --- | --- | --- |
| Sáu lớp unit, `mvnw.cmd -o -Dtest=GuestInputPolicyTests,GuestRuntimeConfigurationTests,StartTicketCodecTests,StartTicketSignerTests,StartTicketTimePolicyTests,GuestSessionEndTests test` | 55 test đạt, không lỗi/bỏ qua, BUILD SUCCESS, exit 0, kết thúc 04:09:22 +07 | Chưa kiểm HTTP/MySQL trong lần này |
| Toàn bộ, `mvnw.cmd -o test` | 88 test đạt, 0 failures/errors/skipped, BUILD SUCCESS, exit 0, kết thúc 04:16:40 +07 | MySQL tạm và MockMvc, chưa browser hoặc môi trường phục vụ |

Đã đối chiếu 11 báo cáo XML Surefire, gồm 9 lớp cũ với 64 ca và hai lớp K04 mới với 24 ca. Log lần chạy toàn bộ nằm ở `..\full-tests.log` so với backend tạm, báo cáo tại `target/surefire-reports`. Tệp sinh có thể mất khi dọn thư mục tạm, không thuộc nhóm commit. Kết quả 64 test ngày 05/11 ở A.14 được giữ nguyên theo thời điểm.

Đã kiểm gửi đồng thời kết thúc, replay sau hạn, từ chối kết thúc lần đầu tại/sau hạn, độ chính xác micro giây, quyền dữ liệu và độc lập hai phiên cùng tên ở backend. Các ca frontend/owner tab, runtime bền vững/restart/restore, grant, D08 và toàn nghiệm thu T03 còn mở. Điểm dừng là nhóm rà kết quả K04, commit tạm hoãn và Project chờ thêm issue.

<a id="practice-history"></a>

### Nhật ký biên tập



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-03-ky-thuat-practice-01-start-session-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
