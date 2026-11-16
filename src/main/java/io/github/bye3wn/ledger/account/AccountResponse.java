package io.github.bye3wn.ledger.account;

import java.time.Instant;

/** What the API returns. Kept separate from the entity so the database shape can change without breaking clients. */
public record AccountResponse(Long id, String ownerName, String currency, long balanceCents, Instant createdAt) {

    static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getOwnerName(), account.getCurrency(),
                account.getBalanceCents(), account.getCreatedAt());
    }
}
