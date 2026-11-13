# Kiểm bản HTTPS Ngọ Môn — 12/11/2026

## Phạm vi và cấu hình

Bản tập dượt riêng `hueqr-rehearsal-15d2f5c0`, cổng loopback 18083, MySQL và kho audio riêng. Trang Khách: https://kiwi-situated-outboard.ngrok-free.dev/; Admin: `/admin.html`. [QR thực tải từ Admin](../../07-van-hanh/qr-tap-duot.png) mở trang bắt đầu. Chỉ Ngọ Môn tiếng Việt được bật theo [hồ sơ bộ đã rà](../../05-noi-dung/duyet-ngo-mon-tap-duot-20261112.md); không suy thành duyệt 20 cặp.

Ảnh đang chạy: `hueqr-local:20261112-https-ngrok`, ID `sha256:2d0fa05368a6885608c12642a3559aa763b3117f00bad85abd3c463e1d3eee08`, schema V13. Có 5 địa điểm, 20 slot, một văn bản/audio/cặp áp dụng, một điểm hiển thị. Bốn worker provider/local/cleanup/retention tắt; không tạo thêm TTS. Khóa, tài khoản, dump và audio giữ ngoài repository. Bản sao offline trước V13 đã đối chiếu Hash, chưa thử restore; không thay thế quy trình backup live.

## Hai lỗi đã sửa

- MySQL JSON làm lệch chữ số cuối của tọa độ, khiến Hash vùng không còn khớp và gate hiển thị từ chối `REGION_INVALID`. V13 lưu JSON nguyên dạng trong LONGTEXT có CHECK JSON_VALID, giữ kiểm Hash nghiêm ngặt. Vùng được lưu lại từ hình gốc đã rà thành revision mới; giữ revision và receipt lịch sử. Không sửa trực tiếp dữ liệu để bỏ gate. [Mô hình dữ liệu](../../03-ky-thuat/data-model.md#model-region-precision).
- API Khách không gửi cookie nên ngrok free trả cảnh báo dạng text/plain thay JSON mặc dù HTTP 200. Cờ build mặc định tắt; bản này bật header công khai `ngrok-skip-browser-warning` chỉ khi origin HTTPS khớp cấu hình. Giữ `credentials: omit` và credential riêng, chặn redirect; HTTP 200 không phải JSON bị từ chối. [Hướng dẫn vận hành](../../07-van-hanh/tap-duot.md).

Đã hoàn tất tám thao tác quản trị có receipt: lưu/áp dụng vùng, duyệt tài nguyên, áp dụng cặp và bật điểm theo điều kiện API. Lần bật trước V13 bị từ chối vẫn giữ trong lịch sử; không lặp thao tác đã commit để khắc phục.

## Kết quả thực chạy

- Frontend: 71/71 ca logic đạt, gồm sáu ca transport ngrok; build Vue và Docker thành công. Không đổi phiên bản phụ thuộc.
- HTTPS Chrome: 2/2 ca đạt trên ảnh cuối. Admin đăng nhập, tải QR 512×512, yêu cầu in và chụp CSS in. Khách tạo phiên vi → nhận bản đồ JSON một điểm → chọn vùng → đọc đúng Hash văn bản → tải đúng Hash MP3 và phát thực hơn 0,7 giây → kết thúc qua giao diện. Request map có header ngrok, không có cookie; không có page error.
- Backend: lượt đầy đủ chạy 522 ca, 521 đạt và một lỗi assertion ở test mới kiểm precision; không có error/skipped. Ca đã sửa assertion chạy lại 1/1 đạt, Maven BUILD SUCCESS. [Ledger](backend-case-ledger.json) đối chiếu 522 ca độc lập có bằng chứng đạt qua hai lượt; [kết quả](result.json) tách số liệu từng lượt, không gọi là một lượt full 522/522. Lượt buổi sáng bị gián đoạn khi máy dừng không được tính là lượt đầy đủ.
- QR PNG: SHA256 `d51f180c0d041336181faccb0c2aa9f0d6d7c3cc0be41ad303b3724886bf00a1`, 9.337 byte. BarcodeDetector không được browser hỗ trợ; chưa kiểm giải mã hoặc quét bằng điện thoại thật. Không tạo PDF.

Các báo cáo JSON và ảnh kèm theo là bản chọn lọc không chứa credential. API, văn bản và MP3 dùng backend thật; tile nền trong bộ HTTPS là fixture. Một tile OSM thật từng tải được trong Chrome ở mốc trước; chưa chứng minh toàn bản đồ trên điện thoại hoặc mạng di động.

## Giới hạn và điểm dừng

Kiểm thủ công thiết bị thật: người kiểm xác nhận đã thử trên Android và nghe thành công. Đây là bằng chứng nghe trên Android do người kiểm báo cáo, bổ sung cho kiểm tự động; chưa có thông tin mẫu máy, phiên bản hệ điều hành, trình duyệt, Wi-Fi/4G/5G hoặc xác nhận riêng từng bước quét QR, hiển thị bản đồ/văn bản và kết thúc phiên. Giữ nguyên JSON kết quả tự động theo thời điểm chạy; ghi kết quả thủ công riêng trong `result.json`.

Còn xác nhận các bước thiết bị nêu trên và iPhone/Safari; chưa đủ 20 cặp đã duyệt, vận hành OPS_GUARD/drain live và nghiệm thu toàn bộ. Ngrok cần cửa sổ vận hành hoạt động; URL không phải cam kết hosting liên tục. Nguồn mới chưa stage/commit/push, không thay checklist hoặc GitHub Project. Sáu tệp T08 ban đầu được đối chiếu riêng với baseline.

**Bổ sung 13/11:** [kiểm iPhone thật](iphone-manual-20261113.json) ghi nhận người kiểm báo bản Ngọ Môn chạy ổn sau khi mở lại ngrok. Preflight độc lập trước lượt thử trả HTTP 200 và có application shell. Đây là xác nhận sử dụng cơ bản trên iPhone, thay trạng thái chưa có lượt thử iPhone ở mốc 12/11; chưa có tên/phiên bản trình duyệt, loại mạng hoặc kết quả riêng từng bước, nên không suy đủ ma trận Safari, các ca lỗi hoặc toàn bộ 20 cặp. Giữ nguyên kết quả tự động và kết quả Android trước đó; không tự tích thêm checklist.
