package io.github.bye3wn.ledger.transfer;

import io.github.bye3wn.ledger.account.Account;
import io.github.bye3wn.ledger.account.AccountNotFoundException;
import io.github.bye3wn.ledger.account.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository accounts;
    private final TransferRepository transfers;

    public TransferService(AccountRepository accounts, TransferRepository transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }

    /**
     * Moves money in ONE database transaction: the withdrawal, the deposit and the transfer
     * record all commit together or not at all. If anything throws, Postgres rolls back and
     * neither balance changes.
     */
    @Transactional
    public Transfer transfer(CreateTransferRequest request) {
        long fromId = request.fromAccountId();
        long toId = request.toAccountId();
        if (fromId == toId) {
            throw new TransferRejectedException("Cannot transfer from an account to itself");
        }

        // Always lock the lower id first. If transfer A->B and B->A run at the same time and
        // each locked its "from" account first, they would wait on each other forever (deadlock).
        Account first = lock(Math.min(fromId, toId));
        Account second = lock(Math.max(fromId, toId));
        Account from = first.getId() == fromId ? first : second;
        Account to = from == first ? second : first;

        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new TransferRejectedException("Accounts use different currencies ("
                    + from.getCurrency() + " and " + to.getCurrency() + ")");
        }
        if (from.getBalanceCents() < request.amountCents()) {
            throw new TransferRejectedException("Insufficient funds in account " + fromId);
        }

        from.withdraw(request.amountCents());
        to.deposit(request.amountCents());
        // No explicit save for the accounts: they are managed by JPA, so their new balances
        // are written when the transaction commits.
        return transfers.save(new Transfer(fromId, toId, request.amountCents(), from.getCurrency()));
    }

    @Transactional(readOnly = true)
    public Transfer get(long id) {
        return transfers.findById(id).orElseThrow(() -> new TransferNotFoundException(id));
    }

    private Account lock(long id) {
        return accounts.findByIdForUpdate(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
}
