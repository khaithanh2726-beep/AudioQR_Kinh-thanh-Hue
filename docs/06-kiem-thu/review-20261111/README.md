# Rà soát thiếu sót toàn hệ thống — 11/11/2026

## Kết luận phạm vi

Hệ thống đã chạy được một số luồng kỹ thuật trên môi trường tách biệt, chưa đạt bản demo hoàn chỉnh hoặc nghiệm thu đồ án. Backend full 520/520 là kiểm Java/MySQL tạm; 60 ca frontend, build, media Linux, restore offline và bảy ca browser sửa UI chứng minh đúng phạm vi từng lượt. Các kết quả đó không thay thế QR/HTTPS thật, bản đồ/dữ liệu chuẩn, bộ 20 cặp, thiết bị thật và vận hành.

Căn cứ: [PRD](../../02-nghiep-vu/PRD.md), [UI](../../04-giao-dien/ui-design.md), [roadmap mục 3](../../01-tong-quan/roadmap.md), [kiểm local](../local-20261111/README.md), [đóng gói/restore](../packaging-20261111/README.md), [sửa UI Khách](../guest-ui-20261111/README.md), nguồn frontend/backend/deploy và đối chiếu runtime thử ngày 11/11. Không đọc hoặc sửa trạng thái GitHub trong lượt này.

## Thiếu sót và điều kiện hoàn thành

