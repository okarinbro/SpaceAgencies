package model;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Shipper extends AdministrationUnit {
    private final static String ADMIN_SHIPPER_ROUTING_KEY = "shipper.*";
    private final List<String> serviceTypes;
    private final String exchangeName;
    private final String administrativeExchangeName;

    //todo: send message back
    public Shipper(List<String> serviceTypes, String exchangeName, String administrativeExchangeName) {
        this.serviceTypes = serviceTypes;
        this.exchangeName = exchangeName;
        this.administrativeExchangeName = administrativeExchangeName;
    }

    public void init() throws IOException, TimeoutException {
        Channel channel = ChannelFactory.createQosChannel();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(administrativeExchangeName, BuiltinExchangeType.TOPIC);
        Preconditions.checkState(serviceTypes.size() == 2, "model.Shipper handles exactly 2 service types");
        for (String serviceType : serviceTypes) {
            handleService(channel, serviceType, exchangeName);
        }
        ConsumptionRunner.startConsumingWithAutoAck(channel, new ConsumeSettings(UUID.randomUUID().toString(), administrativeExchangeName, "shipper.*"), this::createAdministrativeConsumer);

    }

    private void handleService(Channel channel, String serviceType, String exchangeName) throws IOException {
        String queueName = ServiceType.fromString(serviceType).toString();
        Consumer consumer = ConsumptionRunner.startConsumingWithoutAutoAck(channel, new ConsumeSettings(queueName, exchangeName, queueName), this::createConsumer);
        channel.basicConsume(queueName, false, consumer);
    }

    private Consumer createConsumer(final Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received order: " + message);
                workHard();
                sendConfirmation(message);
                channel.basicAck(envelope.getDeliveryTag(), false);

            }

            //it's just for testing QoS in development phase
            //todo: get rid of that in the future
            private void workHard() {
                try {
                    Thread.sleep(1000 * new Random().nextInt(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            private void sendConfirmation(String message) throws IOException {
                String[] split = message.split("###");
                String agencyName = split[0];
                String orderId = split[1];
                String serviceType = split[2];
                System.out.println(String.format("model.Agency name: %s; service type: %s", agencyName, serviceType));
                channel.basicPublish(exchangeName, agencyName, null, orderId.getBytes("UTF-8"));
            }
        };
    }
}
