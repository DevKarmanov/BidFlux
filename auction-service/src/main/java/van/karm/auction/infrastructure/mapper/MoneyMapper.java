package van.karm.auction.infrastructure.mapper;

import van.karm.auction.Money;

import java.math.BigDecimal;

public class MoneyMapper {

    public static BigDecimal fromMoney(Money money) {
        if (money == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(money.getUnits(), money.getScale());
    }

}
