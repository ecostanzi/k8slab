package org.ecostanzi.order.controller;

import org.ecostanzi.order.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    final String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public List<Product> hello() {
        logger.info("Load order page");
        Product[] products = restTemplate.getForObject("/products", Product[].class);
        return Arrays.asList(products);
    }



    @GetMapping("/sysresources")
    public String getSystemResources() {
        long memory = Runtime.getRuntime().maxMemory();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("/sysresources " + hostname);
        return
                " Memory: " + (memory / 1024 / 1024) +
                        " Cores: " + cores + "\n";
    }

    @GetMapping("/dostuff")
    public String doStuff(@RequestParam(value = "base", defaultValue = "1", required = false) Integer baseNum) {
        long start = System.currentTimeMillis();
        mySlowFunction(baseNum);
        long ms = System.currentTimeMillis() - start;
        return "doing stuff took " + ms + "ms";
    }

    @Autowired
    CacheManager cacheManager;

    @GetMapping("/cachetest")
    public String cacheTest(@RequestParam(value = "key") String key) {

        Cache distCache = cacheManager.getCache("dist-cache");

        Cache.ValueWrapper value = distCache.get(key);
        if(value == null) {
            logger.info("CACHE MISS for key {}", key);
            distCache.put(key, key);
        } else {
            logger.info("CACHE HIT for key {}", key);
        }

        return key;
    }

    void mySlowFunction(int baseNumber) {
        System.out.println("mySlowFunction");
        int result = 0;
        for (var i = Math.pow(baseNumber, 7); i >= 0; i--) {
            result += Math.atan(i) * Math.tan(i);
        };
    }
}
