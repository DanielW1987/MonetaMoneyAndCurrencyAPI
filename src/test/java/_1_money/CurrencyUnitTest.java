package _1_money;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.internal.ConfigurableCurrencyUnitProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.money.*;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyUnitTest {

    @Test
    void create_currency_via_constant_locale() {
        CurrencyUnit euro = Monetary.getCurrency(Locale.GERMANY);

        assertEquals("EUR", euro.getCurrencyCode());
        assertEquals(2, euro.getDefaultFractionDigits());
        assertEquals(978, euro.getNumericCode()); // ISO numeric code
    }

    @Test
    void create_currency_via_custom_locale() {
        CurrencyUnit euro = Monetary.getCurrency(new Locale("es", "CO"));

        assertEquals("COP", euro.getCurrencyCode());
        assertEquals(2, euro.getDefaultFractionDigits());
        assertEquals(170, euro.getNumericCode());
    }

    @Test
    void create_currency_via_string_currency_code() {
        CurrencyUnit euro = Monetary.getCurrency("JPY");

        assertEquals("JPY", euro.getCurrencyCode());
        assertEquals(0, euro.getDefaultFractionDigits());
        assertEquals(392, euro.getNumericCode()); // ISO numeric code
    }

    @Test
    void create_custom_currency() {
        CurrencyUnit BTC = CurrencyUnitBuilder.of("BTC", "default")
                                              .setDefaultFractionDigits(3)
                                              .build();

        assertEquals("BTC", BTC.getCurrencyCode());
        assertEquals(3, BTC.getDefaultFractionDigits());
        assertFalse(Monetary.isCurrencyAvailable("BTC"));

        ConfigurableCurrencyUnitProvider.registerCurrencyUnit(BTC);
        assertTrue(Monetary.isCurrencyAvailable("BTC"));
    }

    @Test
    void throw_exception_if_currency_code_not_exist() {
        Assertions.assertThrows(UnknownCurrencyException.class, () -> Monetary.getCurrency("AAA"));
    }
}