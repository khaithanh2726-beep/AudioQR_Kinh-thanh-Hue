package vn.hueqr.backend.entity;

import java.sql.Types;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

// Tài khoản quản trị được cấp trước, lưu mã Hash mật khẩu và trạng thái cho phép đăng nhập
@Entity
@Table(name = "admin_account")
public class AdminAccount {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id ổn định của tài khoản quản trị

	@Column(name = "login_name", nullable = false, updatable = false, length = 255)
	private String loginName; // Tên đăng nhập phân biệt hoa thường và dấu

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash; // Mã Hash mật khẩu kèm định danh thuật toán của PasswordEncoder

	@Column(name = "enabled", nullable = false)
	private boolean enabled; // Trạng thái cho phép tài khoản truy cập quản trị

	protected AdminAccount() {
	}

	public AdminAccount(UUID id, String loginName, String passwordHash, boolean enabled) {
		this.id = id;
		this.loginName = loginName;
		this.passwordHash = passwordHash;
		this.enabled = enabled;
	}

	public UUID getId() {
		return id;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
