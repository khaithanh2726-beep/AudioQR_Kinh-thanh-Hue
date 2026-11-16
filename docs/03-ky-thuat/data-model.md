# Mô hình dữ liệu và ràng buộc

Mô hình logic cho địa điểm/vùng, nội dung/audio/cặp, phiên/lượt xem, hàng đợi và tài nguyên file. [ERD vật lý đầy đủ](erd-vat-ly.md) là bảng tra thống nhất khi đếm bảng/FK; hình trong tài liệu này là các phần trích theo nghiệp vụ. Bản triển khai dùng UUID, thời gian UTC, JPA/JDBC và migrations V1–V14; việc rà/duyệt toàn ERD vẫn được theo dõi trong checklist.

<a id="model-conventions"></a>

## 1. Quy ước và phạm vi

- UUID do hệ thống cấp, không lấy tên làm khóa. Mapping BINARY(16)/Java UUID, thời gian UTC DATETIME(6), tên/ngôn ngữ Unicode, version tăng khi aggregate thay đổi. DDL/mapping hiện hành là V1–V14, gồm phiên/opening/grant/claim, nội dung/cặp, TTS/audio/lifecycle và gate vận hành; bảng phiên bản tại mục 7. Mô hình khái niệm không thay ràng buộc vật lý trong migration.
- PK: khóa chính; FK: tham chiếu được database kiểm; UK: tổ hợp không trùng. FK quan hệ cần chặn dùng RESTRICT/NO ACTION, không CASCADE/JPA xóa ngầm. Pointer nullable để tạo và tháo theo thứ tự trong giao dịch.
- Ngôn ngữ chỉ vi/en/fr/ru. Năm điểm là bộ chuẩn 20 văn bản + 20 audio, không trần số điểm. Tên Q1/D02-BS kiểm grapheme/chuẩn hóa tại Service; độ dài cột/byte không tự thay giới hạn ký tự hiển thị hoặc thêm giới hạn bài mới.
- Bản văn/audio/vùng đã lưu dùng định danh bất biến; sửa sinh revision thay thế. Con trỏ nháp khác con trỏ áp dụng. Không cần UI lịch sử phiên bản phức tạp.
- Dữ liệu Q2 chưa lưu/mốc nghe khách/Blob/owner lock nằm ở tab theo phạm vi đã chốt; không tạo bảng lịch sử audio hoặc đồng bộ thiết bị.

<a id="model-content"></a>

## 2. Địa điểm, vùng, văn bản/audio và cặp

ERD phần trích nhóm nội dung; có node ngữ cảnh dùng chung. Danh mục đầy đủ 25 bảng và 36 FK tại [ERD vật lý](erd-vat-ly.md); các hình nhóm không được hiểu là toàn bộ database.

```mermaid
erDiagram
    ADMIN_ACCOUNT ||--o{ TEXT_REVIEW_REF : ghi_tham_chieu
    ADMIN_ACCOUNT ||--o{ AUDIO_REVIEW_REF : ghi_tham_chieu
    PLACE ||--o{ REGION_REVISION : co
    PLACE ||--o{ CONTENT_SLOT : co
    CONTENT_SLOT ||--o{ TEXT_REVISION : luu
    TEXT_REVISION ||--o| TEXT_REVIEW_REF : doi_chieu
    TEXT_REVISION o|--o{ TTS_JOB : dich_con_song
    TEXT_REVISION ||--o{ AUDIO_REVISION : dung_ban
    TTS_JOB ||--o| AUDIO_REVISION : tao
    AUDIO_REVISION ||--o| AUDIO_REVIEW_REF : doi_chieu
    CONTENT_SLOT ||--o{ CONTENT_PAIR : cap
    TEXT_REVISION ||--o{ CONTENT_PAIR : van_ban
    AUDIO_REVISION ||--o{ CONTENT_PAIR : audio
    FILE_ASSET ||--o{ AUDIO_REVISION : WAV
    FILE_ASSET ||--o{ AUDIO_REVISION : MP3
    PLACE {
        uuid id PK
        string display_name
        boolean visible
        uuid active_region_revision_id FK
        long version
    }
    REGION_REVISION {
        uuid id PK
        uuid place_id FK
        longtext polygon
        string map_space_key
        string geometry_hash
        datetime saved_at
    }
    CONTENT_SLOT {
        uuid id PK
        uuid place_id FK
        string language
        uuid draft_text_id FK
        uuid active_pair_id FK
        long version
    }
    TEXT_REVISION {
        uuid id PK
        uuid slot_id FK
        text body
        string content_hash
        uuid active_tts_job_id FK
        uuid latest_tts_job_key
    }
    AUDIO_REVISION {
        uuid id PK
        uuid text_revision_id FK
        uuid job_id FK
        uuid wav_file_id FK
        uuid mp3_file_id FK
    }
    CONTENT_PAIR {
        uuid id PK
        uuid slot_id FK
        uuid text_revision_id FK
        uuid audio_revision_id FK
        datetime created_at
    }
```

