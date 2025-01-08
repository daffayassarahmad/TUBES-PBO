import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    private ObservableList<Medicine> medicines = FXCollections.observableArrayList();
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    // UI Components
    private TableView<Medicine> medicineTable;
    private TableView<CartItem> cartTable;
    private TextField usernameField, passwordField, nameField, categoryField, priceField, stockField, quantityField;
    private Label totalLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Kasir Apotek");

        // Scene pertama: Login
        VBox loginLayout = new VBox(10);
        loginLayout.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(e -> handleLogin(primaryStage));

        loginLayout.getChildren().addAll(usernameField, passwordField, loginButton);
        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    // Proses Login
    private void handleLogin(Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if ("admin".equals(username) && "admin123".equals(password)) {
            showDashboard(primaryStage);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials", "Please check your username and password.");
        }
    }

    // Menampilkan Dashboard setelah Login
    private void showDashboard(Stage primaryStage) {
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setStyle("-fx-padding: 20; -fx-background-color: #e8e8e8;");
        
        // Membuat tabel untuk menampilkan obat
        medicineTable = new TableView<>();
        
        TableColumn<Medicine, String> nameColumn = new TableColumn<>("Nama Obat");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        TableColumn<Medicine, String> categoryColumn = new TableColumn<>("Kategori");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        
        TableColumn<Medicine, Double> priceColumn = new TableColumn<>("Harga");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        
        TableColumn<Medicine, Integer> stockColumn = new TableColumn<>("Stok");
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        
        medicineTable.getColumns().addAll(nameColumn, categoryColumn, priceColumn, stockColumn);
        loadMedicines(); // Load data obat
        
        // Ketika pengguna memilih obat, tambahkan ke keranjang
        medicineTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Medicine selectedMedicine = medicineTable.getSelectionModel().getSelectedItem();
                if (selectedMedicine != null) {
                    showAddToCartDialog(selectedMedicine);
                }
            }
        });

        // Membuat tombol untuk menambah obat baru
        Button addMedicineButton = new Button("Tambah Obat Baru");
        addMedicineButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addMedicineButton.setOnAction(e -> showAddMedicineDialog());

        // Membuat tabel untuk keranjang belanja
        cartTable = new TableView<>();
        
        TableColumn<CartItem, String> cartNameColumn = new TableColumn<>("Nama Obat");
        cartNameColumn.setCellValueFactory(cellData -> cellData.getValue().medicineNameProperty());
        
        TableColumn<CartItem, Integer> cartQuantityColumn = new TableColumn<>("Quantity");
        cartQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        
        TableColumn<CartItem, Double> cartPriceColumn = new TableColumn<>("Total Harga");
        cartPriceColumn.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asObject());

        cartTable.getColumns().addAll(cartNameColumn, cartQuantityColumn, cartPriceColumn);

        // Tombol Checkout
        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        checkoutButton.setOnAction(e -> showCheckoutDialog());

        // Menampilkan tabel dan tombol
        dashboardLayout.getChildren().addAll(medicineTable, addMedicineButton, cartTable, checkoutButton);
        Scene dashboardScene = new Scene(dashboardLayout, 600, 500);
        primaryStage.setScene(dashboardScene);
        primaryStage.show();
    }