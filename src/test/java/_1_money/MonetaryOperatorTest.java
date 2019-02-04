package _1_money;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonetaryOperatorTest {

    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");

    @Test
    void test() {
        MonetaryAmount value = Money.of(12.34567, EUR);

        // calculate 10 percent of value
        MonetaryAmount tenPercentOfValue = null; // ToDo

        assertEquals(tenPercentOfValue, Money.of(1.234567, EUR));
    }
}
