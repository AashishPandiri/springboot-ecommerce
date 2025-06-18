package com.backend.ecommerce.model.dao;

import com.backend.ecommerce.model.LocalUser;
import com.backend.ecommerce.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByUser(LocalUser user);
}
