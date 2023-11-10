package org.fffd.l23o6.controller;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.vo.order.*;
import org.fffd.l23o6.service.OrderService;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.stp.StpUtil;

@CrossOrigin(origins = "http://172.29.4.130:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("order")
    public CommonResponse<OrderIdVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        StpUtil.checkLogin();
        return CommonResponse.success(new OrderIdVO(orderService.createOrder(StpUtil.getLoginIdAsString(), request.getTrainId(),
                request.getStartStationId(), request.getEndStationId(), request.getSeatType())));
    }

    @GetMapping("order")
    public CommonResponse<List<OrderVO>> listOrders(){
        StpUtil.checkLogin();
        return CommonResponse.success(orderService.listOrders(StpUtil.getLoginIdAsString()));
    }

    @GetMapping("order/{orderId}")
    public CommonResponse<OrderVO> getOrder(@PathVariable("orderId") Long orderId) {
        return CommonResponse.success(orderService.getOrder(orderId));
    }

    @GetMapping("order/credit/{orderId}")
    public CommonResponse<PriceByCreditVO> getPriceByCredit(@PathVariable("orderId") Long orderId) {
        return CommonResponse.success(new PriceByCreditVO(orderService.getPriceBothWay(orderId, true)));
    }

    @PatchMapping("order/{orderId}")
    public CommonResponse<InteractiveOrderInfo> patchOrder(@PathVariable("orderId") Long orderId, @Valid @RequestBody PatchOrderRequest request) {
        String toRet = "";

        switch (request.getStatus()) {
            case PENDING_PAYMENT:
                toRet = orderService.payOrder(orderId,request.getPay_by_credit(), request.getPay_type());
                break;
            case CANCELLED:
                orderService.cancelOrder(orderId);
                break;
            default:
                throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "Invalid order status.");
        }

        return CommonResponse.success(new InteractiveOrderInfo(orderService.getStatus(orderId),toRet));
    }


}