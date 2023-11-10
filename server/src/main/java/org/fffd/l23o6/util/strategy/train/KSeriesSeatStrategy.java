package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;


public class KSeriesSeatStrategy extends TrainSeatStrategy {
    public static final KSeriesSeatStrategy INSTANCE = new KSeriesSeatStrategy();
     
    private final Map<Integer, String> SOFT_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SOFT_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SEAT_MAP = new HashMap<>();

    private final Map<Integer, String> NO_SEAT_MAP = new HashMap<>();

    private final Map<KSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(KSeriesSeatType.SOFT_SLEEPER_SEAT, SOFT_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.HARD_SLEEPER_SEAT, HARD_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.SOFT_SEAT, SOFT_SEAT_MAP);
        put(KSeriesSeatType.HARD_SEAT, HARD_SEAT_MAP);
        put(KSeriesSeatType.NO_SEAT, NO_SEAT_MAP);
    }};


    private KSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("软卧1号上铺", "软卧2号下铺", "软卧3号上铺", "软卧4号上铺", "软卧5号上铺", "软卧6号下铺", "软卧7号上铺", "软卧8号上铺")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, KSeriesSeatType.SOFT_SLEEPER_SEAT);
            SOFT_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("硬卧1号上铺", "硬卧2号中铺", "硬卧3号下铺", "硬卧4号上铺", "硬卧5号中铺", "硬卧6号下铺", "硬卧7号上铺", "硬卧8号中铺", "硬卧9号下铺", "硬卧10号上铺", "硬卧11号中铺", "硬卧12号下铺")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, KSeriesSeatType.HARD_SLEEPER_SEAT);
            HARD_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("1车1座", "1车2座", "1车3座", "1车4座", "1车5座", "1车6座", "1车7座", "1车8座", "2车1座", "2车2座", "2车3座", "2车4座", "2车5座", "2车6座", "2车7座", "2车8座")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, KSeriesSeatType.SOFT_SEAT);
            SOFT_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("3车1座", "3车2座", "3车3座", "3车4座", "3车5座", "3车6座", "3车7座", "3车8座", "3车9座", "3车10座", "4车1座", "4车2座", "4车3座", "4车4座", "4车5座", "4车6座", "4车7座", "4车8座", "4车9座", "4车10座")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, KSeriesSeatType.HARD_SEAT);
            HARD_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("无座1", "无座2", "无座3", "无座4", "无座5", "无座6", "无座7", "无座8", "无座9", "无座10")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, KSeriesSeatType.NO_SEAT);
            NO_SEAT_MAP.put(counter++, s);
        }
    }

    public enum KSeriesSeatType implements TrainSeatStrategy.SeatType {
        SOFT_SLEEPER_SEAT("软卧"), HARD_SLEEPER_SEAT("硬卧"), SOFT_SEAT("软座"), HARD_SEAT("硬座"), NO_SEAT("无座");
        private String text;
        KSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static KSeriesSeatType fromString(String text) {
            for (KSeriesSeatType b : KSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    /**allocSeat
     * startStationIndex:顾名思义，是起始车站id在列表中的index
     * endStationIndex：类似
     * type：座位类型
     * seatMap：座位映射表，true表示占用。第一维表示路段，第二维表示座位
     */
    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, String type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound

        KSeriesSeatType trueKSeat = KSeriesSeatType.fromString(type);
        Map<Integer, String> thisTypeMap = TYPE_MAP.get(trueKSeat);
        return allocSeatFreely(startStationIndex, endStationIndex, thisTypeMap, seatMap);
    }

    public Map<SeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {

        Map<SeatType, Integer> toRet = new HashMap<>();
        int sizeOfSeat = seatMap[0].length;
        int a = 0;
        for (KSeriesSeatType typeHere: KSeriesSeatType.values()){
            toRet.put(typeHere, 0);
        }
        for (int j = 0 ; j < sizeOfSeat; j++){
            if (judgeIfSeatFree(startStationIndex, endStationIndex, j, seatMap)){
                SeatType getType = SEATID_TYPE_MAP.get(j);
                if (getType == null){
                    throw new BizException(CommonErrorType.UNKNOWN_ERROR, "座位类型未初始化");
                }
                toRet.replace(getType, toRet.get(getType) + 1);
            }
        }
        return toRet;
    }

    @Override
    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size() + NO_SEAT_MAP.size()];
    }


}
