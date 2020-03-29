package model;

import com.google.common.base.Function;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

import java.io.IOException;

public class ConsumptionRunner {
    public static Consumer startConsumingWithAutoAck(Channel channel, ConsumeSettings consumeSettings, Function<Channel, Consumer> createConsumer) throws IOException {
        String queueName = consumeSettings.getQueueName();
        Consumer consumer = getConsumer(channel, consumeSettings, createConsumer, queueName);
        channel.basicConsume(queueName, true, consumer);
        return consumer;
    }

    public static Consumer startConsumingWithoutAutoAck(Channel channel, ConsumeSettings consumeSettings, Function<Channel, Consumer> createConsumer) throws IOException {
        String queueName = consumeSettings.getQueueName();
        Consumer consumer = getConsumer(channel, consumeSettings, createConsumer, queueName);
        channel.basicConsume(queueName, false, consumer);
        return consumer;
    }

    private static Consumer getConsumer(Channel channel, ConsumeSettings consumeSettings, Function<Channel, Consumer> createConsumer, String queueName) throws IOException {
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, consumeSettings.getExchangeName(), consumeSettings.getRoutingKey());
        return createConsumer.apply(channel);
    }

}