| Bảng | Trường và khóa chính ngoài ERD | Ý nghĩa / kiểm Service |
| --- | --- | --- |
| PLACE | display_name theo Q1; visible mặc định false; created_at/updated_at | Lưu ẩn dù chưa có vùng/nội dung. Bật hiện chỉ thông tin hợp lệ + vùng đã xem xét/áp dụng + ít nhất cặp vi đã duyệt/áp dụng. Không tự bật khi Apply. |
| REGION_REVISION | geometry_hash do server tính lại; saved_at; UK(id,place_id) | Một đa giác kín đơn giản, cùng không gian nền; hình dở chỉ Q2. Bản đã xem không đổi sau Lưu thì không bắt xem lại. Hồ sơ vùng dùng học phần giữ riêng; Hash không tự chứng minh đã duyệt hoặc là ranh giới khảo sát/pháp lý. |
| CONTENT_SLOT | UK(place_id,language), UK(id,place_id,language) | Một ngăn như Ngọ Môn/vi; một active_pair, không chờ đủ bốn tiếng mới áp dụng. |
| TEXT_REVISION | UK(id,slot_id), saved_at | Mỗi Lưu rõ ràng tạo bản bất biến mới và đổi draft pointer, không sửa bản đang dùng hoặc tự tạo TTS. Hash không thay id. |
| TEXT_REVIEW_REF | text_revision_id PK/FK, locator, limitations nullable, recorded_by FK, recorded_at, version | Gắn đúng bản đã lưu/hash. Không nhập lại ngày/người duyệt nếu đã có trong tài liệu; không tự thẩm định hoặc áp dụng. |
| AUDIO_REVISION | job_id UK; UK(id,text_revision_id); created_at | WAV/MP3 hoàn chỉnh đúng text/ngôn ngữ. Một kết quả chưa nghe/duyệt vẫn chỉ được nghe thử trong Admin. |
| AUDIO_REVIEW_REF | audio_revision_id PK/FK; các trường như text reference | Có thể cùng locator nếu hồ sơ chỉ rõ cả hai đối tượng; vẫn hai liên kết riêng. |
| CONTENT_PAIR | UK(slot_id,text_revision_id,audio_revision_id), UK(id,slot_id) | C2=(T2,A2) khớp cùng ngăn. Chỉ đổi audio thì dùng T hiện hành cùng A mới được duyệt; không bắt duyệt lại T không đổi. |

Phiên bản nội dung bất biến không làm hồ sơ bất biến: sửa tham chiếu có review_version/ETag riêng; Apply kiểm lại đúng hồ sơ hiện có trong TX, không tin snapshot ngoài TX. Phần mềm chỉ kiểm điều kiện/đối tượng và thông tin Admin đối chiếu, không tự đọc tài liệu rồi quyết định đã duyệt.

<a id="model-guest"></a>

## 3. Phiên theo tab, lần mở, quyền tải và lượt xem

ERD phần trích nhóm Khách, không liệt kê toàn bộ database. Các trường snapshot/key logic không tự tạo thêm bảng hoặc FK.

