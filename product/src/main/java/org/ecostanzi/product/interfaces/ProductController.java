package org.ecostanzi.product.interfaces;

import org.ecostanzi.product.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;

@RestController
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final Environment environment;
    private final ReactiveRedisOperations<String, Product> coffeeOps;

    private final DatabaseClient databaseClient;

    public ProductController(Environment environment, ReactiveRedisOperations<String, Product> coffeeOps, DatabaseClient databaseClient) {
        this.environment = environment;
        this.coffeeOps = coffeeOps;
        this.databaseClient = databaseClient;
    }

    @GetMapping("/products")
    public Flux<Product> all() throws UnknownHostException {
        var ip = InetAddress.getLocalHost();
        var hostname = ip.getHostName();
        logger.info("Getting the list of products  from shelve {}, host {}", environment.getProperty("shelf", "UNKNOWN"), hostname);
        return databaseClient.execute("select id, name, price from product")
                .as(Product.class)
                .fetch().all();
    }

    @PostMapping("/product")
    Mono<Void> create(@RequestBody Product product) {
        product.setId(UUID.randomUUID().toString());
        product.setPrice(new Random().nextInt(10));
        return this.coffeeOps.opsForValue().set(product.getId(), product)
                .then();
    }

}
