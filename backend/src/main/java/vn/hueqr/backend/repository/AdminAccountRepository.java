package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hueqr.backend.entity.AdminAccount;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, UUID> {
	Optional<AdminAccount> findByLoginName(String loginName);
}
