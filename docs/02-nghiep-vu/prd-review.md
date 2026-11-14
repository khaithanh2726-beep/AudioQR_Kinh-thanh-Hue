# Đối chiếu yêu cầu và giới hạn nghiệm thu

## 1. Phạm vi kết luận

PRD quy định yêu cầu và tiêu chí; báo cáo phần mềm ghi đáp ứng đến đâu. Các nội dung về Use Case, ranh giới cặp/ngôn ngữ, đối ứng FR/AC và câu chữ xóa/dọn đã được chuẩn hóa trong tài liệu. Chưa kết luận nghiệm thu toàn hệ thống chỉ từ việc có tài liệu, CI xanh hoặc các màn hình đã chạy.

## 2. Nội dung đã xử lý trong đặc tả

- Tách phạm vi hai vai trò, giữ bốn ngôn ngữ và không biến năm điểm chuẩn thành trần số địa điểm.
- Phân biệt chọn vùng, mở bài, tải văn bản, ACK ghi lượt, đổi tiếng và tải audio.
- Tách sửa/lưu nháp, ghi tham chiếu, áp dụng cả cặp và bật hiển thị.
- Phân biệt xóa dữ liệu trong giao dịch với dọn file sau đó; giữ lịch sử và dữ liệu dùng chung.
- Chuẩn hóa đối ứng giữa các bảng FR, Use Case và AC; giữ mã tiêu chí ổn định.

## 3. Giới hạn đánh giá

R06 tiếp tục mở về nghiệm thu kỹ thuật/môi trường/dữ liệu: bộ 20 cặp chưa đủ, kiểm tương thích/ca lỗi/thiết bị chưa đủ, quota và phát âm còn cần rà, CD và một số tình huống vận hành chưa được kiểm. Những phần này được theo dõi ở checklist 27 ô còn mở, không lấy rà văn bản thay phép kiểm thực thi.

## 4. Đối chiếu bao phủ toàn bộ yêu cầu chức năng

