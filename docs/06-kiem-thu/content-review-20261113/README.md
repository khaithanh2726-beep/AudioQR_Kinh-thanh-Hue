# Rà soát bộ nội dung và bốn vùng — 13/11/2026

## Phạm vi và kết quả

Bản thử riêng được phục hồi từ snapshot V14 trên schema, volume, khóa runtime và cổng loopback riêng. Không cập nhật bản Ngọ Môn V13 đang phục vụ, không mở tunnel hoặc bật billing. [Kết quả từng bản](result.json) nhận diện văn bản, job, audio và Hash; [bundle v02](../../05-noi-dung/demo-content-v02.json) chứa 20 văn bản và hình học dùng đối chiếu.

| Hạng mục | Kết quả thực tế |
|---|---|
| Nội dung | Rà 20 bản vi/en/fr/ru; v02 điều chỉnh tám bản thuộc Cửu Đỉnh và Diên Thọ, giữ sáu nháp T08 ban đầu nguyên trạng |
| TTS mới | 14/19 MP3 được tạo thật qua API Admin, Gemini 3.8 Flash TTS / Charon; không dùng audio giả hoặc thay model |
| Tiếng Việt | Đủ bốn audio mới cho bốn điểm còn lại |
| Tiếng Anh | Đủ năm audio mới |
| Tiếng Pháp | Đủ năm audio mới; Thái Hòa cần nghe lại thuật ngữ kiến trúc |
| Tiếng Nga | Chưa có audio đạt; job Ngọ Môn ru trả PROVIDER_QUOTA cả lần đầu và một lần Retry sau hơn hai phút; không gọi tiếp bốn bài còn lại |
| Kỹ thuật MP3 | 14/14 đúng Hash, MP3 mono 24 kHz, thời lượng khớp và giải mã toàn file bằng FFmpeg đạt |
| Đối chiếu lời đọc | 14 lượt chép lời toàn file bằng Gemini 3.5 Flash đạt HTTP 200; 13 bản khớp nghĩa, số liệu, ngôn ngữ và kết bài qua đối chiếu của tác nhân |
| Vùng | Bốn vùng được lưu, đọc lại, tính lại Hash và áp dụng qua API trong bản riêng; chưa chuyển sang bản phục vụ |
| Cặp mới | Chưa ghi tham chiếu duyệt hoặc áp dụng; giữ cổng D09 và trường hợp cần nghe lại |

Ngọ Môn vi đã được duyệt ở [hồ sơ riêng](../../05-noi-dung/duyet-ngo-mon-tap-duot-20261112.md), không nằm trong 14 audio mới. Bộ mới chưa đủ 20 cặp được nghiệm thu.

## Phương pháp và giới hạn

[Bản chép lời](audio-transcripts.json) được tạo bằng cách gửi MP3 thật, không gửi bản văn tham chiếu cho model. Đối chiếu toàn văn sau đó cho phép khác biệt về dấu câu, cách viết số, dấu tên riêng và từ đồng âm khi nghĩa không đổi. Không áp ngưỡng sai số tùy ý làm tiêu chí nghiệm thu.

Thái Hòa fr có cụm “trung thiem diep oc” được chép thành “thong diem diep oc”. Chưa phân biệt được phát âm TTS với lỗi nhận dạng; lượt kiểm bằng model thứ hai trả 503. Bản này giữ trạng thái cần nghe lại, không tự kết luận đạt. Hai dạng giống cách phát âm trong tiếng Pháp như `appelé/appelés` không được suy thành lỗi nội dung chỉ từ chính tả của ASR.

Không gọi ASR, Hash hoặc phát bằng trình duyệt là con người đã nghe trọn bài. Theo [D09 mục 4.9](../../02-nghiep-vu/decisions.md), còn cần nghe trọn audio trước duyệt cuối; bản dịch dùng demo phải giữ giới hạn chưa thẩm định chuyên gia. Chưa ghi nhận người nghe hoặc người có năng lực ngôn ngữ thay cho bằng chứng chưa có. Vì vậy 13 kết quả khớp AI vẫn có trạng thái `AI_COMPARISON_PASSED_PENDING_D09_LISTENING`.

