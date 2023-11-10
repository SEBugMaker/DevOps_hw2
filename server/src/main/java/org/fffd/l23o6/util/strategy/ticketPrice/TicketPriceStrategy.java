package org.fffd.l23o6.util.strategy.ticketPrice;


public interface TicketPriceStrategy {
    int getPrice(int startStationIdx, int endStationIdx, String type);
}
