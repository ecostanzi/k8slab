package org.ecostanzi.product.interfaces;

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
import reactor.core.Disposable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class OrderConsumer {

    private Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    private final StreamReceiver<String, MapRecord<String, String, String>> receiver;
    private final RedisReactiveCommands<String, String> commands;

    private Disposable subscription;


    public OrderConsumer(StreamReceiver<String, MapRecord<String, String, String>> receiver,
                         RedisReactiveCommands<String, String> commands) {
        this.receiver = receiver;
        this.commands = commands;
    }

    @PostConstruct
    public void subscribe() {
        String groupName = "mygroup";
        subscription = commands.xgroupCreate(XReadArgs.StreamOffset.from("orders", "$"), groupName, XGroupCreateArgs.Builder.mkstream(true))
                .onErrorReturn("mygroup") //if the group already exists
                .flatMapMany(s->
                        receiver.receive(
                                Consumer.from(groupName, "my-consumer"),
                                StreamOffset.create("orders", ReadOffset.lastConsumed())
                        )
                ).flatMap(it -> {
                    log.info("Processing message: " + it);
                    return commands.xack("orders", groupName, it.getId().getValue());
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
