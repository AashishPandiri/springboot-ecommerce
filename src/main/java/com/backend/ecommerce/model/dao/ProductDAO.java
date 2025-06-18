package com.backend.ecommerce.model.dao;

import com.backend.ecommerce.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductDAO extends ListCrudRepository<Product, Long>{
}
