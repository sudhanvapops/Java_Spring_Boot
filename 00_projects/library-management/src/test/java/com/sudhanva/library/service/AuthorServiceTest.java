package com.sudhanva.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sudhanva.library.dao.AuthorDAO;
import com.sudhanva.library.dto.AuthorDTO;
import com.sudhanva.library.dto.CreateAuthorRequest;
import com.sudhanva.library.entity.Author;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private AuthorDAO authorDAO;

    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        authorService = new AuthorService(sessionFactory, authorDAO);
    }

    @Test
    void listAuthors_returnsMappedDtos() {
        when(authorDAO.findAll(10, 0)).thenReturn(List.of(sampleAuthor()));

        List<AuthorDTO> result = authorService.listAuthors(10, 0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).authorName).isEqualTo("George Orwell");
    }

    @Test
    void getById_notFound_returnsEmpty() {
        when(authorDAO.findById(99L)).thenReturn(Optional.empty());

        assertThat(authorService.getById(99L)).isEmpty();
    }

    @Test
    void createAuthor_persistsAndReturnsDto() {
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.authorName = "Aldous Huxley";
        request.email = "huxley@test.com";
        request.nationality = "British";

        AuthorDTO result = authorService.createAuthor(request);

        assertThat(result.authorName).isEqualTo("Aldous Huxley");
        verify(authorDAO).save(any(Author.class));
        verify(transaction).commit();
    }

    @Test
    void deleteAuthor_notFound_throws() {
        when(authorDAO.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.deleteAuthor(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Author not found");

        verify(transaction).rollback();
    }

    @Test
    void deleteAuthor_found_deletesAndCommits() {
        Author author = sampleAuthor();
        when(authorDAO.findById(1L)).thenReturn(Optional.of(author));

        authorService.deleteAuthor(1L);

        verify(authorDAO).delete(author);
        verify(transaction).commit();
    }

    private Author sampleAuthor() {
        return new Author("George Orwell", "orwell@test.com", "British", new HashSet<>());
    }
}
