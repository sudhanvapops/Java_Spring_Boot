package com.sudhanva.library_management_v2.config;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.repo.BookRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedBooks(BookRepo bookRepo) {
        return args -> {
            if (bookRepo.count() == 0) {
                List<Book> books = List.of(
                        Book.builder().name("The Alchemist").author("Paulo Coelho").availableCopy(5).totalCopies(5).build(),
                        Book.builder().name("Atomic Habits").author("James Clear").availableCopy(4).totalCopies(4).build(),
                        Book.builder().name("Clean Code").author("Robert C. Martin").availableCopy(3).totalCopies(3).build(),
                        Book.builder().name("Rich Dad Poor Dad").author("Robert Kiyosaki").availableCopy(6).totalCopies(6).build(),
                        Book.builder().name("Think and Grow Rich").author("Napoleon Hill").availableCopy(2).totalCopies(2).build()
                );

                bookRepo.saveAll(books);
                System.out.println("Dummy books inserted successfully!");
            }
        };
    }

    @Bean
    CommandLineRunner seedMembers(MemberRepo memberRepo) {
        return args -> {
            if (memberRepo.count() == 0) {
                List<Member> members = List.of(
                        Member.builder().name("Sudhanva").email("sudhanva@gmail.com").age(20).build(),
                        Member.builder().name("Rahul").email("rahul@gmail.com").age(21).build(),
                        Member.builder().name("Ananya").email("ananya@gmail.com").age(19).build(),
                        Member.builder().name("Kiran").email("kiran@gmail.com").age(22).build(),
                        Member.builder().name("Megha").email("megha@gmail.com").age(20).build()
                );

                memberRepo.saveAll(members);
                System.out.println("Dummy members inserted successfully!");
            }
        };
    }
}