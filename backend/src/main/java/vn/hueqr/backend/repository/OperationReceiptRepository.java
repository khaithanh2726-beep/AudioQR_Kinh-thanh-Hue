package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hueqr.backend.entity.OperationReceipt;

public interface OperationReceiptRepository extends JpaRepository<OperationReceipt, UUID> {
	Optional<OperationReceipt> findByAdminAccountIdAndWindowIdAndOperationKey(UUID accountId, UUID windowId, UUID operationKey);
}
