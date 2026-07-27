package com.sudhanva.springsec.model;

import com.sudhanva.springsec.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // to store in db as users
public class User {
    
    @Id
    private int id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}

