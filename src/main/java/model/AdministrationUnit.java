package model;

import com.rabbitmq.client.*;

import java.io.IOException;

public abstract class AdministrationUnit {
    Consumer createAdministrativeConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Message from admin: " + message);
            }
        };
    }

}
