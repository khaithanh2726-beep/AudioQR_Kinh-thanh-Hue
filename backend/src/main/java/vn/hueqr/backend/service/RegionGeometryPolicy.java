package vn.hueqr.backend.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class RegionGeometryPolicy {
	public static final String MAP_SPACE_KEY = "WGS84_LON_LAT";

	public record Geometry(String polygon, String mapSpaceKey, String geometryHash) {
	}

	private record Point(BigDecimal x, BigDecimal y) {
	}

	private final ObjectMapper json;

	public RegionGeometryPolicy(ObjectMapper json) {
		this.json = json;
	}

	public Geometry validate(JsonNode polygon, String mapSpaceKey, String previewedHash) {
		if (!MAP_SPACE_KEY.equals(mapSpaceKey))
			throw invalid("mapSpaceKey", "Cần không gian tọa độ WGS84_LON_LAT.");
		if (polygon == null || !polygon.isObject() || polygon.size() != 2
				|| !polygon.has("type") || !polygon.get("type").isString()
				|| !"Polygon".equals(polygon.get("type").asString()) || !polygon.has("coordinates"))
			throw invalid("polygon", "Cần hình Polygon gồm type và coordinates.");

		JsonNode rings = polygon.get("coordinates");
		if (!rings.isArray() || rings.size() != 1 || !rings.get(0).isArray() || rings.get(0).size() < 4)
			throw invalid("polygon", "Cần một vòng kín có ít nhất ba đỉnh, không có lỗ hoặc nhiều mảnh.");
		List<Point> points = new ArrayList<>();
		for (JsonNode position : rings.get(0)) {
			if (!position.isArray() || position.size() != 2)
				throw invalid("polygon", "Mỗi đỉnh cần đúng hai tọa độ kinh độ, vĩ độ.");
			points.add(new Point(coordinate(position.get(0), 180), coordinate(position.get(1), 90)));
		}
		if (!points.getFirst().equals(points.getLast()))
			throw invalid("polygon", "Hình phải được khép trước khi lưu.");
		List<Point> vertices = points.subList(0, points.size() - 1);
		if (new HashSet<>(vertices).size() != vertices.size())
			throw invalid("polygon", "Các đỉnh trong vòng không được trùng nhau.");
		checkSimple(vertices);

		String coordinates = points.stream().map(point -> "[" + point.x().toPlainString() + ","
				+ point.y().toPlainString() + "]").collect(Collectors.joining(","));
		String canonical = "{\"type\":\"Polygon\",\"coordinates\":[[" + coordinates + "]]}";
		String hash = hash(mapSpaceKey + "\n" + canonical);
		if (!matches(hash, previewedHash, "geometryHash"))
			throw new AdminCommandException(409, "GEOMETRY_HASH_MISMATCH");
		return new Geometry(canonical, mapSpaceKey, hash);
	}

	public boolean matches(String expected, String actual, String field) {
		if (actual == null || !actual.matches("[0-9a-f]{64}"))
			throw invalid(field, "Cần mã Hash SHA-256 gồm 64 ký tự hex chữ thường.");
		return MessageDigest.isEqual(HexFormat.of().parseHex(expected), HexFormat.of().parseHex(actual));
	}

	// Sắp khóa JSON và chuẩn hóa cách viết số để định dạng JSON không tạo fingerprint khác
	String canonicalJson(JsonNode value) {
		if (value == null || value.isNull())
			return "null";
		if (value.isObject()) {
			Map<String, JsonNode> fields = new TreeMap<>();
			value.properties().forEach(entry -> fields.put(entry.getKey(), entry.getValue()));
			return fields.entrySet().stream().map(entry -> json.writeValueAsString(entry.getKey()) + ":"
					+ canonicalJson(entry.getValue())).collect(Collectors.joining(",", "{", "}"));
		}
		if (value.isArray()) {
			List<String> items = new ArrayList<>();
			value.forEach(item -> items.add(canonicalJson(item)));
			return "[" + String.join(",", items) + "]";
		}
		if (value.isNumber() && Double.isFinite(value.doubleValue()))
			return value.decimalValue().stripTrailingZeros().toString();
		return json.writeValueAsString(value);
	}

	private BigDecimal coordinate(JsonNode value, int bound) {
		if (value == null || !value.isNumber() || !Double.isFinite(value.doubleValue()))
			throw invalid("polygon", "Tọa độ phải là số hữu hạn.");
		BigDecimal number = value.decimalValue().stripTrailingZeros();
		if (number.abs().compareTo(BigDecimal.valueOf(bound)) > 0)
			throw invalid("polygon", "Kinh độ phải trong [-180, 180], vĩ độ trong [-90, 90].");
		return number;
	}

	private void checkSimple(List<Point> vertices) {
		BigDecimal area = BigDecimal.ZERO;
		for (int i = 0; i < vertices.size(); i++) {
			Point a = vertices.get(i);
			Point b = vertices.get((i + 1) % vertices.size());
			Point c = vertices.get((i + 2) % vertices.size());
			area = area.add(a.x().multiply(b.y()).subtract(b.x().multiply(a.y())));
			// Không dùng dung sai hay tự sửa đỉnh, chặn cả cạnh kề quay ngược chồng lên nhau
			if (orientation(a, b, c) == 0 && a.x().subtract(b.x()).multiply(c.x().subtract(b.x()))
					.add(a.y().subtract(b.y()).multiply(c.y().subtract(b.y()))).signum() >= 0)
				throw invalid("polygon", "Các cạnh của vùng không được chồng lên nhau.");
			for (int j = i + 2; j < vertices.size(); j++) {
				if (i == 0 && j == vertices.size() - 1)
					continue;
				if (intersects(a, b, vertices.get(j), vertices.get((j + 1) % vertices.size())))
					throw invalid("polygon", "Vùng không được tự cắt hoặc tự chạm.");
			}
		}
		if (area.signum() == 0)
			throw invalid("polygon", "Các đỉnh phải tạo thành một vùng có diện tích khác không.");
	}

	private boolean intersects(Point a, Point b, Point c, Point d) {
		int abc = orientation(a, b, c);
		int abd = orientation(a, b, d);
		int cda = orientation(c, d, a);
		int cdb = orientation(c, d, b);
		return abc == 0 && onSegment(a, b, c) || abd == 0 && onSegment(a, b, d)
				|| cda == 0 && onSegment(c, d, a) || cdb == 0 && onSegment(c, d, b)
				|| abc * abd < 0 && cda * cdb < 0;
	}

	private int orientation(Point a, Point b, Point c) {
		return b.x().subtract(a.x()).multiply(c.y().subtract(a.y()))
				.subtract(b.y().subtract(a.y()).multiply(c.x().subtract(a.x()))).signum();
	}

	private boolean onSegment(Point a, Point b, Point point) {
		return point.x().compareTo(a.x().min(b.x())) >= 0 && point.x().compareTo(a.x().max(b.x())) <= 0
				&& point.y().compareTo(a.y().min(b.y())) >= 0 && point.y().compareTo(a.y().max(b.y())) <= 0;
	}

	private String hash(String value) {
		try {
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
		}
		catch (NoSuchAlgorithmException error) {
			throw new IllegalStateException("SHA-256 unavailable", error);
		}
	}

	private AdminCommandException invalid(String field, String message) {
		return new AdminCommandException(422, "VALIDATION_FAILED", Map.of(field, message));
	}
}
