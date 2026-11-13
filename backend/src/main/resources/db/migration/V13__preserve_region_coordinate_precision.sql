-- Giữ nguyên số thập phân đã dùng tính Hash, tránh JSON nhị phân MySQL đổi tọa độ khi lưu
-- Bản vùng cũ giữ nguyên dữ liệu đang có; không tự sửa đỉnh hoặc tính lại Hash đã ghi
ALTER TABLE region_revision
    MODIFY COLUMN polygon LONGTEXT NOT NULL,
    ADD CONSTRAINT ck_region_polygon_json CHECK (JSON_VALID(polygon));
