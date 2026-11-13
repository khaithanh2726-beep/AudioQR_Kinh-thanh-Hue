# Khóa bảo trì và backup/restore tách biệt — 13/11/2026

## Phạm vi

Triển khai V14 `ops_guard`/`ops_activity`, repository/service nhận lease bền, filter API và gate cho bốn scheduler. HTTP bị bảo trì dùng `503 SERVICE_UNAVAILABLE` đã có, không thêm endpoint/DTO vận hành. Khởi động lại không tự mở gate hoặc xóa lease; không dùng timeout để chứng minh worker đã kết thúc. [Hướng dẫn công cụ](../../07-van-hanh/bao-tri-va-sao-luu.md) ghi điều kiện và giới hạn.

Ảnh cuối `hueqr-local:20261113-ops-guard-urls`, ID `sha256:1e77e1e14ff8a8f4975c8efb16ffbc6ba108e7a884c681c0cbf9c55ed1798488`, đã build qua Dockerfile hiện hành, frontend 71 ca logic và build thành công. Không thay phiên bản phụ thuộc hoặc cài công cụ hệ thống. Bộ thử được tạo từ snapshot provider đã dừng, chứa năm điểm ẩn, 20 slot, một bản văn và một audio thật, không có cặp áp dụng. Không dùng dữ liệu Ngọ Môn đã duyệt/hiển thị làm nơi chạy fixture. Ảnh thử `20261113-ops-guard` chỉ giữ cho bằng chứng trước sửa URL, không phải ảnh được đề nghị áp dụng.

## Kết quả thực chạy

**Kết luận lượt cuối:** một lần Maven verify sau sửa URL đạt **530/530 ca**, 0 failure/error/skipped, exit 0 trong 17 phút 01 giây. [Result](result.json) và [tổng hợp 51 suite](backend-suite-summary.json) đối chiếu đủ 530 ca độc lập. Chạy hai fork, mỗi fork heap tối đa 1.536 MiB và cache hai context; không đổi dependency hoặc ngưỡng nghiệp vụ. Không cộng các lượt sơ bộ/nhóm riêng vào tổng này.

- Nhóm backend sơ bộ trước sửa URL: 36/36 ca đạt, gồm 7 ca gate, 17 retention, 11 schema và một upgrade precision. Sau bổ sung ca URL mã hóa, nhóm gate cuối đạt 8/8. Có kiểm tranh chấp admission/QUIESCING trên MySQL, chặn API trước xác thực, giữ activity tới khi kết thúc, không tháo lease cũ theo tuổi, giữ FROZEN khi tạo lại service.
- Manifest backup: 8/8 ca Node đạt; kiểm bản đầy đủ, đúng hạn bảy ngày, từ chối gói FAILED/hết hạn, dump hỏng, thiếu audio, trùng key, sai định dạng/path traversal và thư mục liên kết.
- [Chu trình CLI thật](maintenance-rehearsal-result.json): chặn freeze khi còn lease fixture, từ chối owner khác, không tháo lease theo tuổi; freeze sau drain; dump/catalog/copy/Hash đủ hai file; mở lại giữ nguyên khóa/epoch.
- [Restore riêng](restore-result.json): schema/volume mới, ảnh nguồn khớp ID, đọc lại Hash hai file từ volume đích, epoch mới, API khởi động dưới gate FROZEN. Sau đó kiểm mở lại qua công cụ. Bốn fixture phiên/lượt được thêm vào **đích sau import**, lọc ba ca quá hạn và giữ nguyên timestamps ca còn hạn; không diễn giải số dòng cuối là nguyên snapshot.
- [Chrome sau restore](browser-result.json): đăng nhập Admin, đọc văn bản, tải/phát đúng MP3 Hash `b236c64424078f960a7dc164d17294b804fd1ff5ffbb800b1f2f988165011354`, không tạo TTS/duyệt/áp dụng. Một ca đạt; tile fixture, không phải Safari/thiết bị thật. [Ảnh](restored-admin-audio.png).
- Một lượt full trước sửa URL đạt **529/529 ca**, 0 failure/error/skipped, Maven verify BUILD SUCCESS trong 19 phút 11 giây. Đây là lượt Windows với MySQL Testcontainers thật, tách với CI Linux 520 ca của nguồn cũ. [Kết quả sơ bộ](result-before-url-fix.json) giữ riêng, không thay kết quả cuối của nguồn sau sửa; không cộng nhóm vào full.
- [Khóa chống lệnh chồng và dung lượng](mutex-and-capacity-result.json): cùng owner vẫn bị chặn khi mutex đang giữ; backup sau tháo đúng khóa fixture đã COMPLETE với hai tệp. Bước backup đo 5.402 ms trên bộ nhỏ; không coi là downtime tổng/RTO hoặc kết quả cho dữ liệu lớn.
- [Quyền qua backup/restore](recovery-rights-result.json): phiên khách thật giữ ID/started/expires/endedAt sau backup thường; restore chặn credential cũ bằng 401 GUEST_CREDENTIAL_INVALID và từ chối vé cũ, không gán endedAt giả cho lịch sử được giữ, vé/credential mới tạo phiên mới thành công. [Các lượt fixture](recovery-rights-attempts.json) giữ riêng ba lần chưa đạt do sai key/kỳ vọng hoặc lỗi khai báo trong fixture và lần cuối đạt; không sửa backend để ép kết quả. Đây là bộ restore riêng khác với bộ dùng ảnh Chrome ở trên, cùng ID ảnh ứng dụng.
- [URL trước sửa](normalization-before.json): API thường trả 503 nhưng `/%61pi/...` trả 200 khi QUIESCING. Đã sửa filter dùng servletPath được container giải mã. [Kiểm HTTP sau sửa](url-gate-result.json): khi đóng, cả hai đường trả 503; khi mở, cả hai trả 200. Cùng lượt kiểm đã backup/restore trên ảnh cuối, đối chiếu hai tệp và xác nhận worker vẫn tắt dù shell cố bật cờ.
- [Chrome ảnh cuối](browser-after-url-fix.json): một ca đăng nhập/đọc/nghe sau restore đạt, Hash MP3 giữ nguyên; [ảnh](restored-admin-after-url-fix.png). [Kết quả restore ảnh cuối](final-restore-result.json) tách với bộ sơ bộ. Không suy viewport/tile fixture thành kiểm Safari hoặc điện thoại thật.

