package org.fffd.l23o6.service.impl;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alipay.api.AlipayApiException;
import org.apache.catalina.User;
import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.PayType;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.statics.ConstVals;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.strategy.credit.CreditStrategy;
import org.fffd.l23o6.util.strategy.payment.AlipayPaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.WeChatPaymentStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.TrainSeatStrategy;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.GSeriesStrategyFactory;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.KSeriesStrategyFactory;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.TrainStrategyFactory;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;



    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType) {
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;

        TrainSeatStrategy seatStrategy = new GSeriesStrategyFactory().getTrainSeatStrategy();

        if (train.getTrainType().equals(TrainType.NORMAL_SPEED)){
            seatStrategy = new KSeriesStrategyFactory().getTrainSeatStrategy();
        }
        seat = seatStrategy.allocSeat(startStationIndex, endStationIndex, seatType, train.getSeats());
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .price(getPrice(trainId,fromStationId,toStationId,seat)).pay_type(PayType.ALIPAY_PAY.toInteger())
                .stamp_with_info(new Date().getTime() + userId.toString() + trainId)
                .build();
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);
        orderDao.save(order);
        return order.getId();
    }

    public List<OrderVO> listOrders(String username) {
        Long userId = userDao.findByUsername(username).getId();
        List<OrderEntity> orders = orderDao.findByUserId(userId);
        orders.sort((o1,o2)-> o2.getId().compareTo(o1.getId()));
        return orders.stream().map(order -> {
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                    .seat(order.getSeat()).status(order.getStatus().getText())
                    .createdAt(order.getCreatedAt())
                    .startStationId(order.getDepartureStationId())
                    .endStationId(order.getArrivalStationId())
                    .departureTime(train.getDepartureTimes().get(startIndex))
                    .arrivalTime(train.getArrivalTimes().get(endIndex))
                    .build();
        }).collect(Collectors.toList());
    }

    public OrderVO getOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());

        Date departureDate = train.getDepartureTimes().get(startIndex);
        UserEntity user = userDao.findById(order.getUserId()).get();
        Date stopCancel = new Date(departureDate.getTime() - ConstVals.completeRestrict);
        if (order.getStatus().equals(OrderStatus.PAID) && (new Date().after(stopCancel))){
            order.setStatus(OrderStatus.COMPLETED);
            user.setCredit(new CreditStrategy().getNewCredit(user.getCredit(), order.getPrice()));
            userDao.save(user);
            orderDao.save(order);
        }

        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                .seat(order.getSeat()).status(order.getStatus().getText())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .price(order.getPrice())
                .build();
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = 0;
        int endStationIndex = 0;

        for(int i = 0 ; i < route.getStationIds().size();i++){
            if (Objects.equals(route.getStationIds().get(i), order.getDepartureStationId())) {
                startStationIndex = i;
            }
            if(Objects.equals(route.getStationIds().get(i), order.getArrivalStationId() )){
                endStationIndex = i;
            }
        }


        if (order.getStatus() == OrderStatus.PAID){
            int moneyToRefund = order.getPrice();
            PaymentStrategy paymentStrategy = order.getPay_type().equals(PayType.WECHAT_PAY.toInteger()) ? new WeChatPaymentStrategy()
                    :new AlipayPaymentStrategy();
            try {
                if (!paymentStrategy.refundOrder(moneyToRefund, order.getStamp_with_info())){
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }

        int seatId = -1;
        if(train.getTrainType()==TrainType.HIGH_SPEED){
            GSeriesStrategyFactory gSeriesStrategyFactory = new GSeriesStrategyFactory();
            seatId = gSeriesStrategyFactory.getTrainSeatStrategy().findSeatIdByDescription(order.getSeat()).intValue();
        }else if(train.getTrainType()==TrainType.NORMAL_SPEED){
            KSeriesStrategyFactory kSeriesStrategyFactory = new KSeriesStrategyFactory();
            seatId = kSeriesStrategyFactory.getTrainSeatStrategy().findSeatIdByDescription(order.getSeat()).intValue();
        }

        TrainSeatStrategy.deallocSeatById(startStationIndex, endStationIndex, seatId, train.getSeats());
        train.setUpdatedAt(null);
        trainDao.save(train);

        order.setStatus(OrderStatus.CANCELLED);

        orderDao.save(order);
    }

    public String payOrder(Long id, boolean useCredit, PayType payType) {
        OrderEntity order = orderDao.findById(id).get();
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }
        order.setPay_type(payType.toInteger());
        int moneyToPay = getPriceBothWay(id, useCredit);
        UserEntity user = userDao.findById(order.getUserId()).get();
        if(useCredit){
            user.setCredit(0);
            userDao.save(user);
        }
        order.setPrice(moneyToPay);
        orderDao.save(order);
        PaymentStrategy paymentStrategy = payType.equals(PayType.WECHAT_PAY) ? new WeChatPaymentStrategy()
                : new AlipayPaymentStrategy();
        try {
            String toRet = paymentStrategy.PayOrder(moneyToPay, order.getStamp_with_info());
            new Thread(){
                @Override
                public void run() {
                    try {
                        Date toCheck = new Date(new Date().getTime() + 60 * 1000);
                        while (true) {
                            OrderStatus getStatus = paymentStrategy.checkOrderStatus(order.getStamp_with_info());
                            if (new Date().after(toCheck)){
                                cancelOrder(id);
                                break;
                            }
                            if (getStatus == null || getStatus.equals(OrderStatus.PENDING_PAYMENT)){
                                continue;
                            }
                            if (getStatus.equals(OrderStatus.PAID)) {
                                order.setStatus(OrderStatus.PAID);
                                order.setUpdatedAt(null);
                                orderDao.save(order);
                                break;
                            } else if (getStatus.equals(OrderStatus.CANCELLED)){
                                cancelOrder(id);
                                break;
                            }
                            sleep(1000/ 50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return toRet;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public int getPrice(Long trainId,Long departureStationId,Long arrivalStationId,String seatDescription){
        int price = 0;
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = 0;
        int endStationIndex = 0;

        for(int i = 0 ; i < route.getStationIds().size();i++){
            if (Objects.equals(route.getStationIds().get(i), departureStationId )) {
                startStationIndex = i;
            }
            if(Objects.equals(route.getStationIds().get(i), arrivalStationId )){
                endStationIndex = i;
            }
        }

        String seatType;
        if(train.getTrainType()==TrainType.HIGH_SPEED){
            seatType = GSeriesSeatStrategy.INSTANCE.findSeatTypeByDescription(seatDescription);

            GSeriesStrategyFactory gSeriesStrategyFactory = new GSeriesStrategyFactory();
            price = gSeriesStrategyFactory.getTicketPriceStrategy().getPrice(startStationIndex,endStationIndex,seatType);
        }else if(train.getTrainType()==TrainType.NORMAL_SPEED){
            seatType = KSeriesSeatStrategy.INSTANCE.findSeatTypeByDescription(seatDescription);

            KSeriesStrategyFactory kSeriesStrategyFactory = new KSeriesStrategyFactory();
            price = kSeriesStrategyFactory.getTicketPriceStrategy().getPrice(startStationIndex,endStationIndex,seatType);
        }
        return  price;
    }

    @Override
    public OrderStatus getStatus(Long id) {
        return orderDao.findById(id).get().getStatus();
    }

    @Override
    public int getPriceBothWay(Long id, boolean ifCredit) {
        OrderEntity order = orderDao.findById(id).get();
        UserEntity user = userDao.findById(order.getUserId()).get();
        int moneyToPay = order.getPrice();
        if(ifCredit){
            CreditStrategy creditStrategy = new CreditStrategy();

            moneyToPay -= creditStrategy.getReducedMoney(user.getCredit());
            if (moneyToPay < 0){
                moneyToPay = 0;
            }
            //System.err.println(disCount);

            //System.err.println(moneyToPay);
        }
        return moneyToPay;
    }
}