```mermaid
erDiagram
    GUEST_SESSION ||--o{ GUEST_OPEN_INTENT : cac_lan_mo
    GUEST_SESSION ||--o{ GUEST_CONTENT_GRANT : quyen_noi_dung
    GUEST_OPEN_INTENT ||--o{ GUEST_CONTENT_GRANT : thuoc_lan_mo
    GUEST_SESSION ||--o{ PLACE_VIEW : luot_da_tinh
    GUEST_SESSION ||--o{ GUEST_REQUEST_RESULT : ket_qua_lenh_tam
    PLACE o|--o{ GUEST_OPEN_INTENT : live_nullable
    PLACE o|--o{ PLACE_VIEW : live_nullable
    FILE_ASSET o|--o{ GUEST_CONTENT_GRANT : pin_MP3


    GUEST_SESSION {
        uuid id PK
        string credential_hash UK
        string service_epoch
        uuid start_request_key UK
        datetime start_ticket_issued_at
        datetime start_ticket_expires_at
        string start_ticket_digest
        string start_fingerprint
        string display_name
        string initial_language
        string current_language
        datetime started_at
        datetime expires_at
        datetime ended_at
        uuid current_opening_id FK
        uuid document_nonce
        long selection_seq
    }
    GUEST_OPEN_INTENT {
        uuid id PK
        uuid session_id FK
        uuid live_place_id FK
        uuid place_key_snapshot
        string place_name_snapshot
        string current_language
        string state
        long version
    }
    GUEST_CONTENT_GRANT {
        uuid id PK
        uuid session_id FK
        uuid opening_id FK
        uuid place_key_snapshot
        string place_name_snapshot
        string language
        uuid text_revision_key
        string text_hash
        uuid pair_key
        uuid audio_revision_key
        uuid mp3_file_id FK
        string source
        boolean view_eligible
        datetime issued_at
        datetime expires_at
        datetime revoked_at
    }
    GUEST_REQUEST_RESULT {
        uuid session_id PK,FK
        uuid request_key PK
        uuid document_nonce
        long selection_seq
        string fingerprint
        string action
        uuid opening_key
        long opening_version
        uuid grant_key
        string selection_kind
        string language
        string available_languages
    }
    PLACE_VIEW {
        uuid id PK
        uuid session_id FK
        uuid opening_key
        uuid live_place_id FK
        uuid place_key_snapshot
        string place_name_snapshot
        string language
        datetime viewed_at
    }
```

| Bảng / con trỏ | Ràng buộc triển khai và liên kết logic | Vòng đời và giới hạn |
| --- | --- | --- |
| Vé Bắt đầu (không bảng riêng) | Payload ký: requestId/credentialHash/serviceEpoch/issuedAt/ticketExpiresAt=issued+24 giờ/version; không dữ liệu tên/ngôn ngữ trong vé | Cấp nền; chỉ tạo lần đầu bằng vé còn hạn. Có ledger thì kiểm signature/quyền/epoch/fingerprint và trả gốc dù expiry vé đã qua; không JWT Admin hoặc đồng hồ khách |
| GUEST_SESSION | UK(start_request_key), credential_hash UK; start_fingerprint + start_ticket_issued_at/expires_at/digest để đối chiếu; service_epoch; current language/nonce/seq dọn theo end/expiry; current_opening FK tổ hợp | Lưu ID/key/quyền/fingerprint cùng TX. Session 8 giờ/ledger anchor+30 ngày giữ nguyên; purge không phối hợp thế hệ. Vé hết hạn mà ledger còn chỉ tra gốc, không tạo/gia hạn/đổi thông tin |
| GUEST_OPEN_INTENT | Giữ tên bảng 0.2 để truy vết, nghĩa hiện hành là **opening**; UK(id,session_id); created_at/closed_at; tra lượt đã tính qua PLACE_VIEW theo opening_key, không có cột counted_view_key hoặc FK ngược tới view | Sau ACK vẫn giữ current opening để P06/đổi tiếng/reload không thêm lượt. Chọn điểm khác hoặc chủ động quay bản đồ đóng opening; opening/grant kỹ thuật giữ tối đa tới kết thúc/hết hạn rồi dọn an toàn, không lịch sử thất bại 30 ngày. |
| GUEST_CONTENT_GRANT | FK tổ hợp (opening_id,session_id); file FK RESTRICT nullable; text/pair/audio/điểm cũ là id snapshot logical, không FK cản xóa nội dung | Một grant gắn đúng text/tiếng/MP3 của một response; server kiểm session+credential+grant owner. expiresAt không vượt hạn phiên, revoked khi kết thúc. Không phải URL public/token độc lập, không cấp lại audio sau hạn. |
| GUEST_REQUEST_RESULT | UK(session_id,request_key), UK(session_id,document_nonce,selection_seq); PK(session_id,request_key); fingerprint/action + opening/grant key logical, opening_version/selection_kind/language/available_languages; không có id độc lập, operation_type hoặc result_code và không chứa body text | Giữ kết quả K06/K07/K10 tới end/expiry để replay không đổi current selection hoặc tạo grant/lượt mới. Bản gốc mất thì báo không còn khả dụng, không thay bản khác âm thầm; không lịch sử nghe 30 ngày. |
| PLACE_VIEW | UK(session_id,opening_key); session FK, live_place nullable FK; **không FK tới opening/grant/text/audio** | ACK theo grant server và UNIQUE mới tạo một lượt; viewedAt server. Opening/grant được dọn sau hạn, view giữ D08. Không dùng tên gộp điểm/người. |
| document_nonce/selection_seq | Nonce document mới khi resume thành công; seq tăng trong document, kiểm dưới khóa session; không là bằng chứng quyền | Cùng credential mà sai document/seq cũ không đổi lựa chọn mới. Web Locks/owner bên tab vẫn phải kiểm, không dùng cookie chung thay credential. |

