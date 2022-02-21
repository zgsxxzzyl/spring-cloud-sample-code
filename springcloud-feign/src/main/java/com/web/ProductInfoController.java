package com.web;

import com.feign.ProductInfoFeign;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductInfoController {

    //编译器报错，无视。 因为这个Bean是在程序启动的时候注入的，编译器感知不到，所以报错。
    @Resource
    ProductInfoFeign productInfoApi;

    @GetMapping("/test")
    public void test() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 200000; i++) {
            strings.add("AAAAAAA" + i);
        }
        productInfoApi.findByPdCodes(strings, "");
    }
}
