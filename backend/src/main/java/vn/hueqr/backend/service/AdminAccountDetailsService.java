package vn.hueqr.backend.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.hueqr.backend.repository.AdminAccountRepository;

@Service
public class AdminAccountDetailsService implements UserDetailsService {
	private final AdminAccountRepository accounts;

	public AdminAccountDetailsService(AdminAccountRepository accounts) {
		this.accounts = accounts;
	}

	@Override
	public UserDetails loadUserByUsername(String loginName) {
		var account = accounts.findByLoginName(loginName)
				.orElseThrow(() -> new UsernameNotFoundException("Thông tin đăng nhập không hợp lệ"));
		// Principal giữ id ổn định, không dùng tên đăng nhập làm định danh tài khoản trong phiên
		return User.withUsername(account.getId().toString()).password(account.getPasswordHash())
				.disabled(!account.isEnabled()).roles("ADMIN").build();
	}
}
