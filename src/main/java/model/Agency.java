package model;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Agency extends AdministrationUnit {
    private final static String ADMIN_AGENCY_ROUTING_KEY = "*.agency";
    private final String agencyName;
    private final String exchangeName;
    private final String administrativeExchangeName;

    public Agency(String agencyName, String exchangeName, String administrativeExchangeName) {
        this.agencyName = agencyName;
        this.exchangeName = exchangeName;
        this.administrativeExchangeName = administrativeExchangeName;
    }

    public void init() throws IOException, TimeoutException {
        Channel channel = ChannelFactory.createSimpleChannel();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(administrativeExchangeName, BuiltinExchangeType.TOPIC);
        ConsumptionRunner.startConsumingWithAutoAck(channel, new ConsumeSettings(agencyName, exchangeName, agencyName), this::createConsumer);
        ConsumptionRunner.startConsumingWithAutoAck(channel, new ConsumeSettings(UUID.randomUUID().toString(), administrativeExchangeName, ADMIN_AGENCY_ROUTING_KEY), this::createAdministrativeConsumer);
        handleUserInput(channel);
    }

    private void handleUserInput(Channel channel) throws IOException {
        while (true) {
            System.out.println("Type service type below (ct, pt, st)");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            ServiceType serviceType = ServiceType.fromString(br.readLine());
            if (serviceType.equals(ServiceType.Unknown)) {
                System.out.println("Illegal argument");
                continue;
            }
            Order order = new Order(agencyName, UUID.randomUUID().toString(), serviceType);
            orderService(channel, exchangeName, order);
        }
    }

    private Consumer createConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("Received confirmation: " + message);
            }
        };
    }

    private void orderService(Channel channel, String exchangeName, Order order) throws IOException {
        channel.basicPublish(exchangeName, order.getServiceType().toString(), null, order.createMessage().getBytes(StandardCharsets.UTF_8));
    }
}
