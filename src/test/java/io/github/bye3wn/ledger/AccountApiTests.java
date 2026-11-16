package io.github.bye3wn.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.context.WebApplicationContext;

/** Runs the whole app against a real Postgres started by Testcontainers (Docker must be running). */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AccountApiTests {

    @Autowired
    WebApplicationContext context;

    MockMvcTester mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcTester.from(context);
    }

    private MvcTestResult open(String json) {
        return mvc.post().uri("/accounts").contentType(MediaType.APPLICATION_JSON).content(json).exchange();
    }

    @Test
    void opensAnAccountAndReturnsItsLocation() {
        MvcTestResult result = open("""
                {"ownerName": "Ada Lovelace", "currency": "USD", "initialBalanceCents": 5000}
                """);

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result.getResponse().getHeader("Location")).matches("/accounts/\\d+");
        assertThat(result).bodyJson().extractingPath("$.ownerName").isEqualTo("Ada Lovelace");
        assertThat(result).bodyJson().extractingPath("$.balanceCents").asNumber().isEqualTo(5000);
    }

    @Test
    void fetchesAnAccountById() {
        String location = open("""
                {"ownerName": "Grace Hopper", "currency": "USD", "initialBalanceCents": 1200}
                """).getResponse().getHeader("Location");

        MvcTestResult result = mvc.get().uri(location).exchange();

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.ownerName").isEqualTo("Grace Hopper");
        assertThat(result).bodyJson().extractingPath("$.currency").isEqualTo("USD");
    }

    @Test
    void returns404ForAnUnknownAccount() {
        assertThat(mvc.get().uri("/accounts/999999").exchange()).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void rejectsInvalidInputWith400() {
        assertThat(open("""
                {"ownerName": "", "currency": "USD", "initialBalanceCents": 100}
                """)).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(open("""
                {"ownerName": "Alan Turing", "currency": "usd", "initialBalanceCents": 100}
                """)).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(open("""
                {"ownerName": "Alan Turing", "currency": "USD", "initialBalanceCents": -1}
                """)).hasStatus(HttpStatus.BAD_REQUEST);
    }
}
