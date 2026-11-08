# DFD — Luồng dữ liệu của hệ thống

**Phiên bản nội dung:** 0.3; biên tập 05/11/2026. **Trạng thái:** dự thảo, chưa duyệt từng sơ đồ hoặc kiểm chứng bằng phần mềm.
**Mục đích:** một vị trí chính cho mức 0 ngữ cảnh, mức 1 và ba phân rã mức 2; giữ LIFE-P01 vé ký 24 giờ. DFD bổ sung cho ERD/sequence/trạng thái/API, không thay các mô hình đó.
**Nguồn:** [Business Flow](../02-nghiep-vu/business-flow.md), [PRD](../02-nghiep-vu/PRD.md), [decisions](../02-nghiep-vu/decisions.md), [model](data-model.md), [API](api-transactions.md). Mermaid và SVG là nguồn sơ đồ hiện hành; giới hạn hiển thị ở mục 8.3.

**Mục lục:** [1. Quy ước](#dfd-conventions) · [2. Mức 0](#dfd-context) · [3. Kho dữ liệu](#dfd-stores) · [4. Mức 1](#dfd-level1) · [5. Bắt đầu phiên](#dfd-start) · [6. TTS](#dfd-tts) · [7. Áp dụng cặp](#dfd-pair) · [8. Truy vết và rà soát](#dfd-trace).

<a id="dfd-conventions"></a>

## 1. Quy ước, ranh giới và cách đọc

| Ký hiệu / mã | Ý nghĩa |
| --- | --- |
| Hình chữ nhật E-K, E-A, E-T | Tác nhân ngoài: Khách, Admin, Gemini TTS. Khách/Admin là người sử dụng qua giao diện; Gemini là dịch vụ ngoài. |
| Hình bo tròn có mã tiến trình | Xử lý biến đổi dữ liệu; tên mô tả trách nhiệm, không phải một màn hình, lớp Java, container hoặc microservice. |
| Hình trụ D1–D5, F1–F2, T1 | Kho dữ liệu logic. Nhãn DB/FILE/TAB và bảng mục 3 phân biệt nơi lưu; không suy mọi hình trụ thành bảng MySQL. |
| Mũi tên một chiều có tên | Một tập dữ liệu đi từ nguồn tới nơi nhận. Không phải thứ tự bước, điều kiện rẽ nhánh, chuyển màn hình hoặc chỉ dẫn gọi hàm. |
| G01–G08 | Gói dữ liệu qua biên toàn hệ thống, giữ ở mức 0 và mức 1. Một gói có thể gồm nhiều loại request/response được liệt kê dưới sơ đồ. |
| S-*, T-*, A-* | Mã luồng cụ thể của Bắt đầu/TTS/Áp dụng ở mức 1 và phân rã mức 2. [Từ điển 4.2](#dfd-packages) nêu đúng nguồn, đích và dữ liệu; bảng cân bằng đối chiếu trực tiếp. Không thay mã FR/UC/AC/API. |
| R-*, M-* | Luồng còn lại ở mức 1: phục vụ khách, quản trị và bảo trì. Không là chức năng mới hoặc mã quyết định. |
| D1 · lặp, D2 · lặp… | Các lần xuất hiện của cùng ID là cùng kho logic; ghi lặp để đặt gần tiến trình sử dụng. Hậu tố trong nguồn Mermaid chỉ bố trí, không sinh kho hoặc đồng bộ dữ liệu mới. |

**Đánh số mức:** mức 0 là ngữ cảnh với đúng một tiến trình 0; mức 1 gồm 1.0–6.0; mức 2 chỉ phân rã 1.0 thành 1.1–1.5, 4.0 thành 4.1–4.5 và 5.0 thành 5.1–5.4. Quy ước này được dùng xuyên tài liệu, không dùng cách gọi “ngữ cảnh ngoài mức 0”.

Mermaid flowchart được dùng để biểu diễn gần ký pháp DFD vì tài liệu Markdown chưa có bộ ký hiệu DFD chuyên biệt. Không dùng hình thoi, START/END hoặc cạnh không tên. Hai mũi tên ngược chiều là hai luồng dữ liệu riêng. Đọc sơ đồ cùng danh mục kho và bảng cân bằng, không suy trật tự trái/phải thành thuật toán.

**Biên hệ thống** bao gồm Vue trong trình duyệt, Backend/worker, metadata trong MySQL, tài nguyên file và quy trình bảo trì đã thiết kế. Ngrok/HTTPS/Docker/Controller–Service–Repository là đường vận chuyển/cấu trúc triển khai, không thêm tác nhân nghiệp vụ hoặc kho mới trong DFD. Kho T1 thuộc ứng dụng trong tab, ở phía frontend; hình vẽ không có nghĩa Backend đọc trực tiếp bộ nhớ trình duyệt.

Không có giá trị secret, vé thật, credential thật, thông tin tài khoản hoặc dữ liệu khách thật trong sơ đồ. “Quyền”, “bằng chứng quyền” và “tham số xác minh” chỉ mô tả loại dữ liệu; không cho phép log/chia sẻ các giá trị thật.

<a id="dfd-context"></a>

## 2. DFD mức 0 — Ngữ cảnh

```mermaid
flowchart LR
    K["E-K Khách"]
    A["E-A Admin"]
    T["E-T Gemini TTS"]
    P0("0. Hệ thống thuyết minh QR<br/>cho Kinh thành Huế")
    K -->|"G01<br/>Hồ sơ Bắt đầu"| P0
    P0 -->|"G02<br/>Vé và kết quả Bắt đầu"| K
    K -->|"G03<br/>Phiên, chọn điểm và nghe"| P0
    P0 -->|"G04<br/>Bản đồ, bài và phản hồi"| K
    A -->|"G05<br/>Dữ liệu và yêu cầu quản trị"| P0
    P0 -->|"G06<br/>Kết quả quản trị và QR"| A
    P0 -->|"G07<br/>Bản văn và cấu hình đọc"| T
    T -->|"G08<br/>Audio hoặc lỗi nhận được"| P0
```

| Gói | Dữ liệu cụ thể qua giao diện / kết nối |
| --- | --- |
| G01 | Đề nghị chuẩn bị vé gắn quyền tab; tên đã nhập, ngôn ngữ ban đầu; vé/requestId và bằng chứng quyền của yêu cầu Bắt đầu/tra lại. Không tạo phiên chỉ vì mở URL hoặc cấp vé. |
| G02 | Vé ký, requestId, issuedAt/ticketExpiresAt; kết quả cùng sessionId/startedAt/expiresAt/state hoặc lỗi/thông báo phù hợp. API không trả lại credential thô. |
| G03 | Phiên cần kiểm/khôi phục/kết thúc; địa điểm, ngôn ngữ, dữ liệu tương quan lựa chọn và xác nhận văn bản thực đã hiển thị; ý định Phát/Tạm dừng/tua/Nghe lại tại tab. Ý định nghe không mặc định là request Backend. |
| G04 | Vùng đang áp dụng/điểm được phép; đúng văn bản/tiếng/cặp, MP3 được cấp quyền; trạng thái phiên, thiếu nội dung, lỗi và cảnh báo. Âm thanh nghe từ Blob có thể hoàn toàn ở tab. |
| G05 | Đăng nhập/đăng xuất/hoạt động Q3; tên điểm, hình vùng, bản văn và tham chiếu hồ sơ; yêu cầu tạo/kiểm/thử lại TTS, áp dụng cặp, ẩn-hiện/xóa; bộ lọc phiên/ngày thống kê và yêu cầu xem/tải/in QR. |
| G06 | Kết quả quyền/lệnh, dữ liệu đang soạn và đang dùng, trạng thái job, audio nghe thử; phần còn thiếu/đối chiếu/xung đột; phiên/lượt còn giữ, hai bảng/tổng; một QR/URL/PNG hoặc chưa sẵn sàng. Không báo in/lưu file thành công chỉ vì có request. |
| G07 | Body từ đúng bản văn đã lưu, ngôn ngữ, model gemini-3.8-flash-tts và giọng Charon theo lựa chọn hiện hành; không tên khách, hồ sơ duyệt hoặc bí mật tài khoản. |
| G08 | Bytes âm thanh/metadata thực sự nhận được hoặc phản hồi lỗi/quota có căn cứ. Mất phản hồi không phải thông điệp “đã thất bại” từ Gemini; trạng thái chưa rõ do hệ thống xác định từ bằng chứng kết nối/tác vụ. |

QR in trên giấy dẫn vào URL trang bắt đầu; DFD không sinh một sự kiện “lượt quét”. Không có mũi tên Khách–Gemini hoặc Admin–Gemini trực tiếp. Dữ liệu hồ sơ duyệt bên ngoài do Admin đối chiếu và ghi tham chiếu qua G05; chưa thêm dịch vụ duyệt hoặc phần mềm tự đọc/thẩm định hồ sơ.

<a id="dfd-stores"></a>

## 3. Kho dữ liệu theo nghiệp vụ

| Kho | Nội dung logic / nơi lưu | Giới hạn |
| --- | --- | --- |
| D1 — Phiên và lượt khách | **DB**: phiên, ledger Bắt đầu đi cùng phiên; ngữ cảnh lần mở/quyền tải/kết quả lựa chọn; lượt xem và snapshot nhận biết điểm cũ | Không lịch sử từng thao tác audio. D08 dọn phiên/lượt sau 30 ngày từ kết thúc/hết hạn gốc; metadata mở/quyền tải có vòng đời riêng theo model/operations. |
| D2 — Địa điểm và vùng | **DB**: mã/tên/ẩn-hiện, vùng đã lưu và con trỏ vùng đang áp dụng | Lưu bản sửa khác Áp dụng; không ranh giới đã được duyệt sẵn hoặc tọa độ thực của khách. |
| D3 — Nội dung, hồ sơ và tác vụ | **DB**: bản văn/bản dịch đã lưu, metadata audio, tham chiếu duyệt riêng; cặp/con trỏ đang áp dụng; snapshot/trạng thái/kết quả TTS | Gộp theo vòng đời nội dung để tránh vẽ mọi bảng ERD. D3 không chứa bytes WAV/MP3 hoặc bản Q2 chưa lưu. Tạo xong không tự duyệt/áp dụng. |
| D4 — Quyền, lệnh và thiết lập | **DB và ngữ cảnh vận hành được bảo vệ**: tài khoản Admin, trạng thái xác thực; cửa sổ/receipt lệnh; service_epoch và thiết lập ký vé, model/giọng, URL, chính sách nguồn | Không đồng nhất toàn kho với một bảng. Khóa ký/config runtime được bảo vệ ngoài repo; không vẽ giá trị. Admin session/cookie 30 phút; window 24 giờ riêng. Backup chỉ lấy phần DB/phần kê cần thiết theo phạm vi đã chốt, không tự mở rộng thành sao chép secret/runtime. |
| D5 — Danh mục và sử dụng file | **DB**: storage key/checksum/định dạng/thời lượng/trạng thái WAV–MP3, tham chiếu/claim, mốc bảo vệ và kết quả dọn | Không chứa audio bytes. Metadata READY không thay nghe duyệt; xóa record không chứng minh bytes đã xóa. |
| F1 — File audio và workspace | **FILE**: WAV nguồn, MP3 phục vụ/nghe thử, đầu ra xử lý tạm | Tách DB; Backend kiểm quyền trước đọc, không URL thư mục public. File hoàn chỉnh khác workspace dở. Không dùng kho này như kho offline của khách. |
| F2 — Backup cục bộ | **FILE**: dump DB, WAV/MP3 còn tham chiếu, manifest/thông tin phục hồi trong bản backup | Tách dữ liệu đang phục vụ, mỗi bản bảy ngày. Phục hồi riêng/lọc mốc gốc/REC-P01; không thêm yêu cầu ổ ngoài hoặc cứu mất toàn laptop. |
| T1 — Trạng thái riêng trong tab | **TAB**: form, vé/requestId/quyền và payload đang chờ; memento bài/ngôn ngữ/mốc nghe; Blob/object URL; phần Admin chưa lưu đúng Q2/UI-AP05 | Không là bảng DB, kho phục hồi dài hạn hoặc đồng bộ tab/thiết bị. Khi cần request, chỉ DTO được phép được gửi. Q2 không tự Lưu/Áp dụng/gửi TTS; Blob không phải nội dung có thể phục hồi sau hết hạn/reload. |

Lưu/khôi phục T1 do phần frontend của tiến trình tương ứng thực hiện. Các đoạn T1 của khách và Admin được tách quyền/công việc; tài khoản Admin khác không được thấy bản tạm cũ. Dữ liệu đã Lưu mới thành bản ở D2/D3; việc ghi T1 không được đổi active pointer.

<a id="dfd-level1"></a>

## 4. DFD mức 1 — Các xử lý chính

Sơ đồ giữ G01–G08 và sáu tiến trình chính. **Mọi cạnh nối trực tiếp tới tác nhân, tiến trình hoặc đúng kho có ID**, không tới DBS hay khung bố trí. Các kho dùng ở nhiều cụm được lặp ký hiệu, cùng ID và dấu “lặp”; đọc [từ điển 4.2](#dfd-packages) theo ID logic. Giữ phân biệt DB, file audio, backup và TAB; không frontend vượt API để đọc DB/file.

![DFD mức 1 — các gói dữ liệu, bản xuất từ nguồn bên dưới](dfd-level1.svg)

Bản SVG ở trên được xuất từ đúng nguồn Mermaid trong khối mở rộng dưới đây để xem/phóng to và dùng trong báo cáo. **Nguồn hiện hành vẫn là phần Mermaid trong file này**; không sửa SVG độc lập, khi đổi nguồn phải xuất lại. Các khung chỉ bố trí, không nhận/phát luồng dữ liệu; tất cả mũi tên phải chạm đúng ký hiệu kho hoặc tiến trình.

<details>
<summary>Nguồn Mermaid mức 1 — cùng hình SVG, không một phương án song song</summary>

```mermaid
---
config:
  layout: elk
---
flowchart LR
    subgraph LKHACH["Khách tham quan — cùng hệ thống"]
        direction LR
        K["E-K Khách"]
        subgraph C1["Cụm 1 — Bắt đầu phiên"]
            direction LR
            P1("1.0 Bắt đầu phiên")
            D1_1[("D1 · lặp<br/>Phiên/lượt")]
            D4_1[("D4 · lặp<br/>Quyền/lệnh/thiết lập")]
            T1_1[("T1 · lặp · TAB<br/>Trạng thái riêng")]
            T1_1 -->|"S04<br/>Form/yêu cầu giữ"| P1
            P1 -->|"S05<br/>Vé/kết quả tại tab"| T1_1
            D4_1 -->|"S01<br/>Quyền/epoch"| P1
            D1_1 -->|"S02<br/>Phiên/ledger gốc"| P1
            P1 -->|"S03<br/>Phiên mới/đối chiếu"| D1_1
        end
        subgraph C2["Cụm 2 — Phục vụ tham quan"]
            direction LR
            P2("2.0 Phục vụ tham quan")
            D1_2[("D1 · lặp<br/>Phiên/lượt")]
            D2_2[("D2 · lặp<br/>Điểm/vùng")]
            D3_2[("D3 · lặp<br/>Nội dung/hồ sơ/job")]
            D4_2[("D4 · lặp<br/>Quyền/lệnh/thiết lập")]
            D5_2[("D5 · lặp<br/>Danh mục file")]
            F1_2[("F1 · lặp · FILE<br/>WAV/MP3/tạm")]
            T1_2[("T1 · lặp · TAB<br/>Trạng thái riêng")]
            F1_2 -->|"R07<br/>MP3 đúng quyền"| P2
            T1_2 -->|"R08<br/>Memento/Blob"| P2
            P2 -->|"R09<br/>Lựa chọn/mốc/Blob"| T1_2
            D1_2 -->|"R01<br/>Phiên/opening/grant"| P2
            D4_2 -->|"R22<br/>Epoch/quyền khách"| P2
            D2_2 -->|"R03<br/>Điểm/vùng hiện hành"| P2
            D3_2 -->|"R04<br/>Bản văn/cặp hiện hành"| P2
            D5_2 -->|"R05<br/>Metadata MP3"| P2
            P2 -->|"R02<br/>Phiên/lượt đã ghi"| D1_2
            P2 -->|"R06<br/>Claim tải"| D5_2
        end
    K -->|"G01<br/>Hồ sơ Bắt đầu"| P1
    P1 -->|"G02<br/>Vé/kết quả Bắt đầu"| K
    K -->|"G03<br/>Phiên/chọn điểm/nghe"| P2
    P2 -->|"G04<br/>Bản đồ/bài/phản hồi"| K
    end
    subgraph LADMIN["Admin và nội dung — cùng hệ thống"]
        direction LR
        A["E-A Admin"]
        T["E-T Gemini TTS"]
        subgraph C3["Cụm 3 — Quản trị/quyền/tra cứu"]
            direction LR
            P3("3.0 Quản trị/quyền/tra cứu")
            D1_3[("D1 · lặp<br/>Phiên/lượt")]
            D2_3[("D2 · lặp<br/>Điểm/vùng")]
            D3_3[("D3 · lặp<br/>Nội dung/hồ sơ/job")]
            D4_3[("D4 · lặp<br/>Quyền/lệnh/thiết lập")]
            D5_3[("D5 · lặp<br/>Danh mục file")]
            T1_3[("T1 · lặp · TAB<br/>Trạng thái riêng")]
            T1_3 -->|"R20<br/>Soạn chưa lưu"| P3
            P3 -->|"R21<br/>Bản tạm Admin"| T1_3
            D4_3 -->|"R10<br/>Quyền/lệnh/thiết lập"| P3
            D2_3 -->|"R12<br/>Điểm/các bản vùng"| P3
            D3_3 -->|"R14<br/>Bản/cặp/hồ sơ"| P3
            D1_3 -->|"R16<br/>Phiên/lượt/lịch sử"| P3
            D5_3 -->|"R18<br/>Kết quả dọn file"| P3
            P3 -->|"R11<br/>Phiên Admin/lệnh"| D4_3
            P3 -->|"R13<br/>Điểm/vùng đã lưu"| D2_3
            P3 -->|"R15<br/>Bản lưu/tham chiếu"| D3_3
            P3 -->|"R17<br/>Tham chiếu lịch sử"| D1_3
            P3 -->|"R19<br/>Mốc/chờ dọn file"| D5_3
        end
        subgraph C4["Cụm 4 — Tạo và xử lý TTS"]
            direction LR
            P4("4.0 Tạo và xử lý TTS")
            D3_4[("D3 · lặp<br/>Nội dung/hồ sơ/job")]
            D4_4[("D4 · lặp<br/>Quyền/lệnh/thiết lập")]
            D5_4[("D5 · lặp<br/>Danh mục file")]
            F1_4[("F1 · lặp · FILE<br/>WAV/MP3/tạm")]
            F1_4 -->|"T09<br/>WAV/MP3 nguồn"| P4
            P4 -->|"T10<br/>WAV/MP3/tạm"| F1_4
            D3_4 -->|"T03<br/>Bản lưu/job/audio"| P4
            D4_4 -->|"T05<br/>Quyền/lệnh/TTS"| P4
            D5_4 -->|"T07<br/>File/claim cũ"| P4
            P4 -->|"T04<br/>Job/audio metadata"| D3_4
            P4 -->|"T06<br/>Receipt tiếp nhận"| D4_4
            P4 -->|"T08<br/>File/claim/orphan"| D5_4
        end
        subgraph C5["Cụm 5 — Áp dụng cả cặp"]
            direction LR
            P5("5.0 Áp dụng cả cặp")
            D2_5[("D2 · lặp<br/>Điểm/vùng")]
            D3_5[("D3 · lặp<br/>Nội dung/hồ sơ/job")]
            D4_5[("D4 · lặp<br/>Quyền/lệnh/thiết lập")]
            D5_5[("D5 · lặp<br/>Danh mục file")]
            D2_5 -->|"A03<br/>Điểm/version"| P5
            D3_5 -->|"A04<br/>Bản/hồ sơ/cặp"| P5
            D4_5 -->|"A06<br/>Quyền/receipt"| P5
            D5_5 -->|"A08<br/>File READY/claim"| P5
            P5 -->|"A05<br/>Cặp/con trỏ mới"| D3_5
            P5 -->|"A07<br/>Receipt áp dụng"| D4_5
            P5 -->|"A09<br/>Mốc áp dụng/ngừng"| D5_5
        end
    A -->|"G05<br/>Dữ liệu/yêu cầu quản trị"| P3
    P3 -->|"G06<br/>Kết quả quản trị/QR"| A
    P4 -->|"G07<br/>Bản văn/cấu hình đọc"| T
    T -->|"G08<br/>Audio/lỗi nhận được"| P4
    P3 -->|"T01<br/>Lệnh TTS/bản lưu"| P4
    P4 -->|"T02<br/>Job/nghe thử"| P3
    P3 -->|"A01<br/>Đề nghị áp dụng cặp"| P5
    P5 -->|"A02<br/>Kết quả áp dụng"| P3
    end
    subgraph LBAOTRI["Vòng đời dữ liệu — cùng hệ thống"]
        direction LR
        subgraph C6["Cụm 6 — Bảo trì/backup/phục hồi"]
            direction LR
            P6("6.0 Bảo trì/backup/phục hồi")
            D1_6[("D1 · lặp<br/>Phiên/lượt")]
            D2_6[("D2 · lặp<br/>Điểm/vùng")]
            D3_6[("D3 · lặp<br/>Nội dung/hồ sơ/job")]
            D4_6[("D4 · lặp<br/>Quyền/lệnh/thiết lập")]
            D5_6[("D5 · lặp<br/>Danh mục file")]
            F1_6[("F1 · lặp · FILE<br/>WAV/MP3/tạm")]
            F2_6[("F2 · FILE<br/>Backup")]
            F1_6 -->|"M11<br/>File snapshot"| P6
            P6 -->|"M12<br/>File dọn/phục hồi"| F1_6
            F2_6 -->|"M13<br/>Backup/manifest"| P6
            P6 -->|"M14<br/>Backup còn hạn"| F2_6
            D1_6 -->|"M01<br/>Phiên/lượt/mốc gốc"| P6
            D2_6 -->|"M03<br/>Snapshot điểm/vùng"| P6
            D3_6 -->|"M05<br/>Nội dung/job"| P6
            D4_6 -->|"M07<br/>Chính sách/quyền/lệnh"| P6
            D5_6 -->|"M09<br/>File/ref/claim"| P6
            P6 -->|"M02<br/>Phiên/lượt bảo trì"| D1_6
            P6 -->|"M04<br/>Điểm/vùng phục hồi"| D2_6
            P6 -->|"M06<br/>Nội dung/job bảo trì"| D3_6
            P6 -->|"M08<br/>Quyền/lệnh bảo trì"| D4_6
            P6 -->|"M10<br/>Metadata dọn/phục hồi"| D5_6
        end
    end
```

</details>

### 4.1. Ranh giới các nhóm

| Tiến trình | Trách nhiệm và ranh giới quan trọng |
| --- | --- |
| 1.0 | API-K11/K01, chuẩn bị/kiểm vé và chống tạo phiên lặp; không map/audio/lượt hoặc bảng start context. |
| 2.0 | API-K02–K10 và UI khách: kiểm quyền/epoch/hạn phía Backend, khôi phục/kết thúc, map/chọn/đổi tiếng, text/grant/ACK/MP3; xử lý nghe và cảnh báo ở tab. R02 chỉ chứa lượt đủ điều kiện, không mọi lần nhấn nút. |
| 3.0 | Đăng nhập/đăng xuất/Q3; CRUD/ẩn-hiện/xóa điểm, lưu/xem trước/áp dụng vùng; lưu văn bản/hồ sơ; mức đầy đủ, phiên/thống kê chỉ đọc và một QR. Chuyển lệnh TTS/cặp sang 4.0/5.0; vẫn kiểm quyền tại Service, không tin menu hoặc dữ liệu “đã kiểm” của frontend. |
| 4.0 | Nhận tác vụ từ bản lưu, dispatch/nhận kết quả, WAV/MP3 và nghe thử. Ghi audio/job; không ghi hồ sơ “đã duyệt” hoặc active pair. |
| 5.0 | Kiểm đúng cặp đủ điều kiện và chuyển active pointer + receipt cùng giao dịch; không tự Lưu, tạo TTS, bật hiện hoặc đổi vùng. |
| 6.0 | Dọn từng lớp dữ liệu/file đúng hạn; backup tạm ngừng phục vụ và restore có lọc/thu hồi quyền. Là chức năng vận hành nội bộ đã thiết kế, không thêm màn hình/chức năng backup cho Admin website. Không cần mũi tên “đến giờ” vì đó là điều phối, không dữ liệu. |

Các kho D1–D5 là góc nhìn logic, không tách database vật lý. Các ghi nhận thuộc một giao dịch có thể cùng đi tới nhiều kho logic trong một MySQL transaction; DFD không chứng minh tính nguyên tử.

M07/M08 không biến restart/backup thường thành restore: chỉ phục hồi backup thật mới thay service_epoch/thu hồi quyền cũ theo REC-P01; không endedAt giả hoặc gia hạn phiên. Trong BKP-P02, dừng ứng dụng/worker/cleaner có kiểm soát, giữ MySQL chạy, lấy snapshot DB và file khớp; không backup khi còn writer chưa kiểm soát. QUEUED đã nhận vẫn giữ, kết quả TTS chưa rõ không tự gửi lại. Chi tiết ở [operations 3–4](operations-design.md#ops-backup).

P3 xóa địa điểm ghi R15/R17/R19 cùng các thay đổi DB có kiểm soát; không xóa ngay bytes F1 hoặc hồ sơ duyệt trong tài liệu. P6 chỉ dọn file không còn tham chiếu **và** không dùng chung/không tác vụ sử dụng, đủ mốc nguồn. Dữ liệu bảo trì gồm mốc D08, file từng áp dụng tám giờ, file hoàn chỉnh chưa áp dụng 24 giờ, workspace một giờ, metadata TTS LIFE-P03; không một TTL chung. Log kỹ thuật bảy ngày và quyền xem/bảo vệ giữ theo chính sách nguồn; sơ đồ lược chi tiết ghi log kỹ thuật, không thêm kho lịch sử audio hay PII để phục vụ log.

**Cân bằng mức 0 → mức 1:** G01/G02 chỉ vào/ra 1.0; G03/G04 chỉ vào/ra 2.0; G05/G06 chỉ vào/ra 3.0; G07/G08 chỉ vào/ra 4.0. Không có tác nhân ngoài hoặc luồng ngoài mới ở mức 1. Những luồng S/T/A/R/M còn lại nằm bên trong biên hệ thống.

<a id="dfd-packages"></a>

### 4.2. Từ điển luồng — đúng nguồn, đích và chiều

Mỗi dòng tương ứng một luồng trực tiếp trên hình mức 1. Nhãn trên hình được rút gọn; cột dữ liệu giữ ý nghĩa đầy đủ đã có ở 0.2. Chỉ được gom thêm thành phần khi cùng kho/tiến trình nguồn, cùng đích và cùng chiều; không dùng khung để thay một đầu mút. Với bộ luồng hiện có, việc mở các gói 0.2 trả lại 68 quan hệ nguồn–đích; không ép giảm số đó.

| Mã | Nguồn → đích logic | Nhãn ngắn trên hình | Dữ liệu đầy đủ / nơi giữ chi tiết |
| --- | --- | --- | --- |
| G01 | K → P1 | Hồ sơ Bắt đầu | Hồ sơ Bắt đầu |
| G02 | P1 → K | Vé/kết quả Bắt đầu | Vé và kết quả Bắt đầu |
| S01 | D4 → P1 | Quyền/epoch | Tham số xác minh và epoch hiện hành |
| S02 | D1 → P1 | Phiên/ledger gốc | Phiên và ledger theo requestId |
| S03 | P1 → D1 | Phiên mới/đối chiếu | Phiên mới cùng dữ liệu đối chiếu |
| S04 | T1 → P1 | Form/yêu cầu giữ | Form và yêu cầu đang giữ |
| S05 | P1 → T1 | Vé/kết quả tại tab | Vé và kết quả tại tab |
| G03 | K → P2 | Phiên/chọn điểm/nghe | Phiên, chọn điểm và nghe |
| G04 | P2 → K | Bản đồ/bài/phản hồi | Bản đồ, bài và phản hồi |
| R01 | D1 → P2 | Phiên/opening/grant | Phiên, opening, grant và kết quả cũ |
| R02 | P2 → D1 | Phiên/lượt đã ghi | Opening, grant, lượt đã tính và kết thúc |
| R03 | D2 → P2 | Điểm/vùng hiện hành | Điểm hiện và vùng đang áp dụng |
| R04 | D3 → P2 | Bản văn/cặp hiện hành | Văn bản và cặp đang được phục vụ |
| R05 | D5 → P2 | Metadata MP3 | Metadata MP3 và tình trạng sử dụng |
| R06 | P2 → D5 | Claim tải | Claim tải và kết quả sử dụng file |
| R07 | F1 → P2 | MP3 đúng quyền | MP3 đúng quyền |
| R08 | T1 → P2 | Memento/Blob | Memento và Blob đang giữ |
| R09 | P2 → T1 | Lựa chọn/mốc/Blob | Lựa chọn, mốc nghe, Blob |
| R22 | D4 → P2 | Epoch/quyền khách | Epoch hiện hành và chính sách quyền khách |
| G05 | A → P3 | Dữ liệu/yêu cầu quản trị | Dữ liệu và yêu cầu quản trị |
| G06 | P3 → A | Kết quả quản trị/QR | Kết quả quản trị và QR |
| R10 | D4 → P3 | Quyền/lệnh/thiết lập | Quyền, receipt, thiết lập QR và chính sách |
| R11 | P3 → D4 | Phiên Admin/lệnh | Phiên Admin, hoạt động, window và receipt |
| R12 | D2 → P3 | Điểm/các bản vùng | Thông tin điểm và các bản vùng |
| R13 | P3 → D2 | Điểm/vùng đã lưu | Điểm, vùng nháp hoặc vùng áp dụng |
| R14 | D3 → P3 | Bản/cặp/hồ sơ | Bản lưu, cặp, hồ sơ và mức đầy đủ |
| R15 | P3 → D3 | Bản lưu/tham chiếu | Bản văn, tham chiếu và dữ liệu xóa có kiểm soát |
| R16 | D1 → P3 | Phiên/lượt/lịch sử | Phiên, lượt và nhận biết lịch sử còn giữ |
| R17 | P3 → D1 | Tham chiếu lịch sử | Live reference đã tháo, lịch sử được giữ |
| R18 | D5 → P3 | Kết quả dọn file | Trạng thái dọn file |
| R19 | P3 → D5 | Mốc/chờ dọn file | Metadata ngừng áp dụng và file chờ dọn |
| R20 | T1 → P3 | Soạn chưa lưu | Phần Admin chưa lưu |
| R21 | P3 → T1 | Bản tạm Admin | Bản tạm tại tab |
| G07 | P4 → T | Bản văn/cấu hình đọc | Bản văn và cấu hình đọc |
| G08 | T → P4 | Audio/lỗi nhận được | Audio hoặc lỗi nhận được |
| T01 | P3 → P4 | Lệnh TTS/bản lưu | Lệnh TTS và bản đã lưu |
| T02 | P4 → P3 | Job/nghe thử | Job, kết quả và nghe thử |
| T03 | D3 → P4 | Bản lưu/job/audio | Bản văn, đích, job và audio đã lưu |
| T04 | P4 → D3 | Job/audio metadata | Snapshot, trạng thái job và metadata audio |
| T05 | D4 → P4 | Quyền/lệnh/TTS | Quyền, window, receipt và thiết lập TTS |
| T06 | P4 → D4 | Receipt tiếp nhận | Receipt tiếp nhận hoặc thử lại |
| T07 | D5 → P4 | File/claim cũ | Metadata file, claim và kết quả cũ |
| T08 | P4 → D5 | File/claim/orphan | Metadata WAV, MP3, claim và orphan |
| T09 | F1 → P4 | WAV/MP3 nguồn | WAV hoặc MP3 để đọc |
| T10 | P4 → F1 | WAV/MP3/tạm | WAV, MP3 và workspace |
| A01 | P3 → P5 | Đề nghị áp dụng cặp | Đề nghị áp dụng cặp |
| A02 | P5 → P3 | Kết quả áp dụng | Kết quả áp dụng hoặc lỗi |
| A03 | D2 → P5 | Điểm/version | Điểm và phiên bản hiện hành |
| A04 | D3 → P5 | Bản/hồ sơ/cặp | Bản văn, audio, hồ sơ, cặp và slot version |
| A05 | P5 → D3 | Cặp/con trỏ mới | Cặp và con trỏ áp dụng nhất quán |
| A06 | D4 → P5 | Quyền/receipt | Quyền, window, epoch và receipt |
| A07 | P5 → D4 | Receipt áp dụng | Receipt áp dụng đã commit |
| A08 | D5 → P5 | File READY/claim | File READY, tham chiếu và claim |
| A09 | P5 → D5 | Mốc áp dụng/ngừng | Mốc áp dụng và ngừng áp dụng cuối |
| M01 | D1 → P6 | Phiên/lượt/mốc gốc | Phiên, lượt và mốc gốc |
| M02 | P6 → D1 | Phiên/lượt bảo trì | Dữ liệu còn hạn sau dọn hoặc phục hồi |
| M03 | D2 → P6 | Snapshot điểm/vùng | Snapshot điểm và vùng |
| M04 | P6 → D2 | Điểm/vùng phục hồi | Điểm và vùng phục hồi |
| M05 | D3 → P6 | Nội dung/job | Nội dung, tham chiếu và trạng thái tác vụ |
| M06 | P6 → D3 | Nội dung/job bảo trì | Metadata sau dọn hoặc phục hồi |
| M07 | D4 → P6 | Chính sách/quyền/lệnh | Chính sách và phần quyền, lệnh cần sao lưu |
| M08 | P6 → D4 | Quyền/lệnh bảo trì | Quyền sau restore và lệnh sau dọn |
| M09 | D5 → P6 | File/ref/claim | Danh mục, tham chiếu và file đang sử dụng |
| M10 | P6 → D5 | Metadata dọn/phục hồi | Danh mục và kết quả dọn, phục hồi |
| M11 | F1 → P6 | File snapshot | File snapshot và đối chiếu |
| M12 | P6 → F1 | File dọn/phục hồi | File sau dọn và phục hồi |
| M13 | F2 → P6 | Backup/manifest | Backup, manifest còn hạn |
| M14 | P6 → F2 | Backup còn hạn | Backup và tập bản còn hạn |

Các dòng G01–G08 mở rộng tại mục 2; S01–S05 tại mục 5; T01–T10 tại mục 6; A01–A09 tại mục 7. Dữ liệu R*/M* tiếp tục đối chiếu [API](api-transactions.md), [mô hình](data-model.md) và [vòng đời/backup/restore](operations-design.md). Từ điển này không thêm DTO, request, bảng hoặc thời hạn mới.

### 4.3. Cân bằng và ký hiệu lặp

- Bỏ cách nối mũi tên vào DBS của 0.2; các mã gói S06/R23–R26/T11–T12/A10–A11/M15–M16 không còn là luồng hiện hành. S03 giữ mã, sửa đích hiển thị từ khung về đúng D1.
- Cạnh nối tới D1_1, D1_2… trong nguồn Mermaid đều có nhãn nhìn thấy **D1 · lặp** và cùng ánh xạ kho logic D1. Tương tự D2–D5/F1/T1. Hậu tố chỉ định vị ký hiệu trên hình, không mã nghiệp vụ hoặc bản sao dữ liệu mới.
- T1 là loại kho trạng thái theo đúng tab/tài khoản đã mô tả ở mục 3; ký hiệu lặp không biến thành bộ nhớ chia sẻ giữa khách/Admin, các tab hoặc thiết bị.
- Có đúng một tiến trình 1.0–6.0 cho mỗi nhóm. Các khung C1–C6 và khung Khách/Admin/Vòng đời chỉ bố trí cùng một DFD, không đầu mút, tiến trình, kho mới hoặc các hệ thống triển khai riêng. Chúng không biểu diễn thứ tự thao tác.
- Sau khi chuẩn hóa hậu tố ký hiệu kho, mức 1 có đúng các quan hệ nguồn–đích–chiều của 68 thành phần trước đây. Các bảng 5.2/6.2/7.2 đối chiếu trực tiếp luồng S/T/A, không cần “mở gói DBS”. Mức 0 vẫn đúng G01–G08.
- Toàn hình dùng SVG phóng to để học theo từng cụm. Không suy từ số cạnh hoặc kích thước cửa sổ xem ra khả năng đọc trên giấy; giới hạn kiểm hình/khổ giấy ghi tại 8.3.

<a id="dfd-start"></a>

## 5. DFD mức 2 — Phân rã 1.0 Bắt đầu phiên

```mermaid
---
config:
  layout: elk
---
flowchart TB
    K["E-K Khách"]
    D1[("D1 DB: phiên và lượt")]
    D4[("D4 Quyền, lệnh, thiết lập")]
    T1[("T1 TAB: trạng thái riêng")]
    P11("1.1 Tiếp nhận hồ sơ Bắt đầu")
    P12("1.2 Cấp hoặc xác minh vé")
    P13("1.3 Đối chiếu kết quả đã tạo")
    P14("1.4 Ghi phiên và chống lặp")
    P15("1.5 Tổng hợp kết quả tại giao diện")
    K -->|"G01<br/>Hồ sơ Bắt đầu"| P11
    T1 -->|"S04<br/>Form và yêu cầu đang giữ"| P11
    P11 -->|"Hồ sơ cấp vé hoặc yêu cầu đã chuẩn hóa"| P12
    D4 -->|"S01 Tham số xác minh và epoch hiện hành"| P12
    P12 -->|"Vé cấp ở nền hoặc lỗi xác minh"| P15
    P12 -->|"Yêu cầu, vé và bằng chứng quyền đã xác minh"| P13
    D1 -->|"S02<br/>Phiên/ledger gốc"| P13
    D1 -->|"S02<br/>Phiên/ledger gốc"| P14
    P13 -->|"Kết quả gốc hoặc xung đột yêu cầu"| P15
    P13 -->|"Hồ sơ đối chiếu và ứng viên tạo lần đầu"| P14
    P14 -->|"S03<br/>Phiên mới và đối chiếu"| D1
    P14 -->|"Kết quả ghi/tra phiên hoặc lỗi"| P15
    P15 -->|"G02<br/>Vé và kết quả Bắt đầu"| K
    P15 -->|"S05<br/>Vé và kết quả tại tab"| T1
```

### 5.1. Ý nghĩa dữ liệu và điều kiện đã có

- S01 đọc trực tiếp D4; S02 đọc trực tiếp D1 ở mức 1 và mức 2. S01 gồm epoch hiện hành và khả năng xác minh chữ ký theo runtime được bảo vệ; không đưa khóa ký xuống tab. Vé chứa requestId ngẫu nhiên, ràng buộc quyền tab, issuedAt/ticketExpiresAt cách nhau 24 giờ. Không bảng start context/10 phút/start_generation.
- S02/S03 gồm requestId duy nhất, fingerprint tên đã chuẩn hóa/ngôn ngữ ban đầu, thông tin đối chiếu vé/quyền, sessionId và các mốc gốc. Phiên + dữ liệu chống lặp được ghi cùng TX-K01, UNIQUE xử lý concurrent; xung đột insert phải rollback rồi đọc đối chiếu bằng giao dịch phù hợp.
- 1.3 tra ledger sau kiểm chữ ký/quyền/epoch, **trước khi dùng hạn vé để quyết định tạo lần đầu**. Có kết quả đúng yêu cầu thì trả gốc kể cả vé đã hết hạn; cùng mã khác nội dung không sửa phiên. Chưa có ledger chỉ tạo bằng vé còn hạn; tại đúng deadline đã hết hạn.
- S05 chỉ giữ dữ liệu cần cho tab: vé và requestId được cấp, pending payload, kết quả; credential do tab giữ nguyên, không là trường được API trả lại. Vé hết trước lần gửi đầu thì chuẩn bị vé mới nền/giữ form. Pending chưa rõ thì giữ nguyên vé/key/quyền, không tự tạo lần khác.
- Không response không chứng minh thất bại: frontend trong 1.5 giữ pending và thông báo phù hợp; nếu không thể tiếp tục thì khách chủ động xác nhận Bắt đầu mới. Không suy ra phiên đã hết hạn khi không có căn cứ.
- Vé 24 giờ không thay phiên tám giờ. Ledger đi cùng hạn lưu phiên D08; lúc dọn sau 30 ngày, vé cũ đã hết hạn nên không tạo lại từ request cũ. service_epoch/REC-P01 giữ riêng.

Mũi tên “ứng viên tạo lần đầu” là dữ liệu để 1.4 kiểm điều kiện; không hình vẽ nhánh “có/không”. Thứ tự xác minh/lookup/expiry, giao dịch và mã lỗi đọc tại [protocol LIFE-P01](operations-design.md#ops-start-simple), [API-K11/K01](api-transactions.md#api-dto) và [mô hình phiên](data-model.md#model-guest).

### 5.2. Cân bằng biên 1.0

| Luồng mức 1 | Điểm nối mức 2 | Dữ liệu giữ nguyên |
| --- | --- | --- |
| G01 → 1.0 | Khách → 1.1 | Hồ sơ cấp vé/Bắt đầu, không thêm thao tác |
| 1.0 → G02 | 1.5 → Khách | Vé, kết quả gốc/tạo mới hoặc lỗi/thông báo |
| S01: D4 → 1.0 | D4 → 1.2 | Tham số xác minh/epoch |
| S02: D1 → 1.0 | D1 → 1.3 và 1.4 | Cùng dữ liệu phiên/ledger chia cho đối chiếu và concurrent, không một phiên mới |
| 1.0 → S03 | 1.4 → D1 | Session + thông tin chống lặp cùng giao dịch |
| S04 → 1.0 | T1 → 1.1 | Form/pending tại tab |
| 1.0 → S05 | 1.5 → T1 | Vé/pending/kết quả giữ đúng tab |

Chuẩn hóa ký hiệu kho lặp về ID D4/D1 rồi so trực tiếp với mức 2; phân rã không thêm kho hoặc dữ liệu ngoài biên cha. Các ca V-START01.01–10 và trạng thái từng phần giữ tại [operations 5.1](operations-design.md#ops-start-checks); đã có bằng chứng backend, chưa toàn browser/runtime/restore.

<a id="dfd-tts"></a>

## 6. DFD mức 2 — Phân rã 4.0 Tạo và xử lý TTS

```mermaid
flowchart TB
    P3("3.0 Quản trị, quyền và tra cứu<br/>ngoài phân rã 4.0")
    T["E-T Gemini TTS"]
    D3[("D3 DB: nội dung, hồ sơ, tác vụ")]
    D4[("D4 Quyền, lệnh, thiết lập")]
    D5[("D5 DB: danh mục và sử dụng file")]
    F1[("F1 FILE: WAV, MP3, workspace")]
    P41("4.1 Đối chiếu yêu cầu và bản lưu")
    P42("4.2 Lưu và điều phối tác vụ")
    P43("4.3 Trao đổi dữ liệu với Gemini")
    P44("4.4 Xử lý file và gắn đúng kết quả")
    P45("4.5 Cung cấp trạng thái và nghe thử")
    P3 -->|"T01<br/>Lệnh TTS và bản đã lưu"| P41
    D4 -->|"T05 Quyền, window, receipt và thiết lập TTS"| P41
    D3 -->|"T03 Bản văn, đích, job và audio đã lưu"| P41
    D3 -->|"T03 Bản văn, đích, job và audio đã lưu"| P42
    D3 -->|"T03 Bản văn, đích, job và audio đã lưu"| P44
    D3 -->|"T03 Bản văn, đích, job và audio đã lưu"| P45
    P41 -->|"Hồ sơ yêu cầu hợp lệ và snapshot bản văn"| P42
    P41 -->|"Quyền nghe thử, kết quả cũ hoặc lỗi đối chiếu"| P45
    P42 -->|"T04 Snapshot, trạng thái job và metadata audio"| D3
    P42 -->|"T06 Receipt tiếp nhận hoặc thử lại"| D4
    P42 -->|"Snapshot lần tạo đã tiếp nhận, model và giọng"| P43
    P43 -->|"G07<br/>Bản văn và cấu hình đọc"| T
    T -->|"G08<br/>Audio hoặc lỗi nhận được"| P43
    P43 -->|"Dữ liệu âm thanh và metadata thực nhận"| P44
    P43 -->|"Bằng chứng trao đổi, lỗi hoặc kết quả chưa rõ"| P42
    D5 -->|"T07 Metadata file, claim và kết quả cũ"| P44
    D5 -->|"T07 Metadata file, claim và kết quả cũ"| P45
    F1 -->|"T09<br/>WAV hoặc MP3 để đọc"| P44
    P44 -->|"T10<br/>WAV, MP3 và workspace"| F1
    P44 -->|"T08 Metadata WAV, MP3, claim và orphan"| D5
    P45 -->|"T08 Metadata WAV, MP3, claim và orphan"| D5
    P44 -->|"T04 Snapshot, trạng thái job và metadata audio"| D3
    P44 -->|"Kết quả kỹ thuật, lỗi hoặc đích không còn"| P45
    P42 -->|"Job đã nhận và trạng thái có căn cứ"| P45
    F1 -->|"T09<br/>WAV hoặc MP3 để đọc"| P45
    P45 -->|"T02<br/>Job, kết quả và nghe thử"| P3
```

### 6.1. Ý nghĩa và giới hạn

T01 gồm id/hash bản văn đã lưu, loại yêu cầu tạo/kiểm/thử lại/nghe thử, quyền Admin hiện tại, window/key/precondition cần thiết. Body gửi Gemini lấy từ D3, **không lấy phần đang gõ ở T1**. T05 gồm model/Charon đã chọn; không bí mật nào được trả xuống frontend.

4.2 ghi job/snapshot/receipt accepted bền vững theo TX-A03; worker lấy job đã nhận và ghi marker DISPATCHING theo [operations 2](operations-design.md#ops-jobs). DFD không biến mũi tên thành thứ tự commit: gọi provider ở ngoài giao dịch dài, không chỉ phát sự kiện RAM. Yêu cầu lặp/active/unknown đối chiếu job cũ; kiểm trạng thái không tự gửi tạo mới. Retry provider chỉ chủ động sau lỗi xác định; WAV tốt nhưng convert lỗi có nhánh xử lý lại local.

G08 là audio/phản hồi thực nhận, không là cam kết WAV/MP3 đã sẵn sàng hoặc provider có API tra lại. Thiếu phản hồi được 4.3/4.2 ghi nhận thành chưa rõ từ bằng chứng tác vụ; không giả “Gemini báo thất bại”. Kết quả chưa đủ đối chiếu không được gắn nhầm vào bản văn khác.

4.4 giữ WAV đầy đủ, kiểm định dạng/thời lượng, tạo MP3 bằng FFmpeg/ffprobe theo hướng đã chọn; đăng ký nguồn WAV và file metadata bền trước chặng cần phục hồi/local retry. Kiểm lại owner/target/text/hash/ngôn ngữ trước ghi AUDIO_REVISION/TX-A04. Sửa văn bản tạo bản khác, không chuyển audio cũ; điểm đã xóa thì bỏ liên kết kết quả muộn, không phục hồi điểm. File không dùng được xử lý theo lớp dọn đã chốt, không đồng nhất WAV hoàn chỉnh với chunk tạm một giờ.

T04/T08 chỉ nói **kết quả kỹ thuật và metadata**. Không ghi tham chiếu duyệt, active pair hoặc tự bật hiện. Nghe thử MP3 qua T02/G06 cần quyền Admin; hết phiên dừng nghe thử, Q2 giữ phần soạn. Job đã tiếp nhận tiếp tục độc lập, đăng nhập lại không tự phát/gửi lại/áp dụng. Khi nghe thử, 4.5 đối chiếu file/claim qua T07/T08 và giải phóng claim phù hợp; không giữ giao dịch DB trong suốt thời gian nghe.

### 6.2. Cân bằng biên 4.0

| Luồng mức 1 | Điểm nối mức 2 | Dữ liệu giữ nguyên |
| --- | --- | --- |
| T01 → 4.0; 4.0 → T02 | 3.0 → 4.1; 4.5 → 3.0 | Lệnh bản lưu/quyền/key; job/trạng thái/lỗi/MP3 nghe thử |
| T03: D3 → 4.0 | D3 → 4.1/4.2/4.4/4.5 | Bản lưu/target/job/audio; không phần chưa lưu |
| T04: 4.0 → D3 | 4.2/4.4 → D3 | Snapshot/trạng thái và metadata audio đúng bản |
| T05: D4 → 4.0 | D4 → 4.1 | Quyền/window/receipt/model-giọng |
| T06: 4.0 → D4 | 4.2 → D4 | Receipt tiếp nhận hoặc thử lại |
| T07: D5 → 4.0 | D5 → 4.4/4.5 | Catalog/claim/kết quả cũ |
| T08: 4.0 → D5 | 4.4/4.5 → D5 | Metadata file/claim/orphan, không active pair hoặc tự duyệt |
| T09 → 4.0; 4.0 → T10 | F1 → 4.4/4.5; 4.4 → F1 | WAV/MP3 đọc và bytes đầu ra/workspace |
| 4.0 → G07; G08 → 4.0 | 4.3 → Gemini; Gemini → 4.3 | Đúng bản văn/tiếng/model/giọng; audio hoặc phản hồi lỗi thực nhận |

3.0 là tiến trình khác của hệ thống, không tác nhân ngoài mới. Phân rã không thêm provider, chức năng dịch, kho file hoặc luồng tự áp dụng.

<a id="dfd-pair"></a>

## 7. DFD mức 2 — Phân rã 5.0 Áp dụng cả cặp văn bản/audio

```mermaid
---
config:
  layout: elk
---
flowchart TB
    P3("3.0 Quản trị, quyền và tra cứu<br/>ngoài phân rã 5.0")
    D2[("D2 DB: địa điểm và vùng")]
    D3[("D3 DB: nội dung, hồ sơ, tác vụ")]
    D4[("D4 Quyền, lệnh, thiết lập")]
    D5[("D5 DB: danh mục và sử dụng file")]
    P51("5.1 Đối chiếu quyền và kết quả lệnh")
    P52("5.2 Đối chiếu cặp và hồ sơ đúng bản")
    P53("5.3 Ghi cả cặp và kết quả nhất quán")
    P54("5.4 Tổng hợp kết quả và dữ liệu đối chiếu")
    P3 -->|"A01<br/>Đề nghị áp dụng cặp"| P51
    D4 -->|"A06 Quyền, window, epoch và receipt"| P51
    P51 -->|"Kết quả cũ hoặc lỗi đối chiếu lệnh"| P54
    P51 -->|"Yêu cầu áp dụng và ngữ cảnh lệnh đã xác minh"| P52
    D2 -->|"A03 Điểm và phiên bản hiện hành"| P52
    D3 -->|"A04 Bản văn, audio, hồ sơ, cặp và slot version"| P52
    D5 -->|"A08 File READY, tham chiếu và claim"| P52
    P52 -->|"Dữ liệu cặp, phiên bản và điều kiện đã đối chiếu"| P53
    P52 -->|"Điều kiện còn thiếu hoặc xung đột"| P54
    P53 -->|"A05 Cặp và con trỏ áp dụng nhất quán"| D3
    P53 -->|"A07<br/>Receipt đã commit"| D4
    P53 -->|"A09<br/>Mốc áp dụng/ngừng"| D5
    P53 -->|"Kết quả commit hoặc lỗi giao dịch có căn cứ"| P54
    P54 -->|"A02<br/>Kết quả áp dụng hoặc lỗi"| P3
```

### 7.1. Đơn vị áp dụng và kết quả

A01 gồm địa điểm/ngôn ngữ, textRevisionId/audioRevisionId, hash/checksum, phiên bản hồ sơ và slot, window/key/quyền theo [ApplyPairRequest](api-transactions.md#api-dto). Một cặp thuộc đúng một điểm/một ngôn ngữ; không chờ đủ bốn ngôn ngữ.

A04 phải chỉ đúng văn bản đã lưu/được người dùng duyệt và audio khớp bản/ngôn ngữ, đã nghe trọn/duyệt theo hồ sơ D09/UI-04. Tham chiếu văn bản/audio riêng có thể cùng mục tài liệu nếu mục đó xác định rõ cả hai. Admin đối chiếu hồ sơ trước thao tác; phần mềm kiểm liên kết/phiên bản/điều kiện, **không tự thẩm định tài liệu hoặc coi locator là chứng nhận duyệt**.

5.2–5.3 thuộc TX-A02 với kiểm lại dữ liệu/khóa theo API và model; hình DFD không cho phép kiểm ngoài giao dịch rồi ghi trên snapshot cũ. A05/A07/A09 phải nhất quán trong cùng giao dịch DB: chuyển cả cặp và receipt, cập nhật mốc file bị ngừng áp dụng cuối. Không có ghi nửa cặp hoặc đọc bytes/gọi Gemini kéo dài TX. D5/claim/khóa ngăn cleaner tranh chấp theo protocol, không chỉ tin một kiểm tra tồn tại file từ trước.

Thất bại xác định giữ cặp cũ; không response thì UI ở 3.0 giữ key/window và đối chiếu receipt/trạng thái thực trước thử lại, không đoán commit. Receipt phản ánh kết quả tại thời điểm commit, không chứng minh cặp ấy vẫn đang dùng nếu sau đó có lệnh khác.

Chỉ thay audio dùng văn bản hiện tại không đổi cùng audio mới đã duyệt, không bắt sửa/duyệt lại văn bản đó. Áp dụng không tự bật hiện; 3.0 chỉ bật khi D2 hợp lệ/vùng xem xét-applied và ít nhất cặp vi được duyệt/áp dụng. Khách nhận cặp mới ở lần tải/chọn tiếp theo, không có luồng push từ 5.0 tới tab đang nghe. Cặp đã áp dụng nhưng audio runtime thiếu/lỗi vẫn giữ nhánh D03/D03-BS; DFD không xóa nhánh đọc văn bản.

### 7.2. Cân bằng biên 5.0

| Luồng mức 1 | Điểm nối mức 2 | Dữ liệu giữ nguyên |
| --- | --- | --- |
| A01 → 5.0; 5.0 → A02 | 3.0 → 5.1; 5.4 → 3.0 | Đúng cặp/version/quyền/key; kết quả cũ/mới/lỗi/đối chiếu |
| A03: D2 → 5.0 | D2 → 5.2 | Điểm/version |
| A04: D3 → 5.0 | D3 → 5.2 | Bản/hồ sơ/cặp/slot |
| A05: 5.0 → D3 | 5.3 → D3 | Cả cặp và con trỏ áp dụng |
| A06: D4 → 5.0 | D4 → 5.1 | Quyền/window/epoch/receipt |
| A07: 5.0 → D4 | 5.3 → D4 | Receipt đã commit |
| A08: D5 → 5.0 | D5 → 5.2 | File READY/ref/claim |
| A09: 5.0 → D5 | 5.3 → D5 | Mốc áp dụng/ngừng áp dụng cuối |

Không có F1 hoặc Gemini trong phân rã này: nó sử dụng metadata/claim đã được quản lý an toàn, không tạo/chuyển đổi lại audio. Không có luồng áp dụng riêng văn bản hoặc audio.

<a id="dfd-trace"></a>

## 8. Truy vết, giới hạn và rà soát tài liệu

### 8.1. Nguồn đối chiếu

| DFD | Nguồn nghiệp vụ / yêu cầu | Thiết kế chi tiết bổ sung cho DFD |
| --- | --- | --- |
| Mức 0; 1.0/2.0 | [Business Flow khách](../02-nghiep-vu/business-flow.md#bf-3); PRD FR-K01–K10, UC-K01–K05; [DATA-05/LIFE-P01](../02-nghiep-vu/decisions.md#data-05) | [Model phiên](data-model.md#model-guest), [API khách](api-transactions.md#api-guest), [protocol vé](operations-design.md#ops-start-simple), [ma trận V-START](operations-design.md#ops-start-checks), TDD 9–10 |
| 3.0 | [Business Flow Admin](../02-nghiep-vu/business-flow.md#bf-admin-main); Q1–Q3/UI-03/UI-04/UI-05; FR-A01–A04/A07/A08/A10–A12, UC-A01/A02/A03/A05–A08 | [API Admin](api-transactions.md#api-admin), model nội dung/metadata, UI hiện hành; danh sách/thống kê chỉ đọc, một QR |
| 4.0 | A04.1–A04.4/D09/TTS-01/UI-AP07; FR-A05/FR-A06, UC-A04; AC-A08–A13/A18 | [Model nội dung](data-model.md#model-content), [DTO TTS](api-transactions.md#api-dto), [state/worker/file](operations-design.md#ops-jobs) |
| 5.0 | D09/A07/[UI-04](../02-nghiep-vu/decisions.md#ui-admin-confirmed-02); FR-A07/A12, UC-A05, AC-A14/A15/A26 | Model CONTENT_PAIR/refs/file; API-A24/TX-A02, [ca áp dụng cặp](../02-nghiep-vu/PRD.md#ac-pair-apply) |
| 6.0 | D08/A02.1–A02.3; DATA-01–DATA-04; FR-A09/FR-D01, UC-A06/UC-D01, AC-A19–A21/AC-D01/D02 | [Operations lifecycle/backup/restore](operations-design.md), model 5 và TX-D01/TX-F01. LIFE-P02/P03/REC-P01/BKP-P02 không thay đổi |
| T1 | P03/P06/Q2/UI-AP05/TECH-04; FR-K05/K07, FR-A01/A03/A04/A07; DR-09 | [TDD tab](TDD.md#tdd-tab-session)/[audio](TDD.md#tdd-full-audio), [mô hình — quy ước](data-model.md#model-conventions); không metadata audio thao tác gửi lên DB |

### 8.2. Các phân biệt phải giữ khi dùng sơ đồ hướng dẫn

- Backend chỉ nhận DTO cần thiết: dữ liệu pending/quyền gửi cho Bắt đầu, lựa chọn và ACK cần thiết; mốc nghe/Blob/hình đang dở/soạn chưa lưu được T1 giữ theo nguồn. Không “tự giữ tạm” thành API Lưu.
- P06/audio/tua tại tab không tạo session/view/nhật ký nghe. Cảnh báo năm phút và ngoại lệ nghe hết đúng bài đang phát là xử lý frontend dựa mốc server; không API tải audio sau hết hạn, không gia hạn vì nghe tiếp.
- Hết phiên Admin dừng nghe thử, không dùng ngoại lệ khách. Tác vụ đã accepted là dữ liệu bền D3 nên không mất chỉ vì tab đóng/phiên hết hạn.
- D1 giữ snapshot điểm đã xóa để thống kê đúng hạn D08, không gộp điểm mới trùng tên. P3 xóa DB khác P6 dọn F1, hồ sơ tài liệu vẫn giữ.
- Bộ chuẩn năm điểm × bốn ngôn ngữ = 20 văn bản và 20 audio; không trần số điểm, không đồng nghĩa các bản đã được cung cấp/duyệt.
- F2 giữ bảy ngày theo từng bản; phục hồi tách môi trường/lọc theo mốc gốc rồi thu hồi quyền cũ, không tái chạy lệnh/TTS hoặc ghi endedAt giả. Không đổi REC-P01 thành thu hồi mỗi restart.

### 8.3. Kết quả đối chiếu và trạng thái

Đã đối chiếu G01–G08, từng quan hệ nguồn–đích–chiều mức 1 theo từ điển 4.2 và ba bảng cân bằng mức 2 sau chuẩn hóa ký hiệu kho lặp. Giữ nội dung và xử lý của ba phân rã; không cần mở gói có đầu mút ở khung DBS. Chưa phát hiện mâu thuẫn thực chất hoặc quyết định nghiệp vụ mới cần người dùng chọn để thêm DFD này. Việc lặp ký hiệu kho và bố trí cụm chỉ phục vụ cách nhìn luồng dữ liệu, không thay model/API/nguồn.

Bản mức 1 được render bằng Mermaid 11.12.1/ELK 0.1.7 trên Chrome cục bộ để xem toàn hình và từng cụm; SVG xuất lại từ cùng nguồn, thay bản 0.2. Không dùng giảm số cạnh làm bằng chứng dễ đọc. Các sơ đồ mức 0/2 giữ nguyên dữ liệu và cấu trúc. Đã xem toàn hình và các cụm trên bản render cục bộ, kiểm nhãn/đầu mút/đường nối. Đây chưa là kiểm bố cục in thực tế: toàn hình dạng dọc dài, không khẳng định thu nhỏ nguyên hình vào một trang A4 hoặc A3 vẫn đọc rõ. Khi đưa vào báo cáo cần phóng to hoặc trình bày phần cắt theo các cụm từ cùng SVG, giữ mã/ký hiệu lặp và luồng; không vẽ lại thành mô hình khác. Phần cắt chỉ minh họa chi tiết, phải kèm hình tổng hoặc chú thích tiếp nối khi đầu mút/nhãn nằm ngoài phần cắt; không coi nó là một DFD độc lập. Chưa chốt khổ giấy hoặc cam kết bố cục giống nhau trên mọi trình đọc Mermaid; kiểm cú pháp không thay kiểm trực quan. Công cụ render chỉ phục vụ tài liệu, không thêm dependency ứng dụng hoặc đổi TECH-01. Lượt dựng sơ đồ không chạy phần mềm dự án; bằng chứng backend hiện tại theo [handoff](../01-tong-quan/handoff.md#next-step). Các sơ đồ là dự thảo cần nhóm rà soát; không bằng chứng UNIQUE/TX, quyền tab, khóa runtime, Blob/expiry, provider, SSH/Docker hoặc backup/restore đã đáp ứng. Các ca kiểm đã chuẩn bị tiếp tục đọc ở operations; không tích checklist toàn TDD từ việc thêm sơ đồ.

**Điểm dừng thực hành:** [mục 3.1](practice-01-start-session.md#practice-stop) đã ghi kết quả môi trường và bộ khung backend. Lượt chuẩn hóa không hướng dẫn thực hành mới; DFD không chứng minh ứng dụng đã chạy.

### 8.4. Nhật ký



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-03-ky-thuat-dfd-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
