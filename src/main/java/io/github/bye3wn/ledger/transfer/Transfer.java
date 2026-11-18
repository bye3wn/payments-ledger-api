package io.github.bye3wn.ledger.transfer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** A completed movement of money between two accounts. Rows are never updated or deleted. */
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_account_id", nullable = false, updatable = false)
    private Long fromAccountId;

    @Column(name = "to_account_id", nullable = false, updatable = false)
    private Long toAccountId;

    @Column(name = "amount_cents", nullable = false, updatable = false)
    private long amountCents;

    @Column(nullable = false, length = 3, updatable = false)
    private String currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Transfer() {
        // required by JPA
    }

    public Transfer(Long fromAccountId, Long toAccountId, long amountCents, String currency) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amountCents = amountCents;
        this.currency = currency;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public long getAmountCents() { return amountCents; }
    public String getCurrency() { return currency; }
    public Instant getCreatedAt() { return createdAt; }
}