| Mã | Mức ưu tiên / tình trạng | Thiếu sót đã xác định | Đầu ra cần có để kết luận hoàn thành |
|---|---|---|---|
| RV-01 | Chặn hành trình khách — thiếu cấu hình và bằng chứng | Chưa có URL HTTPS thật dùng từ điện thoại; QR chưa có PNG/bản in dùng được để quét. Admin hiện báo chưa có URL. | Một URL trang bắt đầu thực, cấu hình cùng origin/cookie/CSRF đúng; một QR chung, PNG và bản in có URL dự phòng; quét và chạy toàn luồng từ Android/iPhone khác mạng. Không dùng QR localhost hoặc URL giả để đánh dấu đạt. |
| RV-02 | Chặn demo — thiếu tích hợp bản đồ/dữ liệu đã xem xét | GuestMap chỉ vẽ polygon API trên nền lưới. Môi trường thử có các tam giác trùng nhau, nhãn đè. Admin vùng cũng chưa có nền bản đồ để biên tập phù hợp ngữ cảnh Huế. | Bản xem trước nền và năm vùng có nguồn từ tài sản hiện có, nhãn phân biệt/chọn rõ, kiểm chạm/kéo/phóng/chồng lấn; duyệt đúng phạm vi trước đưa vào dữ liệu phục vụ chính thức. |
| RV-03 | Chặn demo — dữ liệu khởi tạo chưa chuẩn bị | Runtime hiện có 6 điểm, 5 điểm hiển thị, **0/5 mã điểm chuẩn**, 5 slot đã áp dụng đều vi; năm điểm hiển thị chỉ có **1 geometry hash**. Có script seed nhưng chưa áp cho bộ demo hiện tại. | Database/kho audio demo riêng, khởi tạo đủ năm điểm chuẩn và 20 slot, nhập đúng văn bản/vùng/cặp có hồ sơ; tách môi trường E2E để các lượt test không làm bẩn demo. Không gộp hoặc xóa dữ liệu hiện có để thay cho chuẩn bị môi trường mới. |
| RV-04 | Chặn nghiệm thu nội dung — thiếu dữ liệu và duyệt | Có 5 bản vi tiếp nhận và 15 bản dịch nháp; manifest hiện ghi 4 WAV/MP3 vi chờ duyệt. Diên Thọ chưa tạo thành công do HTTP 503 ở lượt trước. Chưa đủ bộ 20 cặp được nghe duyệt/áp dụng. | Rà nguồn/nội dung, duyệt rõ từng bản dịch và nhãn UI; tạo audio theo revision đã chốt, nghe trọn, ghi tham chiếu/giới hạn, đối chiếu đủ 20 văn bản +20 audio. Tham chiếu `smoke-review` trong fixture không là hồ sơ duyệt nội dung thật. |
| RV-05 | Chặn demo tính năng TTS — chưa kiểm tích hợp toàn tuyến | Đã gọi Gemini thật và đã kiểm worker WAV→MP3 theo hai bước; chưa chứng minh UI→hàng đợi→Gemini→MP3→nghe thử→áp dụng xuyên suốt. Runtime hiện tắt cả provider worker và local audio worker. | Cấp khóa ngoài repo/quota còn hiệu lực, bật có kiểm soát trên môi trường được phép; chạy từ nút Tạo tới cặp phục vụ và kiểm 429/503/timeout/chưa rõ/Check/Retry. Giữ chống gọi lặp, không tự bật provider trong lượt rà. |
| RV-06 | Lỗi UI xác nhận và phạm vi rà chưa đủ | Trang Mức đầy đủ tải thất bại nhưng vẫn hiện “Đang đọc mức đầy đủ…” khi busy đã kết thúc. Các màn Admin đã có nguồn và một số ca E2E, chưa được rà đầy đủ theo UI/AC: Q2/Q3, version/receipt, soạn/đối chiếu/hồ sơ, vùng, xóa/lịch sử, thống kê và lỗi. | Sửa state tải/lỗi/thử lại rõ ràng, bổ sung ca hồi quy; rà từng màn Windows/Chrome và trạng thái nghiệp vụ theo hợp đồng, không tự giảm quyền/version hoặc thêm tính năng. Phát hiện khác chưa tái hiện phải ghi là cần kiểm, không gọi là lỗi đã chắc chắn. |
| RV-07 | Thiếu bằng chứng thiết bị/biên nghiệp vụ | Chữ/bố cục Khách, audio mạng lỗi, reload và Hủy đã kiểm trong Chrome/VS Code. Chưa đủ Android/Chrome, iPhone/Safari thật; thiếu bằng chứng tổng hợp cho owner/duplicate/BFCache/background, chọn nhanh, đổi tiếng/thiếu dịch, đúng mốc 8 giờ/cảnh báo 5 phút và ngoại lệ nghe tiếp. | Ma trận thực thi có thiết bị/phiên bản, ca đạt/lỗi/chưa kiểm; kiểm API và UI cùng quyền audio/ACK, không dùng viewport giả lập hoặc clock unit thay toàn bộ thiết bị thật. Không tự đặt ngưỡng ngoài PRD. |
| RV-08 | Thiếu cơ chế/bằng chứng vận hành | Đã diễn tập restore offline và Hash/retention fixture. Chưa có OPS_GUARD/QUIESCING/FROZEN/drain live; chưa kiểm job/claim đang xử lý khi backup/restore. Runtime hiện tắt retention/cleanup. | Gate/drain có bằng chứng, checkpoint DB–audio, đối chiếu tiến trình/job/claim, bật và kiểm scheduler theo hạn 30 ngày/8 giờ/bảy ngày; kiểm backup lỗi, restore và quyền cũ trước mở. Không dùng timeout để tự xóa claim. |
| RV-09 | Thiếu pipeline và vận hành tái lập | Docker image đã build/chạy, Compose mới kiểm config. Có workflow CI nguồn nhưng chưa chạy GitHub; CD Tailscale/OpenSSH, HTTPS, Compose update/rollback và quyền Docker chưa kiểm. Môi trường demo còn phụ thuộc cấu hình/dữ liệu thử trong Temp. | Quy trình khởi tạo từ checkout và cấu hình riêng có quyền phù hợp, seed/Admin/kho file đúng; kiểm restart/persistence, build/release đúng nguồn, CI/CD thật, update/rollback tương thích schema và HTTPS. Giữ quyền Git và G02; không tự commit/push/merge hoặc triển khai public. |
| RV-10 | Thiếu hồ sơ nghiệm thu và bàn giao thống nhất | Bằng chứng nằm theo các mốc, chưa có ma trận đủ FR/UC/AC/NFR nối tới lượt thực thi; một số bảng tổng quan còn mô tả trạng thái cũ. Chưa có bộ hướng dẫn demo/QR/bộ nội dung và diễn tập bàn giao hoàn chỉnh. | Một danh sách hiện trạng thống nhất, ma trận tiêu chí→ca→bằng chứng→lỗi, hướng dẫn chạy/khách/Admin/demo đúng bản, hồ sơ nguồn/duyệt và báo cáo cuối. Lịch sử giữ mốc; không tích checklist nếu chưa đủ toàn ô. |

