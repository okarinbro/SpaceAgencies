import com.google.common.base.Preconditions;
import com.rabbitmq.client.*;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Shipper {
    private final List<String> serviceTypes;
    private final String exchangeName;

    //todo: send message back
    Shipper(List<String> serviceTypes, String exchangeName) {
        this.serviceTypes = serviceTypes;
        this.exchangeName = exchangeName;
    }

    public void init() throws IOException, TimeoutException {
        Preconditions.checkState(serviceTypes.size() == 2, "Shipper handles exactly 2 service types");
        for (String serviceType : serviceTypes) {
            handleService(serviceType, exchangeName);
        }
    }

    private void handleService(String serviceType, String exchangeName) throws IOException, TimeoutException {
        Channel channel = createChannel(exchangeName);
        String queueName = ServiceType.fromString(serviceType).toString();
        Consumer consumer = ConsumptionRunner.startConsuming(channel, queueName, exchangeName, this::createConsumer);
        channel.basicConsume(queueName, false, consumer);
    }

    private Channel createChannel(String exchangeName) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
        return channel;
    }

    private Consumer createConsumer(final Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received order: " + message);
                channel.basicAck(envelope.getDeliveryTag(), false);
                String agencyName = message.split("###")[0];
                System.out.println("Agency name: " + agencyName);
                channel.basicPublish(exchangeName, agencyName, null, message.getBytes("UTF-8"));

            }
        };
    }
}
