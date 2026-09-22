package com.sudhanva.library.dto;

import com.sudhanva.library.entity.Author;

public class AuthorDTO {

    public Long id;
    public String authorName;
    public String email;
    public String nationality;

    public static AuthorDTO from(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.id = author.getId();
        dto.authorName = author.getAuthorName();
        dto.email = author.getEmail();
        dto.nationality = author.getNationality();
        return dto;
    }
}
