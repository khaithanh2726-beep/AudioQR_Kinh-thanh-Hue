-- Chạy riêng có chủ đích, không nằm trong đường migration tự động của ứng dụng
-- Chỉ tạo điểm ẩn và ngăn trống, không sinh vùng, văn bản, audio hoặc hồ sơ duyệt
-- Id cố định nhận biết dữ liệu khởi tạo, không dùng tên để gộp địa điểm đã tồn tại
START TRANSACTION;

INSERT INTO place (id, display_name, visible, created_at, updated_at)
SELECT seed.id, seed.display_name, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM (
    SELECT UNHEX('039d5ff2da6b5283b7ed5196dd4dcb2e') AS id, 'Cổng Ngọ Môn' AS display_name
    UNION ALL
    SELECT UNHEX('686677315be9592fbf283e3f0e81e4ef') AS id, 'Điện Thái Hòa' AS display_name
    UNION ALL
    SELECT UNHEX('5a3bc1b07e12593e9570ce3ed726dbae') AS id, 'Tử Cấm Thành' AS display_name
    UNION ALL
    SELECT UNHEX('0ccc056f62035da3b4b318613615ea91') AS id, 'Cửu Đỉnh' AS display_name
    UNION ALL
    SELECT UNHEX('9b30736e75fd5c62916ead8e2b11747a') AS id, 'Cung Diên Thọ' AS display_name
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM place AS existing WHERE existing.id = seed.id);

INSERT INTO content_slot (id, place_id, language)
SELECT seed.id, seed.place_id, seed.language
FROM (
    SELECT UNHEX('405738245e9f52ac8c27a14101bdc07b') AS id, UNHEX('039d5ff2da6b5283b7ed5196dd4dcb2e') AS place_id, 'vi' AS language
    UNION ALL
    SELECT UNHEX('d2ac4106fee855dcaf26c19ad5175bc2') AS id, UNHEX('039d5ff2da6b5283b7ed5196dd4dcb2e') AS place_id, 'en' AS language
    UNION ALL
    SELECT UNHEX('3d4f518a2d8753ebafe659c2a8d81ba7') AS id, UNHEX('039d5ff2da6b5283b7ed5196dd4dcb2e') AS place_id, 'fr' AS language
    UNION ALL
    SELECT UNHEX('ade0abb5c2c350f49b246fbb7dff1d62') AS id, UNHEX('039d5ff2da6b5283b7ed5196dd4dcb2e') AS place_id, 'ru' AS language
    UNION ALL
    SELECT UNHEX('e791b40dfb6e5e35a2a40350a5088177') AS id, UNHEX('686677315be9592fbf283e3f0e81e4ef') AS place_id, 'vi' AS language
    UNION ALL
    SELECT UNHEX('811b85c5449050da9c9a5751a38b2219') AS id, UNHEX('686677315be9592fbf283e3f0e81e4ef') AS place_id, 'en' AS language
    UNION ALL
    SELECT UNHEX('75f22b84f48a54b9ab380cae4c118f5d') AS id, UNHEX('686677315be9592fbf283e3f0e81e4ef') AS place_id, 'fr' AS language
    UNION ALL
    SELECT UNHEX('4f2eb231be615f72a3fb4c89ac7050fb') AS id, UNHEX('686677315be9592fbf283e3f0e81e4ef') AS place_id, 'ru' AS language
    UNION ALL
    SELECT UNHEX('e0ca51466ed95c8cbeccdbbb63d069c8') AS id, UNHEX('5a3bc1b07e12593e9570ce3ed726dbae') AS place_id, 'vi' AS language
    UNION ALL
    SELECT UNHEX('79c71bf8633d5b5d85e5b11bdd5339c2') AS id, UNHEX('5a3bc1b07e12593e9570ce3ed726dbae') AS place_id, 'en' AS language
    UNION ALL
    SELECT UNHEX('7fefe770417b597f95392b1f5b26b1e6') AS id, UNHEX('5a3bc1b07e12593e9570ce3ed726dbae') AS place_id, 'fr' AS language
    UNION ALL
    SELECT UNHEX('92bd553305925a78b79dc09842ec2056') AS id, UNHEX('5a3bc1b07e12593e9570ce3ed726dbae') AS place_id, 'ru' AS language
    UNION ALL
    SELECT UNHEX('321568e272a05ad2bec15fcb1823326e') AS id, UNHEX('0ccc056f62035da3b4b318613615ea91') AS place_id, 'vi' AS language
    UNION ALL
    SELECT UNHEX('5e4523d5830057eb8b842beb2425fd2d') AS id, UNHEX('0ccc056f62035da3b4b318613615ea91') AS place_id, 'en' AS language
    UNION ALL
    SELECT UNHEX('4507bd4e858e53df86b7e84a9cf13b52') AS id, UNHEX('0ccc056f62035da3b4b318613615ea91') AS place_id, 'fr' AS language
    UNION ALL
    SELECT UNHEX('a85b1f7ce448575d81c47461400d43e7') AS id, UNHEX('0ccc056f62035da3b4b318613615ea91') AS place_id, 'ru' AS language
    UNION ALL
    SELECT UNHEX('a1ae910e066d5394b79bd73ee86a592d') AS id, UNHEX('9b30736e75fd5c62916ead8e2b11747a') AS place_id, 'vi' AS language
    UNION ALL
    SELECT UNHEX('23411ab055a85caf97ec3476ba134ea6') AS id, UNHEX('9b30736e75fd5c62916ead8e2b11747a') AS place_id, 'en' AS language
    UNION ALL
    SELECT UNHEX('9737906cce08536c97f5b04503386792') AS id, UNHEX('9b30736e75fd5c62916ead8e2b11747a') AS place_id, 'fr' AS language
    UNION ALL
    SELECT UNHEX('9ef3b6a94ebe5486bfddf7f5a2f6783d') AS id, UNHEX('9b30736e75fd5c62916ead8e2b11747a') AS place_id, 'ru' AS language
) AS seed
WHERE NOT EXISTS (
    SELECT 1 FROM content_slot AS existing
    WHERE existing.place_id = seed.place_id AND existing.language = seed.language
);

COMMIT;
