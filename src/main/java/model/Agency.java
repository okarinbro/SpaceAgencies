package model;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeoutException;

public class Agency {
    //todo: handle threads better than copyonwrite ;)
    private final Set<Order> tasksInProgress = new CopyOnWriteArraySet<Order>();
    private final String agencyName;
    private final String exchangeName;

    public Agency(String agencyName, String exchangeName) {
        this.agencyName = agencyName;
        this.exchangeName = exchangeName;
    }

    public void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);

        ConsumptionRunner.startConsuming(channel, agencyName, exchangeName, this::createConsumer);

        while (true) {
            System.out.println("Type service type below (ct, pt, st)");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String serviceType = br.readLine();
            Order order = new Order(agencyName, UUID.randomUUID().toString(), serviceType);
            tasksInProgress.add(order);
            orderService(channel, exchangeName, order);
        }

    }


    private Consumer createConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received confirmation: " + message);
                //todo remove from set
            }
        };
    }

    private void orderService(Channel channel, String exchangeName, Order order) throws IOException {
        channel.basicPublish(exchangeName, order.getServiceType().toString(), null, order.createMessage().getBytes("UTF-8"));
    }
}
