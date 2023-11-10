package org.fffd.l23o6.util.strategy.payment;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

import com.alibaba.fastjson.JSONObject;
import org.fffd.l23o6.pojo.enum_.OrderStatus;


public abstract class PaymentStrategy {



    /**
     *
     * @param money 支付金额
     * @param id    支付订单id
     * @return      返回response，也可以修改为返回（string）response.getbody()
     * @throws AlipayApiException
     */
    public abstract String PayOrder(int money, String id) throws Exception;

    /**
     *
     * @param out_trade_no 订单号
     * @return             返回是否支付
     * @throws AlipayApiException
     */
    public abstract OrderStatus checkOrderStatus(String out_trade_no) throws Exception;

    public abstract boolean refundOrder(int money, String id) throws Exception;
}
