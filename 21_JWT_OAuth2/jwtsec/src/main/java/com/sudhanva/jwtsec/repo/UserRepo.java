package com.sudhanva.jwtsec.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sudhanva.jwtsec.model.User;



public interface UserRepo extends JpaRepository<User,Integer>{
    User findByUsername(String username);
}
