# Báo cáo kiểm thử và giới hạn nghiệm thu

## 1. Phạm vi báo cáo

Báo cáo tổng hợp các lượt đã thực thi cho nguồn được lưu tại main `601c09a`. Đợt biên tập tài liệu không chạy lại phần mềm, không thay mã nguồn hoặc mở rộng phạm vi kết quả. Log, XML, ảnh, JSON và các lần thử lỗi/chạy lại được giữ nguyên trong hồ sơ kiểm thử riêng cạnh dự án; bộ chấm giữ phần tổng hợp này.

Mã kiểm nằm trong `backend/src/test` và `frontend/tests`. Các bằng chứng đã được commit trước đợt biên tập có thể tra tại [nguồn theo phiên bản](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/tree/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu).

## 2. Các kết quả chính

| Nhóm | Kết quả đã ghi nhận | Giới hạn |
|---|---|---|
| Backend | Một lượt full V14 530/530, 0 failure/error/skipped; 51 suite. CI của nguồn tổng hợp và PR #15 đạt | Không coi test dữ liệu kỹ thuật là duyệt nội dung T08 |
| Frontend | 71/71 logic; Vue build đạt; kiểm Chrome/API/MP3 riêng | Không thay ma trận thiết bị thật hoặc mọi ca lỗi |
| Bản đồ/UI | Kiểm chọn trong polygon, ghim đúng điểm nhấn, chồng lấn/Hủy, kéo/phóng, nhiều viewport và lỗi audio | Một số ca dùng tile/API fixture; không xác nhận toàn nền OSM/mạng thật từ fixture |
| Admin | Q2/reload/401, vùng/cặp, thống kê/filter, provider và nghe thử MP3 có các lượt kiểm thật | Chưa nghiệm thu mọi trạng thái trên toàn bộ màn Admin |
| TTS | Một lượt Admin→Gemini→WAV→MP3 thật; sau đó thêm 14 audio vi/en/fr, probe/giải mã và Hash đạt | Năm ru bị chặn quota; 14 audio mới chưa được nghe duyệt cuối, Thái Hòa fr cần rà thuật ngữ |
| HTTPS/QR | Chrome 2/2 trên bộ Ngọ Môn; QR PNG 512×512, URL/read-only, print request/CSS | Không tính request in là đã in giấy hoặc BarcodeDetector là đã giải mã |
| Thiết bị | Người kiểm báo nghe Ngọ Môn trên Android và chạy ổn trên iPhone | Chưa ghi đủ mẫu máy/OS/browser/mạng và từng ca lỗi/time/tab |
| Vận hành V14 | Gate/drain, backup/restore riêng, snapshot V13 và chuyển Ngọ Môn V14 có kiểm trước/sau; hai scheduler dọn đã bật | CD, lịch backup/log và một số ca crash còn mở |
| Manifest backup | 8/8 ca V14 đạt khi chạy riêng; workflow CI còn có bộ nền 5 ca | Không cộng hai bộ hoặc các lần chạy thành một lượt full |

## 3. Nguồn kết quả để đối chiếu

Lượt kiểm vận hành bổ sung sau mốc tài liệu `20cd3ec`: bộ kiểm tính toàn vẹn backup đạt 8/8; backup nội dung V14 và restore vào project mới đạt 30/30 tệp, Hash nguồn/đích khớp, giữ số lượng nghiệp vụ 5 điểm/20 slot/16 text revision/15 audio revision/0 cặp/30 file. 20 bài trong bundle không đồng nghĩa đã lưu 20 revision vào database. Gate khởi động đóng, epoch mới và worker TTS tắt theo công cụ restore.

Scheduler trên bản phục hồi giữ nguyên dữ liệu khi gate đóng qua 75 giây; khi mở đã dọn ba phiên fixture quá hạn và file fixture đủ hạn, giữ phiên fixture còn hạn cùng timestamp gốc, giữ file từng áp dụng mới ngừng bảy giờ và toàn bộ 30 Hash audio thật. Cấu hình worker được trả về tắt, container thử dừng sau kiểm. Lượt kiểm 30 tệp này không gọi provider hoặc thay bản phục vụ; bước snapshot/nâng V13 thực hiện riêng ở mốc bổ sung bên dưới. Bằng chứng bổ sung được giữ tại hồ sơ nội bộ `van-hanh-20261113-dcc5f86a`, không cộng vào lượt full 530 ca hoặc tự tích thêm checklist.

Lượt 14/11 bổ sung: snapshot V13 Ngọ Môn hai file đã kiểm và restore bằng đúng ảnh V13; diễn tập nâng V13→V14 trên bản restore giữ inventory/vùng/Hash, phiên cũ và khóa/epoch, đọc văn bản/tải MP3 93.740 byte khớp Hash, gate close/freeze/resume đạt. Bản phục vụ sau đó đã chuyển V14, backup/restore lại hai file, giữ volume và cặp; bật cleanup/retention, provider/converter tắt. Local/API và HTTPS csrf trả 200; chưa có lượt thiết bị thật mới sau cập nhật. Bằng chứng riêng tại hồ sơ nội bộ vận hành 14/11; không gộp các lượt thành full backend mới.

