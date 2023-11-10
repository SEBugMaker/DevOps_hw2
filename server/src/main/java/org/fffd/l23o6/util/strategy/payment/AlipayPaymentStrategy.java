package org.fffd.l23o6.util.strategy.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.fffd.l23o6.pojo.enum_.OrderStatus;

import java.util.HashMap;
import java.util.Map;

public class AlipayPaymentStrategy extends PaymentStrategy{
    private final AlipayClient alipayClient = new DefaultAlipayClient("https://openapi-sandbox.dl.alipaydev.com/gateway.do",
            "9021000122697352",
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCO6LQS5I5EcQhelX/O1ND8eSBoxS971rQqJ6ziBsQIuxWjEF5tKUEZqVdBvg6Yzk4LGYQSEHS3O+us0VCH/fwdxSdORUIYF6oVP4Hh+/NY/p0Puq74NVlDIYrMNBbBZRGireZXAFSot0Q4fhtfeiIYpXjv2sdURwQAF19Io2RcSAD+vMGD/xEK6xkMW2uEC/Ioqt5f+BxlrQNczGUoaXiUPSIn71mr/x4eka1QBJzQJ7lzlAezo1teO4F9vhx/ovAJVwK7vSBze80EDroy2Ffnb8dRGB/sEPgw5uMZhnBHN6jGjuJdYBt9UgX0o9GjuVxSf/DNjFq13mCOQxwWxvqnAgMBAAECggEAc0fus2riBP31lW8d6bgtWe1QIpQmiMybC4Jz6OUyUCSzAJacxYxI8q1zMRpi5gtOU4/ixTu3G3Tf2coEH5/YOB+pZG9kMNEuJ/PSD31SQdEq29xpdtp6RjgRokbGsH2aa1PDAx3GLU+hXb0E0dFRHfamBz7CGw8qf/Rardr4g6NrOYC4UIS6Km61CHGUHD2tovprY6lAXNTRHCA+EpR0753imFWQRarPTSOB4ylvMoJ58lJ1INXSn5aPxURnSaWq0rDMSWU+hnvHlHgrSv591kHSaVqPQU1bJ7iU6e5Zn/GVDY4LkC3ExMyFr98sP2DiGD6RdAq1dxOpgG0helqciQKBgQDpnbmCbNctrP1owm2vZZU547BWhY0z31CWO18XF0pgaML/2eNOi2p7kta+EzfOSf4JRanoOLxLN7MyK5REDZJwJm6NHhgbQQNXsknW0QwABPkE56MzP66eAffZxYXFKxkRU2p2yXx0q6y/d8NrTqHjDA1kkSpSHmBe0NjLEfmwrQKBgQCcmg+A1+tjtSAG//V9VYbrA5UimDl8vSHC+dHcKo0F0SdeIYQgL3wkfYJaBbs7bbcnq3+pdNDlwPub7Dn8tuYGbdYsxEYz7BXtEH7dQXPSQHSTvtZG3NVsZlxFjzzYp8z225TVpyVI88chcSYq546s4lzdgbq0PAFY/lzPyLZ/IwKBgEtwRlZqpTW3tgEdP+QyznRqJShbahSFGXXZ6EQBnl/xlH6gMrnLZXBQI44vkIbJ+AGwgERgBdXYP8KEthUfuNkEYBCr1ib+4IByL0vyJ4jeEjfWQSosd0vI4Otu0FqyJx8kK63+NHVMdrVBjFZbhwVVgAGoqtznuEEF+evhexrlAoGAV5XawecBYfy1cvTJKlYhZWPVj0mwLVHihhgPBaJOyUgXSpBYQ2ALgKUpuIuoEi7k1y0V84LAKx/qGWTfcbYhpMrdlKAZ24RZJwOo0LqAusEN8mwzj+KHA1+kj8ry7wG6ov5gmbYoJkshvlR4Ulq783mPEtwxvggpgMqSwiux1h0CgYEA3aeI04mWCL2vJT3RqiBy9/Bdz0foJ+icstXHSXJ5y9PCqKRmSXqad9O17w4Zj8dmOM3uBD5mSloIDEJ/KdeF/xqCj5YGHmtxNGQWF3+juH2HMVIm3qutKh3PzxQOf7sb6XJNqwSzx1+IdYVHCJTBL6U9vcHec7AQhUqlxgk3Fr8="
            , "json", "utf-8",
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxCaq1zQPahEz7iPhgVvvhycEwwhsBA4anZPZ3t+1pqeCsdGMYZvPTmVGbcdpjVM6hDK4fflpF+VX/Ra6DUREARvkd1idmO3SscUBgrKkgmq3jAnsY9WpKB7FwQKD1tWUHLyohZKt3by3x6wPI6ianHzNEvpylmzCrs21nU913e3u4cXlgXSKpRWGNrPRhKRcuJ/d5Vw3VnTF/mfNGOLs20KMgJz+Q34iak6xcDA3n6cHpqzIWn409YO+nGUR7odwP7V2bMqIHVjdCu1EfkLHof8nIeJp6W0j6Ot7KIMdYRHEgyU0zHcSZzpXXl4qnX84yCKugSEljAeUXArXhlY/jwIDAQAB"
            , "RSA2");

