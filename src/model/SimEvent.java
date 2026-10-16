package model;

public class SimEvent {
    private int simId;
    private int orderId;
    private String simTime;
    private String status;
    private String eventType;
    private boolean peakHour;
    private int customerCount;

    public int getSimId() { return simId; }
    public void setSimId(int simId) { this.simId = simId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getSimTime() { return simTime; }
    public void setSimTime(String simTime) { this.simTime = simTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public boolean isPeakHour() { return peakHour; }
    public void setPeakHour(boolean peakHour) { this.peakHour = peakHour; }
    public int getCustomerCount() { return customerCount; }
    public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }
}