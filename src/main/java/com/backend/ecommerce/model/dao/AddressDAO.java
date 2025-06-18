package com.backend.ecommerce.model.dao;

import org.springframework.data.repository.ListCrudRepository;
import com.backend.ecommerce.model.Address;

import java.util.List;

public interface AddressDAO extends ListCrudRepository<Address, Long> {

    List<Address> findByUser_Id(Long id);
}
