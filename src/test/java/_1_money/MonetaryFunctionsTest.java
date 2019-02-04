package _1_money;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.javamoney.moneta.function.MonetarySummaryStatistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MonetaryFunctionsTest {

    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");
    private static final CurrencyUnit USD = Monetary.getCurrency("USD");
    private static final CurrencyUnit YEN = Monetary.getCurrency("JPY");

    private static final MonetaryAmount TEN_DOLLAR     = Money.of(10, USD);
    private static final MonetaryAmount FIFTEEN_DOLLAR = Money.of(15, USD);
    private static final MonetaryAmount TWENTY_DOLLAR  = Money.of(20, USD);

    private static final List<MonetaryAmount> amounts = new ArrayList<>();

    @BeforeAll
    private static void init() {
        amounts.add(Money.of(2, EUR));
        amounts.add(Money.of(42, USD));
        amounts.add(Money.of(7, USD));
        amounts.add(Money.of(13.37, YEN));
        amounts.add(Money.of(18, USD));
    }

    @Test
    void test_filter_by_currency() {
        List<MonetaryAmount> onlyEuro = amounts.stream()
                                               .filter(MonetaryFunctions.isCurrency(EUR))
                                               .collect(Collectors.toList());

        List<MonetaryAmount> onlyEuroAndYen = amounts.stream()
                                                     .filter(MonetaryFunctions.isCurrency(EUR, YEN))
                                                     .collect(Collectors.toList());

        List<MonetaryAmount> excludingEuroAndYen = amounts.stream()
                                                          .filter(MonetaryFunctions.filterByExcludingCurrency(EUR, YEN))
                                                          .collect(Collectors.toList());

        assertEquals(1, onlyEuro.size());
        assertEquals(2, onlyEuroAndYen.size());
        assertEquals(3, excludingEuroAndYen.size());
    }

    @Test
    void test_filter_by_comparison() {
        List<MonetaryAmount> greaterThan10Dollars = amounts.stream()
                                                           .filter(MonetaryFunctions.isCurrency(USD))
                                                           .filter(MonetaryFunctions.isGreaterThan(TEN_DOLLAR))
                                                           .collect(Collectors.toList());

        List<MonetaryAmount> between15And20 = amounts.stream()
                                                     .filter(MonetaryFunctions.isCurrency(USD))
                                                     .filter(MonetaryFunctions.isBetween(FIFTEEN_DOLLAR, TWENTY_DOLLAR))
                                                     .collect(Collectors.toList());

        assertEquals(2, greaterThan10Dollars.size());
        assertEquals(1, between15And20.size());
    }

    @Test
    void test_grouping() {
        Map<CurrencyUnit, List<MonetaryAmount>> groupedByCurrency = amounts.stream().collect(MonetaryFunctions.groupByCurrencyUnit());

        // {USD=[USD 42, USD 7, USD 18], EUR=[EUR 2], JPY=[JPY 13.37]}
        assertEquals(3, groupedByCurrency.size());
        assertTrue(groupedByCurrency.containsKey(USD));
        assertTrue(groupedByCurrency.containsKey(EUR));
        assertTrue(groupedByCurrency.containsKey(YEN));
    }

    @Test
    void test_reduce_methods() {
        List<MonetaryAmount> amounts = new ArrayList<>();
        amounts.add(Money.of(10, EUR));
        amounts.add(Money.of(7.5, EUR));
        amounts.add(Money.of(12, EUR));

        Optional<MonetaryAmount> min = amounts.stream().reduce(MonetaryFunctions.min()); // "EUR 7.5"
        Optional<MonetaryAmount> max = amounts.stream().reduce(MonetaryFunctions.max()); // "EUR 12"
        Optional<MonetaryAmount> sum = amounts.stream().reduce(MonetaryFunctions.sum()); // "EUR 29.5"

        assertEquals(Money.of(7.5, EUR), min.get());
        assertEquals(Money.of(12, EUR), max.get());
        assertEquals(Money.of(29.5, EUR), sum.get());
    }

    @Test
    void test_summarizing() {
        Map<CurrencyUnit, MonetarySummaryStatistics> summary = amounts.stream().collect(MonetaryFunctions.groupBySummarizingMonetary()).get();

        assertEquals(3, summary.get(USD).getCount());
        assertEquals(Money.of(42, USD), summary.get(USD).getMax());
        assertEquals(Money.of(7, USD), summary.get(USD).getMin());
        assertEquals(Money.of(67, USD), summary.get(USD).getSum());
    }

}
