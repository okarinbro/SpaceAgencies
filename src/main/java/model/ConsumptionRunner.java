package model;

import com.google.common.base.Function;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

import java.io.IOException;

public class ConsumptionRunner {
    public static Consumer startConsuming(Channel channel, String queueName, String exchangeName,
                                          Function<Channel, Consumer> createConsumer) throws IOException {
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, queueName);
        Consumer consumer = createConsumer.apply(channel);
        return consumer;
    }
}
