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

    // Load data obat
    private void loadMedicines() {
        medicines.add(new Medicine("Paracetamol", "Tablet", 5000, 100));
        medicines.add(new Medicine("Amoxicillin", "Kapsul", 15000, 50));
        medicineTable.setItems(medicines);
    }

    // Menampilkan dialog untuk menambah produk ke keranjang
    private void showAddToCartDialog(Medicine selectedMedicine) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tambah ke Keranjang");
        dialog.setHeaderText("Masukkan jumlah obat yang ingin dibeli");
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty > 0 && qty <= selectedMedicine.getStock()) {
                    addToCart(selectedMedicine, qty);
                } else {
                    showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Jumlah tidak valid", "Masukkan jumlah yang sesuai dengan stok.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Masukkan angka yang valid", "Quantity harus berupa angka.");
            }
        });
    }

    // Menambah obat ke keranjang
    private void addToCart(Medicine medicine, int quantity) {
        // Cek jika obat sudah ada di keranjang
        CartItem existingItem = null;
        for (CartItem item : cartItems) {
            if (item.getMedicineName().equals(medicine.getName())) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(medicine, quantity);
            cartItems.add(newItem);
        }
        cartTable.setItems(cartItems);
    }

    // Menampilkan dialog Checkout
    private void showCheckoutDialog() {
        double total = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        showAlert(Alert.AlertType.INFORMATION, "Checkout Successful", "Pembayaran Berhasil", "Total pembayaran: Rp " + total);
        cartItems.clear(); // Kosongkan keranjang setelah checkout
        cartTable.setItems(cartItems);
    }

    // Menampilkan dialog untuk menambah obat baru
    private void showAddMedicineDialog() {
        // Membuat form untuk menambah obat baru
        VBox addMedicineLayout = new VBox(10);
        addMedicineLayout.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

        nameField = new TextField();
        nameField.setPromptText("Nama Obat");

        categoryField = new TextField();
        categoryField.setPromptText("Kategori");

        priceField = new TextField();
        priceField.setPromptText("Harga");

        stockField = new TextField();
        stockField.setPromptText("Stok");

        Button saveButton = new Button("Simpan");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> handleAddMedicine());

        Button cancelButton = new Button("Batal");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> handleCloseDialog());

        addMedicineLayout.getChildren().addAll(nameField, categoryField, priceField, stockField, saveButton, cancelButton);

        Stage addMedicineStage = new Stage();
        Scene addMedicineScene = new Scene(addMedicineLayout, 300, 250);
        addMedicineStage.setScene(addMedicineScene);
        addMedicineStage.setTitle("Tambah Obat Baru");
        addMedicineStage.show();
    }

    // Menambahkan obat baru
    private void handleAddMedicine() {
        String name = nameField.getText();
        String category = categoryField.getText();
        double price = Double.parseDouble(priceField.getText());
        int stock = Integer.parseInt(stockField.getText());

        Medicine newMedicine = new Medicine(name, category, price, stock);
        medicines.add(newMedicine);
        handleCloseDialog();
    }

    // Menutup dialog
    private void handleCloseDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    // Menampilkan alert
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}