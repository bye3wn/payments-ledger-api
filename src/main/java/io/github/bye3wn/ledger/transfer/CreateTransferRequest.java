package io.github.bye3wn.ledger.transfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Body of POST /transfers. */
public record CreateTransferRequest(
        @NotNull Long fromAccountId,
        @NotNull Long toAccountId,
        @Positive long amountCents) {
}
