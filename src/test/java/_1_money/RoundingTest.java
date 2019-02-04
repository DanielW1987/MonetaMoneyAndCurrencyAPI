package _1_money;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import javax.money.*;

import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RoundingTest {

    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");
    private static final CurrencyUnit CHF = Monetary.getCurrency("CHF");

    @Test
    void test_default_rounding() {
        MonetaryAmount a      = FastMoney.of(100, EUR);
        MonetaryAmount result = a.divide(3);

        // standardmäßig immer 5 Nachkommastellen bei FastMoney
        assertEquals(FastMoney.of(33.33333, EUR), result);

        // DefaultRounding rundet anhand der DefaultFractionDigits der Währung
        assertEquals(FastMoney.of(33.33, EUR), result.with(Monetary.getDefaultRounding()));
    }

    @Test
    void test_cash_rounding() {
        MonetaryAmount a      = Money.of(100, CHF);
        MonetaryAmount result = a.divide(3);

        MonetaryRounding rounding = Monetary.getRounding(RoundingQueryBuilder.of()
                                                                            .setCurrency(CHF)
                                                                            .set("cashRounding", true)
                                                                            .build());

        // standardmäßig hat Money bzw. BigDecimal eine precision von 256, also in diesem Fall 254 Nachkommastellen
        assertNotEquals(Money.of(33.33333, CHF), result);
        assertEquals(Money.of(33.35, CHF), rounding.apply(result));
    }

    @Test
    void test_individual_rounding() {
        MonetaryAmount a      = Money.of(100, EUR);
        MonetaryAmount result = a.divide(3);

        MonetaryRounding rounding = Monetary.getRounding(RoundingQueryBuilder.of()
                                                                             .setScale(3)
                                                                             .set(RoundingMode.HALF_EVEN)
                                                                             .build());

        assertEquals(Money.of(33.333, EUR), result.with(rounding));
    }

}
