package org.fffd.l23o6.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.pojo.statics.ConstVals;
import org.fffd.l23o6.pojo.vo.route.AddRouteRequest;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://172.29.4.130:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @SaCheckRole("routeAdmin")
    @PostMapping("admin/route")
    public CommonResponse<?> addRoute(@Valid @RequestBody AddRouteRequest request) {
        routeService.addRoute(request.getName(), request.getStationIds());
        return CommonResponse.success();
    }

    @SaCheckRole("routeAdmin")
    @GetMapping("admin/route")
    public CommonResponse<List<RouteVO>> getRoutes() {
        return CommonResponse.success(routeService.listRoutes());
    }

    @SaCheckRole("routeAdmin")
    @GetMapping("admin/route/{routeId}")
    public CommonResponse<RouteVO> getRoute(@PathVariable("routeId") Long routeId) {
        return CommonResponse.success(routeService.getRoute(routeId));
    }

    @SaCheckRole("routeAdmin")
    @PutMapping("admin/route/{routeId}")
    public CommonResponse<?> editRoute(@PathVariable("routeId") Long routeId, @Valid @RequestBody AddRouteRequest request) {
        routeService.editRoute(routeId, request.getName(), request.getStationIds());
        return CommonResponse.success();
    }
}