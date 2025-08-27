package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order implements Comparable<Order> {
    private final String orderId;
    private String customerName;
    private String shippingAddress;
    private List<BookItem> items;
    private String status;

    public Order(String customerName, String shippingAddress) {
        this.orderId = UUID.randomUUID().toString().substring(0,8);
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.items = new ArrayList<>();
        this.status = "Pending";
    }

    public void addItem(BookItem b) { items.add(b); }
    public List<BookItem> getItems() { return items; }
    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getCustomerName() { return customerName; }
    public String getShippingAddress() { return shippingAddress; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ").append(orderId).append("\n")
          .append("Customer: ").append(customerName).append("\n")
          .append("Address: ").append(shippingAddress).append("\n")
          .append("Status: ").append(status).append("\n")
          .append("Items:\n");
        for (BookItem i : items) {
            sb.append("  - ").append(i).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Order o) {
        return this.orderId.compareTo(o.orderId);
    }
}


