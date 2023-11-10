package org.fffd.l23o6.util.strategy.trainStrategyFactory;

import org.fffd.l23o6.util.strategy.ticketPrice.GSeriesTicketPriceStrategy;
import org.fffd.l23o6.util.strategy.ticketPrice.TicketPriceStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.TrainSeatStrategy;

public class GSeriesStrategyFactory implements TrainStrategyFactory{
    @Override
    public TrainSeatStrategy getTrainSeatStrategy() {
        return GSeriesSeatStrategy.INSTANCE;
    }

    @Override
    public TicketPriceStrategy getTicketPriceStrategy() {
        return GSeriesTicketPriceStrategy.INSTANCE;
    }
}