**LIFE-P01 đã duyệt tại DATA-05:** không start_generation, context hoặc gate giữa start và purge. [Protocol chính](operations-design.md#ops-start-simple): xác minh signature/quyền/epoch → tra ledger/đối chiếu → ledger có trả gốc kể cả vé hết hạn; ledger không có chỉ insert vé còn hạn 24 giờ. UNIQUE bảo vệ insert lặp, xung đột rollback TX rồi đọc lại đối chiếu.

Ticket issuedAt ≤ startedAt; thời điểm xóa ledger D08 sớm nhất là endedAt+30 ngày (endedAt ≥ startedAt), hoặc startedAt+8 giờ+30 ngày. Vì thế khi ledger bị purge đúng D08, vé đã hết 24 giờ và không thể tạo lại. Không lưu key/PII thêm sau purge; restore thật thu hồi epoch cũ trước public. Timestamp vé khác expires_at của phiên.

**Lượt đầu và reload:** SELECT/LANGUAGE grant có thể viewEligible=true khi opening chưa tính; RESTORE luôn false. ACK đầu đúng văn bản đã hiển thị lấy điểm/tên/tiếng/hash từ grant, không client tự khai. Nếu thiếu dịch lúc đầu rồi chủ động chọn tiếng có bài, lần đầu văn bản thành công tính đúng một lượt; các lần đổi sau không cộng. ACK đang bay của lựa chọn thật có thể ghi snapshot sau điểm bị xóa khi phiên còn hạn; không phải gửi bù offline. Server không thể chứng minh con người thực sự đọc: ACK là hợp đồng giữa client đã kiểm và server, cần thử race/hiển thị thực.

**Giữ lịch sử khi xóa:** giữ snapshot id/tên của lượt đã tính, đặt live_place_id NULL. Một điểm mới cùng tên có UUID khác; nhóm theo snapshot id/ngày/tiếng. Nhãn thống kê đề xuất dùng snapshot mới nhất trong nhóm nếu tên từng đổi, không gộp theo tên hoặc thêm lịch sử đổi tên mới.

<a id="model-operations"></a>

## 4. Chống thao tác lặp, job và tài nguyên file

ERD phần trích nhóm lệnh/TTS/file. Cạnh chấm retry là liên kết logic, không phải FK SQL. Runtime/workspace/gate và toàn bộ FK được vẽ trong ERD vật lý đầy đủ.

```mermaid
erDiagram
    ADMIN_ACCOUNT ||--o{ ADMIN_COMMAND_WINDOW : cua_so_lenh
    ADMIN_COMMAND_WINDOW ||--o{ OPERATION_RECEIPT : ket_qua
    TTS_JOB o|..o{ TTS_JOB : lien_ket_logic_retry
    FILE_ASSET ||--o{ FILE_CLAIM : dang_su_dung
    ADMIN_COMMAND_WINDOW {
        uuid id PK
        uuid admin_account_id FK
        string service_epoch
        datetime issued_at
        datetime expires_at
    }
    OPERATION_RECEIPT {
        uuid id PK
        uuid window_id FK
        uuid admin_account_id FK
        uuid operation_key
        string fingerprint
        string operation_type
        string result_code
        json result_summary
        datetime committed_at
    }
    TTS_JOB {
        uuid id PK
        uuid target_text_revision_id FK
        uuid retry_of_job_id
        uuid place_key_snapshot
        uuid text_revision_key_snapshot
        string language_snapshot
        string text_hash_snapshot
        string model
        string voice
        string phase
        boolean target_valid
        uuid worker_instance
        uuid worker_token
        uuid source_wav_file_id FK
        long phase_version
        datetime accepted_at
        datetime terminal_at
        string failure_code
        uuid conversion_instance FK
        uuid conversion_task
        uuid conversion_token
        uuid conversion_file_id
    }
    FILE_ASSET {
        uuid id PK
        string storage_key UK
        string format
        string checksum
        long byte_size
        decimal duration
        string state
        boolean ever_applied
        datetime last_unapplied_at
        datetime orphan_eligible_since
    }
    FILE_CLAIM {
        uuid id PK
        uuid file_id FK
        uuid owner_key
        uuid owner_instance FK
        string kind
        datetime created_at
    }
```

| Bảng | Khóa/trường bổ sung | Điều kiện dùng / không được suy ra |
| --- | --- | --- |
| ADMIN_ACCOUNT | V3 đã có id BINARY(16), login_name UK phân biệt hoa thường/dấu, password_hash kèm tiền tố thuật toán, enabled | Một role cấp trước, bảng khởi tạo rỗng. Entity/Repository và xác thực/Q3 xem bằng chứng Admin, HttpSession tách guest |
| ADMIN_COMMAND_WINDOW | V4 đã có account FK; issued/expires do server, service_epoch thuộc môi trường; mỗi lệnh dùng key=(windowId,randomId) | LIFE-P02 đã duyệt: 24 giờ từ cấp, không gia hạn qua login. Đăng nhập lại cùng tài khoản vẫn tra window cũ còn hạn; window thiếu/hết hạn không được nhận key cũ như lệnh mới. Không là phiên Admin mới, không gia hạn 30 phút. |
| OPERATION_RECEIPT | V4 đã có UK(admin_account_id,window_id,operation_key); FK tổ hợp (window_id,admin_account_id) tới UK(id,admin_account_id) của window; fingerprint BINARY(32), http_status/result_code/operation_type, result_summary JSON tối thiểu, committed_at DATETIME(6) | Receipt và lệnh DB cùng TX, mã Hash gồm method/resource/canonical body/If-Match. Không lưu secret/text/tên khách hoặc payload đầy đủ, không FK target PLACE/TEXT đã xóa. Receipt tiếp nhận TTS trỏ job key, không chứng minh audio thành công hoặc đã duyệt/áp dụng; kết quả provider/MP3 và hồ sơ hiện tại đọc tại bàn giao/ma trận nội dung. |
| TTS_JOB | UK(admin_account_key,request_window_key,request_operation_key) là snapshot logical không FK bắt giữ window; target FK nullable + snapshot; worker_instance/token/phase_version; dispatched_at/result_staged_at/updated_at; retry_of logical nếu cần dọn job cũ | TEXT.active_tts_job_id cùng text ngăn hai job active/unknown; latest_tts_job_key logical giữ nhận biết kết quả mới nhất khi text còn sống. LIFE-P03 đã duyệt: job cũ terminal rõ chỉ dọn sau bảy ngày nếu không còn ref/use/current; audio/nháp còn liên kết và active/unknown giữ. Không tự biến TTL thành lỗi/provider retry. |
| FILE_ASSET | storage_key chỉ server tạo, checksum/byte_size/duration/ready_at; state, deletion_token/deletion_owner/deletion_task/deletion_started_at/deleted_at/cleanup_error | File bất biến, không ghi đè cùng key; không publish pointer trước bytes hoàn tất và kiểm. Metadata phải còn để báo dọn chờ/lỗi; trỏ WAV và MP3 riêng. |
| FILE_CLAIM | FK file_id; owner_key, owner_instance FK nullable; kind chỉ READ; created_at. Claim tồn tại trong bảng khi đang giữ, giải phóng bằng xóa hàng; không có cột state/opened_at | Bảo vệ đọc/copy/worker đang thực dùng; không là tham chiếu nội dung giữ mãi. Không dọn chỉ vì thiếu heartbeat khi tiến trình còn sống/chưa rõ. |
| RUNTIME_INSTANCE | id BINARY(16), node_key/root_key, process_id/process_started_at/registered_at/stopped_at, state RUNNING/STOPPED | Định danh process sở hữu tác vụ/claim; không tự suy stopped từ timeout. |
| RUNTIME_TASK | id, instance_id FK, kind, owner_key, created_at | Theo dõi tác vụ READ/TTS/RECOVER/DELETE/TEMP đang giữ; không đồng nhất owner_key logic với một FK tới mọi bảng. |
| WORKSPACE_FILE | storage_key PK, job_key/worker_token, owner_instance FK, file_key nullable, kind, created_at/eligible_since, cleanup_task nullable | Metadata đầu ra WAV/MP3/TEMP/MANIFEST; bytes ở F1. Job/file/task key là tương quan logic, không FK giả. |
| OPS_ACTIVITY | id CHAR(36), instance_id CHAR(36), kind, started_at | Admission lease HTTP/TTS_QUEUE/LOCAL_AUDIO/CLEANUP/RETENTION; không FK tới runtime_instance BINARY(16), không dọn theo tuổi để ép freeze. |
| OPS_GUARD | Hàng bền cho mode RUNNING/QUIESCING/FROZEN và owner_token theo V14; checkpoint/manifest thuộc quy trình vận hành | Chỉ điều phối backup/dispatch/cleaner như BKP-P02; **không start_generation hoặc khóa start/purge**. service_epoch vẫn quản lý riêng theo REC-P01, backup/restart thường không thu hồi quyền |
| Manifest backup — không có bảng SQL BACKUP_RUN | backupId,state,createdAt,expiresAt=created+7 ngày,owner/checkpoint,dump/catalog/file hashes,error | BKP-P02 stop app/worker/cleaner, MySQL còn hoạt động và không writer/DDL; dump/catalog/copy khớp. Không MySQL tạm mỗi lần backup; restore thử vẫn riêng |

V3 tạo bảng tài khoản quản trị, không sinh tài khoản hoặc secret. V4 quản lý window/receipt, V6–V7 hàng đợi/WAV, V9 audio/cặp, V10–V14 quyền đọc/lifecycle/kết quả local/vùng/gate. Bảng phiên bản mục 7 và DDL là nguồn cấu trúc thực; không dùng số mục của các phụ lục triển khai cũ làm trạng thái hiện hành.

retry_of_job_id trong hình là **liên kết logic**, không FK bắt giữ chuỗi job vô hạn; id cũ dùng đối chiếu nếu còn dữ liệu. Trường target_text FK và audio→job FK tổ hợp vẫn phải kiểm thật. Receipt/window chỉ dành Admin; guest dùng vé ký 24 giờ/ledger session, không sao chép dữ liệu khách vào receipt Admin hoặc thêm bảng context.

<a id="model-constraints"></a>

## 5. Ràng buộc, giao dịch và thứ tự xóa hiện hành

1. SLOT UK(place,language); TEXT UK(id,slot); AUDIO UK(id,text); PAIR FK tổ hợp trỏ đúng text của slot và audio của text; SLOT(active_pair,id) trỏ đúng pair cùng slot. REGION UK(id,place), PLACE(active_region,id) trỏ vùng của mình; draft_text cũng đúng slot.
2. TTS_JOB UK(id,target_text); V9 thêm UK(id,target_text,source_wav_file_id), AUDIO(job,text,wav) trỏ đúng job/đích/WAV. File có UK(id,format); WAV/MP3 của audio và WAV nguồn job được FK định dạng kiểm, không tráo file chỉ vì đều là READY. TEXT.active_tts_job_id FK tổ hợp trỏ job đúng text; chỉ tháo target sau bỏ pointer và xử lý AUDIO/PAIR. Target_invalid không xóa nhầm text khi job đã gửi.
3. SESSION.current_opening dùng FK tổ hợp; GRANT(opening,session) đúng chủ; VIEW chỉ UK tương quan để dọn grant/opening mà vẫn giữ lượt. Start ticket không FK/bảng riêng; request key UK và thông tin đối chiếu lưu trong session cùng TX. Purge theo D08 chỉ xóa con/session; vé cũ chắc đã hết hạn, không gate/generation/tombstone.
4. Lệnh Admin khóa window/kiểm account + expiry, receipt cùng TX; serialize key trước mutate, rồi PLACE→SLOT/TEXT→JOB→FILE theo một thứ tự đã thống nhất. Receipt replay có quyền vẫn trả kết quả dù target đã xóa; không replay khi service epoch đã đổi sau restore theo REC-P01 đã duyệt. Window 24 giờ vẫn kiểm trước replay, login không gia hạn.
5. Xóa điểm: khóa PLACE/version → vô hiệu target job, tháo TEXT.active_job → bỏ active/draft pointers → giữ VIEW và opening/grant snapshot, đặt live refs NULL → xóa PAIR/review refs DB/AUDIO rồi tháo FK job target → xóa TEXT/SLOT/REGION/PLACE. Giữ FILE_ASSET/claim/job tối thiểu và receipt. Các thay đổi DB một TX; hồ sơ tài liệu bên ngoài không xóa.
6. Điểm bị xóa không nhận chọn/grant mới. Grant đã cấp pin file đúng bản cho ACK/tải trong hạn theo API; nó không khôi phục PLACE/TEXT. Xóa asset chỉ sau DB commit + không tham chiếu VÀ không dùng chung/không tác vụ đang sử dụng + đủ hạn nguồn.
7. File refs là AUDIO_REVISION WAV/MP3, TTS_JOB.source_wav_file_id đang giữ để convert/retry và GRANT còn cần; active claims chặn thao tác dọn trong lúc đọc. Counter nếu có chỉ hỗ trợ, không thay kiểm mọi refs. ever_applied áp cho cả WAV/MP3; last_unapplied_at cập nhật khi bỏ **tham chiếu áp dụng cuối cùng**, không khi file vừa được tạo hoặc restore.
8. Metadata có FK/retry chưa khép thì chưa dọn. Lịch xóa không reset mốc gốc; backup/restore không tự tạo session/view hoặc gửi lại TTS. Backup thường giữ epoch/hạn; restore thật REC-P01 thu hồi quyền cũ trước public, không endedAt giả.

**Ví dụ dễ hiểu:** khách chọn C1=(T1,A1) nhận opening O1 và grant G1 ghim A1. Admin áp dụng C2, lần chọn mới nhận C2; G1 không tự đổi sang A2. Sau hết hạn O1/G1 được vô hiệu/dọn, V1 vẫn còn tới D08. Admin xóa điểm, V1 vẫn nhận biết id cũ; key xóa cũ trả receipt, không xóa điểm mới trùng tên.

### 5.1. Chỉ mục và khóa: phân biệt DDL với đề xuất

Ngoài PK/UK/FK đã nêu, đề xuất index theo truy vấn: session(started_at,id), session(expires_at), session(ended_at); view(viewed_at,place_key_snapshot,language), view(session_id,viewed_at); opening(session_id,state); grant(session_id,expires_at), grant(mp3_file_id); receipt(window_id,operation_key); guest_request_result(session_id,request_key); job(phase,updated_at), job(target_text_revision_id); file(state,last_unapplied_at), claim(file_id,owner_instance). Có thể dùng purge_due_at tính từ anchor để index dọn; không đổi timestamp gốc hoặc tin field client.

Độ chọn lọc/index thực phải đo trên MySQL và truy vấn JPA; không coi danh sách này là migration đã có. Không dùng partial index không được MySQL hỗ trợ để giả bảo đảm một active job; dùng pointer TEXT.active_tts_job_id dưới khóa. Lỗi lock/deadlock rollback DB thuần mới cân nhắc retry kỹ thuật, không provider retry.

<a id="model-trace"></a>

## 6. Truy vết và phần chưa kiểm

| Nhóm | Nguồn / API | Cần kiểm thực |
| --- | --- | --- |
| PLACE/REGION/SLOT/PAIR/hồ sơ | Q1/D09/A02.4–A02.6/UI-03/UI-04, FR-A02–A09/A12, UC-A02–A06, AC-A03–A21/A25/A26 | FK tổ hợp/pointer vòng, Apply/delete/worker cùng lúc, gate cặp vi và Q2 khi 412. |
| SESSION/OPENING/GRANT/VIEW/start ticket | D04/P01–P08/TECH-04, FR-K01–K10, UC-K01–K05; API-K01–K11 | Hai tab/reload/duplicate, UNIQUE ACK/late response, MP3/expiry, start key sau dọn không tạo phiên lần hai. |
| WINDOW/RECEIPT/JOB/FILE | A04/A07/UI-04/TECH-05/DATA-01–03; API-A01–A31/TX-* | Replay/unknown/khóa/worker restart; dọn file không race với đọc/backup. |
| Backup metadata | DATA-02–04; operations-design BKP-P02/REC-P01 | Snapshot DB–file/restore/lọc mốc gốc, quyền/chỗ trống/thời gian thực. |

LIFE-P02/P03/REC-P01 và BKP-P02 giữ phạm vi đã duyệt; LIFE-P01 dùng vé ký 24 giờ theo DATA-05. Mã và DDL V1–V14 đã có; báo cáo kiểm thử phân biệt kiểm tự động, diễn tập riêng và phần chưa nghiệm thu. Chưa coi toàn bộ ERD/ràng buộc đồng thời và môi trường phục vụ đã được nghiệm thu theo gói.


<a id="model-implementation"></a>

## 7. Cấu trúc thực thi và migrations

**Danh mục chuẩn:** [ERD vật lý đầy đủ](erd-vat-ly.md) có 25 bảng (24 ứng dụng + 1 Flyway), 36 FK. Năm bảng runtime_instance/runtime_task/workspace_file/ops_guard/ops_activity được kê ở ERD đầy đủ, không còn bị bỏ khỏi phạm vi dữ liệu chỉ vì các hình 2–4 là phần trích. Các tên cột trong phần trích đã đối chiếu schema V14.

DDL hiện hành được quản lý bằng Flyway V1–V14. Các biểu đồ phía trên mô tả quan hệ nghiệp vụ; ràng buộc vật lý được đối chiếu với DDL và kiểm MySQL. Việc sửa tài liệu không tự thay đổi checksum migration hoặc xác nhận đã nâng database đang phục vụ.

| Phiên bản | Nội dung |
|---|---|
| V1 | Phiên tham quan |
| V2 | Địa điểm và revision vùng |
| V3 | Tài khoản Admin |
| V4 | Cửa sổ lệnh và receipt |
| V5 | Slot nội dung, revision văn bản và tham chiếu hồ sơ |
| V6 | Hàng đợi TTS |
| V7 | Đăng ký WAV của TTS |
| V8 | Lịch sử lượt xem |
| V9 | Audio revision và cặp nội dung |
| V10 | Opening, grant và claim đọc file |
| V11 | Owner runtime, task và trạng thái dọn |
| V12 | Kết quả xử lý audio cục bộ |
| V13 | Giữ nguyên độ chính xác tọa độ vùng |
| V14 | Gate bảo trì và admission lease |

V14 bổ sung `ops_guard` (trạng thái RUNNING/QUIESCING/FROZEN và owner bảo trì) cùng `ops_activity` (lease của hoạt động đang thực thi). Admission và chuyển gate dùng giao dịch ngắn; lease không được xóa chỉ dựa trên tuổi. V13 lưu nguyên JSON tọa độ vùng trong LONGTEXT có CHECK JSON_VALID để Hash đọc lại không lệch. V12 tách marker chuyển đổi local khỏi lần dispatch provider, cho phép Retry dùng lại WAV hợp lệ.

Phạm vi triển khai và nghiệm thu được tổng hợp tại [hiện trạng bàn giao](../01-tong-quan/handoff.md); kết quả thực thi tại [báo cáo kiểm thử](../06-kiem-thu/README.md).

<a id="model-place-region-result"></a>
<a id="place-region-test-result"></a>
<a id="model-text-result"></a>
<a id="model-tts-queue-result"></a>
<a id="model-worker-report-result"></a>
<a id="model-audio-pair-result"></a>
<a id="audio-pair-data-test-result"></a>
<a id="model-pair-application-result"></a>
<a id="model-guest-content-result"></a>
<a id="model-guest-retention-result"></a>
<a id="model-lifecycle-cleanup-result"></a>
<a id="model-local-audio-result"></a>
<a id="model-region-precision"></a>
