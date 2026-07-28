package com.sudhanva.jwtsec.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sudhanva.jwtsec.model.User;



public interface UserRepo extends JpaRepository<User,Integer>{
    Optional<User> findByUsername(String username);
}
