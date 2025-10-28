package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Product;
import com.wileyedge.flooring.exceptions.PersistenceException;

import java.util.List;

public interface ProductDao {

    /**
     * Gets all available products
     * @return list of all products
     * @throws PersistenceException if unable to read from persistence
     */
    List<Product> getAllProducts() throws PersistenceException;

    /**
     * Gets a product by its type
     * @param productType the product type to find
     * @return the product if found, null otherwise
     * @throws PersistenceException if unable to read from persistence
     */
    Product getProduct(String productType) throws PersistenceException;
}