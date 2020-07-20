package org.ecostanzi.product.interfaces;

import org.ecostanzi.product.domain.Product;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProductLoader {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Product> productOps;

    public ProductLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Product> productOps) {
        this.factory = factory;
        this.productOps = productOps;
    }

    @PostConstruct
    public void loadData() {
////        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
//                Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
//                        .map(name -> new Product(UUID.randomUUID().toString(), name, new Random().nextInt(10)))
//                        .flatMap(product -> productOps.opsForValue().set(product.getId(), product))
////        )
//                .thenMany(productOps.keys("*")
//                        .flatMap(productOps.opsForValue()::get))
//                .subscribe(p -> System.out.println("Loaded product " + p));
    }
}
