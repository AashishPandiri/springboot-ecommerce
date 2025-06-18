package com.backend.ecommerce.model.dao;

import com.backend.ecommerce.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalUserDAO extends JpaRepository<LocalUser, Long> {

    Optional<LocalUser> findByUsernameIgnoreCase(String username);

    Optional<LocalUser> findByEmailIgnoreCase(String email);
}
