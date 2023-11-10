package org.fffd.l23o6.util.strategy.payment;

import org.fffd.l23o6.pojo.enum_.OrderStatus;

public class WeChatPaymentStrategy extends PaymentStrategy{
    @Override
    public String PayOrder(int money, String id) throws Exception {
        return """
                    <form name="punchout_form" method="post" action="https://www.baidu.com">
                <input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;128&quot;,&quot;total_amount&quot;:150,&quot;subject&quot;:&quot;train ticket&quot;,&quot;timeout_express&quot;:&quot;1m&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
                <input type="submit" value="立即支付" style="display:none" >
                </form>
                <script>document.forms[0].submit();</script>""";
    }

    @Override
    public OrderStatus checkOrderStatus(String out_trade_no) throws Exception {
        return OrderStatus.PAID;
    }


    @Override
    public boolean refundOrder(int money, String id) throws Exception {
        return true;
    }

}
