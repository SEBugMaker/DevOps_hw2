package org.fffd.l23o6.pojo.vo.order;

import org.fffd.l23o6.pojo.enum_.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.fffd.l23o6.pojo.enum_.PayType;

@Data
@Schema(description = "订单状态修改")
public class PatchOrderRequest {

    @Schema(description = "订单状态", required = true)
    @NotNull
    private OrderStatus status;

    @Schema(description = "使用订单支付", required = true)
    @NotNull
    private Boolean pay_by_credit;

    @Schema(description = "支付途径", required = true)
    @NotNull
    private PayType pay_type;
}