Lượt full cuối sau sửa URL được ghi riêng trong [result.json](result.json). Lượt full sau sửa đầu tiên được chủ động dừng để chỉnh cách dựng request mã hóa trong fixture MockMvc, chưa tới kết luận toàn bộ; không tính lượt dừng là pass. Lượt được dùng làm kết luận phải có exit 0 và tổng Surefire tương ứng.

Một lượt thực thi tiếp theo bị gián đoạn trước tổng kết cũng không được tính là full đạt. Lượt mới ngày 13/11 kết thúc với 530/530 như kết luận trên. XML/log đầy đủ giữ ngoài repo; báo cáo chia rõ các lần thực thi.

## Scheduler trong bản sao riêng

[Kết quả scheduler](scheduler-result.json) bật **chỉ cleanup/retention** trên một bản restore riêng, provider/local TTS vẫn tắt. Khi gate đóng, chờ ba khoảng 25 giây để vượt một nhịp mặc định 60 giây: cả bốn phiên fixture và file orphan vẫn còn. Sau mở gate, scheduler thật xóa ba phiên quá hạn, giữ đúng mốc phiên còn hạn; asset đủ hạn đạt DELETED và tệp thực sự không còn. Asset có last_unapplied_at cách bảy giờ được giữ, hai tệp audio thật giữ nguyên Hash và dữ liệu phiên không thuộc fixture không bị mất.

Hai file synthetic chỉ dùng kiểm cleaner, không có audio revision/cặp và không được coi là thuyết minh thật. Cấu hình worker được trả về giá trị ban đầu; container ứng dụng đã được recreate ở trạng thái dừng để lần docker start sau cũng không dùng lại cờ thử. Không đổi hạn 8 giờ/24 giờ/30 ngày hoặc tự bật worker bản Ngọ Môn.

Snapshot, cấu hình, dump, mật khẩu và audio nằm ngoài repo với ACL riêng. Hồ sơ này chỉ sao chép JSON/ảnh đã chọn lọc. Các bộ app/database thử đã được dừng sau kiểm; giữ volume/bản sao để rà, không prune hoặc xóa dữ liệu cũ. Không gọi provider, không mở tunnel, không áp V14 lên bản Ngọ Môn đang giữ schema V13.

## Đầu vào còn thiếu

[Preflight CD](cd-preflight.json): có SSH client và Docker context desktop-linux; không thấy OpenSSH Server/service, chưa tìm thấy Tailscale trong PATH. Chưa kiểm đăng nhập từ xa/host key hoặc quyền Docker của tài khoản CD, chưa triển khai từ workflow.

[Đối chiếu nội dung](content-readiness.json): 20 bài nháp, bốn bộ audio vi cũ còn đúng Hash; chỉ cặp Ngọ Môn trong hồ sơ riêng được chấp nhận cho tập dượt. Ba audio vi khác có thể nghe đối chiếu, nhưng chưa có đường nhập registry đã nghiệm thu. [Danh sách cần duyệt](../../05-noi-dung/san-sang-noi-dung-20261113.md) ghi kế hoạch 19 bài TTS theo hợp đồng hiện hành; không lấy khả năng tái sử dụng file ngoài để tự mở rộng API.

Android đã có xác nhận nghe thành công ở mốc trước. Chưa có thiết bị iOS nên Safari thật vẫn chưa kiểm. Chưa hoàn tất lịch backup/dọn backup, xử lý mọi claim/lease do crash, cập nhật V13→V14 và bật scheduler trên bản phục vụ hoặc nghiệm thu toàn bộ 20 cặp. Kiểm scheduler ở đây chỉ xác nhận trên bản sao riêng. Không tự tích thêm checklist, commit/push hay thay Project trong lượt này; dừng rà sản phẩm và bằng chứng trước khi chuyển mốc.

## Nguồn và mốc đối chiếu

[Danh sách tệp/Hash](source-file-inventory.json) so với tag `checkpoint-20261112-https-ngo-mon` ghi tệp mới/sửa, Git blob và SHA-256 bytes hiện tại; loại trừ chính tệp inventory để tránh vòng Hash. Sáu nháp T08 được đối chiếu nguyên trạng, index chưa stage; không đưa runtime hoặc dump vào Git. [Bản tập dượt ổn định đã mở lại local](stable-runtime-resume.json) vẫn schema V13, một điểm/cặp hiển thị, cùng ảnh/khóa/epoch đã lưu và bốn worker tắt; không tự mở tunnel hoặc áp V14 lên bản đó.
