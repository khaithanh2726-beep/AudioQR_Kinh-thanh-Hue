package vn.hueqr.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CleanupFileStore {
	private final String root;

	public CleanupFileStore(@Value("${HUEQR_AUDIO_ROOT:}") String root) {
		this.root = root;
	}

	public Path path(String key) throws IOException {
		if (root.isBlank() || !Path.of(root).isAbsolute() || key == null || key.contains("/") || key.contains("\\")
				|| key.equals(".") || key.equals(".."))
			throw new IOException("CLEANUP_PATH_INVALID");
		Path directory = Path.of(root).toRealPath();
		Path result = directory.resolve(key).normalize();
		if (!directory.equals(result.getParent()))
			throw new IOException("CLEANUP_PATH_INVALID");
		return result;
	}

	public byte[] read(String key) throws IOException {
		Path file = path(key);
		if (!Files.readAttributes(file, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS).isRegularFile())
			throw new IOException("CLEANUP_FILE_UNSAFE");
		try (var input = Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS)) {
			return input.readAllBytes();
		}
	}

	// Chỉ NoSuchFileException xác nhận vắng file, lỗi quyền hoặc đường dẫn chưa rõ phải giữ trạng thái chờ
	public void remove(String key) throws IOException {
		Path file = path(key);
		try {
			if (!Files.readAttributes(file, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS).isRegularFile())
				throw new IOException("CLEANUP_FILE_UNSAFE");
			Files.delete(file);
			Files.readAttributes(file, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			throw new IOException("CLEANUP_FILE_STILL_PRESENT");
		}
		catch (NoSuchFileException absent) {
			// File đã vắng, có thể hoàn tất cả khi lần chạy trước dừng sau unlink
		}
	}
}
