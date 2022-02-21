package com;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProductInfoController {

    @PostMapping(value = "/productInfo/findByPdCodes")
    Map<String, String> findByPdCodes(@RequestBody List<String> pdCodes,
                                      @RequestParam String date) {
        System.out.println(pdCodes.size());
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 200000; i++) {
            map.put("AAAAAAA" + i, "AAAAAA" + i);
        }
        return map;
    }
}
