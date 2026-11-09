package vn.hueqr.backend.service;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminSessionResponse;

@Service
public class AdminAuthService {
	private final AuthenticationManager authentication;
	private final SessionAuthenticationStrategy strategy;
	private final SecurityContextRepository contexts;
	private final AdminSessionService sessions;
	private final SecurityContextLogoutHandler logout;

	public AdminAuthService(AuthenticationManager authentication, SessionAuthenticationStrategy strategy,
			SecurityContextRepository contexts, AdminSessionService sessions, SecurityContextLogoutHandler logout) {
		this.authentication = authentication;
		this.strategy = strategy;
		this.contexts = contexts;
		this.sessions = sessions;
		this.logout = logout;
	}

	public AdminSessionResponse login(AdminLoginRequest input, HttpServletRequest request, HttpServletResponse response) {
		if (input.loginName() == null || input.loginName().isBlank() || input.password() == null || input.password().isEmpty())
			throw new BadCredentialsException("Thông tin đăng nhập không hợp lệ");

		var authenticated = authentication.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(input.loginName(), input.password()));
		// Controller đăng nhập phải tự đổi session id, xoay CSRF và lưu SecurityContext
		strategy.onAuthentication(authenticated, request, response);
		AdminSessionResponse result = sessions.start(request, UUID.fromString(authenticated.getName()));
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authenticated);
		SecurityContextHolder.setContext(context);
		contexts.saveContext(context, request, response);
		return result;
	}

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		logout.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
	}
}
