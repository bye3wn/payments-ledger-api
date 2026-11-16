package io.github.bye3wn.ledger.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for accounts. Controllers stay thin; rules live here. */
@Service
public class AccountService {

    private final AccountRepository accounts;

    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional
    public Account open(CreateAccountRequest request) {
        return accounts.save(new Account(request.ownerName().trim(), request.currency(), request.initialBalanceCents()));
    }

    @Transactional(readOnly = true)
    public Account get(long id) {
        return accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
}
