package pay.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pay.domain.model.DepositHistory;

import java.util.UUID;

@Repository
public interface DepositHistoryRepository extends JpaRepository<DepositHistory, UUID> {
}
