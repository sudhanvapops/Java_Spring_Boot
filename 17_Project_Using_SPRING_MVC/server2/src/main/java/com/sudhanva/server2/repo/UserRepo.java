package com.sudhanva.server2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.server2.Model.User;


@Repository
public interface UserRepo extends JpaRepository<User,Integer>{

    User findByUsername(String username);

}
