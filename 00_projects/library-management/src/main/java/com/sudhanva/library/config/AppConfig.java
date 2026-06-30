package com.sudhanva.library.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sudhanva.library.service.AuthorService;
import com.sudhanva.library.service.BookService;
import com.sudhanva.library.service.BorrowService;
import com.sudhanva.library.service.BorrowerService;
import com.sudhanva.library.util.HibernateUtil;

@Configuration
public class AppConfig {

    @Bean
    public SessionFactory sessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    @Bean
    public BookService bookService(SessionFactory sf) {
        return new BookService(sf);
    }

    @Bean
    public BorrowerService borrowerService(SessionFactory sf) {
        return new BorrowerService(sf);
    }

    @Bean
    public BorrowService borrowService(SessionFactory sf) {
        return new BorrowService(sf);
    }

    @Bean
    public AuthorService authorService(SessionFactory sf) {
        return new AuthorService(sf);
    }
}
