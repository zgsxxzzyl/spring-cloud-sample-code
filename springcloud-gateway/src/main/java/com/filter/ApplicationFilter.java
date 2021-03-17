package com.filter;

import com.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ApplicationFilter
 */
//@Component
public class ApplicationFilter implements GlobalFilter, Ordered {

    @Autowired
    public GatewayProperties gatewayProperties;

    @Autowired
    public DiscoveryClient discoveryClient;

    //应用名集合
    public ArrayList<String> list = new ArrayList<String>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //TODO 平台应用上下文的预加载
        settingPlatformContextPath();
        //TODO 其他应用上下或数据的预加载，后续可以扩展...
        return chain.filter(exchange);
    }

    private void settingPlatformContextPath() {
        if (CollectionUtils.isEmpty(list)) {
            // TODO  2020-08-21 获取路由列表
            List<RouteDefinition> routes = gatewayProperties.getRoutes();
            // TODO  2020-08-21 设置_platform_contextpath
            String applicationName = routes.get(0).getUri().getAuthority().toUpperCase();       //SERVICE-CLIENT
            Constant._platform_contextpath = getApplicationContextPath(applicationName);
        }
    }

    private String getApplicationContextPath(String applicationName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(applicationName);
        if (CollectionUtils.isEmpty(instances)) return "";
        //获取应用元数据
        Map<String, String> metadata = instances.get(0).getMetadata();
        if (CollectionUtils.isEmpty(metadata)) return "";
        String contextPath = metadata.get("context-path");
        return StringUtils.isEmpty(contextPath) ? "" : contextPath;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
