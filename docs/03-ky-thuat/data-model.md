# TDD — Mô hình dữ liệu hiện hành

**Phiên bản nội dung:** 0.4, biên tập 09/11/2026. **Trạng thái:** ERD còn dự thảo. Đã có V1 guest_session, V2 địa điểm/vùng theo [phần nền địa điểm/vùng](#model-place-region-result), V3 tài khoản và V4 window/receipt theo [bằng chứng quản trị](api-transactions.md#admin-command-implementation), V5 văn bản nháp/hồ sơ theo [mục 7.2](#model-text-result), V6 hàng đợi TTS theo [mục 7.3](#model-tts-queue-result), V7/V8 WAV và lượt xem theo [mục 7.4](#model-worker-report-result). V9 bổ sung audio/cặp và seed riêng theo [mục 7.5](#model-audio-pair-result); nghiệp vụ áp dụng/bật-ẩn và mốc file có bằng chứng riêng tại [API 11.8](api-transactions.md#admin-pair-visibility-implementation). V10/grant/ACK/READ claim và quyền phục vụ có bằng chứng tại [mục 7.7](#model-guest-content-result); chưa toàn schema/vòng đời hoặc duyệt toàn mô hình.
**Mục đích:** vị trí chính duy nhất của mô hình nội dung, phiên/lượt, quyền tải, thao tác lặp, job và file. LIFE-P01 theo DATA-05: vé ký 24 giờ/ledger, không bảng start context hoặc start_generation.
**Đọc cùng:** [TDD](TDD.md), [API](api-transactions.md), [operations](operations-design.md), [PRD](../02-nghiep-vu/PRD.md), [decisions](../02-nghiep-vu/decisions.md). [Mô hình cũ](../99-luu-tru/data-model-history.md) chỉ để truy vết.

Mục lục: [1. Quy ước](#model-conventions) · [2. Nội dung](#model-content) · [3. Phiên/quyền](#model-guest) · [4. Thao tác/job/file](#model-operations) · [5. Ràng buộc/xóa](#model-constraints) · [6. Truy vết](#model-trace) · [7. Triển khai/kiểm chứng](#model-implementation).

<a id="model-conventions"></a>

## 1. Quy ước và phạm vi

- UUID do hệ thống cấp, không lấy tên làm khóa. Mapping BINARY(16)/Java UUID, thời gian UTC DATETIME(6), tên/ngôn ngữ Unicode, version tăng khi aggregate thay đổi. V1–V9 đã có DDL/mapping cho các bảng nêu tại mục 7, gồm audio/cặp và lịch sử lượt. Opening/grant/READ claim đã có V10 theo mục 7.7; dọn metadata/claim sau crash và phần vận hành còn chờ triển khai. Bằng chứng phiên khách tại [thực hành](practice-01-start-session.md#practice-jpa-mapping), địa điểm/vùng tại mục 7.1.
- PK: khóa chính; FK: tham chiếu được database kiểm; UK: tổ hợp không trùng. FK quan hệ cần chặn dùng RESTRICT/NO ACTION, không CASCADE/JPA xóa ngầm. Pointer nullable để tạo và tháo theo thứ tự trong giao dịch.
- Ngôn ngữ chỉ vi/en/fr/ru. Năm điểm là bộ chuẩn 20 văn bản + 20 audio, không trần số điểm. Tên Q1/D02-BS kiểm grapheme/chuẩn hóa tại Service; độ dài cột/byte không tự thay giới hạn ký tự hiển thị hoặc thêm giới hạn bài mới.
- Bản văn/audio/vùng đã lưu dùng định danh bất biến; sửa sinh revision thay thế. Con trỏ nháp khác con trỏ áp dụng. Không cần UI lịch sử phiên bản phức tạp.
- Dữ liệu Q2 chưa lưu/mốc nghe khách/Blob/owner lock nằm ở tab theo phạm vi đã chốt; không tạo bảng lịch sử audio hoặc đồng bộ thiết bị.

<a id="model-content"></a>

## 2. Địa điểm, vùng, văn bản/audio và cặp

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
        json polygon
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
| REGION_REVISION | geometry_hash do server tính lại; saved_at; UK(id,place_id) | Một đa giác kín đơn giản, cùng không gian nền; hình dở chỉ Q2. Bản đã xem không đổi sau Lưu thì không bắt xem lại. Ranh giới thật vẫn chờ xem xét, hash không chứng minh duyệt. |
| CONTENT_SLOT | UK(place_id,language), UK(id,place_id,language) | Một ngăn như Ngọ Môn/vi; một active_pair, không chờ đủ bốn tiếng mới áp dụng. |
| TEXT_REVISION | UK(id,slot_id), saved_at | Mỗi Lưu rõ ràng tạo bản bất biến mới và đổi draft pointer, không sửa bản đang dùng hoặc tự tạo TTS. Hash không thay id. |
| TEXT_REVIEW_REF | text_revision_id PK/FK, locator, limitations nullable, recorded_by FK, recorded_at, version | Gắn đúng bản đã lưu/hash. Không nhập lại ngày/người duyệt nếu đã có trong tài liệu; không tự thẩm định hoặc áp dụng. |
| AUDIO_REVISION | job_id UK; UK(id,text_revision_id); created_at | WAV/MP3 hoàn chỉnh đúng text/ngôn ngữ. Một kết quả chưa nghe/duyệt vẫn chỉ được nghe thử trong Admin. |
| AUDIO_REVIEW_REF | audio_revision_id PK/FK; các trường như text reference | Có thể cùng locator nếu hồ sơ chỉ rõ cả hai đối tượng; vẫn hai liên kết riêng. |
| CONTENT_PAIR | UK(slot_id,text_revision_id,audio_revision_id), UK(id,slot_id) | C2=(T2,A2) khớp cùng ngăn. Chỉ đổi audio thì dùng T hiện hành cùng A mới được duyệt; không bắt duyệt lại T không đổi. |

Phiên bản nội dung bất biến không làm hồ sơ bất biến: sửa tham chiếu có review_version/ETag riêng; Apply kiểm lại đúng hồ sơ hiện có trong TX, không tin snapshot ngoài TX. Phần mềm chỉ kiểm điều kiện/đối tượng và thông tin Admin đối chiếu, không tự đọc tài liệu rồi quyết định đã duyệt.

<a id="model-guest"></a>

## 3. Phiên theo tab, lần mở, quyền tải và lượt xem

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
        uuid counted_view_key
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
        uuid id PK
        uuid session_id FK
        uuid request_key
        uuid document_nonce
        long selection_seq
        string fingerprint
        string operation_type
        uuid opening_key
        uuid grant_key
        string result_code
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

| Bảng / con trỏ | Ràng buộc hiện hành đề xuất | Vòng đời và giới hạn |
| --- | --- | --- |
| Vé Bắt đầu (không bảng riêng) | Payload ký: requestId/credentialHash/serviceEpoch/issuedAt/ticketExpiresAt=issued+24 giờ/version; không dữ liệu tên/ngôn ngữ trong vé | Cấp nền; chỉ tạo lần đầu bằng vé còn hạn. Có ledger thì kiểm signature/quyền/epoch/fingerprint và trả gốc dù expiry vé đã qua; không JWT Admin hoặc đồng hồ khách |
| GUEST_SESSION | UK(start_request_key), credential_hash UK; start_fingerprint + start_ticket_issued_at/expires_at/digest để đối chiếu; service_epoch; current language/nonce/seq dọn theo end/expiry; current_opening FK tổ hợp | Lưu ID/key/quyền/fingerprint cùng TX. Session 8 giờ/ledger anchor+30 ngày giữ nguyên; purge không phối hợp thế hệ. Vé hết hạn mà ledger còn chỉ tra gốc, không tạo/gia hạn/đổi thông tin |
| GUEST_OPEN_INTENT | Giữ tên bảng 0.2 để truy vết, nghĩa hiện hành là **opening**; UK(id,session_id); created_at/closed_at; counted_view_key logical, không FK ngược tới view | Sau ACK vẫn giữ current opening để P06/đổi tiếng/reload không thêm lượt. Chọn điểm khác hoặc chủ động quay bản đồ đóng opening; opening/grant kỹ thuật giữ tối đa tới kết thúc/hết hạn rồi dọn an toàn, không lịch sử thất bại 30 ngày. |
| GUEST_CONTENT_GRANT | FK tổ hợp (opening_id,session_id); file FK RESTRICT nullable; text/pair/audio/điểm cũ là id snapshot logical, không FK cản xóa nội dung | Một grant gắn đúng text/tiếng/MP3 của một response; server kiểm session+credential+grant owner. expiresAt không vượt hạn phiên, revoked khi kết thúc. Không phải URL public/token độc lập, không cấp lại audio sau hạn. |
| GUEST_REQUEST_RESULT | UK(session_id,request_key), UK(session_id,document_nonce,selection_seq); fingerprint/action + opening/grant key logical + kết quả tối thiểu, không body text | Giữ kết quả K06/K07/K10 tới end/expiry để replay không đổi current selection hoặc tạo grant/lượt mới. Bản gốc mất thì báo không còn khả dụng, không thay bản khác âm thầm; không lịch sử nghe 30 ngày. |
| PLACE_VIEW | UK(session_id,opening_key); session FK, live_place nullable FK; **không FK tới opening/grant/text/audio** | ACK theo grant server và UNIQUE mới tạo một lượt; viewedAt server. Opening/grant được dọn sau hạn, view giữ D08. Không dùng tên gộp điểm/người. |
| document_nonce/selection_seq | Nonce document mới khi resume thành công; seq tăng trong document, kiểm dưới khóa session; không là bằng chứng quyền | Cùng credential mà sai document/seq cũ không đổi lựa chọn mới. Web Locks/owner bên tab vẫn phải kiểm, không dùng cookie chung thay credential. |

**LIFE-P01 đã duyệt tại DATA-05:** không start_generation, context hoặc gate giữa start và purge. [Protocol chính](operations-design.md#ops-start-simple): xác minh signature/quyền/epoch → tra ledger/đối chiếu → ledger có trả gốc kể cả vé hết hạn; ledger không có chỉ insert vé còn hạn 24 giờ. UNIQUE bảo vệ insert lặp, xung đột rollback TX rồi đọc lại đối chiếu.

Ticket issuedAt ≤ startedAt; thời điểm xóa ledger D08 sớm nhất là endedAt+30 ngày (endedAt ≥ startedAt), hoặc startedAt+8 giờ+30 ngày. Vì thế khi ledger bị purge đúng D08, vé đã hết 24 giờ và không thể tạo lại. Không lưu key/PII thêm sau purge; restore thật thu hồi epoch cũ trước public. Timestamp vé khác expires_at của phiên.

**Lượt đầu và reload:** SELECT/LANGUAGE grant có thể viewEligible=true khi opening chưa tính; RESTORE luôn false. ACK đầu đúng văn bản đã hiển thị lấy điểm/tên/tiếng/hash từ grant, không client tự khai. Nếu thiếu dịch lúc đầu rồi chủ động chọn tiếng có bài, lần đầu văn bản thành công tính đúng một lượt; các lần đổi sau không cộng. ACK đang bay của lựa chọn thật có thể ghi snapshot sau điểm bị xóa khi phiên còn hạn; không phải gửi bù offline. Server không thể chứng minh con người thực sự đọc: ACK là hợp đồng giữa client đã kiểm và server, cần thử race/hiển thị thực.

**Giữ lịch sử khi xóa:** giữ snapshot id/tên của lượt đã tính, đặt live_place_id NULL. Một điểm mới cùng tên có UUID khác; nhóm theo snapshot id/ngày/tiếng. Nhãn thống kê đề xuất dùng snapshot mới nhất trong nhóm nếu tên từng đổi, không gộp theo tên hoặc thêm lịch sử đổi tên mới.

<a id="model-operations"></a>

## 4. Chống thao tác lặp, job và tài nguyên file

```mermaid
erDiagram
    ADMIN_ACCOUNT ||--o{ ADMIN_COMMAND_WINDOW : cua_so_lenh
    ADMIN_COMMAND_WINDOW ||--o{ OPERATION_RECEIPT : ket_qua
    TTS_JOB o|--o{ TTS_JOB : thu_lai_co_chu_dich
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
        boolean outcome_uncertain
        uuid worker_token
        uuid source_wav_file_id FK
        string workspace_key
        long phase_version
        string provider_request_id
        datetime accepted_at
        datetime terminal_at
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
        uuid owner_instance
        string kind
        string state
        datetime opened_at
    }
```

| Bảng | Khóa/trường bổ sung | Điều kiện dùng / không được suy ra |
| --- | --- | --- |
| ADMIN_ACCOUNT | V3 đã có id BINARY(16), login_name UK phân biệt hoa thường/dấu, password_hash kèm tiền tố thuật toán, enabled | Một role cấp trước, bảng khởi tạo rỗng. Entity/Repository và xác thực/Q3 xem bằng chứng Admin, HttpSession tách guest |
| ADMIN_COMMAND_WINDOW | V4 đã có account FK; issued/expires do server, service_epoch thuộc môi trường; mỗi lệnh dùng key=(windowId,randomId) | LIFE-P02 đã duyệt: 24 giờ từ cấp, không gia hạn qua login. Đăng nhập lại cùng tài khoản vẫn tra window cũ còn hạn; window thiếu/hết hạn không được nhận key cũ như lệnh mới. Không là phiên Admin mới, không gia hạn 30 phút. |
| OPERATION_RECEIPT | V4 đã có UK(admin_account_id,window_id,operation_key); FK tổ hợp (window_id,admin_account_id) tới UK(id,admin_account_id) của window; fingerprint BINARY(32), http_status/result_code/operation_type, result_summary JSON tối thiểu, committed_at DATETIME(6) | Receipt và lệnh DB cùng TX, mã Hash gồm method/resource/canonical body/If-Match. Không lưu secret/text/tên khách hoặc payload đầy đủ, không FK target PLACE/TEXT đã xóa. Receipt tiếp nhận TTS đã trỏ job key theo nền A18/A19, không audio success; worker/WAV và Check/Retry đã bổ sung, provider thật/MP3/audio chưa được kiểm. |
| TTS_JOB | UK(admin_account_key,request_window_key,request_operation_key) là snapshot logical không FK bắt giữ window; target FK nullable + snapshot; worker_instance/token/phase_version; dispatched_at/result_staged_at/updated_at; retry_of logical nếu cần dọn job cũ | TEXT.active_tts_job_id cùng text ngăn hai job active/unknown; latest_tts_job_key logical giữ nhận biết kết quả mới nhất khi text còn sống. LIFE-P03 đã duyệt: job cũ terminal rõ chỉ dọn sau bảy ngày nếu không còn ref/use/current; audio/nháp còn liên kết và active/unknown giữ. Không tự biến TTL thành lỗi/provider retry. |
| FILE_ASSET | key chỉ server tạo, checksum/size/duration/ready_at; cleanup_status/retry/cause | File bất biến, không ghi đè cùng key; không publish pointer trước bytes hoàn tất và kiểm. Metadata phải còn để báo dọn chờ/lỗi; trỏ WAV và MP3 riêng. |
| FILE_CLAIM | FK file, owner_instance/task/download key, kind READ/WORKER; ACTIVE→RELEASED→xóa | Bảo vệ đọc/copy/worker đang thực dùng; không là tham chiếu nội dung giữ mãi. Không dọn chỉ vì thiếu heartbeat khi tiến trình còn sống/chưa rõ. |
| OPS_GUARD | Hàng bền cho maintenance_mode OPEN/QUIESCING/FROZEN, owner/checkpoint/version | Chỉ điều phối backup/dispatch/cleaner như BKP-P02; **không start_generation hoặc khóa start/purge**. service_epoch vẫn quản lý riêng theo REC-P01, backup/restart thường không thu hồi quyền |
| BACKUP_RUN (metadata vận hành cục bộ riêng kho phục vụ) | backupId,state,createdAt,expiresAt=created+7 ngày,owner/checkpoint,dump/catalog/file hashes,error | BKP-P02 stop app/worker/cleaner, MySQL còn hoạt động và không writer/DDL; dump/catalog/copy khớp. Không MySQL tạm mỗi lần backup; restore thử vẫn riêng |

V3 chỉ tạo bảng tài khoản quản trị, không sinh tài khoản hoặc secret. [AdminAccount](../../backend/src/main/java/vn/hueqr/backend/entity/AdminAccount.java), [Repository](../../backend/src/main/java/vn/hueqr/backend/repository/AdminAccountRepository.java) và [hợp đồng/bằng chứng xác thực](api-transactions.md#admin-auth-implementation) là nguồn triển khai. V4 bổ sung window/receipt cùng Entity/Repository, giữ khóa tài khoản dạng UUID trong Java và FK trong database. [Hợp đồng, giao dịch và kiểm chứng](api-transactions.md#admin-command-implementation) là nguồn chính. Nền job/marker có bằng chứng ở mục 7.3; worker/WAV/READY ở mục 7.4. Provider thật, dọn metadata/file và các bảng vận hành khác vẫn chờ triển khai/kiểm chứng.

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

### 5.1. Chỉ mục và lock cần mapping khi triển khai

Ngoài PK/UK/FK đã nêu, đề xuất index theo truy vấn: session(started_at,id), session(expires_at), session(ended_at); view(viewed_at,place_key_snapshot,language), view(session_id,viewed_at); opening(session_id,state); grant(session_id,expires_at), grant(mp3_file_id); receipt(window_id,operation_key); guest_request_result(session_id,request_key); job(phase,updated_at), job(target_text_revision_id); file(state,last_unapplied_at), claim(file_id,state). Có thể dùng purge_due_at tính từ anchor để index dọn; không đổi timestamp gốc hoặc tin field client.

Độ chọn lọc/index thực phải đo trên MySQL và truy vấn JPA; không coi danh sách này là migration đã có. Không dùng partial index không được MySQL hỗ trợ để giả bảo đảm một active job; dùng pointer TEXT.active_tts_job_id dưới khóa. Lỗi lock/deadlock rollback DB thuần mới cân nhắc retry kỹ thuật, không provider retry.

<a id="model-trace"></a>

## 6. Truy vết và phần chưa kiểm

| Nhóm | Nguồn / API | Cần kiểm thực |
| --- | --- | --- |
| PLACE/REGION/SLOT/PAIR/hồ sơ | Q1/D09/A02.4–A02.6/UI-03/UI-04, FR-A02–A09/A12, UC-A02–A06, AC-A03–A21/A25/A26 | FK tổ hợp/pointer vòng, Apply/delete/worker cùng lúc, gate cặp vi và Q2 khi 412. |
| SESSION/OPENING/GRANT/VIEW/start ticket | D04/P01–P08/TECH-04, FR-K01–K10, UC-K01–K05; API-K01–K11 | Hai tab/reload/duplicate, UNIQUE ACK/late response, MP3/expiry, start key sau dọn không tạo phiên lần hai. |
| WINDOW/RECEIPT/JOB/FILE | A04/A07/UI-04/TECH-05/DATA-01–03; API-A01–A31/TX-* | Replay/unknown/khóa/worker restart; dọn file không race với đọc/backup. |
| Backup metadata | DATA-02–04; operations-design BKP-P02/REC-P01 | Snapshot DB–file/restore/lọc mốc gốc, quyền/chỗ trống/thời gian thực. |

LIFE-P02/P03/REC-P01 và hướng BKP-P02 **đã được duyệt đúng phạm vi ngày 04/11**. DATA-05 chốt LIFE-P01 vé ký 24 giờ, bỏ generation/context/10 phút. V1/phiên khách và V2/địa điểm-vùng có bằng chứng kiểm thử riêng. Các phần mô hình/API còn lại tiếp tục cần triển khai và kiểm chứng, TECH-05/DATA-03 giữ phạm vi đã chốt.


<a id="model-implementation"></a>

## 7. Sản phẩm dữ liệu và bằng chứng kiểm chứng

Bằng chứng theo từng mốc được đặt sau mô hình, ràng buộc và truy vết. V3/V4 được dẫn tới nguồn kiểm chứng quản trị; không coi các bảng chưa triển khai đã tồn tại.

<a id="model-place-region-result"></a>

### 7.1. Nền dữ liệu địa điểm và vùng — T02

Hòa phụ trách cấu trúc/mapping theo G02. Phần này bổ sung nền dữ liệu cho T05, API và các quy tắc nghiệp vụ quản trị được triển khai tiếp theo phụ thuộc.

| Sản phẩm | Phạm vi đã triển khai |
| --- | --- |
| [V2__create_place_and_region_revision.sql](../../backend/src/main/resources/db/migration/V2__create_place_and_region_revision.sql) | Tạo place, region_revision, khóa ngoại RESTRICT và khóa tổ hợp bảo đảm active_region_revision_id thuộc chính địa điểm. V1 được giữ nguyên |
| [Place](../../backend/src/main/java/vn/hueqr/backend/entity/Place.java) và [PlaceRepository](../../backend/src/main/java/vn/hueqr/backend/repository/PlaceRepository.java) | UUID độc lập tên, mặc định ẩn và chưa có vùng áp dụng, tên trùng được lưu riêng. @Version phát hiện cập nhật từ phiên bản cũ |
| [RegionRevision](../../backend/src/main/java/vn/hueqr/backend/entity/RegionRevision.java) và [RegionRevisionRepository](../../backend/src/main/java/vn/hueqr/backend/repository/RegionRevisionRepository.java) | JSON hình vùng, mã không gian nền, mã Hash 32 byte và mốc lưu UTC. Các trường của bản đã lưu không có setter và dùng updatable=false trong JPA; đã bổ sung truy vấn theo cả revision.id và place.id để phục vụ A13/A14 |
| [PlaceRegionSchemaTests](../../backend/src/test/java/vn/hueqr/backend/schema/PlaceRegionSchemaTests.java) và [PlaceRegionRepositoryTests](../../backend/src/test/java/vn/hueqr/backend/repository/PlaceRegionRepositoryTests.java) | Ràng buộc MySQL, mapping, con trỏ, phiên bản và bảo toàn dữ liệu cũ khi nâng migration |

Lựa chọn mapping giữ con trỏ bản vùng trong Place dưới dạng UUID, database kiểm quan hệ bằng khóa ngoại tổ hợp. RegionRevision giữ Entity Place bằng ManyToOne lazy, database lưu place_id. Không cấu hình cascade xóa, việc tháo con trỏ và xóa từng đối tượng thuộc giao dịch nghiệp vụ T05. Lưu bản vùng không tự áp dụng hoặc bật hiện địa điểm.

Cột JSON kiểm cú pháp JSON tại database. Service/API đã kiểm đa giác kín đơn giản, không gian tọa độ, tính lại mã Hash và đổi con trỏ vùng theo [A12–A14](api-transactions.md#admin-region-implementation), [bằng chứng 204 test](api-transactions.md#admin-region-test-result); V1–V4 và Entity không đổi trong bước này. Frontend/xem xét ranh giới thực tế, gate cặp vi khi bật hiện, nội dung/cặp/hồ sơ, seeds năm điểm/bốn ngôn ngữ và xóa phối hợp toàn schema chưa hoàn tất. Các fixture chỉ dùng tên/vùng thử trong database tạm, không cung cấp vùng hay nội dung chưa duyệt cho khách.

Khóa tham chiếu tổ hợp dùng UNIQUE(id, place_id) của region_revision. JSON được ánh xạ bằng JdbcTypeCode JSON. Đây là lựa chọn kỹ thuật cho mapping đã kiểm, giữ nghiệp vụ PRD. [MySQL 8.4 về khóa ngoại](https://dev.mysql.com/doc/refman/8.4/en/create-table-foreign-keys.html), [Hibernate về JSON](https://docs.hibernate.org/stable/orm/userguide/html_single/#basic-mapping-json)

<a id="place-region-test-result"></a>

#### Bằng chứng kiểm thử ngày 08/11/2026

Chạy trên bản sao backend tại `C:\Users\khait\AppData\Local\Temp\hueqr-place-region-cd1e1cbd21b04ecf89bc1198d734d31f\backend`, Java 21.0.12.1, Maven Wrapper 3.9.12, Spring Boot 4.1.2, Docker Linux Engine 29.8.1 và MySQL 8.4.13 Testcontainers. Database và fixture được tạo tạm, tách môi trường phục vụ. Lệnh hồi quy trên bản sao là `mvnw.cmd -o test`.

Lần cuối kết thúc 17:02:17 +07, đạt **104 test**, 0 failures/errors/skipped, **BUILD SUCCESS, exit 0**. Đã đối chiếu đủ 13 báo cáo XML Surefire, gồm 88 ca hồi quy trước và 16 ca mới trong PlaceRegionSchemaTests/PlaceRegionRepositoryTests. Ca nâng V1 có dữ liệu lên V2 giữ phiên khách, mốc bắt đầu và checksum V1, tạo hai bảng mới rỗng. Log bộ cuối ở `..\full-tests.log`, báo cáo XML/TXT tại `target/surefire-reports` của bản sao. Lần đầu 103 test đạt lúc 16:59:32 +07 được giữ ở `..\full-tests-103.log`. Tệp sinh và thư mục tạm không thuộc nhóm commit.

Kiểm tra ở tầng dữ liệu và JPA. API/UI quản trị, xác thực Admin, kiểm hình học/ranh giới thật, triển khai trên database phục vụ và toàn T02 còn cần kiểm chứng. Checklist giữ nguyên 89 ô/18 dấu tích.

<a id="model-text-result"></a>

### 7.2. Nền dữ liệu văn bản nháp và tham chiếu hồ sơ — T02

Theo G02, Hòa phụ trách cấu trúc/mapping và test schema; Thành phụ trách nghiệp vụ/HTTP quản trị. [V5](../../backend/src/main/resources/db/migration/V5__create_content_slot_and_text_revision.sql) chỉ bổ sung ba bảng dưới đây, giữ nguyên V1–V4. ERD mục 2 tiếp tục là thiết kế đầy đủ; các con trỏ active_pair/active_tts_job và bảng audio/job/cặp chưa được tạo trong V5.

| Sản phẩm | Mapping và ràng buộc đã có |
| --- | --- |
| [ContentSlot](../../backend/src/main/java/vn/hueqr/backend/entity/ContentSlot.java) | UUID BINARY(16), Entity Place lazy/database lưu place_id; UNIQUE(place_id,language), CHECK vi/en/fr/ru phân biệt hoa thường, @Version. draft_text_id là UUID nullable với FK tổ hợp (draft_text_id,id) → text_revision(id,slot_id), không thể trỏ nháp của tiếng hoặc địa điểm khác |
| [TextRevision](../../backend/src/main/java/vn/hueqr/backend/entity/TextRevision.java) | Entity ContentSlot lazy/database lưu slot_id, body LONGTEXT, content_hash BINARY(32), saved_at DATETIME(6). Các trường nội dung/định danh/mã Hash/mốc lưu updatable=false, không có setter; Service tạo bản mới, không sửa body bản cũ. Hai con trỏ job được bổ sung riêng tại mục 7.3. Mã Hash trùng không đồng nhất Id |
| [TextReviewReference](../../backend/src/main/java/vn/hueqr/backend/entity/TextReviewReference.java) | text_revision_id PK/FK, một tham chiếu mỗi văn bản đã lưu; locator, limitations nullable, recorded_by FK Admin, recorded_at DATETIME(6), @Version riêng. Gắn đúng văn bản bất biến/hash; không sao chép tham chiếu khi lưu bản mới và không lưu người/ngày duyệt tự suy đoán |

Các Repository tương ứng được bổ sung. FK đều RESTRICT, không cascade hoặc dọn tự động; xóa phối hợp toàn schema vẫn là phần nghiệp vụ cần triển khai. Lưu nháp tạo ngăn khi cần, tạo revision rồi đổi con trỏ và ghi receipt trong cùng giao dịch. Ghi hồ sơ không đổi slot version; hai thao tác giữ Place version/visible/vùng áp dụng. [Hợp đồng và khóa giao dịch](api-transactions.md#api-text-contract) là nguồn chi tiết; có tham chiếu hồ sơ không chứng minh bản đã được phê duyệt.

[ContentSchemaTests](../../backend/src/test/java/vn/hueqr/backend/schema/ContentSchemaTests.java) kiểm bốn tiếng/UNIQUE/FK cùng ngăn/RESTRICT, nâng V4 có dữ liệu lên V5 giữ checksum V1–V4 và dữ liệu địa điểm/vùng/tài khoản/window/receipt, ba bảng mới rỗng. [Hồi quy 240 test](api-transactions.md#admin-content-test-result) kiểm cả mapping qua API, thời gian micro giây, đồng thời và rollback receipt. Chưa áp V5 lên database phục vụ; không có seeds nội dung/ranh giới hoặc dữ liệu đã duyệt từ fixture này.


<a id="model-tts-queue-result"></a>

### 7.3. Nền dữ liệu hàng đợi TTS — T02

[V6](../../backend/src/main/resources/db/migration/V6__create_tts_job_queue.sql) bổ sung tts_job và hai con trỏ active_tts_job_id/latest_tts_job_key trong text_revision; V1–V5 giữ nguyên. [TtsJob](../../backend/src/main/java/vn/hueqr/backend/entity/TtsJob.java) lưu khóa văn bản sống dạng UUID nullable với FK, snapshot Id địa điểm/văn bản/tiếng/mã Hash/model/giọng, khóa tiếp nhận logic, mốc UTC micro giây, phase/@Version và owner/token của marker. Không sao chép body hoặc prompt vào bảng job. Khóa tổ hợp active job chỉ trỏ job thuộc chính văn bản; latest key là liên kết logic. FK RESTRICT, không cascade xóa; phần xóa/phục hồi/dọn worker chưa được suy thành đã có từ ràng buộc này.

Khóa tài khoản/window/operation ở job là snapshot với UNIQUE bộ ba, không FK giữ window sống theo job. Service vẫn phải kiểm quyền/window khi tạo lệnh; snapshot không phải quyền truy cập. Đích mất có thể tháo FK và giữ snapshot, không tạo lại bản ghi theo tên hoặc Id cũ. Job active/unknown chưa có terminal/dọn trong V6.

Schema hiện chỉ hỗ trợ QUEUED/DISPATCHING/UNCERTAIN; CHECK yêu cầu owner/token/dispatched_at trước hai chặng sau và không có marker ở QUEUED. Bản đầy đủ của ERD/state machine vẫn là thiết kế: source WAV/file, audio, các chặng kết quả/terminal, retry và dọn cần bổ sung cùng module tương ứng. Nội dung, định danh, mã Hash và saved_at của TextRevision tiếp tục bất biến ở JPA/Service; chỉ các con trỏ job được cập nhật.

[TtsJobSchemaTests](../../backend/src/test/java/vn/hueqr/backend/schema/TtsJobSchemaTests.java) kiểm FK đúng text, UNIQUE/CHECK/RESTRICT và nâng V5→V6 giữ văn bản/hồ sơ/nháp/checksum. [Bằng chứng 267 test](api-transactions.md#admin-tts-queue-test-result) gồm các ca API/concurrent/rollback/marker; database đều tạm, chưa migration môi trường phục vụ hoặc chứng minh toàn T02/T07.


<a id="model-worker-report-result"></a>

### 7.4. WAV của job và lịch sử lượt xem — V7/V8

V7 thêm [FileAsset](../../backend/src/main/java/vn/hueqr/backend/entity/FileAsset.java)/Repository và bảng file_asset cho WAV READY: storage key UNIQUE do server cấp, checksum BINARY(32), số byte/thời lượng dương và ready_at micro giây. Mốc ever_applied/last_unapplied/orphan giữ để nối vòng đời, chưa có cleaner tự thay đổi các mốc này. Job thêm FK source_wav_file_id RESTRICT, result_staged_at, terminal_at, failure_code và retry_of logical. CHECK phân biệt RESULT_STAGED có file/mốc với các phase chưa có file, terminal chỉ dành FAILED/DISCARDED. Text content/hash vẫn bất biến; active pointer được tháo khi lỗi xác định, latest key giữ kết quả để không ngầm tạo lại provider bằng nút Tạo.

V8 thêm [PlaceView](../../backend/src/main/java/vn/hueqr/backend/entity/PlaceView.java)/Repository và bảng place_view theo phần PLACE_VIEW của ERD mục 3. UNIQUE(session_id,opening_key) chống ghi hai lượt cho cùng opening; session FK RESTRICT, live_place FK SET NULL, snapshot Id/tên/ngôn ngữ/mốc không bị xóa theo địa điểm. Không FK opening/grant/text/audio, nên lịch sử không bắt giữ dữ liệu kỹ thuật hoặc file. Thêm index theo thời gian/địa điểm/ngôn ngữ và theo phiên; guest_session có index started_at/id. Nền bảng đã có, luồng khách opening/grant/ACK chưa nối ghi lượt thật.

[TtsWavSchemaTests](../../backend/src/test/java/vn/hueqr/backend/schema/TtsWavSchemaTests.java) kiểm CHECK/FK WAV, nâng V6→V7 giữ job/marker/owner/version/checksum; [PlaceViewSchemaTests](../../backend/src/test/java/vn/hueqr/backend/schema/PlaceViewSchemaTests.java) kiểm UNIQUE/FK/xóa điểm giữ snapshot và xóa phiên phải dọn lượt trước. V1–V6 giữ nguyên byte; database kiểm thử tách biệt. [Bằng chứng 318 test](api-transactions.md#worker-report-test-result) là nguồn kết quả, chưa toàn schema/grant/audio/retention/backup. Chặn đọc quá hạn D08 không thay cho job xóa vật lý.


<a id="model-audio-pair-result"></a>

### 7.5. Nền dữ liệu audio/cặp và khởi tạo năm điểm — V9

Theo G02, schema, mapping, dữ liệu khởi tạo và kiểm thử dữ liệu thuộc T02/Hòa. Phần này triển khai nền để T05 kiểm hồ sơ/áp dụng cặp và T07 đăng ký kết quả MP3; không khẳng định hai luồng nghiệp vụ đó đã chạy.

| Sản phẩm | Ràng buộc và phạm vi đã triển khai |
| --- | --- |
| [V9](../../backend/src/main/resources/db/migration/V9__create_audio_revision_and_content_pair.sql) | Thêm audio_revision, audio_review_ref, content_pair và content_slot.active_pair_id nullable; không sửa V1–V8 hoặc tự tạo audio/cặp/áp dụng |
| [AudioRevision](../../backend/src/main/java/vn/hueqr/backend/entity/AudioRevision.java)/Repository | Entity TextRevision lazy trong Java, database lưu text_revision_id. Job duy nhất cho mỗi audio; FK (job_id,text_revision_id,wav_file_id) khớp cả đích văn bản và WAV nguồn của job. Bản đã lưu không có setter và dùng updatable=false |
| [AudioReviewReference](../../backend/src/main/java/vn/hueqr/backend/entity/AudioReviewReference.java)/Repository | Một tham chiếu riêng mỗi audio, locator/limitations và tài khoản/mốc ghi/version; không sao chép hồ sơ của bản khác, không tự suy người/ngày duyệt. Có tham chiếu không chứng minh đã được duyệt |
| [ContentPair](../../backend/src/main/java/vn/hueqr/backend/entity/ContentPair.java)/Repository | FK text cùng slot, audio đúng text; UNIQUE(slot,text,audio), UNIQUE(id,slot). Tạo một cặp không tự đổi active_pair hoặc nháp, không tự bật hiện địa điểm |
| [ContentSlot](../../backend/src/main/java/vn/hueqr/backend/entity/ContentSlot.java) | Hai UUID draft_text_id/active_pair_id riêng. FK active_pair đúng ngăn, @Version phát hiện thay đổi đồng thời; lưu nháp mới không làm mất cặp cũ |
| [FileAsset](../../backend/src/main/java/vn/hueqr/backend/entity/FileAsset.java) | Mở định dạng WAV/MP3, giữ READY/metadata dương/mã Hash và storage key không trùng. Khóa (id,format) chặn tráo định dạng; hai file của audio khác Id. Job.source_wav vẫn chỉ nhận WAV. Nhiều audio được cùng tham chiếu file, FK chặn xóa file còn dùng |

Hai cột wav_format/mp3_format trong audio và source_wav_format trong job là giá trị cố định do DEFAULT/CHECK trong schema; không phải ngôn ngữ, lựa chọn từ client hoặc trường thay đổi theo job. Các FK mới dùng RESTRICT, không CASCADE hoặc xóa ngầm qua JPA. Audio giữ tham chiếu job/đích/WAV trong suốt thời gian bản audio còn tồn tại; khi xóa phải bỏ cặp/hồ sơ/audio trước khi tháo đích job hoặc dọn job/file. Đây là phần ràng buộc dữ liệu; xử lý xóa, quyền và khóa giao dịch vẫn cần Service T05.

Database kiểm quan hệ và metadata, không kiểm bytes MP3 hoặc nội dung hồ sơ bên ngoài. Service áp dụng phải kiểm lại đúng text/audio, hồ sơ/version, file sẵn sàng và điều kiện nghiệp vụ trong cùng giao dịch; chỉ có các Repository hoặc gọi setter không chứng minh đã áp dụng hợp lệ. V9 không thêm phase SUCCEEDED, không chuyển RESULT_STAGED thành thành công, không nối FFmpeg hoặc phát file cho khách.

#### Dữ liệu khởi tạo tách khỏi migration tự động

[five-places.sql](../../backend/src/main/resources/db/seed/five-places.sql) là script chạy riêng trong một giao dịch, không nằm trong db/migration và không được Spring tự chạy. Script tạo năm địa điểm D06 ở trạng thái ẩn: Cổng Ngọ Môn, Điện Thái Hòa, Tử Cấm Thành, Cửu Đỉnh và Cung Diên Thọ; mỗi điểm có bốn ngăn vi/en/fr/ru trống. Không có tọa độ/vùng, bài thuyết minh, audio, tài khoản, secret, hồ sơ duyệt hoặc cặp đang áp dụng được sinh từ script.

UUID khởi tạo cố định, lưu ngay trong script và độc lập tên. Chạy lại chỉ bổ sung Id điểm/ngăn ngôn ngữ còn thiếu, không đặt lại tên/version/thời điểm/con trỏ đã được sửa. Điểm có tên trùng nhưng Id khác được giữ riêng; ngăn cùng điểm/ngôn ngữ đã có được dùng nguyên Id. Không chạy script này để reset dữ liệu; việc áp migration/seed lên database phục vụ do người vận hành thực hiện sau khi đối chiếu hiện trạng và quy trình backup.

<a id="audio-pair-data-test-result"></a>

#### Bằng chứng kiểm thử ngày 09/11/2026

Chạy `mvnw.cmd --offline test` trên bản sao `C:\Users\khait\AppData\Local\Temp\hueqr-audio-data-ft4lchr0\backend`, Java 21.0.12.1/Maven Wrapper 3.9.12/Boot 4.1.2/Security 7.1.2 và MySQL 8.4.13 Testcontainers/Docker Linux Engine 29.8.1. Kết thúc **13:27:28 +07 ngày 09/11/2026**, **336 test đạt, 0 failures/errors/skipped, BUILD SUCCESS, exit 0**, đối chiếu 36 báo cáo XML Surefire. Nguồn chính có 167 file/150 Java, thêm 12 file và sửa 4 file so với mốc 318 test; SHA-256 khớp bản đã kiểm. V1–V8/pom.xml/Wrapper/cấu hình đang dùng được giữ nguyên.

- [AudioPairSchemaTests](../../backend/src/test/java/vn/hueqr/backend/schema/AudioPairSchemaTests.java): chống sai text/job/WAV/định dạng/ngăn/cặp, UNIQUE, active pointer, FK RESTRICT và file dùng chung; xóa theo thứ tự chỉ bỏ dữ liệu riêng, còn metadata file cho cleaner sau. Nâng V8 có WAV/job/nháp/hồ sơ lên V9 giữ dữ liệu/mốc/marker/checksum, không tạo audio/cặp/duyệt giả.
- [AudioPairRepositoryTests](../../backend/src/test/java/vn/hueqr/backend/repository/AudioPairRepositoryTests.java): mapping/Unicode/UTC micro giây, UUID đúng quan hệ, mã Hash không dùng chung mảng mutable, hồ sơ có version và không ghi đè bản mới, nháp mới giữ cặp đang áp dụng.
- [InitialPlaceDataTests](../../backend/src/test/java/vn/hueqr/backend/schema/InitialPlaceDataTests.java): migration không tự seed; chạy script riêng có 5 điểm/20 ngăn trống, chạy lại không ghi đè; điểm trùng tên và ngăn ngôn ngữ có sẵn giữ định danh. Script chỉ chạy trong database tạm ở bộ kiểm này.
- Hồi quy toàn bộ bộ kiểm trước đó. PlaceRegionSchemaTests kiểm đủ V1–V9; TtsWavSchemaTests tiếp tục chặn định dạng ngoài phạm vi bằng FLAC, còn MP3 nay được V9 cho phép; con trỏ WAV được kiểm riêng vẫn từ chối MP3. Không giảm assertion về checksum hoặc bảo toàn dữ liệu cũ.

Log `..\full-tests.log`, XML tại `target/surefire-reports`, baseline.json/tested-source-hashes.json/verification.json ở thư mục tạm cha. Các lần kiểm chưa đạt được giữ trong log audio-data-tests*.log để truy vết; kết quả ở trên chỉ ghi bộ cuối sau sửa. Fixture chỉ dựng metadata/file giả lập, không chứng minh chuyển MP3, nghe duyệt hoặc quyền phục vụ audio.

**Điểm tiếp tục:** T05 triển khai hồ sơ audio, áp dụng cặp và điều kiện bật hiện; T07 bổ sung chuyển MP3/đăng ký audio rồi terminal job khi môi trường sẵn sàng. T03 còn opening/grant/ACK và luồng nội dung khách; D08, file claims/cleaner, backup/restore và bộ nội dung thực tế chưa hoàn tất. Không tích toàn T02 hoặc thay dấu tích chỉ từ kết quả schema.


<a id="model-pair-application-result"></a>

### 7.6. Sử dụng cặp và vòng đời file trong nghiệp vụ Admin

Không thay schema V1–V9 hoặc seed sau mốc 7.5. Place thêm thao tác đổi visible/mốc cập nhật; FileAsset thêm markApplied/markUnapplied theo mốc tham chiếu áp dụng cuối. Repository bổ sung locking read SLOT/FILE, projection chủ audio, tìm/reuse cặp và đếm tham chiếu file qua mọi active_pair. Truy vấn đếm dùng Id file, không tên/storage key hoặc số điểm cố định.

[Hợp đồng API 9.13](api-transactions.md#api-pair-visibility-contract) giữ thứ tự khóa, quyền và kiểm file; [kết quả 366 test](api-transactions.md#admin-pair-visibility-test-result) kiểm cả rollback và file dùng chung. Các phương thức Entity/Repository không tự là quyền duyệt/áp dụng. Dọn file/claims, xóa điểm và thu hồi liên kết phục vụ vẫn cần nghiệp vụ riêng, không suy metadata last_unapplied_at thành file đã bị xóa.


<a id="model-guest-content-result"></a>

### 7.7. V10 — lần mở, grant, kết quả lựa chọn và claim đọc

[V10](../../backend/src/main/resources/db/migration/V10__create_guest_opening_grant_and_file_claim.sql) mở rộng guest_session với current_language/document_nonce/selection_seq/current_opening_id và thêm guest_open_intent, guest_content_grant, guest_request_result, file_claim. Không sửa migration V1–V9 hoặc seed. Nâng V9 có dữ liệu lên V10 đã kiểm giữ bản ghi/mốc/credential hash/checksum cũ; chỉ điền current_language=initial_language cho phiên chưa ended, không tạo opening/grant/lượt giả.

- current_opening FK tổ hợp kiểm cùng session. Opening giữ snapshot điểm/tên, live_place FK SET NULL và trạng thái OPEN/CLOSED/version/mốc tạo-đóng. ACK không đóng opening.
- Grant kiểm FK opening cùng session, source/RESTORE không viewEligible, deadline sau lúc cấp; text/pair/audio là khóa logical, MP3 file FK RESTRICT. Runtime kiểm file đúng MP3/READY dưới khóa FILE, không coi FK đơn này thay kiểm format/quyền.
- guest_request_result dùng PK(session_id,request_key) và UK(session_id,document_nonce,selection_seq), fingerprint/action và khóa kết quả; không body text hoặc lịch sử nghe. Mô hình logic mục 3 không là bản DDL: id/result_code không cần cột riêng; action giữ SELECT/LANGUAGE/LEAVE, selection_kind/opening_version/language/available_languages đủ tái tạo kết quả. counted_view_key không lưu dư trong opening; UNIQUE của PLACE_VIEW là nguồn xác định lượt.
- file_claim có file FK RESTRICT, owner_key logical là grant, kind READ và created_at. Claim không tự hết hạn theo thời gian; xóa khi stream thực đóng. Không giữ FK session khiến claim cũ hồi sinh quyền hoặc chặn D08; cleaner vẫn phải xét claim, không chỉ đếm FK grant.

[Bằng chứng và giới hạn](api-transactions.md#guest-content-test-result) ghi 26 ca API/5 ca schema mới cùng hồi quy; snapshot/claim độc lập dữ liệu phục vụ. Quyền hết hạn bị chặn ngay nhưng metadata/grant pin hiện còn tới khi dọn được triển khai. Thu hồi khi end đã có, purge kỹ thuật/D08/claim sau crash và backup/restore chưa có; không coi toàn schema/vòng đời hoàn tất.

<a id="model-guest-retention-result"></a>

### 7.8. Dọn context và D08 ngày 10/11/2026

Không thêm hoặc sửa migration. GuestRetentionRepository dùng các quan hệ V1/V8/V10, tháo current_opening_id trước xóa request-result/grant/opening; tới hạn D08 mới xóa PLACE_VIEW rồi GUEST_SESSION. READ claim còn giữ grant chặn dọn; FILE_ASSET/bytes không bị xóa. Một TX/phiên giữ các lần tháo/xóa cùng rollback, không ON DELETE CASCADE hoặc tombstone mới. UTC đọc/ghi đồng nhất JPA và DATETIME(6).

[Operations 6.2](operations-design.md#ops-guest-retention-result) giữ thuật toán, 24 ca mới, hồi quy toàn cây và giới hạn scheduler/claim rò. Repository thuộc T02; policy/Service/runner và test thuộc T05. Chưa đủ toàn mô hình/vòng đời hoặc tiêu chí nghiệm thu retention vận hành.

### Nhật ký biên tập



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-03-ky-thuat-data-model-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
