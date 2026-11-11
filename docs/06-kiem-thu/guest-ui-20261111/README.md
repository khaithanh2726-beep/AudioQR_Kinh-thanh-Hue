# Chữ và bố cục Khách — kiểm ngày 11/11/2026

Phạm vi UI-K01–UI-K04: sửa chữ và bố cục theo [thiết kế UI mục 3](../../04-giao-dien/ui-design.md#ui-layout). Giữ API, phiên/owner document, lựa chọn mới nhất, grant/ACK, quy tắc audio, thời hạn và dữ liệu địa điểm hiện có. Đây là mốc sửa frontend, không nghiệm thu bản đồ hoặc toàn bộ giao diện.

## Phát hiện và thay đổi

Chữ tiếng Việt ở bản serif cũ bị tách dấu/mất nét khi quan sát trong VS Code. Chrome DevTools ghi nhận cùng nút/đoạn giới thiệu trộn Georgia với Times New Roman. Sau đổi bộ phông sang Segoe UI/Arial/sans-serif, nút và đoạn giới thiệu dùng Segoe UI; bốn ngôn ngữ vi/en/fr/ru đều có glyph dựng bằng Segoe UI trên máy Windows thử. Không thay bytes bản văn, tự sửa nội dung lịch sử hoặc quy kết thành lỗi UTF-8. [Phông trước/sau](fonts-before-after.json), [ảnh trước](before-start.png), [ảnh sau](start-vi.png).

Trang bắt đầu đặt tên → ngôn ngữ → Bắt đầu trong cùng form. Trang tham quan xếp dọc bản đồ → tên điểm → audio → ô văn bản, đưa tên khách/Kết thúc lên đầu và tách bộ chọn ngôn ngữ. Chiều cao các phần thích ứng viewport; chỉ ô văn bản cuộn khi đọc, bản đồ/tên/audio giữ trong vùng nhìn thấy. Một điều khiển phát và thanh tua giữ hành vi, bổ sung hiển thị tổng thời lượng. Hộp chọn vùng giữ tiêu đề/Hủy cố định, chỉ danh sách ứng viên cuộn. Không tự chọn hoặc gộp ứng viên.

## Kiểm đã chạy

| Phép kiểm | Kết quả |
|---|---|
| `npm test` | 60/60, exit 0; [log](unit.log). |
| `npm run build` | Exit 0; [log](build.log). |
| Docker build | Exit 0, ảnh `hueqr-local:20261111-guest-ui`, SHA tại [cấu hình chuyển bản thử](demo-switch.json). Vue test/build và backend package đạt; package bỏ qua test Java. |
| Chrome với bản Docker/API thật | 7/7 đạt, exit 0, 54,5 giây; [log](packaged-browser.log), [runner](browser-run.json). Sáu ca chữ/bố cục/lỗi audio và một ca Admin/Khách sau restore. |
| Phông chữ | Kiểm chuỗi đúng ngôn ngữ và platform font thực của nút vi/en/fr/ru, không suy từ tên CSS; [report](platform-fonts.json). |
| Computer Use trong VS Code | Quan sát chữ rõ sau sửa, bố cục dọc, mốc nghe 0:05 được khôi phục tạm dừng qua reload. Mở hộp ứng viên trong khung nhỏ, thấy Hủy ngay; Hủy giữ bài và mốc nghe. Không nhập mật khẩu hoặc thay dữ liệu Admin qua Computer Use. |

Các viewport kiểm tự động: 1280×800, 960×600, 390×844 và 320×568. Assertion kiểm thứ tự hình học, nút Kết thúc trong header, không tràn ngang/dọc toàn trang khi đọc, vùng văn bản đủ chỗ, cuộn văn bản không dịch bản đồ hoặc gửi opening/ACK mới. Hủy không phát lại, không đổi currentTime hoặc phát sinh opening/ACK. Ca 320×568 chặn riêng audio, giữ văn bản và điều khiển trong viewport; bỏ chặn/thử lại tải MP3 và giữ tạm dừng.

Để ép cuộn trên màn hình rộng, ca bố cục nhân đoạn văn **chỉ trong DOM của trình duyệt kiểm**, không ghi API/database. Ảnh bài đọc được chụp trước thao tác ép cuộn; không dùng đoạn nhân bản làm bằng chứng nội dung thực hoặc bài đã duyệt. Không giả lập API phản hồi thành chức năng hoàn tất. Viewport điện thoại vẫn là giả lập, không thay kiểm Android/iPhone thật.

Ảnh: [bài đọc điện thoại](visit-390x844.png), [khung nhỏ](visit-960x600.png), [320×568](visit-320x568.png), [Hủy luôn thấy](overlap-320x568.png), [audio lỗi](audio-error-320x568.png). Có thêm ảnh trang bắt đầu en/fr/ru trong cùng thư mục.

## Bản chạy và giới hạn

Ứng dụng thử tiếp tục tại `http://localhost:18081/`, container mới `hueqr-packaged-ui-426f3b91`, cùng database/kho audio và epoch/khóa cũ, provider/worker/cleaner/retention vẫn tắt. Container ảnh cũ được giữ ở trạng thái dừng để đối chiếu. Admin có thể cần đăng nhập lại vì HttpSession không lưu qua thay tiến trình; không tự khôi phục lệnh hoặc phát nghe thử. Không dùng database đang vận hành, không gọi dịch vụ ngoài, không đổi dependency hoặc commit/push.

**Bản đồ hiện chưa đạt demo:** vẫn dùng nền lưới và năm bản ghi E2E có đa giác tam giác trùng nhau, nhãn đè nhau. Các ảnh trong hồ sơ minh chứng chữ/bố cục, không minh chứng bản đồ năm khu vực Huế. Cần chuẩn bị dữ liệu demo riêng và bản xem trước nền/vùng từ tài sản hiện có, duyệt trước sử dụng chính thức; không tự coi dữ liệu đề xuất đã được duyệt. Chưa xử lý phân bố nhãn bản đồ trong mốc này.

Còn rà toàn Admin, nền/vùng, đủ 20 cặp được duyệt, TTS từ UI xuyên provider, thiết bị thật, HTTPS/QR và vận hành. Bằng chứng API/audio ở các mốc trước chỉ đúng phạm vi đã kiểm, không tự chứng minh giao diện đạt. Không tích checklist T04/T06 hoặc issue/Project từ mốc sửa này.
