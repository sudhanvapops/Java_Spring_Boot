package com.sudhanva.restspringjpa.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JobPost {
    
    @Id
    private int postId;
    private String postProfile;
    private String postDesc;
    private int reqExperience;


    // Without @ElementCollection, JPA doesn't know how to store a List<String> in a relational database and will typically throw a mapping error when the application starts.
    @ElementCollection
    private List<String> postTechStack;


}
