package com.sudhanva.springsec.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sudhanva.springsec.model.User;


public interface UserRepo extends JpaRepository<User,Integer>{
    User findByUsername(String username);
}
