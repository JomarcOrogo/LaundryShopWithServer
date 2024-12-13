import java.io.Serializable;

public class LaundryOrder implements Serializable {
    private String customerName;
    private String contactNumber;
    private String packageType;
    private double weight;
    private double price;
    private boolean completed;
    private boolean readyForPickup;

    public LaundryOrder(String customerName, String contactNumber, String packageType, double weight, double price) {
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.packageType = packageType;
        this.weight = weight;
        this.price = price;
        this.completed = false;
        this.readyForPickup = false;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isReadyForPickup() {
        return readyForPickup;
    }

    public void setReadyForPickup(boolean readyForPickup) {
        this.readyForPickup = readyForPickup;
    }

    @Override
    public String toString() {
        return customerName + " (" + packageType + ") - Weight: " + weight + "kg, Price: ₱" + price
                + ", Completed: " + completed + ", Ready for Pickup: " + readyForPickup;
    }
}
