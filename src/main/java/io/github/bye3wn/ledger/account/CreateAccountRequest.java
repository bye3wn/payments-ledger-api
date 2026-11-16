package io.github.bye3wn.ledger.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Body of POST /accounts. Bad input is rejected with 400 before it reaches the database. */
public record CreateAccountRequest(
        @NotBlank @Size(max = 100) String ownerName,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter ISO code like USD") String currency,
        @PositiveOrZero long initialBalanceCents) {
}
