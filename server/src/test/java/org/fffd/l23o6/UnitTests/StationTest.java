package org.fffd.l23o6.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.fffd.l23o6.dao.StationDao;
import org.fffd.l23o6.pojo.entity.StationEntity;
import org.fffd.l23o6.service.StationService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationTest {
    @Autowired
    private StationService stationService;

    @Autowired
    private StationDao stationDao;

    @Test
    public void test1(){
        String toAdd = "上海虹桥";
        stationService.addStation(toAdd);

        StationEntity entity = stationDao.findByName(toAdd);
        assertNotNull(entity);
        assertEquals(stationService.getStation(entity.getId()).getName(), toAdd);

    }

    @Test
    public void test2(){
        String toAdd = "黑龙江站";
        stationService.addStation(toAdd);

        StationEntity entity = stationDao.findByName(toAdd);
        assertNotNull(entity);
        assertEquals(stationService.getStation(entity.getId()).getName(), toAdd);

    }


    @Test
    public void test3(){
        String toAdd = "宿迁站";
        stationService.addStation(toAdd);

        StationEntity entity = stationDao.findByName(toAdd);
        assertNotNull(entity);
        assertEquals(stationService.getStation(entity.getId()).getName(), toAdd);
    }

    @Test
    public void test4(){
        String toAdd = "青海站";
        stationService.addStation(toAdd);

        StationEntity entity = stationDao.findByName(toAdd);
        assertNotNull(entity);
        assertEquals(stationService.getStation(entity.getId()).getName(), toAdd);

    }





}
