package org.fffd.l23o6.dao;

import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.service.RouteService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteDao extends JpaRepository<RouteEntity, Long>{

    public RouteEntity findRouteEntityByName(String name);
}
