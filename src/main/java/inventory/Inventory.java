package inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class Inventory {
    static ObservableList<Part> allParts = FXCollections.observableList(new ArrayList<>());
    static ObservableList<Product> allProducts = FXCollections.observableList(new ArrayList<>());

    /**
     * Adds a new part to allParts.
     *
     * @param newPart
     */
    static public void addPart(Part newPart) {
        allParts.add(newPart);
    }

    /**
     * Add a new product to allProducts.
     *
     * @param newProduct
     */
    static public void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    /**
     * Searches allParts for a matching ID.
     *
     * @param partId id to search.
     * @return Part with matching id.
     * @exception RuntimeException if the id is not found.
     */
    static public Part lookupPart(int partId) {
        for (Part part : allParts) {
            if (part.getId() == partId) {
                return part;
            }
        }

        throw new RuntimeException("Could not find any parts with that ID");
    }

    /**
     * Searches allParts for parts with a name containing the given string.
     *
     * @param partName the string to compare against each part name.
     * @return ObservableList of parts that match.
     */
    static public ObservableList<Part> lookupPart(String partName) {
        ArrayList<Part> matchingParts = new ArrayList<>();
        ObservableList<Part> observableListMatchingParts = FXCollections.observableList(matchingParts);

        for (Part part: allParts) {
            if (part.getName().toLowerCase().contains(partName.toLowerCase())) {
                observableListMatchingParts.add(part);
            }
        }

        return observableListMatchingParts;
    }

    /**
     * Searches  allProducts for a matching ID.
     *
     * @param productId id to search
     * @return Product with matching id
     * @exception RuntimeException If the id is not found.
     */
    static public Product lookupProduct(int productId) {
        for (Product product : allProducts) {
            if (product.getId() == productId) {
                return product;
            }
        }

        throw new RuntimeException("Could not find any products with that ID");
    }

    /**
     * Searches for all product names containing a given string.
     *
     * @param productName string to compare against each product name
     * @return ObservableList of products that match
     */
    static public ObservableList<Product> lookupProduct(String productName) {
        ArrayList<Product> matchingProducts = new ArrayList<>();
        ObservableList<Product> observableListMatchingProduct = FXCollections.observableList(matchingProducts);

        for (Product product: allProducts) {
            if (product.getName().toLowerCase().contains(productName.toLowerCase())) {
                observableListMatchingProduct.add(product);
            }
        }

        return observableListMatchingProduct;
    }

    /**
     * Updates a given index in allParts to a new part.
     *
     * @param index index to change
     * @param selectedPart new part
     */
    static public void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    /**
     * Updates a given index in allProducts to a new product.
     *
     * @param index index to change
     * @param selectedProduct new product
     */
    static public void updateProduct(int index, Product selectedProduct) {
        allProducts.set(index, selectedProduct);
    }

    /**
     * Removed a part from allParts.
     *
     * @param selectedPart
     * @return true if removed, otherwise false
     */
    static public boolean deletePart(Part selectedPart) {
        try {
            allParts.remove(selectedPart);

            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Removes a product from allProducts.
     *
     * @param selectedProduct product to delete
     * @return true if removed, otherwise false
     */
    static public boolean deleteProduct(Product selectedProduct) {
        try {
            allProducts.remove(selectedProduct);

            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    /**
     *
     * @return allParts
     */
    static public ObservableList<Part> getAllParts() {
        return allParts;
    }

    /**
     *
     * @return allProducts
     */
    static public ObservableList<Product> getAllProducts() {
        return allProducts;
    }
}
