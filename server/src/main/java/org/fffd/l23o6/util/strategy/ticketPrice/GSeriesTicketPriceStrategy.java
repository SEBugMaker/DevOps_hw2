package org.fffd.l23o6.util.strategy.ticketPrice;


import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;

public class GSeriesTicketPriceStrategy implements TicketPriceStrategy{
    public static final GSeriesTicketPriceStrategy INSTANCE = new GSeriesTicketPriceStrategy();

    private int NO_SEAT_PRICE = 40;
    private int BUSINESS_SEAT_PRICE = 200;
    private int FIRST_CLASS_SEAT_PRICE = 150;
    private int SECOND_CLASS_SEAT_PRICE = 100;
    @Override
    public int getPrice(int startStationIdx, int endStationIdx, String typeStr) {
        GSeriesSeatStrategy.GSeriesSeatType typeK = GSeriesSeatStrategy.GSeriesSeatType.fromString(typeStr);
        int base = NO_SEAT_PRICE;
        if (typeK == null){
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        switch (typeK){
            case BUSINESS_SEAT:
                base = BUSINESS_SEAT_PRICE;
                break;
            case FIRST_CLASS_SEAT:
                base = FIRST_CLASS_SEAT_PRICE;
                break;
            case SECOND_CLASS_SEAT:
                base = SECOND_CLASS_SEAT_PRICE;
            default:
                break;
        }
        return base * (endStationIdx - startStationIdx);
    }
}