Provider phân tích trả 404 cho Gemini 2.5 Flash, 400 do JSON mode cho Gemini 3.5 Transcribe và 503 cho Gemini 3.8 Flash; không tính các lượt này là đạt. Gemini 3.5 Flash thực hiện các lượt chép lời thành công. Model TTS vẫn giữ nguyên model đã chốt. Một lỗi quota fr đã hết sau một lần Retry có kiểm; lỗi quota ru tiếp tục sau Retry, không suy đó là quota ngày hoặc phút vì API nghiệp vụ chưa cung cấp chi tiết này.

## Bằng chứng và bảo quản

- [Giải mã toàn file](audio-technical-check.json) ghi kết quả FFprobe/FFmpeg thực tế trên ảnh ứng dụng đã ghim.
- [Hồ sơ vùng](../../05-noi-dung/duyet-vung-demo-v02.json) giới hạn vùng chọn chạm dùng demo, không khẳng định ranh giới di tích pháp lý/khảo sát hoặc vị trí GPS của khách.
- [Chrome Admin](admin-review-browser.json): một lượt cuối mở đúng văn bản, chọn đúng audio, tải Blob đúng Hash và bắt đầu phát đạt 14/14, không có lệnh nghiệp vụ ghi mới hoặc thay cặp. [Ảnh Thái Hòa fr](admin-pronunciation-review.png) giữ màn hình chờ rà. Đây là kiểm bắt đầu phát, không phải nghe trọn. Lượt fixture đầu mở/phát được bốn bài vi rồi lỗi bộ chọn nhãn ngôn ngữ; không tính là lượt toàn bộ đạt và không sửa sản phẩm để né lỗi fixture.

MP3/WAV, cấu hình, tài khoản, khóa, ledger lệnh/receipt và snapshot nằm ngoài repository trong thư mục runtime riêng có ACL. Tệp công khai chỉ chứa dữ liệu kiểm đã chọn lọc. Không tự tích checklist, commit/push hoặc thay Project từ kết quả từng phần này.

[Bảo quản cuối lượt](content-preservation.json): đã đóng gate/drain, dừng ứng dụng, trả cả bốn worker về false, gỡ khóa provider khỏi cấu hình bản thử và recreate container ở trạng thái dừng. Snapshot COMPLETE có 30 tệp thật: WAV/MP3 của 14 bài mới cùng bộ Ngọ Môn cũ. Database riêng cũng đã dừng; bản Ngọ Môn phục vụ vẫn giữ nguyên. Chưa restore kiểm lại chính snapshot nội dung này, không suy kiểm restore V14 trước đó thành lần kiểm của snapshot mới. [Các lượt Chrome](browser-attempts.json) phân biệt lượt fixture lỗi với lượt cuối đạt.

Mốc này chỉ thêm dữ liệu, tài liệu và fixture; không đổi hợp đồng API hoặc mã sản phẩm Vue/Java đã kiểm ở mốc V14. Không có lượt Maven/npm full mới cho bộ nội dung; 530/530 ở hồ sơ V14 là lượt trước, không gắn thành kết quả chạy lại trong lượt này. [Danh mục Hash hiện hành](../operations-guard-20261113/source-file-inventory.json) đối chiếu nguồn với tag checkpoint và giữ sáu nháp T08 nguyên trạng.

## Phần tiếp tục

1. Khi quota TTS khả dụng, xem ledger và receipt trước tạo thêm; cần năm audio ru. Không lặp job thất bại theo vòng tự động hoặc chuyển model âm thầm.
2. Nghe trọn 14 audio mới, chú ý thuật ngữ Thái Hòa fr; ghi hồ sơ đúng revision/Hash, rà bản dịch theo giới hạn demo. Chỉ ghi tham chiếu và áp dụng cặp đã đủ điều kiện.
3. Kiểm Khách đủ năm vùng và bốn ngôn ngữ sau khi đủ cặp. Chỉ sau mốc này mới chuẩn bị chuyển bản phục vụ, backup trước chuyển và kiểm HTTPS/QR lại.
4. Giữ các điều kiện vận hành còn mở tại [hồ sơ V14](../operations-guard-20261113/README.md): Safari thật, CD từ xa và lịch backup/crash recovery không được suy là hoàn tất từ bộ nội dung.
