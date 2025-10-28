package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Product;
import com.wileyedge.flooring.exceptions.PersistenceException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

@Component
public class ProductDaoFileImpl implements ProductDao {

    private final String PRODUCT_FILE;
    private static final String DELIMITER = ",";
    private Map<String, Product> products = new HashMap<>();

    public ProductDaoFileImpl() {
        this.PRODUCT_FILE = "Data/Products.txt";
    }

    public ProductDaoFileImpl(String productFile) {
        this.PRODUCT_FILE = productFile;
    }

    @Override
    public List<Product> getAllProducts() throws PersistenceException {
        loadProducts();
        return new ArrayList<>(products.values());
    }

    @Override
    public Product getProduct(String productType) throws PersistenceException {
        loadProducts();
        return products.get(productType);
    }

    private void loadProducts() throws PersistenceException {
        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load product data.", e);
        }

        products.clear();
        String currentLine;
        Product currentProduct;

        // Skip header line
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentProduct = unmarshallProduct(currentLine);
            products.put(currentProduct.getProductType(), currentProduct);
        }

        scanner.close();
    }

    private Product unmarshallProduct(String productAsText) {
        String[] productTokens = productAsText.split(DELIMITER);

        String productType = productTokens[0];
        BigDecimal costPerSquareFoot = new BigDecimal(productTokens[1]);
        BigDecimal laborCostPerSquareFoot = new BigDecimal(productTokens[2]);

        Product product = new Product(productType, costPerSquareFoot, laborCostPerSquareFoot);

        return product;
    }
}