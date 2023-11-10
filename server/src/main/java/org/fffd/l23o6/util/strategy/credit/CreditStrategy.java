package org.fffd.l23o6.util.strategy.credit;

import java.util.HashMap;
import java.util.Map;

public class CreditStrategy {

    private Map<Integer, Double> discountTable =new HashMap<Integer, Double>();;
    private Map<Integer, Double> basicPrice = new HashMap<Integer, Double>();;

    private final int moneyToCreditRate = 5;

    int firstStandard = 1000;
    double firstRate = 0.005;

    int secondStandard = 3000;
    double secondRate = 0.01;

    int thirdStandard = 10000;
    double thirdRate = 0.015;

    int fourthStandard = 50000;
    double fourthRate = 0.02;

    double fifthRate = 0.025;

    private final Integer[] key = {firstStandard,secondStandard,thirdStandard,fourthStandard};
    public CreditStrategy() {

        double curBasic = firstStandard * firstRate;
        // 添加折扣表中的规则
        discountTable.put(firstStandard, secondRate);
        basicPrice.put(firstStandard, curBasic);

        curBasic += (secondStandard-firstStandard) * secondRate;
        discountTable.put(secondStandard, thirdRate);
        basicPrice.put(secondStandard, curBasic);

        curBasic += (thirdStandard - secondStandard) * thirdRate;
        discountTable.put(thirdStandard, fourthRate);
        basicPrice.put(thirdStandard, curBasic);

        curBasic += (fourthStandard - thirdStandard) * fourthRate;
        discountTable.put(fourthStandard, fifthRate);
        basicPrice.put(fourthStandard, curBasic);
    }

    public int getReducedMoney(int credit) {
//        double discount = 0.0;
        // 遍历折扣表中的规则，找到符合条件的最高折扣
        for (int i = 3 ; i >= 0;i--) {
            if (credit >= key[i]) {
//                discount = discountTable.getOrDefault(key[i],0.0);
                return (int)(basicPrice.get(key[i]) + (credit - key[i]) * discountTable.get(key[i]));
            }
        }

        return (int)(credit * firstRate);
    }

    public int getNewCredit(int before, int truePay){
        return before+ truePay * moneyToCreditRate;
    }

    public int getDeltaCredit(int truePay){
        return truePay * moneyToCreditRate;
    }
}
