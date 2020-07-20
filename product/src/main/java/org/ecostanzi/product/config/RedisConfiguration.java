package org.ecostanzi.product.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.ecostanzi.product.interfaces.OrderConsumer;
import org.ecostanzi.product.domain.Product;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamReceiver;

import java.time.Duration;

/**
 * https://github.com/mp911de/redis-stream-demo/blob/main/redis-stream-demo/src/main/java/biz/paluch/redis/streamfeaturepoll/config/RedisConfiguration.java
 */
@Configuration
public class RedisConfiguration {

    @Bean
    ReactiveRedisOperations<String, Product> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Product> serializer = new Jackson2JsonRedisSerializer<>(Product.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Product> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Product> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public StreamReceiver<String, MapRecord<String, String, String>> streamReceiver(
            ReactiveRedisConnectionFactory factory) {
        StreamReceiver.StreamReceiverOptions<String, MapRecord<String, String, String>> options =
                StreamReceiver.StreamReceiverOptions.builder()
                        .pollTimeout(Duration.ofSeconds(3))
                        .batchSize(100)
                        .build();
        return StreamReceiver.create(factory, options);
    }

    @Bean
    public OrderConsumer updater(StreamReceiver<String, MapRecord<String, String, String>> streamReceiver,
                                 RedisReactiveCommands<String, String> commands) {
        return new OrderConsumer(streamReceiver, commands);
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> connection(ReactiveRedisConnectionFactory factory) {

        DirectFieldAccessor accessor = new DirectFieldAccessor(factory);

        RedisClient client = (RedisClient) accessor.getPropertyValue("client");

        return client.connect();
    }

    @Bean
    public RedisReactiveCommands<String, String> commands(StatefulRedisConnection<String, String> connection) {
        return connection.reactive();
    }

}
