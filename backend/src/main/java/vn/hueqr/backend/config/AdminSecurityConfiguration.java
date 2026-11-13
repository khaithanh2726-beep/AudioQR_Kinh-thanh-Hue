package vn.hueqr.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import vn.hueqr.backend.service.AdminAccountDetailsService;
import vn.hueqr.backend.service.AdminSessionService;

@Configuration(proxyBeanMethods = false)
public class AdminSecurityConfiguration {
	@Bean
	PasswordEncoder adminPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	AuthenticationManager adminAuthenticationManager(AdminAccountDetailsService accounts, PasswordEncoder encoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(accounts);
		provider.setPasswordEncoder(encoder);
		return new ProviderManager(provider);
	}

	@Bean
	SecurityContextRepository adminSecurityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	HttpSessionCsrfTokenRepository adminCsrfRepository() {
		return new HttpSessionCsrfTokenRepository();
	}

	@Bean
	SessionAuthenticationStrategy adminSessionAuthenticationStrategy(HttpSessionCsrfTokenRepository csrf) {
		return new CompositeSessionAuthenticationStrategy(List.of(new ChangeSessionIdAuthenticationStrategy(),
				new CsrfAuthenticationStrategy(csrf)));
	}

	@Bean
	SecurityContextLogoutHandler adminLogoutHandler(SecurityContextRepository contexts) {
		SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
		handler.setSecurityContextRepository(contexts);
		return handler;
	}

	@Bean
	@Order(1)
	SecurityFilterChain adminSecurity(HttpSecurity http, SecurityContextRepository contexts,
			HttpSessionCsrfTokenRepository csrf, AdminSessionService sessions,
			SecurityContextLogoutHandler logout, AdminSecurityErrorHandler errors) throws Exception {
		http.securityMatcher("/api/v1/auth/**", "/api/v1/admin/**")
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/csrf", "/api/v1/auth/login")
						.permitAll().anyRequest().hasRole("ADMIN"))
				.securityContext(context -> context.securityContextRepository(contexts))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.csrf(config -> config.csrfTokenRepository(csrf))
				.exceptionHandling(config -> config.authenticationEntryPoint(errors).accessDeniedHandler(errors))
				.requestCache(config -> config.disable())
				.formLogin(config -> config.disable()).httpBasic(config -> config.disable())
				.logout(config -> config.disable());
		// Chỉ gắn filter vào chuỗi Admin, tránh Servlet tự đăng ký và chạy cả trên API khách
		http.addFilterAfter(new AdminSessionFilter(sessions, logout, errors), SecurityContextHolderFilter.class);
		return http.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain publicAndGuestSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(config -> config.ignoringRequestMatchers("/api/v1/guest/**"))
				.requestCache(config -> config.disable());
		return http.build();
	}
}
