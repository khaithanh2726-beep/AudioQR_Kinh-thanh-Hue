# Kiểm trình duyệt trên bản phục hồi riêng

Hai công cụ chuẩn bị/kiểm bổ sung chỉ nhận project `hueqr-ops-check-*` đã được restore thành công, khác project phục vụ. Dùng Chrome và Playwright sẵn có trong frontend; không tải browser hoặc gọi provider.

```powershell
node backend/src/test/fixtures/smoke/prepare-acceptance.mjs '<restore-da-dung>' '<runtime-cap-tai-khoan-chi-doc>' '<thu-muc-bang-chung-moi>'
node backend/src/test/fixtures/smoke/acceptance-browser.mjs '<thu-muc-bang-chung>'
```

Chuẩn bị kiểm gate/ảnh/project, yêu cầu cả hai container ban đầu dừng, provider/converter/cleaner/retention tắt, chỉ loopback. Tài khoản đọc từ runtime nguồn và chỉ lưu vào private-runtime.json ngoài Git với ACL của tài khoản/SYSTEM. Không in hoặc sao chép tệp đó vào báo cáo chung.

Bộ kiểm chờ HTTP sẵn sàng rồi chạy 10 nhóm UI và một nhóm crash. API và database là thật; tile bị chặn và thay bằng ảnh fixture. Fault injection chỉ làm mất mạng/phản hồi sau khi backend đã xử lý, hoặc trả 401 để kiểm UI. Ca expiry chỉ đổi timestamp của phiên fixture mới có tiền tố Acceptance- trong DB riêng, giữ quan hệ tám giờ và dùng clock browser; không đổi cấu hình hạn hoặc giờ hệ điều hành. Không ghi rằng đã chờ đủ tám giờ/30 phút thật.

Ca crash chỉ kill/restart container riêng đã kiểm label, giữ gate và lease chưa rõ owner; freeze phải bị từ chối. Chỉ xóa lease fixture do chính ca kiểm tạo, không dùng mã này như công cụ giải phóng lease/claim thật. File crash-fixture-private-*.json giữ nhận diện fixture ngoài Git để rà khi lỗi. Bộ kiểm không chứng minh đã giải quyết mọi reconcile sau crash.

Kết quả browser-acceptance.json ghi từng ca, phiên bản Chrome/OS/Node và phạm vi fixture. Ca lỗi được giữ trước chạy lại, không cộng các lượt thành full mới. Sau kiểm dừng đúng hai container ở private-runtime.json bằng tên/label đã đối chiếu; không dọn project khác hoặc volume phục vụ. Kết quả không thay phép thử Android/iPhone hoặc nghe duyệt D09.

## Kiểm bộ 20 cặp đã xuất bản

```powershell
node backend/src/test/fixtures/smoke/verify-published-content.mjs '<runtime-loopback>' '<bao-cao-ngoai-repository.json>'
```

Dùng trên bản sao trước khi thực hiện trên bản phục vụ đã được phép kiểm. Bộ kiểm tạo/kết thúc bốn phiên, chọn năm điểm theo từng ngôn ngữ, đối chiếu văn bản/MP3 theo SHA256 và thử replay cùng key. Không gửi view-ack vì không hiển thị văn bản. Không ghi đây là nghe thử trên thiết bị; kết quả API/bytes thật và phép kiểm điện thoại được lưu riêng.
