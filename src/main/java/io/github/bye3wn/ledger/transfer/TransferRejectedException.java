package io.github.bye3wn.ledger.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The request was well-formed but breaks a business rule (not enough money, different
 * currencies, same account on both sides). 422 tells the client "understood, but no".
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class TransferRejectedException extends RuntimeException {

    public TransferRejectedException(String message) {
        super(message);
    }
}
