package vn.hueqr.backend.config;

import java.io.IOException;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.hueqr.backend.service.OperationsGateService;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class OperationsGateFilter extends OncePerRequestFilter {

	private final OperationsGateService gate;

	public OperationsGateFilter(OperationsGateService gate) {
		this.gate = gate;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		// Container đã giải mã servletPath; URI thô có thể dùng /%61pi để đi vào cùng API.
		String path = request.getServletPath();
		if (!path.isEmpty())
			return !path.startsWith("/api/");
		return !request.getRequestURI().startsWith(request.getContextPath() + "/api/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		var lease = gate.enter("HTTP");
		if (lease == null) {
			response.setStatus(503);
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Retry-After", "30");
			response.getWriter().write("{\"code\":\"SERVICE_UNAVAILABLE\",\"fieldErrors\":{}}");
			return;
		}
		try {
			chain.doFilter(request, response);
		}
		finally {
			if (!request.isAsyncStarted())
				lease.close();
			else {
				// Timeout/lỗi chưa chứng minh writer đã kết thúc; chỉ tháo lease khi complete.
				var listener = new AsyncListener() {
					@Override public void onComplete(AsyncEvent event) { lease.close(); }
					@Override public void onTimeout(AsyncEvent event) { }
					@Override public void onError(AsyncEvent event) { }
					@Override public void onStartAsync(AsyncEvent event) { event.getAsyncContext().addListener(this); }
				};
				try {
					request.getAsyncContext().addListener(listener);
				}
				catch (IllegalStateException completed) {
					// Complete xảy ra trước khi gắn listener; servlet đã kết thúc request.
					lease.close();
				}
			}
		}
	}
}