| Mã FR | Nghiệp vụ đã đối chiếu / UC | Kết quả tài liệu |
| --- | --- | --- |
| FR-K01 | QR, URL, tên 1–50, bốn ngôn ngữ, bắt đầu hợp lệ — UC-K01 | Phù hợp D01-BS/D02/BS/D03-BS/A05; đã đối ứng UC-A07 trong bảng FR. Kiểm tra ngôn ngữ có ở bước 4 UC-K01; không kết luận bỏ sót chỉ vì chưa tách AC riêng cho đầu vào ngôn ngữ sai. |
| FR-K02 | Phiên theo tab, 8 tiếng cố định, tải lại — UC-K01/K04/K05 | Phù hợp D04/P02/P03; R02/R03 đã xử lý câu chung/truy vết. Không lẫn phiên với người. |
| FR-K03 | Vùng/điện thoại, trong-ngoài/bấm lại/kéo-phóng/chồng lấn — UC-K02 | Bao phủ D01/BS, P05–P08, A02.5; R01/R03 đã xử lý; thiết kế R06 còn mở. |
| FR-K04 | Văn bản được phép, thiếu dịch/audio — UC-K02 | Phù hợp D03/BS/D09; không tự chuyển ngôn ngữ hoặc đưa nháp ra khách. |
| FR-K05 | Audio chọn vùng, Phát/Tạm dừng/tua, lỗi — UC-K02/K03/K04 | Phù hợp D07/D03-BS/P04/P06/P08; R01 đã xử lý câu khái quát; không thiếu thanh tua. |
| FR-K06 | Đổi ngôn ngữ giữ phiên/điểm, không tự phát — UC-K03 | Phù hợp D07, kể cả chọn ngôn ngữ thay thế khi thiếu dịch; không đồng nhất chọn vùng. |
| FR-K07 | Khôi phục tab và đúng bài — UC-K04 | Phù hợp P03/A07; về đầu khi mốc không có/bài đổi, không đòi đúng từng giây; R03 đã xử lý; R06 còn phụ thuộc. |
| FR-K08 | Mạng/dịch vụ, thử lại audio từ đầu — UC-K04 | Phù hợp P04/D03-BS; không ép dừng audio còn phát được, không offline/gửi bù. P01 vẫn ưu tiên khi hết hạn. |
| FR-K09 | Cảnh báo, hết hạn/kết thúc/quay lại — UC-K05 | Phù hợp D04-BS/P01/P02; R02 đã sửa câu dẫn, giữ các nhánh đã mô tả đúng. |
| FR-K10 | Đếm lượt theo lần mở, không đếm lặp — UC-K02/K03/K04/A08 | Phù hợp D04/D08/A06; thiếu dịch chưa tính, lần đầu có văn bản tính theo ngôn ngữ đó; A→B→A là lượt mới. |
| FR-A01 | Admin/30 phút/hết phiên/nghe thử — UC-A01/A04 | UI-04 thêm dừng nghe thử/che nội dung, sau xác thực không tự phát/gửi lại; TTS đã nhận tiếp tục, Q2/UI-AP05 giữ công việc. AC-A13 và truy vết đã đồng bộ. |
| FR-A02 | CRUD/lưu ẩn/gate hiện — UC-A02/A06 | UI-04 thay gate văn bản vi bằng cặp vi đã duyệt/áp dụng cùng thông tin/vùng hợp lệ đã xem xét/áp dụng; không trần năm điểm hoặc chờ đủ bốn ngôn ngữ. |
| FR-A03 | Vẽ/xem trước/lưu/sửa/áp dụng vùng — UC-A02 | Phù hợp A02.4/A02.6; giữ vùng hiện hành đến áp dụng, lần tải bản đồ tiếp theo; ranh giới/thao tác cụ thể chưa duyệt; phạm vi giữ tạm/khôi phục vùng đã chốt Q2. |
| FR-A04 | Văn bản/bản dịch/đối chiếu/nháp — UC-A03 | UI-AP04/UI-AP05 giữ bản khách chỉ đọc và phần tham chiếu trong cùng công việc; không sửa trực tiếp cặp đang phục vụ hoặc tự áp dụng. |
| FR-A05 | Chủ động tạo, nghe thử, trạng thái/thử lại — UC-A04 | Phù hợp A04.1/A04.2; không tự tạo mỗi lần lưu; R03 đã bổ sung liên kết AC-A13. |
| FR-A06 | Đúng bản, qua hết phiên, kết quả muộn — UC-A04/A06 | Phù hợp A04.3/A04.4; giữ bản đang phục vụ, không hồi sinh điểm đã xóa; R03 đã xử lý. |
| FR-A07 | Hồ sơ cặp và một thao tác áp dụng — UC-A05 | UI-04 hiện hành: chỉ cả cặp đủ hồ sơ/khớp, không hai nút/áp dụng nửa cặp. Lỗi xác định giữ cặp cũ, chưa rõ kiểm tra thực tế; runtime audio lỗi vẫn giữ D03. |
| FR-A08 | Xóa điều phối/FK/giao dịch, giữ lịch sử — UC-A06 | Phù hợp A02.1/A02.2; không ON DELETE CASCADE tại quan hệ cần chặn, không xóa lượt hàng loạt/gộp điểm trùng tên. |
| FR-A09 | Dọn file sau database, bảo vệ/chờ-thử lại — UC-A06 | Phù hợp A02.3/A04.4; R05 đã xử lý câu O-06 và điều kiện liên quan; R06 về file chưa từng áp dụng/lịch dọn còn mở. |
| FR-A10 | Một QR/URL chỉ đọc/PNG/bản in — UC-A07 | UI-05 chốt đúng hướng đơn giản, chưa URL thì chưa sẵn sàng/không QR giả; không QR riêng/quét/chuyển hướng hoặc tự báo in/lưu thành công. Cần kiểm tra bản thật. |
| FR-A11 | Phiên/box chi tiết, lọc và hai bảng-tổng — UC-A08 | UI-05 thay trang riêng UI-A21 bằng box, nền khóa/Đóng/cuộn/giữ lọc. Ngày trọn ngày Asia/Saigon, đếm theo đúng mốc/ngày/điểm/ngôn ngữ, không cộng phiên+lượt hoặc thêm chức năng. |
| FR-A12 | Mức đầy đủ theo điểm-ngôn ngữ và 20+20 — UC-A08/A05 | Phù hợp A03/D06/D09; đếm file không thay duyệt đúng bản và không tự tăng bộ chuẩn theo mọi điểm thêm. |
| FR-D01 | Tự xóa phiên/lượt sau 30 ngày — UC-D01 | Phù hợp D08/A02.2/A06 về yêu cầu; R04 đã sửa phép kết luận AC-D01; vận hành R06 còn mở. |

## 5. Bao phủ Use Case, NFR và tiêu chí

### 5.1. Use Case

