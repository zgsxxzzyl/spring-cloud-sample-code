package com.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class ProxyAddrUtil {

    /**
     * 获取代理服务器ip/域名
     *
     * @param request
     * @return ip/域名
     */
    public static String getProxyServerAddr(ServerHttpRequest request) {
        //此方式获取的地址可以是ip也可以是域名
        String hosts = request.getHeaders().getFirst("x-forwarded-host");
        if(StringUtils.isNotBlank(hosts)) {
            //返回第一个代理服务器地址
            return hosts.split(",")[0];
        } else{
            //x-forwarded-host为空证明网关就是第一级代理服务器
            return request.getURI().getHost();
        }
    }

}
