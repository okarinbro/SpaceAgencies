package model;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class ChannelFactory {
    static Channel createSimpleChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    static Channel createQosChannel() throws IOException, TimeoutException {
        Channel channel = createSimpleChannel();
        channel.basicQos(1);
        return channel;

    }
}
