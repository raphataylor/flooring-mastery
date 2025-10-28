package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.exceptions.PersistenceException;
import com.wileyedge.flooring.model.Product;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProductDaoFileImpl implements ProductDao {

    private static final String PRODUCT_FILE = "Data/Products.txt";
    private static final String DELIMITER = ",";

    private Map<String, Product> allProducts = new HashMap<>();

    @Override
    public Map<String, Product> getAllProducts() throws PersistenceException {
        loadFile();
        return allProducts;
    }

    @Override
    public List<Product> getProducts() throws PersistenceException {
        loadFile();
        return new ArrayList<>(allProducts.values());
    }

    private void loadFile() throws PersistenceException {
        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load product data into memory.", e);
        }

        String currentLine;
        Product currentProduct;

        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentProduct = unmarshallProduct(currentLine);
            allProducts.put(currentProduct.getProductType(), currentProduct);
        }

        scanner.close();
    }

    private Product unmarshallProduct(String productAsText) {
        String[] productTokens = productAsText.split(DELIMITER);

        String productType = productTokens[0];

        Product productFromFile = new Product();
        productFromFile.setProductType(productType);
        productFromFile.setCostPerSquareFoot(new BigDecimal(productTokens[1]));
        productFromFile.setLaborCostPerSquareFoot(new BigDecimal(productTokens[2]));

        return productFromFile;
    }
}


