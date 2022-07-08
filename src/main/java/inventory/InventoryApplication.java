package inventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class InventoryApplication extends Application {
    /**
     *
     * @param stage the stage for launching the app
     * @throws IOException if mainForm can't be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Inventory.class.getResource("mainForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 808, 400);

        ((TableView<Part>) scene.lookup("#partTable")).setItems(Inventory.getAllParts());
        ((TableView<Product>) scene.lookup("#productTable")).setItems(Inventory.getAllProducts());

        stage.setTitle("Inventory Management");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}