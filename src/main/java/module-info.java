module inventory.inventorymanagement {
    requires javafx.controls;
    requires javafx.fxml;


    opens inventory to javafx.fxml;
    exports inventory;
}