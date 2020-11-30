package com.common;

public class Constant {

    //过期的Authorization
    public static final String EXPIRE_AUTHORIZATION = "Expire-Authorization";

    //证书的最后访问时间
    public static final String lastviewtime = "lastviewtime";

    //token的有效时间默认30分钟
    public static final long TokenValidTime = 30 * 60 * 1000;

    //登录地址
    public static final String login_url = "/signon";

    //sofaboot-平台contextpath
    public static String sofaboot_platform_contextpath = "";

    //不需要拦截的请求 -- 以后可扩展--->用来实现sofa全局配置文件中filter.exclude.url_pattern=*/xxx/*|*/xxx/* 的功能
    //不拦截hessian服务调用的请求
    public static final String[] skipAuth_Urls = {"hessian"};

    //不需要拦截的资源
    public static final String[] skipAuth_resources = {"css","js","images"};

    //跳过登录相关请求
    public static final String[] skip_login_correlation = {"signon","login","validate"};

    //那些请求可以跳过设置cookie最后访问时间
    public static final String[] skip_refresh_lastviewtime = {"signout","css","js","images","hessian"};

    //sofa回调网关的授权码地址
    public static final String principal = "principal";

    //sofa注销地址
    public static final String logout = "signout";

    public static final String applicationFilter_error = "地址错误,无法匹配请求中的[应用名]信息,注意:[应用名]必须大写";

}
