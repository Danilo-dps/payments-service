package pay.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pay.domain.model.TransferHistory;

import java.util.UUID;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, UUID> {
}