Kiểm cập nhật ảnh trên restore riêng đạt cả nhánh thành công và nhánh quay ảnh cũ: ảnh có tùy chọn Java sai không khởi động, công cụ trả FAILED nhưng khôi phục ảnh cũ/health/gate RUNNING khi schema không đổi, cấu hình và metadata file giữ nguyên. Tag gây lỗi được gỡ sau kiểm. Đây là fault injection có lỗi dự kiến; không là bằng chứng ảnh lỗi đã phát hành thành công. Bộ Node cục bộ mới đạt 20/20 (5 manifest, 12 integrity, 3 runtime-env), không chạy lại full backend cục bộ trong lượt này.

Đường CD đã kiểm SSH bằng khóa, host key ghim, đúng tài khoản Windows và Docker context desktop-linux; firewall chỉ dải Tailscale. Truyền ảnh/bộ công cụ bằng SFTP và chạy receiver trên restore riêng đạt kiểm Hash tar/JAR/công cụ, backup, đổi ảnh, gate/health và giữ volume/khóa/epoch; không thay bản phục vụ. Lượt này dùng ảnh ứng dụng nền 20cd3ec và bộ công cụ đang phát triển, không là một lượt GitHub runner. Cú pháp YAML, ba script Bash và hai script PowerShell được sinh từ workflow đã kiểm; kết quả CD GitHub chỉ ghi khi có run deploy thật.

- [CI nguồn tổng hợp: 530 ca backend và frontend/build](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43402780832).
- [CI của PR #15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43406790912).
- [Full V14 và 51 suite](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/operations-guard-20261113/result.json), [bằng chứng vận hành](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/operations-guard-20261113/README.md).
- [Chrome/API/MP3 và reload/audio lỗi](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/local-20261111/README.md), [kiểm ghim/bản đồ](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/map-pin-20261111/README.md).
- [TTS thật/Compose](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/release-readiness-20261112/README.md), [HTTPS/QR/Android/iPhone](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/https-ngo-mon-20261112/README.md).
- [Đối chiếu 14 audio mới](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/content-review-20261113/result.json).

Lượt đầu CI `791c581` lỗi tải Maven trước compile; chạy lại job lỗi đạt, không đổi phiên bản. Các lượt bị hủy vì trùng SHA không được tính là kiểm đạt độc lập. Lượt 522 cũ có một fixture sửa/rerun và ledger riêng; không gọi là một full 522/522. Các kết quả này được phân biệt trong hồ sơ gốc.

## 4. Đối chiếu checklist

Đợt rà đủ 89 ô xác nhận **62 ô đủ toàn điều kiện**, còn **27 ô**. GitHub trước rà 28 ô, thêm 34 ô; local trước rà 23 ô và đã được đồng bộ. Chỉ cập nhật checkbox, không đổi trạng thái issue/assignee/Project. Các ô xây chức năng có chứng cứ được phân biệt với ô nghiệm thu toàn bộ thiết bị/dữ liệu/vận hành.

| Task | Đã tích / tổng |
|---|---:|
| T01 | 12/18 |
| T02 | 4/5 |
| T03 | 6/6 |
| T04 | 5/7 |
| T05 | 9/9 |
| T06 | 7/8 |
| T07 | 7/9 |
| T08 | 2/7 |
| T09 | 3/6 |
| T10 | 5/9 |
| T11 | 2/5 |

Chi tiết tiêu chí tại [roadmap](../01-tong-quan/roadmap.md). Bản đọc lại GitHub 11 issue cùng lý do từng ô đã được bảo quản trong hồ sơ kiểm riêng; việc di chuyển bằng chứng không làm thay đổi kết quả rà hoặc tạo lượt test mới.

## 5. Điều kiện chưa đạt

1. Đủ 20 văn bản/20 audio/cặp được duyệt; nghe trọn và hồ sơ D09, quota ru và thuật ngữ Thái Hòa fr.
2. Ma trận FR/UC/AC/NFR→ca→bằng chứng, các ca mạng/time/tab/thiết bị và toàn trạng thái Admin.
3. CD-L01/Tailscale/OpenSSH/quyền Docker từ xa; lịch log/backup và xử lý crash/claim thiếu bằng chứng owner.
4. Chuyển phục vụ, kiểm tái lập môi trường cuối và người nhận thực hiện lại bộ hướng dẫn.

Không kết luận nghiệm thu chính thức toàn đồ án từ mốc lưu mã nguồn hoặc CI xanh. [Hiện trạng bàn giao](../01-tong-quan/handoff.md) ghi việc tiếp theo.
