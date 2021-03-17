package com.filter;

import com.common.Constant;
import com.util.JwtUtil;
import com.util.ProxyAddrUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * 请求认证Filter
 */
//@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取当前url
        String url = request.getURI().getPath();
        //放行不需要验证的资源-->静态资源js、css、images
        for (String skip_url : Constant.skipAuth_resources) {
            if (url.contains("jsp")) break; //jsp资源需要认证
            if (url.contains(skip_url)) return chain.filter(exchange);
        }
        //放行登录相关请求
        for (String skip_url : Constant.skip_login_correlation) {
            if (url.contains(skip_url)) return chain.filter(exchange);
        }
        //放行不需要认证的请求--->满足sofa后期特殊需求
        for (String skip_url : Constant.skipAuth_Urls) {
            if (url.contains(skip_url)) {
                return chain.filter(exchange);
            }
        }
        String token = null;
        //获取token
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();//sofa平台登录时已将jwt放入了cookie中。
        if (cookies.containsKey(HttpHeaders.AUTHORIZATION))
            token = cookies.getFirst(HttpHeaders.AUTHORIZATION).getValue();
        if (StringUtils.isBlank(token)) {
            //没有token,重定向(redirect)到登录地址--->这里的重定向的地址其实是没有变的，因为还是通过注册中心获取服务的方式调用
            logger.info("从cookie中未获取到token,将跳转到登录地址...");
        } else {
            //有token
            try {
                JwtUtil.parseJWT(token);
                //向下游服务传递jwt
                //这里将jwt放到请求头中而不是直接让下游服务从cookie中取的原因是
                // 1:直接有从请求头中解析jwt的实现；2:常用做法。
                ServerHttpRequest change_request = request.mutate().header(HttpHeaders.AUTHORIZATION, new String[]{token}).build();
                ServerWebExchange change_exchange = exchange.mutate().request(change_request).build();
                return chain.filter(change_exchange);//直接放行
            } catch (ExpiredJwtException e) {
                logger.info("token过期，检测最后访问时间...");
                if (checkAccessTime(request.getCookies())) {//未过期
                    logger.info("token过期，cookie最后访问时间未过期，将刷新token...");
                    String new_token = JwtUtil.createJWT((String) e.getClaims().get("userId"));
                    //刷新cookie中的token
                    response.addCookie(ResponseCookie.from(HttpHeaders.AUTHORIZATION, new_token).domain(ProxyAddrUtil.getProxyServerAddr(request)).path("/").build());
                    //向下游服务传递已过期的token、新创建的token
                    ServerHttpRequest change_request = request.mutate().header(Constant.EXPIRE_AUTHORIZATION, token)//已过期token
                            .header(HttpHeaders.AUTHORIZATION, new_token)//新token
                            .build();
                    ServerWebExchange change_exchange = exchange.mutate().request(change_request).build();
                    return chain.filter(change_exchange);//放行
                } else//过期
                    logger.info("token过期，cookie最后访问时间过期,将跳转到登录地址...");
            } catch (Exception e) {
                logger.error("token认证失败{},将跳转到登录地址...", e.getMessage());
            }
        }
        //重定向登录
        ////303状态码表示由于请求对应的资源存在着另一个URI，应使用GET方法定向获取请求的资源
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set(HttpHeaders.LOCATION, Constant._platform_contextpath + Constant.login_url);
        return response.setComplete();//此方式不会执行后续的filter链
    }

    //当前时间是否在（cookie最后访问时间+token有效时间）之前
    private boolean checkAccessTime(MultiValueMap<String, HttpCookie> cookies) {
        if (cookies.containsKey(Constant.lastviewtime)) {
            String lastviewtime = cookies.get(Constant.lastviewtime).get(0).getValue();
            if (StringUtils.isNotBlank(lastviewtime)) {
                Date current_date = new Date();//当前时间
                Date cookie_lastviewtime = new Date(Long.parseLong(new String(Base64.getDecoder().decode(lastviewtime))));//cookie最后访问时间
                Date lastviewtime_expr = new Date(cookie_lastviewtime.getTime() + Constant.TokenValidTime);//cookie的最后访问时间+token有效时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                logger.info("当前时间:[{}],cookie最后访问时间[{}],lastviewtime过期时间[{}]", df.format(current_date), df.format(cookie_lastviewtime), df.format(lastviewtime_expr));
                //当前时间是否在（cookie最后访问时间+token有效时间）之前
                if (current_date.before(lastviewtime_expr)) return true;//没过期
                else return false;//过期
            }
        }
        logger.info("未获取到cookie的lastviewtime...");
        return false; //不存在lastviewtime直接按过期处理
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
