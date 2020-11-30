package com.filter;

import com.common.Constant;
import com.util.ProxyAddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * LogoutFilter
 */
@Component
public class LogoutFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LogoutFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //拦截注销请求
        if (request.getURI().getPath().contains(Constant.logout)) {
            logger.info("由用户从门户页面发起注销...");
            //清空cookie中的Authorization信息。
            response.addCookie(ResponseCookie.from(HttpHeaders.AUTHORIZATION, "").domain(ProxyAddrUtil.getProxyServerAddr(request)).path("/").build());
            //清空cookie的最后访问时间信息。
            response.addCookie(ResponseCookie.from(Constant.lastviewtime, "").domain(ProxyAddrUtil.getProxyServerAddr(request)).path("/").build());
        }
        //直接放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
