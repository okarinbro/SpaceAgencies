package model;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Shipper extends AdministrationUnit {
    private final static String ADMIN_SHIPPER_ROUTING_KEY = "shipper.*";
    private final List<ServiceType> serviceTypes;
    private final String exchangeName;
    private final String administrativeExchangeName;

    public Shipper(List<ServiceType> serviceTypes, String exchangeName, String administrativeExchangeName) {
        this.serviceTypes = serviceTypes;
        this.exchangeName = exchangeName;
        this.administrativeExchangeName = administrativeExchangeName;
    }

    public void init() throws IOException, TimeoutException {

        Preconditions.checkState(serviceTypes.size() == 2, "model.Shipper handles exactly 2 service types");

        for (ServiceType serviceType : serviceTypes) {
            Channel channel = ChannelFactory.createQosChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
            handleService(channel, serviceType, exchangeName);
        }

        Channel channel = ChannelFactory.createSimpleChannel();
        channel.exchangeDeclare(administrativeExchangeName, BuiltinExchangeType.TOPIC);
        ConsumptionRunner.startConsumingWithAutoAck(channel, new ConsumeSettings(UUID.randomUUID().toString(), administrativeExchangeName, ADMIN_SHIPPER_ROUTING_KEY), this::createAdministrativeConsumer);

    }

    private void handleService(Channel channel, ServiceType serviceType, String exchangeName) throws IOException {
        String queueName = serviceType.toString();
        ConsumptionRunner.startConsumingWithoutAutoAck(channel, new ConsumeSettings(queueName, exchangeName, queueName), this::createConsumer);
    }

    private Consumer createConsumer(final Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("Received order: " + message);
                try {
                    sendConfirmation(message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void sendConfirmation(String message) throws IOException {
                String[] split = message.split("###");
                String agencyName = split[0];
                String orderId = split[1];
                String serviceType = split[2];
                System.out.println(String.format("model.Agency name: %s; service type: %s", agencyName, serviceType));
                channel.basicPublish(exchangeName, agencyName, null, orderId.getBytes(StandardCharsets.UTF_8));
            }
        };
    }
}
