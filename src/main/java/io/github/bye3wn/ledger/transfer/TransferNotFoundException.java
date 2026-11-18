package io.github.bye3wn.ledger.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransferNotFoundException extends RuntimeException {

    public TransferNotFoundException(long id) {
        super("Transfer " + id + " not found");
    }
}