| Mã | Phần đã kiểm tra | Kết luận |
| --- | --- | --- |
| UC-K01 | Tác nhân, vào QR/URL, kiểm tra tên/ngôn ngữ, tạo phiên, lỗi bắt đầu | Phù hợp; R02 đã giới hạn câu dẫn chung. |
| UC-K02 | Chọn vùng/ứng viên, dừng cũ, văn bản/lượt/audio, thiếu/lỗi/chọn liên tục | Ngoại lệ đầy đủ; R01 đã giới hạn câu chính và dẫn P06. |
| UC-K03 | Phát/Tạm dừng/tua và đổi ngôn ngữ | Phù hợp, không tự phát khi đổi ngôn ngữ. |
| UC-K04 | Phân biệt tải lại với thử lại sau lỗi, khôi phục/mạng/phiên | Phù hợp P03/P04, không yêu cầu offline hoặc gửi bù. |
| UC-K05 | 5 phút cuối, hết 8 tiếng, chủ động kết thúc/quay lại/xác nhận mới | Phù hợp; R02 đã giữ rõ quyền đọc văn bản sau hết hạn. |
| UC-A01 | Tài khoản/30 phút/Q2/UI-AP05 và hết phiên khi nghe thử | UI-04 bổ sung dừng nghe thử/che nội dung, không tự phát/gửi lại; không đổi mốc Admin hoặc ngoại lệ khách. |
| UC-A02 | CRUD, vùng mới/sửa, Lưu khác Áp dụng, bật hiện/ẩn | Phù hợp A02.4–A02.6 và Q1/Q2; thông tin/lưu ẩn/cảnh báo trùng và khôi phục vùng đã cụ thể hóa; ranh giới vẫn chưa được duyệt theo luồng. |
| UC-A03 | Nháp, giữ bản đang dùng, sửa bản duyệt cần rà soát lại | Phù hợp A03/D09/Q2: giữ tạm khác nháp/áp dụng, khôi phục đúng công việc, không tự dịch. |
| UC-A04 | Bốn trạng thái, chống lặp, đúng bản/ngôn ngữ, tác vụ qua hết phiên/xóa điểm | Phù hợp A04.1–A04.4; không bảo đảm nhà cung cấp hủy được yêu cầu. |
| UC-A05 | Hồ sơ cặp, cặp cũ/cặp mới và một nút áp dụng | UI-04 thay mô tả áp dụng văn bản trước audio; đủ D09 mới áp dụng cặp, giữ D03 runtime và trường hợp chỉ đổi audio dùng văn bản không đổi. |
| UC-A06 | Xác nhận xóa, phụ thuộc/giao dịch, giữ lịch sử, dọn file riêng | Phù hợp; không đồng nhất bản ghi với file, không làm mất dữ liệu dùng chung/hồ sơ tài liệu. |
| UC-A07 | QR chung/URL chỉ đọc/tải PNG/bản in gọn | Phù hợp UI-05/A05/D01-BS; giữ chưa URL thật/không QR giả, mở hộp in hoặc yêu cầu tải không là bằng chứng thành công. |
| UC-A08 | Mức đầy đủ, danh sách/box phiên/hai bảng, tải-rỗng-lỗi/lịch sử điểm xóa | UI-05 đồng bộ đúng phạm vi A06/D04/D08/A02.2; box không thêm quản lý phiên, không lọc mới/xuất báo cáo/kho vô hạn. |
| UC-D01 | Đến hạn, xóa tự động thực tế và phần giữ lại | Phù hợp nguồn; R04 đã sửa tiêu chí kết quả xóa; vận hành R06 còn mở. |

### 5.2. NFR

| Mã | Đối chiếu và giới hạn |
| --- | --- |
| NFR-01 | D01-BS/P07/P08/D04-BS; không thêm kích thước vùng/màn hình bắt buộc. Cần môi trường/thiết kế để kết luận “dễ phân biệt” theo R06. |
| NFR-02 | UI-04 bổ sung không lộ nửa cặp khi áp dụng, lỗi xác định giữ cặp cũ/chưa rõ kiểm tra thực tế; vẫn giữ toàn vẹn/lượt/phiên/xóa theo nguồn. |
| NFR-03 | Quyền và dữ liệu Q2/Q3/UI-04: hết phiên dừng nghe thử/che nội dung, không tự phát/gửi lại hoặc ngoại lệ khách; không chọn công nghệ/chính sách bảo mật mới. |
| NFR-04 | P03/P04/A01/A04/A02.3; phục hồi và báo đúng trạng thái có căn cứ, phạm vi bảo vệ chưa lưu đã chốt Q2, chỉ cơ chế còn chờ thiết kế. |
| NFR-05 | D02-BS/D03/D09; Unicode, ngôn ngữ, bằng chứng duyệt/giới hạn thẩm định được giữ; không dùng AI làm chuyên gia. |
| NFR-06 | D08/P04; trực tuyến và phục hồi lỗi, không biến audio đệm còn phát được thành chế độ offline. |

