package com.sudhanva.library.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    // fields
    @Column(name = "author_name",nullable = false)
    private String authorName;

    @Column(unique = true,nullable = true)
    private String email;

    @Column(nullable = true)
    private String nationality;


    // relationship
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getAuthorName() {
        return authorName;
    }


    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getNationality() {
        return nationality;
    }


    public void setNationality(String nationality) {
        this.nationality = nationality;
    }


    public Set<Book> getBooks() {
        return books;
    }


    public void setBooks(Set<Book> books) {
        this.books = books;
    }


    @Override
    public String toString() {
        return "Author [id=" + id + ", authorName=" + authorName + ", email=" + email + ", nationality=" + nationality
                + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Author other = (Author) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
}
