package van.karm.bid.service.converter;

import van.karm.auction.Money;

import java.math.BigDecimal;

public class MoneyConverter {

    public static BigDecimal fromMoney(Money money) {
        if (money == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(money.getUnits(), money.getScale());
    }
}
