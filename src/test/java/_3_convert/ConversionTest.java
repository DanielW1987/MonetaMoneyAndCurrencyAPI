package _3_convert;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionTest {

    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");
    private static final CurrencyUnit USD = Monetary.getCurrency("USD");
    private static final CurrencyUnit COP = Monetary.getCurrency("COP");

    private static final MonetaryAmount ONE_HUNDRED_EUR = Money.of(100, EUR);
    private static final MonetaryAmount ONE_HUNDRED_USD = Money.of(100, USD);
    private static final MonetaryAmount ONE_HUNDRED_COP = Money.of(100, COP);

    private static final String ECB         = "ECB";
    private static final String ECB_HIST_90 = "ECB-HIST90";
    private static final String ECB_HIST    = "ECB-HIST";
    private static final String IDENT       = "IDENT";
    private static final String IMF         = "IMF";
    private static final String IMF_HIST    = "ECB-HIST";

    @Test
    void test_exchange_rate_provider() {
        List<String> defaultProvider = MonetaryConversions.getDefaultConversionProviderChain();
        assertTrue(List.of(ECB, ECB_HIST_90, ECB_HIST, IDENT, IMF, IMF_HIST).containsAll(defaultProvider));
    }

    @Test
    void test_exchange_rate() {
        ExchangeRateProvider exchangeRateProvider = MonetaryConversions.getExchangeRateProvider(ECB);
        ExchangeRate exchangeRate                 = exchangeRateProvider.getExchangeRate(EUR, USD);

        assertEquals(EUR, exchangeRate.getBaseCurrency());
        assertEquals(USD, exchangeRate.getCurrency());

        // zum Zeitpunkt der Testerstellung korrekt, heute wahrscheinlich nicht mehr
        assertEquals("1.1471", exchangeRate.getFactor().toString());
    }

    @Test
    void test_convert_with_current_exchange_rate_and_default_provider_chain() {
        // benutzt intern die Default-Provider-Chain
        CurrencyConversion conversion = MonetaryConversions.getConversion(USD);

        // 100 EUR x 1.1471 = USD 114.710177 (zum Zeitpunkt der Testerstellung korrekt, heute wahrscheinlich nicht mehr)
        assertEquals("USD 114.710177", ONE_HUNDRED_EUR.with(conversion).toString());
        assertEquals("1.14710176811811755324961691331997", conversion.getExchangeRate(ONE_HUNDRED_EUR).getFactor().toString());
    }

    @Test
    void test_convert_with_current_exchange_rate_and_specific_provider() {
        ExchangeRateProvider exchangeRateProvider = MonetaryConversions.getExchangeRateProvider(IMF);
        CurrencyConversion   conversion           = exchangeRateProvider.getCurrencyConversion(USD);

        // 100 COP x 0.00032 = USD 0.032095 (zum Zeitpunkt der Testerstellung korrekt, heute wahrscheinlich nicht mehr)
        assertEquals("USD 0.032095", ONE_HUNDRED_COP.with(conversion).toString());
        assertEquals("0.00032095481873513296175545042871436", conversion.getExchangeRate(ONE_HUNDRED_COP).getFactor().toString());
    }

    @Test
    void convert_with_historical_exchange_rate() {
        LocalDate historicalDate = LocalDate.of(2018, 12, 31);
        ConversionQuery query    = ConversionQueryBuilder.of()
                                                         .setBaseCurrency(EUR)
                                                         .setTermCurrency(USD)
                                                         .setRateTypes(RateType.HISTORIC)
                                                         .setProviderName(ECB_HIST)
                                                         .set(LocalDate.class, historicalDate)
                                                         .build();

        CurrencyConversion conversion = MonetaryConversions.getConversion(query);

        // 100 EUR x 1.1471 = USD 114.710177
        assertEquals("USD 114.710177", ONE_HUNDRED_EUR.with(conversion).toString());
        assertEquals("1.14710176811811755324961691331997", conversion.getExchangeRate(ONE_HUNDRED_EUR).getFactor().toString());
    }

}
