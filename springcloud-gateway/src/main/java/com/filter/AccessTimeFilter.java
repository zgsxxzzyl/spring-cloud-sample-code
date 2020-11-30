package com.filter;

import com.common.Constant;
import com.util.ProxyAddrUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Date;

/**
 * AccessTimeFilter --->最后执行的filter刷新cookie最后访问时间
 */
@Component
public class AccessTimeFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AccessTimeFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取当前url
        String url = request.getURI().getPath();
        //跳过不需要刷新最后访问时间的请求
        for (String skip_url : Constant.skip_refresh_lastviewtime)
            if (url.contains(skip_url)) return chain.filter(exchange);
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (cookies.containsKey(Constant.lastviewtime)) {
            if (StringUtils.isNotBlank(cookies.getFirst(Constant.lastviewtime).getValue())) {
                logger.debug("更新cookie最后访问时间");
                //更新cookie的最后访问时间信息。
                response.addCookie(ResponseCookie.from(Constant.lastviewtime, Base64.getEncoder().encodeToString(String.valueOf(new Date().getTime()).getBytes()))
                        .domain(ProxyAddrUtil.getProxyServerAddr(request))
                        .path("/")
                        .build());
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
