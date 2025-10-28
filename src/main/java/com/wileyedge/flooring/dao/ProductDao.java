package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.exceptions.PersistenceException;
import com.wileyedge.flooring.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductDao {

    Map<String, Product> getAllProducts() throws PersistenceException;

    List<Product> getProducts() throws PersistenceException;
}
