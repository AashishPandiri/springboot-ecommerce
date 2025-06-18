package com.backend.ecommerce.service;

import com.backend.ecommerce.model.Product;
import com.backend.ecommerce.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductDAO productDAO;

    public ProductService(ProductDAO productDAO){
        this.productDAO = productDAO;
    }

    public List<Product> getProducts(){
        return productDAO.findAll();
    }
}