    private final Map<String,OrderStatus> revertToOrderStatus = new HashMap<>();


    public AlipayPaymentStrategy(){
        revertToOrderStatus.put("WAIT_BUYER_PAY", OrderStatus.PENDING_PAYMENT);
        revertToOrderStatus.put("TRADE_SUCCESS", OrderStatus.PAID);
        revertToOrderStatus.put("TRADE_CLOSED", OrderStatus.CANCELLED);
    }

    @Override
    public String PayOrder(int money, String id) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl("");
        request.setReturnUrl("http://172.29.4.130:5173/user");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", id);
        bizContent.put("total_amount", money);
        bizContent.put("subject", "train ticket");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("timeout_express", "1m");

        request.setBizContent(bizContent.toString());

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

        System.out.println(response.getBody());
        return response.getBody();
    }


    @Override
    public OrderStatus checkOrderStatus(String out_trade_no) throws AlipayApiException{
        //AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","app_id","your private_key","json","GBK","alipay_public_key","RSA2");
//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi-sandbox.dl.alipaydev.com/gateway.do",
//                "9021000122697352",
//                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCO6LQS5I5EcQhelX/O1ND8eSBoxS971rQqJ6ziBsQIuxWjEF5tKUEZqVdBvg6Yzk4LGYQSEHS3O+us0VCH/fwdxSdORUIYF6oVP4Hh+/NY/p0Puq74NVlDIYrMNBbBZRGireZXAFSot0Q4fhtfeiIYpXjv2sdURwQAF19Io2RcSAD+vMGD/xEK6xkMW2uEC/Ioqt5f+BxlrQNczGUoaXiUPSIn71mr/x4eka1QBJzQJ7lzlAezo1teO4F9vhx/ovAJVwK7vSBze80EDroy2Ffnb8dRGB/sEPgw5uMZhnBHN6jGjuJdYBt9UgX0o9GjuVxSf/DNjFq13mCOQxwWxvqnAgMBAAECggEAc0fus2riBP31lW8d6bgtWe1QIpQmiMybC4Jz6OUyUCSzAJacxYxI8q1zMRpi5gtOU4/ixTu3G3Tf2coEH5/YOB+pZG9kMNEuJ/PSD31SQdEq29xpdtp6RjgRokbGsH2aa1PDAx3GLU+hXb0E0dFRHfamBz7CGw8qf/Rardr4g6NrOYC4UIS6Km61CHGUHD2tovprY6lAXNTRHCA+EpR0753imFWQRarPTSOB4ylvMoJ58lJ1INXSn5aPxURnSaWq0rDMSWU+hnvHlHgrSv591kHSaVqPQU1bJ7iU6e5Zn/GVDY4LkC3ExMyFr98sP2DiGD6RdAq1dxOpgG0helqciQKBgQDpnbmCbNctrP1owm2vZZU547BWhY0z31CWO18XF0pgaML/2eNOi2p7kta+EzfOSf4JRanoOLxLN7MyK5REDZJwJm6NHhgbQQNXsknW0QwABPkE56MzP66eAffZxYXFKxkRU2p2yXx0q6y/d8NrTqHjDA1kkSpSHmBe0NjLEfmwrQKBgQCcmg+A1+tjtSAG//V9VYbrA5UimDl8vSHC+dHcKo0F0SdeIYQgL3wkfYJaBbs7bbcnq3+pdNDlwPub7Dn8tuYGbdYsxEYz7BXtEH7dQXPSQHSTvtZG3NVsZlxFjzzYp8z225TVpyVI88chcSYq546s4lzdgbq0PAFY/lzPyLZ/IwKBgEtwRlZqpTW3tgEdP+QyznRqJShbahSFGXXZ6EQBnl/xlH6gMrnLZXBQI44vkIbJ+AGwgERgBdXYP8KEthUfuNkEYBCr1ib+4IByL0vyJ4jeEjfWQSosd0vI4Otu0FqyJx8kK63+NHVMdrVBjFZbhwVVgAGoqtznuEEF+evhexrlAoGAV5XawecBYfy1cvTJKlYhZWPVj0mwLVHihhgPBaJOyUgXSpBYQ2ALgKUpuIuoEi7k1y0V84LAKx/qGWTfcbYhpMrdlKAZ24RZJwOo0LqAusEN8mwzj+KHA1+kj8ry7wG6ov5gmbYoJkshvlR4Ulq783mPEtwxvggpgMqSwiux1h0CgYEA3aeI04mWCL2vJT3RqiBy9/Bdz0foJ+icstXHSXJ5y9PCqKRmSXqad9O17w4Zj8dmOM3uBD5mSloIDEJ/KdeF/xqCj5YGHmtxNGQWF3+juH2HMVIm3qutKh3PzxQOf7sb6XJNqwSzx1+IdYVHCJTBL6U9vcHec7AQhUqlxgk3Fr8="
//                , "json", "utf-8",
//                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxCaq1zQPahEz7iPhgVvvhycEwwhsBA4anZPZ3t+1pqeCsdGMYZvPTmVGbcdpjVM6hDK4fflpF+VX/Ra6DUREARvkd1idmO3SscUBgrKkgmq3jAnsY9WpKB7FwQKD1tWUHLyohZKt3by3x6wPI6ianHzNEvpylmzCrs21nU913e3u4cXlgXSKpRWGNrPRhKRcuJ/d5Vw3VnTF/mfNGOLs20KMgJz+Q34iak6xcDA3n6cHpqzIWn409YO+nGUR7odwP7V2bMqIHVjdCu1EfkLHof8nIeJp6W0j6Ot7KIMdYRHEgyU0zHcSZzpXXl4qnX84yCKugSEljAeUXArXhlY/jwIDAQAB"
//                , "RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "  \"out_trade_no\":" +
                "\"" + out_trade_no +
                "\"" +
                "}");
        String toRevert = alipayClient.execute(request).getTradeStatus();
        return revertToOrderStatus.getOrDefault(toRevert, OrderStatus.PENDING_PAYMENT);
    }

    @Override
    public boolean refundOrder(int money, String id) throws AlipayApiException {

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        //bizContent.put("trade_no", id);
        bizContent.put("out_trade_no", id);
        bizContent.put("refund_amount", money);

        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            System.out.println("调用成功");
            return true;
        } else {
            System.out.println("调用失败");
            return false;
        }

    }
}
