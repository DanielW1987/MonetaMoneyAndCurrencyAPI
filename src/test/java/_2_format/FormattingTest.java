package _2_format;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormattingTest {

    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");
    private static final CurrencyUnit USD = Monetary.getCurrency("USD");
    private static final CurrencyUnit CHF = Monetary.getCurrency("CHF");

    private static final MonetaryAmountFormat formatterDE = MonetaryFormats.getAmountFormat(Locale.GERMANY);
    private static final MonetaryAmountFormat formatterUS = MonetaryFormats.getAmountFormat(Locale.US);
    private static final MonetaryAmountFormat formatterCH = MonetaryFormats.getAmountFormat(new Locale("de", "ch"));

    private static final MonetaryAmount TEN_THOUSAND_EURO = FastMoney.of(10_000, EUR);

    @Test
    void default_formatter_for_locales() {
        assertEquals("10.000,00 EUR", formatterDE.format(TEN_THOUSAND_EURO));
        assertEquals("EUR10,000.00", formatterUS.format(TEN_THOUSAND_EURO));
        assertEquals("EUR 10’000.00", formatterCH.format(TEN_THOUSAND_EURO));

        assertEquals("-10.000,00 EUR", formatterDE.format(TEN_THOUSAND_EURO.negate()));
        assertEquals("EUR-10,000.00", formatterUS.format(TEN_THOUSAND_EURO.negate()));
        assertEquals("EUR--10’000.00", formatterCH.format(TEN_THOUSAND_EURO.negate()));
    }

    @Test
    void parse_with_default_formatter() {
        MonetaryAmount val1 = formatterDE.parse("123,45 USD");
        MonetaryAmount val2 = formatterUS.parse("CHF-12,345.67");
        MonetaryAmount val3 = formatterCH.parse("EUR 12’345.67");

        assertEquals("USD", val1.getCurrency().getCurrencyCode());
        assertEquals("CHF", val2.getCurrency().getCurrencyCode());
        assertEquals("EUR", val3.getCurrency().getCurrencyCode());

        assertEquals(Money.of(123.45, USD), val1);
        assertEquals(Money.of(-12345.67, CHF), val2);
        assertEquals(Money.of(12345.67, EUR), val3);
    }

    @Test
    void custom_formatter() {
        MonetaryAmountFormat customFormat = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(Locale.GERMANY)
                                                                                                    .set(CurrencyStyle.SYMBOL)
                                                                                                    .set("pattern", "#,###,###.## ¤")
                                                                                                    .build());

        String formatted = customFormat.format(TEN_THOUSAND_EURO);

        assertEquals("10.000 €", formatted);
    }
}
