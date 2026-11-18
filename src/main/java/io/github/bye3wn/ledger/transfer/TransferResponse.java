package io.github.bye3wn.ledger.transfer;

import java.time.Instant;

public record TransferResponse(Long id, Long fromAccountId, Long toAccountId, long amountCents,
                               String currency, Instant createdAt) {

    static TransferResponse from(Transfer t) {
        return new TransferResponse(t.getId(), t.getFromAccountId(), t.getToAccountId(),
                t.getAmountCents(), t.getCurrency(), t.getCreatedAt());
    }
}
