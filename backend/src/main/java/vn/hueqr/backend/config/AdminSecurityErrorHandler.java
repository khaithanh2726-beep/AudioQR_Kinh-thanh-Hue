package vn.hueqr.backend.config;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminErrorResponse;

@Component
public class AdminSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
	private final ObjectMapper json;

	public AdminSecurityErrorHandler(ObjectMapper json) {
		this.json = json;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException error)
			throws IOException {
		write(response, 401, "ADMIN_SESSION_EXPIRED");
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException error)
			throws IOException {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if ((authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken)
				&& !(request.getContextPath() + "/api/v1/auth/login").equals(request.getRequestURI())) {
			write(response, 401, "ADMIN_SESSION_EXPIRED");
			return;
		}
		write(response, 403, error instanceof CsrfException ? "CSRF_INVALID" : "FORBIDDEN");
	}

	public void write(HttpServletResponse response, int status, String code) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-store");
		json.writeValue(response.getOutputStream(), new AdminErrorResponse(code, "Yêu cầu không thể thực hiện.",
				Map.of(), false, UUID.randomUUID().toString()));
	}
}
