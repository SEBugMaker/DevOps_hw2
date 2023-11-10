package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import jakarta.annotation.Nullable;
import org.fffd.l23o6.exception.BizError;


public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static final GSeriesSeatStrategy INSTANCE = new GSeriesSeatStrategy();
     
    private final Map<Integer, String> BUSINESS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> FIRST_CLASS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SECOND_CLASS_SEAT_MAP = new HashMap<>();

    private final Map<Integer, String> NO_SEAT_MAP = new HashMap<>();


    private final Map<GSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(GSeriesSeatType.BUSINESS_SEAT, BUSINESS_SEAT_MAP);
        put(GSeriesSeatType.FIRST_CLASS_SEAT, FIRST_CLASS_SEAT_MAP);
        put(GSeriesSeatType.SECOND_CLASS_SEAT, SECOND_CLASS_SEAT_MAP);
        put(GSeriesSeatType.NO_SEAT, NO_SEAT_MAP);
    }};

    private GSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("1车1A","1车1C","1车1F")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, GSeriesSeatType.BUSINESS_SEAT);
            BUSINESS_SEAT_MAP.put(counter++, s);

        }

        for (String s : Arrays.asList("2车1A","2车1C","2车1D","2车1F","2车2A","2车2C","2车2D","2车2F","3车1A","3车1C","3车1D","3车1F")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, GSeriesSeatType.FIRST_CLASS_SEAT);
            FIRST_CLASS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("4车1A","4车1B","4车1C","4车1D","4车2F","4车2A","4车2B","4车2C","4车2D","4车2F","4车3A","4车3B","4车3C","4车3D","4车3F")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, GSeriesSeatType.SECOND_CLASS_SEAT);
            SECOND_CLASS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("无座1", "无座2", "无座3", "无座4", "无座5")) {
            DESCRIPTION_ID_MAP.put(s, counter);
            SEATID_TYPE_MAP.put(counter, GSeriesSeatType.NO_SEAT);
            NO_SEAT_MAP.put(counter++, s);
        }
        
    }

    public enum GSeriesSeatType implements TrainSeatStrategy.SeatType {
        BUSINESS_SEAT("商务座"), FIRST_CLASS_SEAT("一等座"), SECOND_CLASS_SEAT("二等座"), NO_SEAT("无座");
        private String text;
        GSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static GSeriesSeatType fromString(String text) {
            for (GSeriesSeatType b : GSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    @Override
    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, String type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound

        GSeriesSeatType trueGSeat = GSeriesSeatType.fromString(type);
        Map<Integer, String> thisTypeMap = TYPE_MAP.get(trueGSeat);
        return allocSeatFreely(startStationIndex, endStationIndex, thisTypeMap, seatMap);
    }

    @Override
    public Map<SeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {

        Map<SeatType, Integer> toRet = new HashMap<>();
        int sizeOfSeat = seatMap[0].length;
        for (GSeriesSeatType typeHere: GSeriesSeatType.values()){
            toRet.put(typeHere, 0);
        }

        for (int j = 0; j < sizeOfSeat; j++) {
            if (judgeIfSeatFree(startStationIndex, endStationIndex, j, seatMap)) {
//                for (Map.Entry<GSeriesSeatType, Map<Integer, String>> mapHere: TYPE_MAP.entrySet()){
//                    if (mapHere.getValue().get(j) != null){
//                        toRet.replace(mapHere.getKey(), toRet.get(mapHere.getKey()) + 1);
//                        break;
//                    }
//                }
//           }
                SeatType getType = SEATID_TYPE_MAP.get(j);
                if (getType == null) {
                    throw new BizException(CommonErrorType.UNKNOWN_ERROR, "座位类型未初始化");
                }
                toRet.replace(getType, toRet.get(getType) + 1);
            }
        }
        return toRet;
    }


    @Override
    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size() + NO_SEAT_MAP.size()];
    }

}
