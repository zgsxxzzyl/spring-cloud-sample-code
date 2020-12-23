package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DiscoveryController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/service")
    public List<String> service() {
        return discoveryClient.getServices();
    }

    @GetMapping("/service/{name}")
    public List<ServiceInstance> service(@PathVariable String name) {
        return discoveryClient.getInstances(name);
    }
}
