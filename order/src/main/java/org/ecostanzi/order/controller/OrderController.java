package org.ecostanzi.order.controller;

import org.ecostanzi.order.Order;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class OrderController {

    private final RedisTemplate<String, String> redisTemplate;

    public OrderController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/order")
    public void placeOrder(@RequestBody Order order) {
        order.setId(UUID.randomUUID().toString());
        redisTemplate.opsForStream().add(ObjectRecord.create("orders", order));
    }
}
