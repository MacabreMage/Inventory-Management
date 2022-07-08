package inventory;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class InventoryController {
    private static int partId = 0;
    private static int productId = 0;

    /**
     *
     * @return unique part id
     */
    private static int nextPartId() {
        partId += 1;

        return partId;
    }

    /**
     *
     * @return unique product id
     */
    private static int nextProductId() {
        productId += 1;

        return productId;
    }

    /**
     * Exits the application.
     *
     * @param event click event
     */
    @FXML
    public void onExitButtonClick(ActionEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Redirects user to the mainForm.
     *
     * @param event click event
     * @throws IOException If the mainForm can not be opened.
     */
    @FXML
    public void onCancelButtonClick(ActionEvent event) throws IOException {
        Scene scene = ((Button) event.getSource()).getScene();
        Parent root = scene.getRoot();

        Parent pane = FXMLLoader.load(Objects.requireNonNull(Inventory.class.getResource("mainForm.fxml")));
        root.getScene().setRoot(pane);

        Scene main = pane.getScene();
        main.getWindow().setHeight(450);
        main.getWindow().setWidth(830);

        ((TableView<Part>) scene.lookup("#partTable")).setItems(Inventory.getAllParts());
        ((TableView<Product>) scene.lookup("#productTable")).setItems(Inventory.getAllProducts());
    }

    /**
     * Redirects user to the addPart form.
     *
     * @param event click event
     */
    @FXML
    public void onAddPartClick(ActionEvent event) throws IOException {
        Scene scene = ((Button) event.getSource()).getScene();
        Parent root = scene.getRoot();

        Parent pane = FXMLLoader.load
                (Objects.requireNonNull(Inventory.class.getResource("addPartForm.fxml")));
        root.getScene().setRoot(pane);

        Scene addPart = pane.getScene();
        addPart.getWindow().setHeight(582);
        addPart.getWindow().setWidth(600);
    }

    /**
     * Redirects user to the modifyPart form.
     *
     * @param event click event
     */
    @FXML
    public void onModifyPartClick(ActionEvent event) throws IOException {
        Scene mainScene = ((Button)event.getSource()).getScene();

        TableView<Part> partTable = (TableView<Part>) mainScene.lookup("#partTable");

        Integer index = partTable.getSelectionModel().getSelectedIndex();
        Part part = partTable.getSelectionModel().getSelectedItem();

        if (part != null) {
            Parent root = mainScene.getRoot();

            Parent pane = FXMLLoader.load
                    (Objects.requireNonNull(Inventory.class.getResource("modifyPartForm.fxml")));
            root.getScene().setRoot(pane);

            Scene addPart = pane.getScene();
            addPart.getWindow().setHeight(582);
            addPart.getWindow().setWidth(600);

            addPart.setUserData(index);

            try {
                ((TextField) addPart.lookup("#id")).setText(String.valueOf(part.getId()));
                ((TextField) addPart.lookup("#name")).setText(String.valueOf(part.getName()));
                ((TextField) addPart.lookup("#inv")).setText(String.valueOf(part.getStock()));
                ((TextField) addPart.lookup("#max")).setText(String.valueOf(part.getMax()));
                ((TextField) addPart.lookup("#min")).setText(String.valueOf(part.getMin()));
                ((TextField) addPart.lookup("#price")).setText(String.valueOf(part.getPrice()));

                if (part.getClass().getName().equals("inventory.InHouse")) {
                    InHouse inhouse = (InHouse) part;
                    ((TextField) addPart.lookup("#idCompanyName"))
                            .setText(String.valueOf((inhouse.getMachineId())));

                    ((RadioButton) addPart.lookup("#inHouse")).setSelected(true);
                } else if (part.getClass().getName().equals("inventory.Outsourced")) {
                    Outsourced outsourced = (Outsourced) part;
                    ((TextField) addPart.lookup("#idCompanyName"))
                            .setText(String.valueOf((outsourced.getCompanyName())));

                    ((RadioButton) addPart.lookup("#outsourced")).setSelected(true);
                }
            }
            catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Could not open part for modifying.\n\n" + e);

                alert.show();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("There are no parts in the system.");

            alert.show();
        }
    }

    /**
     * Prompts user to remove the selected part from the part pane.
     *
     * @param event click event
     */
    @FXML
    public void onRemovePartClick(ActionEvent event) {
        try {
            Scene scene = ((Button) event.getSource()).getScene();

            TableView<Part> partTable = ((TableView<Part>) scene.lookup("#partTable"));

            StringBuilder message = new StringBuilder("This part is associated with these products:\n");
            int count = 0;
            for (Product product : Inventory.getAllProducts())
            {
                if (product.getAllAssociatedParts().contains(partTable.getSelectionModel().getSelectedItem())) {
                     message.append("\t").append(product.getName()).append("\n");
                     count++;
                }
            }

            if (count > 0) {
                ((Label) scene.lookup("#MessageLabel")).setText("Failed to removed part...");
                throw new RuntimeException(message.toString());
            }

            if (partTable.getItems().size() > 0) {
                Part selectedPart = partTable.getSelectionModel().getSelectedItem();
                if (selectedPart == null) {
                    throw new RuntimeException("No part selected.");
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Are you sure you want to remove this part from inventory?");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK) {
                    boolean deleted = Inventory
                            .deletePart(selectedPart);

                    if (!deleted) {
                        Alert dialog = new Alert(Alert.AlertType.ERROR);
                        dialog.setContentText("Failed to remove part from inventory.");

                        dialog.show();
                    }
                    else
                    {
                        ((Label) scene.lookup("#MessageLabel")).setText("Successfully removed part...");
                    }
                }
            }
            else {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setContentText("Part list is empty.");

                dialog.show();
            }
        }
        catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());

            alert.show();
        }
    }

    /**
     * Searches parts in inventory.
     *
     * @param event text entered event
     */
    @FXML
    public void onPartSearch(KeyEvent event) {
        Scene scene = ((TextField) event.getSource()).getScene();

        TextField searchBox = (TextField) scene.lookup("#partSearch");
        TableView<Part> partTable = ((TableView<Part>) scene.lookup("#partTable"));

        partTable.setItems(Inventory.getAllParts());
        partTable.getSelectionModel().clearSelection();

        if (!searchBox.getText().isBlank()) {
            try {
                partTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                int id = Integer.parseInt(searchBox.getText());

                partTable.getSelectionModel().select(Inventory.lookupPart(id));
                partTable.scrollTo(partTable.getSelectionModel().getSelectedIndex());
            }
            catch (NumberFormatException e) {
                try {
                    String name = searchBox.getText();

                    partTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                    ObservableList<Part> filteredParts = Inventory.lookupPart(name);

                    partTable.setItems(filteredParts);
                }
                catch (RuntimeException ex) {
                    Alert error = new Alert(Alert.AlertType.INFORMATION);
                    error.setContentText(ex.getMessage());

                    error.show();
                }
            }
            catch (RuntimeException e)
            {
                Alert error = new Alert(Alert.AlertType.INFORMATION);
                error.setContentText(e.getMessage());

                error.show();
            }
        }
        else {
            // Rebind table to all parts
            partTable.setItems(Inventory.getAllParts());
        }
    }

    /**
     * Redirects user to the addProduct form.
     *
     * @param event click event
     */
    @FXML
    public void onAddProductClick(ActionEvent event) throws IOException {
        Scene scene = ((Button) event.getSource()).getScene();
        Parent root = scene.getRoot();

        Parent pane = FXMLLoader.load
                (Objects.requireNonNull(Inventory.class.getResource("addProductForm.fxml")));
        root.getScene().setRoot(pane);

        Scene addPart = pane.getScene();
        addPart.getWindow().setHeight(675.0);
        addPart.getWindow().setWidth(925.0);

        ArrayList<Object> userData = new ArrayList<>();
        Product newProduct = new Product(-1, null, 1 ,1, 1, 1);

        userData.add(newProduct);
        userData.add(0);
        scene.setUserData(userData);

        ObservableList<Part> parts = newProduct.getAllAssociatedParts();

        TableView<Part> partTable = ((TableView<Part>) scene.lookup("#partTable"));
        TableView<Part> associatedParts = ((TableView<Part>) scene.lookup("#associatedParts"));

        partTable.setItems(Inventory.getAllParts());
        associatedParts.setItems(parts);
    }

    /**
     * Redirects user to the modifyProduct form.
     *
     * @param event click event
     */
    @FXML
    public void onModifyProductClick(ActionEvent event) {
        Scene mainScene = ((Button) event.getSource()).getScene();

        TableView<Product> productTable = ((TableView<Product>) mainScene.lookup("#productTable"));

        try {
            ArrayList<Object> userData = new ArrayList<>();

            Product oldProduct = productTable.getItems().get(productTable.getSelectionModel().getSelectedIndex());
            Integer index = productTable.getSelectionModel().getSelectedIndex();

            userData.add(oldProduct);
            userData.add(index);

            if (oldProduct != null) {
                Parent root = mainScene.getRoot();

                Parent pane = FXMLLoader.load
                        (Objects.requireNonNull(Inventory.class.getResource("modifyProductForm.fxml")));
                root.getScene().setRoot(pane);

                Scene modifyProduct = pane.getScene();
                modifyProduct.getWindow().setHeight(675.0);
                modifyProduct.getWindow().setWidth(925.0);

                modifyProduct.setUserData(userData);

                ((TextField) modifyProduct.lookup("#id")).setText(String.valueOf(oldProduct.getId()));
                ((TextField) modifyProduct.lookup("#name")).setText(String.valueOf(oldProduct.getName()));
                ((TextField) modifyProduct.lookup("#inv")).setText(String.valueOf(oldProduct.getStock()));
                ((TextField) modifyProduct.lookup("#max")).setText(String.valueOf(oldProduct.getMax()));
                ((TextField) modifyProduct.lookup("#min")).setText(String.valueOf(oldProduct.getMin()));
                ((TextField) modifyProduct.lookup("#price")).setText(String.valueOf(oldProduct.getPrice()));

                TableView<Part> partTable = ((TableView<Part>) modifyProduct.lookup("#partTable"));
                TableView<Part> associatedParts = ((TableView<Part>) modifyProduct.lookup("#associatedParts"));

                partTable.setItems(Inventory.getAllParts());
                associatedParts.setItems(oldProduct.getAllAssociatedParts());
            }
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("No product selected.");

            alert.show();
        }
    }

    /**
     * Prompts user to remove selected product from products pane.
     *
     * @param event click event
     */
    @FXML
    public void onRemoveProductClick(ActionEvent event) {
        Scene scene = ((Button) event.getSource()).getScene();
        TableView<Product> productTable = ((TableView<Product>) scene.lookup("#productTable"));

        try {
            if (productTable.getItems().size() > 0) {
                Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
                if (selectedProduct == null) {
                    throw new RuntimeException("No product selected.");
                }

                if (selectedProduct.getAllAssociatedParts().size() > 0)
                {
                    throw new RuntimeException("Can not remove a product that has associated parts.");
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Are you sure you want to remove this product from inventory?");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK) {
                    boolean deleted = Inventory
                            .deleteProduct(selectedProduct);

                    if (!deleted && alert.getResult() != ButtonType.CANCEL) {
                        Alert dialog = new Alert(Alert.AlertType.ERROR);
                        dialog.setContentText("Failed to remove part from inventory.");

                        dialog.show();
                    }
                    else
                    {
                        ((Label) scene.lookup("#MessageLabel")).setText("Successfully removed product...");
                    }
                }
            } else {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setContentText("Product list is empty.");

                dialog.show();
            }
        }
        catch (RuntimeException e) {
            ((Label) scene.lookup("#MessageLabel")).setText("Failed to remove product...");
            Alert runtimeError = new Alert(Alert.AlertType.ERROR);
            runtimeError.setContentText(e.getMessage());

            runtimeError.show();
        }
    }

    /**
     * Searches products in inventory.
     *
     * @param event text entered event
     */
    @FXML
    public void onProductSearch(KeyEvent event) {
        Scene scene = ((TextField) event.getSource()).getScene();
        TextField searchBox = (TextField) scene.lookup("#productSearch");
        TableView<Product> productTable = ((TableView<Product>) scene.lookup("#productTable"));

        productTable.setItems(Inventory.getAllProducts());
        productTable.getSelectionModel().clearSelection();

        if (!searchBox.getText().isBlank()) {
            try {
                productTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                int id = Integer.parseInt(searchBox.getText());

                productTable.getSelectionModel().select(Inventory.lookupProduct(id));
                productTable.scrollTo(productTable.getSelectionModel().getSelectedIndex());
            }
            catch (NumberFormatException e) {
                try {
                    String name = searchBox.getText();

                    ObservableList<Product> filteredProducts = Inventory.lookupProduct(name);
                    productTable.setItems(filteredProducts);
                }
                catch (RuntimeException ex) {
                    Alert error = new Alert(Alert.AlertType.INFORMATION);
                    error.setContentText(ex.getMessage());

                    error.show();
                }
            }
            catch (RuntimeException e) {
                Alert error = new Alert(Alert.AlertType.INFORMATION);
                error.setContentText(e.getMessage());

                error.show();
            }
        }
        else {
            productTable.setItems(Inventory.getAllProducts());
        }
    }

    /**
     * Changes part creation to inHouse.
     *
     * @see InHouse
     * @param event click event
     */
    @FXML
    public void inHouseRadio(ActionEvent event) {
        Scene scene = ((RadioButton)event.getSource()).getScene();

        RadioButton radio = (RadioButton) scene.lookup("#outsourced");
        radio.setSelected(false);

        Label label = (Label) scene.lookup("#mIdCompanyName");
        label.setText("Machine ID");

        scene.lookup("#idCompanyName").setOnKeyReleased(this::validateInt);
    }

    /**
     * Sets part creation to Outsourced.
     *
     * @see Outsourced
     * @param event click event
     */
    @FXML
    public void outsourcedRadio(ActionEvent event) {
        Scene scene = ((RadioButton)event.getSource()).getScene();

        RadioButton radio = (RadioButton) scene.lookup("#inHouse");
        radio.setSelected(false);

        Label label = (Label) scene.lookup("#mIdCompanyName");
        label.setText("Company Name");

        scene.lookup("#idCompanyName").setOnKeyReleased(null);
    }

    /**
     * Creates Part and adds it to Inventory
     *
     * @see Part
     * @see Inventory
     * @param event click event
     */
    @FXML
    public void onSavePart(ActionEvent event) {
        Scene scene = ((Button)event.getSource()).getScene();

        try {
            String name = ((TextField) scene.lookup("#Name")).getText();
            int inv = Integer.parseInt(((TextField) scene.lookup("#Inv")).getText());
            int max = Integer.parseInt(((TextField) scene.lookup("#Max")).getText());
            int min = Integer.parseInt(((TextField) scene.lookup("#Min")).getText());
            double price = Double.parseDouble(((TextField) scene.lookup("#Price")).getText());

            if (max < min) {
                throw new RuntimeException("Max can not be less than Min.");
            }
            if (inv > max || inv < min) {
                throw new RuntimeException("Inv must be between Min and Max values");
            }

            if (((RadioButton)(scene.lookup("#inHouse"))).isSelected()) {
                int machineId = Integer.parseInt(((TextField) scene.lookup("#idCompanyName")).getText());
                int id = nextPartId();
                InHouse part = new InHouse(id, name, price, inv, min, max, machineId);

                Inventory.addPart(part);
            }
            else if (((RadioButton)(scene.lookup("#outsourced"))).isSelected()) {
                String companyName = ((TextField) scene.lookup("#idCompanyName")).getText();
                int id = nextPartId();
                Outsourced part = new Outsourced(id, name, price, inv, min, max, companyName);

                Inventory.addPart(part);
            }

            onCancelButtonClick(event);
        }
        catch (NumberFormatException e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText("Incorrect data type.");
            typeError.show();
        }
        catch (RuntimeException e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText(e.getMessage());
            typeError.show();
        }
        catch (Exception e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText("Unexpected Error.");
            typeError.show();
        }
    }

    /**
     * Adds a selected part to the products associatedParts.
     *
     * @see Part
     * @see Product
     * @param event click event
     */
    @FXML
    public void onAddAssociatedPartClick(ActionEvent event) {
        Scene scene = ((Button) event.getSource()).getScene();

        ArrayList<Object> userData = (ArrayList<Object>) scene.getUserData();
        Product product = (Product) userData.get(0);

        TableView<Part> partTable = (TableView<Part>) scene.lookup("#partTable");

        Part part = partTable.getSelectionModel().getSelectedItem();

        try {
            if (part != null) {
                product.addAssociatedPart(part);
                ((Label) scene.lookup("#MessageLabel")).setText("Successfully added part...");
            } else {
                throw new RuntimeException("No part selected.");
            }
        }
        catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());

            alert.show();
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());

            alert.show();
        }
    }

    /**
     * Removes a selected part from the products associatedParts.
     * @see Part
     * @see Product
     * @param event click event
     */
    @FXML
    public void onRemoveAssociatedPartClick(ActionEvent event) {
        Scene scene = ((Button) event.getSource()).getScene();

        Product product = (Product) ((ArrayList<Object>) (scene.getUserData())).get(0);
        TableView<Part> associatedParts = ((TableView<Part>) scene.lookup("#associatedParts"));

        if (associatedParts.getSelectionModel().getSelectedItem() != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setContentText("Are you sure you want to remove the associated part?");
            confirmation.showAndWait();

            try {
                if (confirmation.getResult() == ButtonType.OK) {
                    boolean removed =
                            product.deleteAssociatedPart(associatedParts.getSelectionModel().getSelectedItem());

                    if (!removed) {
                        throw new RuntimeException("Failed to remove associated part.");
                    }
                    else {
                        ((Label) scene.lookup("#MessageLabel")).setText("Successfully removed part...");
                    }
                }
            } catch (RuntimeException e) {
                ((Label) scene.lookup("#MessageLabel")).setText("Failed to remove part...");
                Alert error = new Alert(Alert.AlertType.INFORMATION);
                error.setContentText(e.getMessage());

                error.show();
            }
        }
    }

    /**
     * Creates a new product and adds it to Inventory.
     *
     * @param event click event
     */
    @FXML
    public void onSaveProductClick(ActionEvent event) {
        Scene scene = ((Button)event.getSource()).getScene();

        try {
            Product product = (Product) ((ArrayList<Object>) (scene.getUserData())).get(0);

            product.setName(((TextField) scene.lookup("#name")).getText());
            product.setStock(Integer.parseInt(((TextField) scene.lookup("#inv")).getText()));
            product.setMax(Integer.parseInt(((TextField) scene.lookup("#max")).getText()));
            product.setMin(Integer.parseInt(((TextField) scene.lookup("#min")).getText()));
            product.setPrice(Double.parseDouble(((TextField) scene.lookup("#price")).getText()));

            if (product.getMax() < product.getMin()) {
                throw new RuntimeException("Max can not be less than min.");
            }

            if (product.getStock() > product.getMax() || product.getStock() < product.getMin()) {
                throw new RuntimeException("Inv must be between Min and Max values");
            }

            product.setId(nextProductId());
            
            Inventory.addProduct(product);
            onCancelButtonClick(event);
        }
        catch (NumberFormatException e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText("Incorrect data type entered.");
            typeError.show();
        }
        catch (RuntimeException e) {
            Alert runTimeError = new Alert(Alert.AlertType.ERROR);
            runTimeError.setContentText(e.getMessage());
            runTimeError.show();
        }
        catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Unexpected Error.");

            error.show();
        }
    }

    /**
     *
     *
     * @param event click event
     */
    @FXML
    public void onModifyPartSaveClick(ActionEvent event) {
        Scene scene = ((Button)event.getSource()).getScene();

        try {
            int id = Integer.parseInt(((TextField) scene.lookup("#id")).getText());
            String name = ((TextField) scene.lookup("#name")).getText();
            int inv = Integer.parseInt(((TextField) scene.lookup("#inv")).getText());
            int max = Integer.parseInt(((TextField) scene.lookup("#max")).getText());
            int min = Integer.parseInt(((TextField) scene.lookup("#min")).getText());
            double price = Double.parseDouble(((TextField) scene.lookup("#price")).getText());

            if (max < min) {
                throw new RuntimeException("Max can not be less than Min.");
            }
            if (inv > max || inv < min) {
                throw new RuntimeException("Inv must be between Min and Max values");
            }

            if (((RadioButton)(scene.lookup("#inHouse"))).isSelected()) {
                int machineId = Integer.parseInt(((TextField) scene.lookup("#idCompanyName")).getText());
                InHouse part = new InHouse(id, name, price, inv, min, max, machineId);

                Inventory.updatePart((Integer) scene.getUserData(), part);

                onCancelButtonClick(event);
            }
            else if (((RadioButton)(scene.lookup("#outsourced"))).isSelected()) {
                String companyName = ((TextField) scene.lookup("#idCompanyName")).getText();
                Outsourced part = new Outsourced(id, name, price, inv, min, max, companyName);

                Inventory.updatePart((Integer) scene.getUserData(), part);

                onCancelButtonClick(event);
            }
        }
        catch (NumberFormatException e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText("Incorrect data type.");
            typeError.show();
        }
        catch (Exception e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText(e.getMessage());
            typeError.show();
        }
    }

    /**
     *
     * @param event click event
     */
    @FXML
    public void onModifyProductSave(ActionEvent event) {
        Scene scene = ((Button)event.getSource()).getScene();

        try {
            Product product = (Product) ((ArrayList<Object>) scene.getUserData()).get(0);
            Integer index = (Integer) ((ArrayList<Object>) scene.getUserData()).get(1);

            product.setId(Integer.parseInt(((TextField) scene.lookup("#id")).getText()));
            product.setName(((TextField) scene.lookup("#name")).getText());
            product.setStock(Integer.parseInt(((TextField) scene.lookup("#inv")).getText()));
            product.setMax(Integer.parseInt(((TextField) scene.lookup("#max")).getText()));
            product.setMin(Integer.parseInt(((TextField) scene.lookup("#min")).getText()));
            product.setPrice(Double.parseDouble(((TextField) scene.lookup("#price")).getText()));

            if (product.getMax() < product.getMin()) {
                throw new RuntimeException("Max can not be less than min.");
            }

            if (product.getStock() > product.getMax() || product.getStock() < product.getMin()) {
                throw new RuntimeException("Inv must be between Min and Max values");
            }

            Inventory.updateProduct(index, product);

            onCancelButtonClick(event);
        }
        catch (NumberFormatException e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText("Incorrect data type.");
            typeError.show();
        }
        catch (RuntimeException e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText(e.getMessage());
            typeError.show();
        }
        catch (Exception e) {
            Alert typeError = new Alert(Alert.AlertType.ERROR);
            typeError.setContentText("Unexpected Error.");
            typeError.show();
        }
    }

    /**
     * Verify that an entered value is an int.
     *
     * @param event text entered event
     */
    @FXML
    public void validateInt(KeyEvent event) {
        try {
            if (!((TextField) event.getSource()).getText().isBlank()) {
                Integer.parseInt(((TextField) event.getSource()).getText());
            }
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid data type entered. Must be an integer.");

            alert.show();
        }
    }

    /**
     * Verify that an entered value is a double.
     *
     * @param event text entered event
     */
    @FXML
    public void validateDouble(KeyEvent event) {
        try {
            if (!((TextField) event.getSource()).getText().isBlank()) {
                Double.parseDouble(((TextField) event.getSource()).getText());
            }
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid data type entered. Must be an decimal.");

            alert.show();
        }
    }
}
