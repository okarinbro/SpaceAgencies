package model;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Administrator {
    private final String administrativeExchangeName;
    private final String commonExchangeName;
    private final ImmutableMap<String, String> routingKeys = new ImmutableMap.Builder<String, String>()
            .put("all", "shipper.agency")
            .put("agencies", "X.agency")
            .put("shippers", "shipper.X")
            .build();

    public Administrator(String administrativeExchangeName, String commonExchangeName) {
        this.administrativeExchangeName = administrativeExchangeName;
        this.commonExchangeName = commonExchangeName;
    }

    public void init() throws IOException, TimeoutException {
        Channel channel = ChannelFactory.createSimpleChannel();
        String adminQueue = "adminQueue";
        channel.exchangeDeclare(administrativeExchangeName, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(commonExchangeName, BuiltinExchangeType.TOPIC);
        ConsumptionRunner.startConsumingWithAutoAck(channel, new ConsumeSettings(adminQueue, commonExchangeName, "#"), this::createConsumer);
        handleUserInput(channel);
    }

    private void handleUserInput(Channel channel) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print(">");
                String message = br.readLine();
                System.out.println("all | agencies | shippers");
                String key = br.readLine();
                channel.basicPublish(administrativeExchangeName, routingKeys.get(key), null, message.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private Consumer createConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("Received copy of message: " + message);
            }
        };
    }


}
