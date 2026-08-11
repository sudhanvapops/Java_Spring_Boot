package com.sudhanva.library_management_v2.config;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.LibrarySettings;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.User;
import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.enums.Setting.SettingValueType;
import com.sudhanva.library_management_v2.enums.User.UserRoles;
import com.sudhanva.library_management_v2.repo.BookRepo;
import com.sudhanva.library_management_v2.repo.LibrarySettingsRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;
import com.sudhanva.library_management_v2.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                        Member.builder().name("Sudhanva").email("sudhanva@gmail.com").age(20).role(UserRoles.MEMBER).build(),
                        Member.builder().name("Rahul").email("rahul@gmail.com").age(21).role(UserRoles.MEMBER).build(),
                        Member.builder().name("Ananya").email("ananya@gmail.com").age(19).role(UserRoles.MEMBER).build(),
                        Member.builder().name("Kiran").email("kiran@gmail.com").age(22).role(UserRoles.MEMBER).build(),
                        Member.builder().name("Megha").email("megha@gmail.com").age(20).role(UserRoles.MEMBER).build()
                );

                memberRepo.saveAll(members);
                System.out.println("Dummy members inserted successfully!");
            }
        };
    }

    // Dev-only bootstrap: without this there's no way to reach an ADMIN
    // account at all, since public registration only ever creates MEMBER
    // accounts and staff accounts can only be created by an existing admin
    // (see AuthService.registerStaff / AuthController#registerStaff).
    // Change/rotate this password before deploying anywhere real.
    @Bean
    CommandLineRunner seedAdminUser(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepo.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@library.local")
                        .password(passwordEncoder.encode("Admin@12345"))
                        .role(UserRoles.ADMIN)
                        .isActive(true)
                        .build();

                userRepo.save(admin);
                System.out.println("Seeded default admin user - email: admin@library.local / password: Admin@12345 (change this before deploying anywhere real)");
            }
        };
    }

    @Bean
    CommandLineRunner seedLibrarySettings(LibrarySettingsRepo librarySettingsRepo) {
        return args -> {
            if (librarySettingsRepo.count() == 0) {
                List<LibrarySettings> settings = List.of(
                        LibrarySettings.builder()
                                .settingKey(SettingKey.MAX_BOOKS)
                                .settingValue("5")
                                .valueType(SettingValueType.INTEGER)
                                .description("Maximum number of books a member can borrow at once")
                                .build(),
                        LibrarySettings.builder()
                                .settingKey(SettingKey.MAX_BORROW_DAYS)
                                .settingValue("5")
                                .valueType(SettingValueType.INTEGER)
                                .description("Number of days a book can be borrowed for before it is due")
                                .build(),
                        LibrarySettings.builder()
                                .settingKey(SettingKey.FINE_PER_DAY)
                                .settingValue("10")
                                .valueType(SettingValueType.DECIMAL)
                                .description("Fine charged per day for overdue books")
                                .build()
                );

                librarySettingsRepo.saveAll(settings);
                System.out.println("Dummy library settings inserted successfully!");
            }
        };
    }
}