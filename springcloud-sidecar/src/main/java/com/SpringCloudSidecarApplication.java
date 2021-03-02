package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

/**
 * https://docs.spring.io/spring-cloud-netflix/docs/2.2.7.RELEASE/reference/html/#polyglot-support-with-sidecar
 */
@EnableSidecar
@SpringBootApplication
public class SpringCloudSidecarApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSidecarApplication.class, args);
    }
}
