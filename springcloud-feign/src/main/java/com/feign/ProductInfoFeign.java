package com.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("SERVICE-CLIENT")
public interface ProductInfoFeign {

    @PostMapping(value = "/productInfo/findByPdCodes")
    Map<String, String> findByPdCodes(@RequestBody List<String> pdCodes
            , @RequestParam String date);
}
