package _1_money;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import javax.money.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MonetaryAmountTest {

    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");
    private static final CurrencyUnit USD = Monetary.getCurrency("USD");

    @Test
    void create_with_factory() {
        // Default factory
        MonetaryAmount monetaryAmount = Monetary.getDefaultAmountFactory()
                                                .setCurrency(EUR)
                                                .setNumber(new BigDecimal("200.12"))
                                                .setContext(MonetaryContextBuilder.of().setMaxScale(3).build())
                                                .create();

        assertEquals("EUR 200.120", monetaryAmount.toString());
        assertTrue(monetaryAmount instanceof Money);

        // Explizite factory
        MonetaryAmount fastMonetaryAmount = Monetary.getAmountFactory(FastMoney.class)
                                                    .setCurrency(EUR)
                                                    .setNumber(200.12)
                                                    .create();

        assertEquals("EUR 200.12000", fastMonetaryAmount.toString());
        assertTrue(fastMonetaryAmount instanceof FastMoney);
    }

    @Test
    void create_with_of() {
        MonetaryAmount money     = Money.of(12, EUR);
        MonetaryAmount fastMoney = FastMoney.of(2, EUR);

        assertEquals("EUR 12", money.toString());
        assertEquals("EUR 2.00000", fastMoney.toString()); // Standardmäßig immer 5 Nachkommastellen
        assertThrows(ArithmeticException.class, () -> FastMoney.of(1.123456, EUR)); // Message: 1.123456 can not be represented by this class, scale > 5
    }

    @Test
    void calculate_with_monetary_amounts() {
        MonetaryAmount a = FastMoney.of(100, EUR);
        MonetaryAmount b = FastMoney.of(200, EUR);

        // Fluid API
        MonetaryAmount result = a.add(b)
                                 .subtract(b)
                                 .multiply(2)
                                 .divide(2);

        assertEquals(FastMoney.of(100, EUR), result);
    }

    @Test
    void calculate_with_monetary_amounts_advanced() {
        MonetaryAmount a = FastMoney.of(100, EUR);

        // Ergebnis der Division + REST in einem Array
        MonetaryAmount[] divideAndRemainder = a.divideAndRemainder(6);
        assertEquals(FastMoney.of(16, EUR), divideAndRemainder[0]);
        assertEquals(FastMoney.of(4, EUR), divideAndRemainder[1]);

        // Nur Rest der Division
        MonetaryAmount remainder = a.remainder(6);
        assertEquals(FastMoney.of(4, EUR), remainder);

        // Ganzzahliger Wert der Division
        MonetaryAmount integralValue = a.divideToIntegralValue(6);
        assertEquals(FastMoney.of(16, EUR), integralValue);

        // Potenz
        MonetaryAmount scaledValue = a.scaleByPowerOfTen(2);
        assertEquals(FastMoney.of(10_000, EUR), scaledValue);
    }

    @Test
    void compare_with_monetary_amounts() {
        MonetaryAmount a = FastMoney.of(100, EUR);
        MonetaryAmount b = Money.of(200, EUR);

        // compare with b
        assertFalse(a.isEqualTo(b));
        assertFalse(a.isGreaterThan(b));
        assertFalse(a.isGreaterThanOrEqualTo(b));
        assertTrue(a.isLessThan(b));
        assertTrue(a.isLessThanOrEqualTo(b));
    }

    @Test
    void calculate_with_monetary_amounts_of_different_currency() {
        MonetaryAmount a = FastMoney.of(100, EUR);
        MonetaryAmount b = FastMoney.of(100, USD);

        assertThrows(MonetaryException.class, () -> a.add(b));
    }

    @Test
    void be_careful_with_equals_and_isEqualTo() {
        MonetaryAmount a = FastMoney.of(100, EUR);
        MonetaryAmount b = Money.of(100, EUR);

        assertTrue(a.isEqualTo(b));
        assertNotEquals(a, b);
        assertEquals(a, FastMoney.of(100, EUR));
    }

    @Test
    void working_with_large_numbers() {
        MonetaryAmount a = FastMoney.of(1_000_000_000L, EUR);
        MonetaryAmount b = FastMoney.of(1_000_000_000L, EUR);

        // long overflow
        assertThrows(ArithmeticException.class, () -> a.multiply(b.getNumber()));

        // funtkioniert aber mit Money, da intern BigDecimal verwendet wird
        MonetaryAmount c      = Money.of(1_000_000_000L, EUR);
        MonetaryAmount d      = Money.of(1_000_000_000L, EUR);
        MonetaryAmount result = c.multiply(d.getNumber());

        assertEquals(Money.of(1_000_000_000_000_000_000L, EUR), result);

    }

}
