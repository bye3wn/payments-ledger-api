package io.github.bye3wn.ledger.account;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/** Spring Data generates the SQL for save, findById, findAll and so on. */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Loads an account and locks its row (SELECT ... FOR UPDATE) until the transaction ends,
     * so two transfers touching the same account run one after the other instead of both
     * reading the same old balance.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(Long id);
}
