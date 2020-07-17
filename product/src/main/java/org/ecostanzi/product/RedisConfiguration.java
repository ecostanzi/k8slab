package org.ecostanzi.product;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamReceiver;
import reactor.core.publisher.Flux;

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
                StreamReceiver.StreamReceiverOptions.builder().pollTimeout(Duration.ofSeconds(3))
                        .build();
        return StreamReceiver.create(factory, options);
    }

    @Bean
    public OrderSubscription updater(StreamReceiver<String, MapRecord<String, String, String>> streamReceiver,
                                    RedisReactiveCommands<String, String> commands) {
        return new OrderSubscription(streamReceiver, commands);
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
