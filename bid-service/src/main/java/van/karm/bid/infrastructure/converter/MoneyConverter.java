package van.karm.bid.infrastructure.converter;

import van.karm.auction.Money;

import java.math.BigDecimal;

public class MoneyConverter {

    public static Money toMoney(BigDecimal value) {
        if (value == null) return Money.newBuilder().setUnits(0).setScale(0).build();

        int scale = value.scale();
        long units = value.movePointRight(scale).longValueExact();

        return Money.newBuilder()
                .setUnits(units)
                .setScale(scale)
                .build();
    }
}
