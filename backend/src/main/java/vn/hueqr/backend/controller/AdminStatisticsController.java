package vn.hueqr.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminStatisticsResponse;
import vn.hueqr.backend.service.AdminSessionService;
import vn.hueqr.backend.service.AdminStatisticsService;

@RestController
@RequestMapping("/api/v1/admin/statistics")
public class AdminStatisticsController {
	private final AdminStatisticsService statistics;
	private final AdminSessionService sessions;

	public AdminStatisticsController(AdminStatisticsService statistics, AdminSessionService sessions) {
		this.statistics = statistics;
		this.sessions = sessions;
	}

	@GetMapping
	public ResponseEntity<AdminStatisticsResponse> read(@RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate, HttpServletRequest request) {
		sessions.current(request);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(statistics.read(fromDate, toDate));
	}
}
