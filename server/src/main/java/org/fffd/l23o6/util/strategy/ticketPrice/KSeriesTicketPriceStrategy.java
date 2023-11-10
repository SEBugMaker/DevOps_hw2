package org.fffd.l23o6.util.strategy.ticketPrice;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;

public class KSeriesTicketPriceStrategy implements TicketPriceStrategy{
    public static final KSeriesTicketPriceStrategy INSTANCE = new KSeriesTicketPriceStrategy();

    private int NO_SEAT_PRICE = 20;
    private int HARD_SEAT_PRICE = 50;
    private int SOFT_SEAT_PRICE = 75;
    private int HARD_SLEEPER_PRICE = 100;
    private int SOFT_SLEEPER_PRICE = 150;
    @Override
    public int getPrice(int startStationIdx, int endStationIdx, String typeStr) {
        KSeriesSeatStrategy.KSeriesSeatType typeK = KSeriesSeatStrategy.KSeriesSeatType.fromString(typeStr);
        int base = NO_SEAT_PRICE;
        if (typeK == null){
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        switch (typeK){
            case SOFT_SLEEPER_SEAT:
                base = SOFT_SLEEPER_PRICE;
                break;
            case HARD_SLEEPER_SEAT:
                base = HARD_SLEEPER_PRICE;
                break;
            case SOFT_SEAT:
                base = SOFT_SEAT_PRICE;
                break;
            case HARD_SEAT:
                base = HARD_SEAT_PRICE;
            default:
                break;
        }
        return base * (endStationIdx - startStationIdx);
    }
}