Không tìm thấy ngưỡng hiệu năng/dung lượng/số người đồng thời hoặc công nghệ chưa chốt bị dùng làm điều kiện bắt buộc. Các con số 1–50 ký tự, 8 tiếng, 5 phút, 30 phút và 30 ngày có căn cứ trong quyết định nguồn; 20 văn bản/20 audio thuộc bộ nội dung người dùng chọn.

### 5.3. Bao phủ đủ 50 tiêu chí đã đọc

| Nhóm AC | Số tiêu chí | Phần đã đối chiếu / kết luận |
| --- | --- | --- |
| AC-K01–K04 | 4 | QR/tên/bắt đầu/phiên-tab: phù hợp nguồn. |
| AC-K05–K09 | 5 | Vùng/chuyển/bên ngoài/chọn nhanh/chồng lấn: phù hợp nhánh nguồn; R01 đã sửa diễn đạt; R06 còn phép đánh giá theo thiết kế. |
| AC-K10–K11 | 2 | Thiếu dịch/audio, không tự chuyển/dùng sai bản: phù hợp. |
| AC-K12–K14 | 3 | Điều khiển/tua/bấm lại/đổi ngôn ngữ: phù hợp; AC-K13 là căn cứ đối chiếu R01. |
| AC-K15–K16 | 2 | Khôi phục và lỗi audio/mạng được phân biệt đúng; R06 không được đổi thành đòi chính xác từng giây. |
| AC-K17–K20 | 4 | Cảnh báo/hết hạn/quay lại/kết thúc: phù hợp, kể cả câu cảnh báo/P02 nguyên văn; R02 đã sửa câu dẫn chung. |
| AC-K21–K22 | 2 | Lượt xem, thiếu dịch và không lặp: phù hợp D04 và các P liên quan. |
| AC-A01–A02 | 2 | Đăng nhập/quyền/hết phiên: phù hợp; Q2/Q3 đã chốt và phân rã AC-A02; R06 còn cơ chế kỹ thuật. |
| AC-A03–A06 | 4 | CRUD, điều kiện hiện, vẽ và hiệu lực sửa vùng: Q1/Q2 đã cụ thể hóa trường/tên/mã/trùng/lưu ẩn/giữ tạm, thiết kế cụ thể còn R06. |
| AC-A07–A13 | 7 | Nháp, chủ động tạo, trạng thái/chưa rõ/đúng bản/qua hết phiên/nghe thử: phù hợp; R03 đã làm đủ đối ứng dẫn chiếu. |
| AC-A14–A15 | 2 | Hồ sơ cặp/đúng bản, một thao tác cả cặp/giữ cặp cũ khi lỗi; phân rã phép quan sát ở PRD 8.2.3. D03 runtime không bị bỏ. |
| AC-A16–A18 | 3 | Xóa/giao dịch/FK/lịch sử/kết quả muộn: phù hợp, không hứa chặn mọi quyền database hoặc luôn hủy dịch vụ ngoài. |
| AC-A19–A21 | 3 | Dọn file riêng/sau thành công/8 tiếng/lỗi-thử lại: phù hợp; file chưa từng áp dụng chờ thiết kế, không tự thêm mốc. |
| AC-A22–A24 | 3 | QR chung và thống kê/đọc/lọc/rỗng-lỗi: phù hợp, không suy người/lượt nghe. |
| AC-A25–A26 | 2 | Mức đầy đủ/bộ 20+20/duyệt đúng bản: phù hợp; việc có bản nháp không được tính như duyệt. |
| AC-D01 | 1 | Tự xóa 30 ngày: R04 đã làm rõ kết quả; R06 còn phần vận hành. |
| AC-D02 | 1 | Dữ liệu tối thiểu/quyền/giới hạn log-bản sao lưu: phù hợp; dẫn chiếu R03 đã xử lý. |
| **Tổng** | **50** | Đây là tổng số tiêu chí được đối chiếu trong bảng tài liệu; kết quả chạy phần mềm ở báo cáo kiểm thử riêng. |



## 6. Nguồn kiểm chứng hiện tại

[Hiện trạng](../01-tong-quan/handoff.md), [checklist](../01-tong-quan/roadmap.md) và [báo cáo kiểm thử](../06-kiem-thu/README.md) là nguồn chính để đọc kết quả đã đạt/chưa đạt. Nội dung đối chiếu yêu cầu ở trên không được dùng để tự tích thêm ô hoặc thay phê duyệt nội dung D09. Hồ sơ diễn biến dài và các bảng sửa câu chữ theo thời gian được bảo quản riêng.

<a id="review-open-final"></a>
<a id="review-approval-scope"></a>
<a id="review-data-05"></a>
