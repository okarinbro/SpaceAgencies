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
        Channel channel = createChannel();
        String adminQueue = "adminQueue";
        ConsumptionRunner.startConsuming(channel, adminQueue, commonExchangeName, this::createConsumer, true);
        channel.exchangeDeclare(administrativeExchangeName, BuiltinExchangeType.TOPIC);

        handleUserInput(channel);
        return;

    }

    private Channel createChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    private void handleUserInput(Channel channel) throws IOException {
        while (true) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.print(">");
                String message = br.readLine();
                System.out.println("shipper.agency | .agency | shipper.");
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
