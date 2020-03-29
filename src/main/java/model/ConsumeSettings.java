package model;

public class ConsumeSettings {
    private final String queueName;
    private final String exchangeName;
    private final String routingKey;

    public ConsumeSettings(String queueName, String exchangeName, String routingKey) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
