package vn.hueqr.backend.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;

public interface Mp3Encoder {
	void encode(Path wav, OutputStream output) throws IOException;

	BigDecimal inspect(Path mp3) throws IOException;
}
