package model;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Administrator {
    private final String administrativeExchangeName;
    private final String commonExchangeName;

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
        return;

    }

    private void handleUserInput(Channel channel) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print(">");
                String message = br.readLine();
                System.out.println("shipper.agency | X.agency | shipper.X");
                String key = br.readLine();
                channel.basicPublish(administrativeExchangeName, key, null, message.getBytes("UTF-8"));
            }
        }
    }

    private Consumer createConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received copy of message: " + message);
            }
        };
    }


}
