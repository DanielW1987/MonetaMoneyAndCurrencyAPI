package _4_enhancements;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import java.math.BigDecimal;
import java.util.Objects;

public class PresentValue implements MonetaryOperator {

  private BigDecimal rate;
  private int        periods;
  private BigDecimal divisor;

  public PresentValue(BigDecimal rate, int periods){
    Objects.requireNonNull(rate);
    this.rate    = rate;
    this.periods = periods;
    this.divisor = BigDecimal.ONE.add(rate).pow(periods);
  }

  public int getPeriods() {
    return periods;
  }

  public BigDecimal getRate() {
    return rate;
  }

  @Override
  public MonetaryAmount apply(MonetaryAmount monetaryAmount) {
    return monetaryAmount.divide(divisor);
  }

}
