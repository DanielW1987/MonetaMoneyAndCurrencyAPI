package _4_enhancements;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.Locale;

class MonetaryOperatorTest {

    private static final CurrencyUnit         EUR          = Monetary.getCurrency("EUR");
    private static final MonetaryAmountFormat FORMATTER_DE = MonetaryFormats.getAmountFormat(Locale.GERMANY);

    @Test
    void test_present_value() {
        int periods = 40;
        BigDecimal rate = new BigDecimal("0.03");
        MonetaryAmount targetValue = Money.of(1_000_000, EUR);

        // Implement me dwg
    }

}