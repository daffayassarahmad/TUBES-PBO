public class CartItem {
    private final Medicine medicine;
    private int quantity;

    public CartItem(Medicine medicine, int quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
    }

    public String getMedicineName() {
        return medicine.getName();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return medicine.getPrice() * quantity;
    }

    // JavaFX Property
    public javafx.beans.property.StringProperty medicineNameProperty() {
        return new javafx.beans.property.SimpleStringProperty(medicine.getName());
    }

    public javafx.beans.property.IntegerProperty quantityProperty() {
        return new javafx.beans.property.SimpleIntegerProperty(quantity);
    }

    public javafx.beans.property.DoubleProperty totalPriceProperty() {
        return new javafx.beans.property.SimpleDoubleProperty(getTotalPrice());
    }
}
