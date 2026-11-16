package io.github.bye3wn.ledger.account;

import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data generates the SQL for save, findById, findAll and so on. */
public interface AccountRepository extends JpaRepository<Account, Long> {
}
