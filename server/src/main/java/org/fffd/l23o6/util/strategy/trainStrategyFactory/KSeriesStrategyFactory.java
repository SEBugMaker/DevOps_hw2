package org.fffd.l23o6.util.strategy.trainStrategyFactory;

import org.fffd.l23o6.util.strategy.ticketPrice.KSeriesTicketPriceStrategy;
import org.fffd.l23o6.util.strategy.ticketPrice.TicketPriceStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.TrainSeatStrategy;

public class KSeriesStrategyFactory implements TrainStrategyFactory{
    @Override
    public TrainSeatStrategy getTrainSeatStrategy() {
        return KSeriesSeatStrategy.INSTANCE;
    }

    @Override
    public TicketPriceStrategy getTicketPriceStrategy() {
        return KSeriesTicketPriceStrategy.INSTANCE;
    }
}
