# Đối chiếu issue và công việc còn mở — 11/11/2026

Phạm vi T01–T11, phân công và quyền thực hiện giữ theo [G02](../02-nghiep-vu/decisions.md#g02) và [roadmap mục 3](roadmap.md). Lượt đối chiếu không sửa issue, checklist hoặc Project, không suy kết quả local thành hoàn thành toàn ô. [Báo cáo thiếu sót](../06-kiem-thu/review-20261111/README.md) là căn cứ chi tiết RV-01–RV-10.

**Bổ sung 12/11:** [CI, Compose và TTS thật](../06-kiem-thu/release-readiness-20261112/README.md) đã có bằng chứng: push chín nhánh; CI Linux đạt 520/520 backend, 65/65 frontend, 5/5 manifest/build; Compose restart/update/rollback trên hai ảnh cùng schema; một lượt Admin → Gemini → MP3/nghe thử. Các dòng công việc bên dưới giữ phạm vi rộng hơn: còn áp dụng/Khách trên audio provider được duyệt, lỗi provider thực, HTTPS/QR, 20 cặp, thiết bị và vận hành/CD. Không cập nhật GitHub Project hoặc tích toàn ô từ các kết quả bộ phận này; T08 chung tiếp tục giữ nháp chưa commit.

## Phạm vi đã có và điểm cần làm rõ

**HTTPS Ngọ Môn 12/11:** [hồ sơ mới](../06-kiem-thu/https-ngo-mon-20261112/README.md) đã có một cặp vi áp dụng/hiển thị, QR tải thật và 2/2 Chrome HTTPS đạt. Đã sửa precision vùng bằng V13 và transport ngrok cho Khách không cookie; 71/71 logic frontend đạt. Còn quét QR/map/audio trên điện thoại, các cặp còn lại, lỗi provider thực và vận hành live. Các nhận định chưa mở điểm/public ở mốc chuẩn bị bên dưới là lịch sử; không dùng để phủ định mốc HTTPS mới. Không thay checklist hoặc Project.

**Bổ sung bản tập dượt 12/11:** [mốc mới](../06-kiem-thu/rehearsal-20261112/README.md) đã dựng riêng app/database/audio ở 18083, loại preview khỏi build thường và kiểm lại sau restart. Chẩn đoán một tile Chrome thật đạt; còn kiểm toàn map/điện thoại. Cặp/điểm vẫn chưa bật khi chưa duyệt; ngrok/HTTPS/QR, 20 cặp và vận hành/CD còn mở. Không coi mốc chuẩn bị này là Done của T04/T08/T09/T10.

Danh sách công khai tại [GitHub Issues](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues) hiện có 11 issue mở. Đã đọc riêng nội dung [T04](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/4), [T06](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/6), [T09](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/9), [T10](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/10): bản đồ, trạng thái UI, QR, kiểm thiết bị và môi trường demo đã thuộc checklist hiện có. Các thiếu sót triển khai không tự chứng minh phải tạo issue mới. Không kiểm hoặc suy trạng thái GitHub Project từ trang issue công khai.

Bảng dưới cụ thể hóa đầu ra/phép kiểm trong phạm vi đã chốt, dùng cho lượt tiếp tục; chưa phải nội dung đã đăng lên GitHub. Với T01/T02/T03/T05/T07/T08/T11, phạm vi đối chiếu từ roadmap local hiện hành, chưa đọc lại toàn bộ thân issue GitHub trong lượt này.

| Issue / G02 | Công việc tiếp tục và đầu ra kiểm được | Liên quan báo cáo |
|---|---|---|
| T01 — Thành | Duyệt đúng phạm vi nền/vùng và thiết kế, ghi quyết định còn mở; cập nhật trạng thái hiện hành tách lịch sử. Bản đồ đề xuất không tự thành ranh giới chính thức. | RV-02, RV-10 |
| T02 — Hòa | Quy trình seed năm điểm/20 slot cho database demo riêng, kiểm lặp không phá dữ liệu; phân biệt seed, vùng/bản nháp và cặp đã áp dụng. | RV-03, RV-09 |
| T03 — Thành | Phối hợp kiểm Khách trên bộ demo/HTTPS; owner, thời hạn, lựa chọn/ACK/audio đúng quyền, lỗi và reload trên thiết bị thật. | RV-01, RV-07 |
| T04 — Nghĩa | Tích hợp nền có nguồn, vùng/nhãn phân biệt, tương tác map và UI thực trên điện thoại. Ghi riêng kết quả sửa chữ/bố cục và bản xem trước; chưa tích hoàn tất từ ảnh preview. | RV-02, RV-07 |
| T05 — Thành | Điều kiện cặp/gate, version/receipt và xóa/lịch sử; phối hợp chạy TTS, scheduler/claim/file và backup/drain theo thiết kế. | RV-05, RV-06, RV-08 |
| T06 — Nghĩa | Sửa trạng thái Mức đầy đủ, kiểm tải/lỗi/thử lại/dữ liệu cũ; rà Q2/Q3, vùng/soạn/cặp/thống kê. QR cần URL/PNG/bản in thật, không coi màn chưa cấu hình là QR đã xong. | RV-01, RV-06 |
| T07 — Thành | Kiểm từ thao tác Admin qua worker/provider đến MP3 thực; quota, kết quả chưa rõ, Check/Retry và đúng revision; không dùng fixture thay bằng chứng provider xuyên suốt. | RV-05 |
| T08 — cả nhóm | Nguồn và duyệt năm vùng/bài, rà 15 bản dịch, đủ audio khớp và nghe trọn; bảng 20 cặp có hồ sơ. Giữ nháp và giới hạn rõ trong khi chờ duyệt. | RV-02, RV-04 |
| T09 — Hòa | Ma trận FR/UC/AC/NFR nối tới lượt kiểm; kiểm bản đồ/QR và thiết bị thật, lỗi UI đã phát hiện, Q2/Q3/8 giờ/5 phút/cặp/xóa-file/restore. Ghi đạt/chưa đạt/chưa kiểm. | RV-06–RV-10 |
| T10 — Hòa | Cấu hình HTTPS/URL, SSH/Tailscale và quyền Docker; CI/CD thực, Compose/persistence/update/rollback, backup/drain và hướng dẫn tái lập. Chưa có cấu hình thì ghi phụ thuộc, không tự triển khai. | RV-01, RV-08, RV-09 |
| T11 — Thành | Tổng hợp bản nguồn/runtime, hướng dẫn demo/QR/khách/Admin, hồ sơ duyệt và bằng chứng kiểm; mô tả đúng giới hạn trước bàn giao Git/PR. | RV-10 |

## Mốc đang triển khai

### Bản chuẩn bị cập nhật Project sau rà UI

Nguồn checklist chính vẫn là [roadmap mục 3](roadmap.md#t01). Bảng sau là các đầu việc còn lại cần ghi rõ trong issue hiện có, không thay thế hoặc tự tích 89 ô gốc. Đề xuất `In Progress` cho T01–T11 vì mỗi task đã có sản phẩm hoặc hoạt động kiểm/tài liệu, nhưng chưa task nào đủ căn cứ `Done`. Đây là trạng thái đề xuất, chưa đọc được bảng Project trực tiếp và chưa thay đổi GitHub.

| Issue / phụ trách G02 | Checklist còn lại để cập nhật vào tiến độ |
|---|---|
| #1 / T01 — Thành | Chốt mẫu thị giác đỏ sẫm theo [UI 3.2.1](../04-giao-dien/ui-design.md#ui-reference-red); rà nền/vùng đúng phạm vi; hoàn thiện thiết kế vận hành và ma trận tương thích/truy vết. |
| #2 / T02 — Hòa | Kiểm tái lập migrations/seed trên database riêng; đủ 5 điểm/20 slot; nhập vùng/cặp đúng hồ sơ; kiểm ràng buộc và seed lặp không phá dữ liệu. Đã có bộ preview 5 điểm/20 slot/5 nháp, còn 0 cặp áp dụng. |
| #3 / T03 — Thành | Đối chiếu API Khách với bộ phục vụ thật; kiểm quyền/ACK/chống lặp, mất mạng/reload, hết hạn 8 giờ và ngoại lệ nghe tiếp qua UI/thiết bị. Giữ bằng chứng backend đã có. |
| #4 / T04 — Nghĩa | Rà kiểu UI mới trên Khách; duyệt và ghép nền/vùng vào luồng thật; kiểm chọn/chồng lấn/kéo/phóng; kiểm audio/đổi tiếng/lỗi và Android/iPhone. Bản xem trước không thay luồng Khách. |
| #5 / T05 — Thành | Rà đăng nhập/Q2/Q3, version/receipt/chưa rõ kết quả; kiểm cặp/hiển thị/xóa/lịch sử với UI; hoàn thiện gate/drain và kiểm scheduler/vòng đời cùng T09/T10. |
| #6 / T06 — Nghĩa | Rà toàn bộ màn Admin theo hợp đồng: địa điểm/vùng, soạn/duyệt/cặp, TTS/nghe thử, thống kê; kiểm bảo vệ phần chưa lưu. Lỗi tải Mức đầy đủ đã sửa và kiểm lại; QR còn cần URL/PNG/bản in thật. |
| #7 / T07 — Thành | Chạy Admin → hàng đợi → Gemini → WAV/MP3 → nghe thử → áp dụng; kiểm quota/429/503/timeout, Check/Retry và đúng revision. Cần khóa/quota hiệu lực ngoài repo. |
| #8 / T08 — cả nhóm | Rà/duyệt 5 văn bản vi, 15 bản dịch, 5 vùng; tạo đủ 20 audio khớp revision; nghe trọn và ghi hồ sơ trước áp dụng. Bốn audio vi hiện có còn là nháp. |
| #9 / T09 — Hòa | Ma trận FR/UC/AC/NFR → ca → bằng chứng; hồi quy UI/map; kiểm Q2/Q3, chủ tab/BFCache/background, mốc thời gian và thiết bị thật; ghi rõ ca dùng fixture và ca tích hợp thật. |
| #10 / T10 — Hòa | Có HTTPS cùng origin; tạo/quét QR từ điện thoại khác mạng; kiểm Compose/persistence/update/rollback, CI/CD/Tailscale/SSH và backup/drain/restore. Dọn Docker đã có báo cáo, không thay nghiệm thu vận hành. |
| #11 / T11 — Thành | Đồng bộ hiện trạng, hướng dẫn tái lập/demo/khách/Admin/QR; tập hợp bằng chứng và hồ sơ nội dung; rà nguồn, cấu hình, giới hạn và hướng dẫn bàn giao. |

**Thứ tự thực hiện:** T04/T06/T09 rà và sửa UI trước; T01/T08 duyệt vùng/nội dung để T02/T03/T05/T07 ghép một hành trình thật; T10 cung cấp HTTPS/QR và kiểm môi trường; mở rộng 20 cặp, thiết bị, vận hành và T11 bàn giao. Các đầu việc độc lập thực hiện song song theo G02.

### Sổ lỗi và khoảng trống hiện hành

| Mã | Phân loại hiện tại | Hành động / điều kiện xác nhận |
|---|---|---|
| UI-01 | Chữ/fallback và bố cục Khách đã sửa ở mốc trước | Giữ bằng chứng cũ; mẫu mới tiếp tục dùng Segoe UI/Arial, không khôi phục serif gây lỗi. Còn kiểm phông/thiết bị thật. |
| UI-02 | Phong cách Khách chưa phù hợp mẫu thị giác mới | Đã áp đỏ sẫm/thẻ trắng/tiêu đề và vùng đỏ vào nguồn, build và browser; chờ rà. Không suy ảnh tham khảo thành GPS/vùng tròn. |
| UI-03 | Đã nối lớp OSM; chưa xác nhận nền thật do kết nối | Ảnh Docker mới ở 18082 có lớp raster OSM cho Khách/Admin, trạng thái lỗi/thử lại; Chrome dùng tile kiểm đạt. HEAD tới tile thật bị reset, chưa chốt hiển thị thật. Năm vùng còn đề xuất, điểm còn ẩn; cần duyệt/áp dụng và kiểm cặp thật. [Hồ sơ](../06-kiem-thu/osm-map-20261111/README.md). |
| UI-04 | Lỗi tải Mức đầy đủ đã sửa và hồi quy đạt | Sau lỗi không còn nhãn đang tải; tải lại được; kết quả cũ được gắn nhãn. Không ghi lỗi này là đang mở. |
| UI-05 | Đã rà thêm bảo vệ Admin; toàn bộ màn vẫn chưa nghiệm thu | Ba ca Chrome mới đạt: Hủy/tải lại giữ bản soạn, 401 che dữ liệu/đăng nhập lại đúng tài khoản phục hồi, thống kê API thật và bộ lọc cũ. Còn Q3 thời gian thật, version/receipt, cặp/xóa/lịch sử, TTS/nghe thử và thiết bị. Chỉ tạo lỗi cụ thể sau tái hiện; không coi các phần chưa kiểm là lỗi chắc chắn. |
| DEMO-01 | Thiếu cấu hình/dữ liệu và bằng chứng | HTTPS/QR thật, nội dung/audio được duyệt, TTS xuyên suốt, thiết bị và vận hành chưa đủ. Đây là công việc chưa hoàn tất, không mặc định là lỗi lập trình. |

Kết quả mẫu thị giác và giới hạn tại [hồ sơ kiểm UI](../06-kiem-thu/ui-reference-20261111/README.md). Không cập nhật dấu tích, assignee, issue hoặc Project trong lượt chuẩn bị này.

### Mốc bản đồ trước thay đổi mẫu thị giác

Mốc bản đồ/dữ liệu xem trước tập trung T02/T04/T06, phối hợp T01/T08/T09/T11: nền OSM offline chỉ bật cho preview, năm vùng từ tài sản đề xuất, database mới có năm điểm/20 slot/năm văn bản vi nháp và sửa lỗi trạng thái Mức đầy đủ. Giữ dữ liệu E2E cũ, không tạo hồ sơ duyệt/cặp áp dụng giả. Sản phẩm và kết quả kiểm được ghi tại handoff cùng mốc, không đổi người phụ trách hoặc tác giả commit theo người chạy công cụ.

Các đầu vào cần nhóm: rà/duyệt bản đồ và nội dung; khóa/quota để kiểm provider; cấu hình và quyền mở HTTPS/tunnel/CD; Android/iPhone thật. Chưa đề xuất thêm chức năng ngoài PRD, issue mới, assignee, label hoặc thay trạng thái Project. Nếu cần làm rõ checklist trên GitHub, chỉ chuẩn bị nội dung cụ thể sau đối chiếu đầy đủ và thực hiện trong lượt được giao quyền tương ứng.