Đây là mười **nhóm thiếu sót**, không phải mười issue mới hoặc khẳng định chỉ còn mười thao tác. Phân công T01–T11/G02 giữ nguyên.

## Bằng chứng mới của lượt rà

[review.json](review.json) ghi thời điểm, số đếm database và hai ca UI. Truy vấn chỉ đọc schema `hueqr_smoke` trong container restore riêng. Không in mật khẩu, credential, tên khách hoặc khóa runtime.

- [Trang QR chưa cấu hình](qr-not-configured.png): đăng nhập Admin ở context kiểm riêng, mở QR và xác nhận thông báo chưa có URL.
- [Lỗi Mức đầy đủ](completeness-error.png): chặn riêng GET `content-completeness` trong browser kiểm; sau lỗi, nút tải lại đã được bật nhưng nhãn “Đang đọc…” vẫn hiện cùng thông báo không kết nối được. Không sửa API hoặc database để tái hiện.
- Runtime được đối chiếu chỉ các flags không chứa secret: `HUEQR_TTS_ENABLED=false`, `HUEQR_TTS_LOCAL_ENABLED=false`, `HUEQR_CLEANUP_ENABLED=false`, `HUEQR_RETENTION_ENABLED=false`.
- Seed năm điểm chuẩn tồn tại ở `backend/src/main/resources/db/seed/five-places.sql`, có test riêng; seed không chạy tự động theo Flyway trong runtime thử đang mở. Kết quả kiểm seed không chứng minh môi trường demo đã được khởi tạo.

Lượt này không sửa mã ứng dụng, không gọi Gemini/dịch vụ ngoài, không bật worker/scheduler/tunnel, không thay database, không chạy Git ghi hoặc thay checklist. Dữ liệu E2E đang mở được giữ nguyên để đối chiếu.

## Thứ tự xử lý

1. Chuẩn bị bộ demo riêng và bản xem trước bản đồ/năm vùng (RV-02/RV-03); rà và sửa các lỗi UI chắc chắn (RV-06).
2. Chốt một hành trình demo đầy đủ, gồm URL HTTPS và QR thật (RV-01), một cặp vi có hồ sơ và TTS xuyên suốt (RV-04/RV-05).
3. Mở rộng đủ năm điểm/bốn ngôn ngữ, kiểm thiết bị và các ca biên (RV-04/RV-07).
4. Hoàn thiện vận hành/pipeline và nghiệm thu/bàn giao theo yêu cầu đã chốt (RV-08–RV-10). Các phần độc lập có thể làm song song theo G02, không giảm phạm vi để coi demo là nghiệm thu.

Phần triển khai/kiểm mã và dữ liệu thử có thể thực hiện độc lập. Nền/vùng và nội dung cần nhóm duyệt; HTTPS/tunnel/CD cần cấu hình và quyền thao tác môi trường; Gemini cần khóa/quota hiệu lực; thiết bị thật cần có máy để kiểm. Những đầu vào đó không được suy thành đã có chỉ vì từng được khảo sát hoặc cho phép dùng dịch vụ.
