package com;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@EnableEurekaServer
@SpringBootApplication
public class SpringcloudEurekaServerApplication implements ApplicationContextAware {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudEurekaServerApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
