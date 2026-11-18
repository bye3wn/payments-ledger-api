package io.github.bye3wn.ledger.transfer;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService service;

    public TransferController(TransferService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> create(@Valid @RequestBody CreateTransferRequest request) {
        Transfer transfer = service.transfer(request);
        return ResponseEntity.created(URI.create("/transfers/" + transfer.getId()))
                .body(TransferResponse.from(transfer));
    }

    @GetMapping("/{id}")
    public TransferResponse get(@PathVariable long id) {
        return TransferResponse.from(service.get(id));
    }
}
