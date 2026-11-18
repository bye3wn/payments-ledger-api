package io.github.bye3wn.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TransferApiTests {

    private static final Pattern ID = Pattern.compile("/(\\d+)$");

    @Autowired
    WebApplicationContext context;

    MockMvcTester mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcTester.from(context);
    }

    private long openAccount(String currency, long balanceCents) {
        MvcTestResult result = mvc.post().uri("/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ownerName\":\"Test\",\"currency\":\"" + currency + "\",\"initialBalanceCents\":" + balanceCents + "}")
                .exchange();
        Matcher m = ID.matcher(result.getResponse().getHeader("Location"));
        assertThat(m.find()).isTrue();
        return Long.parseLong(m.group(1));
    }

    private MvcTestResult transfer(long from, long to, long amountCents) {
        return mvc.post().uri("/transfers").contentType(MediaType.APPLICATION_JSON)
                .content("{\"fromAccountId\":" + from + ",\"toAccountId\":" + to + ",\"amountCents\":" + amountCents + "}")
                .exchange();
    }

    private void assertBalance(long accountId, long expectedCents) {
        assertThat(mvc.get().uri("/accounts/" + accountId).exchange())
                .bodyJson().extractingPath("$.balanceCents").asNumber().isEqualTo((int) expectedCents);
    }

    @Test
    void movesMoneyBetweenAccounts() {
        long alice = openAccount("USD", 10_000);
        long bob = openAccount("USD", 500);

        MvcTestResult result = transfer(alice, bob, 2_500);

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.amountCents").asNumber().isEqualTo(2500);
        assertBalance(alice, 7_500);
        assertBalance(bob, 3_000);
    }

    @Test
    void rejectsInsufficientFundsAndChangesNothing() {
        long alice = openAccount("USD", 1_000);
        long bob = openAccount("USD", 0);

        assertThat(transfer(alice, bob, 1_001)).hasStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        assertBalance(alice, 1_000);
        assertBalance(bob, 0);
    }

    @Test
    void rejectsDifferentCurrencies() {
        long usd = openAccount("USD", 1_000);
        long eur = openAccount("EUR", 1_000);

        assertThat(transfer(usd, eur, 100)).hasStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        assertBalance(usd, 1_000);
    }

    @Test
    void rejectsTransferToSameAccount() {
        long alice = openAccount("USD", 1_000);

        assertThat(transfer(alice, alice, 100)).hasStatus(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void rejectsZeroOrNegativeAmounts() {
        long alice = openAccount("USD", 1_000);
        long bob = openAccount("USD", 0);

        assertThat(transfer(alice, bob, 0)).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(transfer(alice, bob, -5)).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void returns404WhenAnAccountDoesNotExist() {
        long alice = openAccount("USD", 1_000);

        assertThat(transfer(alice, 999_999, 100)).hasStatus(HttpStatus.NOT_FOUND);
        assertBalance(alice, 1_000);
    }

    @Test
    void fetchesATransferById() {
        long alice = openAccount("USD", 1_000);
        long bob = openAccount("USD", 0);
        String location = transfer(alice, bob, 250).getResponse().getHeader("Location");

        MvcTestResult result = mvc.get().uri(location).exchange();

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.fromAccountId").asNumber().isEqualTo((int) alice);
    }
}
