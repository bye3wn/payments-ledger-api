package io.github.bye3wn.ledger.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

/**
 * A customer account holding a balance in one currency.
 * Balances are whole cents (a long), never doubles, to avoid rounding errors.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "balance_cents", nullable = false)
    private long balanceCents;

    /** Optimistic-locking counter: Hibernate bumps it on every update. Used for concurrent transfers later. */
    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Account() {
        // required by JPA
    }

    public Account(String ownerName, String currency, long balanceCents) {
        this.ownerName = ownerName;
        this.currency = currency;
        this.balanceCents = balanceCents;
        this.createdAt = Instant.now();
    }

    /** Takes money out. The caller has already checked the balance; this is a last line of defense. */
    public void withdraw(long amountCents) {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (amountCents > balanceCents) {
            throw new IllegalStateException("insufficient funds");
        }
        balanceCents -= amountCents;
    }

    public void deposit(long amountCents) {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        balanceCents = Math.addExact(balanceCents, amountCents);
    }

    public Long getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public String getCurrency() { return currency; }
    public long getBalanceCents() { return balanceCents; }
    public Instant getCreatedAt() { return createdAt; }
}
