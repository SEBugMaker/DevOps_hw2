package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PayType {

    @JsonProperty ALIPAY_PAY(0), WECHAT_PAY(1);
    private final int PAY_TYPE;
    PayType(int type){
        PAY_TYPE = type;
    }

    public int toInteger(){
        return PAY_TYPE;
    }
}
