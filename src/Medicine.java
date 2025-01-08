public class Medicine {
    private String name;
    private String category;
    private double price;
    private int stock;

    public Medicine(String name, String category, double price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    // JavaFX Property
    public javafx.beans.property.StringProperty nameProperty() {
        return new javafx.beans.property.SimpleStringProperty(name);
    }

    public javafx.beans.property.StringProperty categoryProperty() {
        return new javafx.beans.property.SimpleStringProperty(category);
    }

    public javafx.beans.property.DoubleProperty priceProperty() {
        return new javafx.beans.property.SimpleDoubleProperty(price);
    }

    public javafx.beans.property.IntegerProperty stockProperty() {
        return new javafx.beans.property.SimpleIntegerProperty(stock);
    }
}
