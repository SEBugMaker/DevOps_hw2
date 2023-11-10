package org.fffd.l23o6.pojo.vo.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.fffd.l23o6.pojo.enum_.OrderStatus;

@Data
@AllArgsConstructor
@Builder
public class InteractiveOrderInfo {
    private OrderStatus status;

    private String pay_info;
}
