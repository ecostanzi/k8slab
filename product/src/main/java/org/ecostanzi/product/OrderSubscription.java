package org.ecostanzi.product;

import io.lettuce.core.XGroupCreateArgs;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class OrderSubscription {

    private Logger log = LoggerFactory.getLogger(OrderSubscription.class);

    private final StreamReceiver<String, MapRecord<String, String, String>> receiver;
    private final RedisReactiveCommands<String, String> commands;

    private Disposable subscription;


    public OrderSubscription(StreamReceiver<String, MapRecord<String, String, String>> receiver,
                             RedisReactiveCommands<String, String> commands) {
        this.receiver = receiver;
        this.commands = commands;
    }

    @PostConstruct
    public void subscribe() {
        commands.xgroupCreate(XReadArgs.StreamOffset.from("orders", "$"), "mygroup", XGroupCreateArgs.Builder.mkstream(true))
                .doOnNext(s->{
                    Flux<MapRecord<String, String, String>> messages =
                            receiver.receive(
                                    Consumer.from("mygroup", "my-consumer"),
                                    StreamOffset.create("orders", ReadOffset.lastConsumed())
                            );

                    subscription = messages.flatMap(it -> {
                        log.info("Processing message: " + it);
                        return commands.xack("orders", "mygroup", it.getId().getValue());

                    }).subscribe();
                }).subscribe();
    }

    @PreDestroy
    private void preDestroy() {
        if (subscription != null) {
            subscription.dispose();
            subscription = null;
        }
    }


}
