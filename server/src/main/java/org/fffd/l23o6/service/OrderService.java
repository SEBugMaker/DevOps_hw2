package org.fffd.l23o6.service;

import java.util.List;

import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.enum_.PayType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;

public interface OrderService {
    Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType);
    List<OrderVO> listOrders(String username);
    OrderVO getOrder(Long id);

    void cancelOrder(Long id);
    String payOrder(Long id, boolean useCredit, PayType pay_type);
    int getPrice(Long trainId,Long departureStationId,Long arrivalStationId,String seatDescription);

    int getPriceBothWay(Long id, boolean ifCredit);
    OrderStatus getStatus(Long id);

}
