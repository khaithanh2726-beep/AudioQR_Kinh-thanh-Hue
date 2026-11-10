package vn.hueqr.backend.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.hueqr.backend.service.AdminSessionException;
import vn.hueqr.backend.service.AdminSessionService;

public class AdminSessionFilter extends OncePerRequestFilter {
	private final AdminSessionService sessions;
	private final SecurityContextLogoutHandler logout;
	private final AdminSecurityErrorHandler errors;

	public AdminSessionFilter(AdminSessionService sessions, SecurityContextLogoutHandler logout,
			AdminSecurityErrorHandler errors) {
		this.sessions = sessions;
		this.logout = logout;
		this.errors = errors;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			try {
				sessions.current(request);
			}
			catch (AdminSessionException | IllegalStateException error) {
				logout.logout(request, response, authentication);
				// Sau hết hạn vẫn cho lấy CSRF của ngữ cảnh mới để đăng nhập lại
				if (!"GET".equals(request.getMethod())
						|| !(request.getContextPath() + "/api/v1/auth/csrf").equals(request.getRequestURI())) {
					errors.write(response, 401, "ADMIN_SESSION_EXPIRED");
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}
}
