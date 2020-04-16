package model;

import com.google.common.base.Objects;

public class Order {
    private final String orderId;
    private final ServiceType serviceType;

    public Order(String agencyName, String internalOrderId, ServiceType serviceType) {
        this.orderId = agencyName + "###" + internalOrderId;
        this.serviceType = serviceType;
    }

    public String createMessage() {
        return orderId + "###" + serviceType;
    }

    public String getOrderId() {
        return orderId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equal(orderId, order.orderId) &&
                serviceType == order.serviceType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId, serviceType);
    }
}
