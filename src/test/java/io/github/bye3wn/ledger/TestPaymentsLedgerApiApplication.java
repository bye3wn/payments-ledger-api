package io.github.bye3wn.ledger;

import org.springframework.boot.SpringApplication;

public class TestPaymentsLedgerApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentsLedgerApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
