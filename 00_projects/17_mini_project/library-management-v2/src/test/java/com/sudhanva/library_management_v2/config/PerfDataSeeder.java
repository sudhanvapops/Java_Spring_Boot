package com.sudhanva.library_management_v2.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;


// Performence data seeder class


@Component
// activate this component when only pref is selected
@Profile ("perf")
@Order (Ordered.HIGHEST_PRECEDENCE)
public class PerfDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PerfDataSeeder.class);

    // to execute Sql Directly against postgres
    private final JdbcTemplate jdbc;
    // runs a block of operations isnide a db transaction
    private final TransactionTemplate tx;

    public PerfDataSeeder(JdbcTemplate jdbc, PlatformTransactionManager txManager) {
        this.jdbc = jdbc;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public void run(ApplicationArguments args) {

        long start = System.currentTimeMillis();


        // start a transaction
        // Execute Sql Statments
        // Commit if everything success
        // else rollback
        tx.executeWithoutResult(status -> {
            // same random() sequence every run
            jdbc.execute("SELECT setseed(0.42)");   

            jdbc.execute("""
                INSERT INTO book (name, author, isbn, available_copy, total_copies, is_active)
                SELECT 'book ' || g, 'author ' || (g % 5000),
                       '978' || lpad(g::text, 10, '0'), 5, 5, true
                FROM generate_series(1, 100000) AS g
                """);

            jdbc.execute("""
                INSERT INTO member (name, email, age, role, is_active)
                SELECT 'member ' || g, 'member' || g || '@perf.test',
                       18 + (g % 50), 'MEMBER', true
                FROM generate_series(1, 10000) AS g
                """);

            jdbc.execute("""
                INSERT INTO borrow_transaction (member_id, borrow_date)
                SELECT m.id, LOCALTIMESTAMP - (floor(random() * 60) * interval '1 day')
                FROM member m CROSS JOIN generate_series(1, 5) AS t
                """);

            jdbc.execute("""
                INSERT INTO borrow_record (borrow_transaction_id, book_id, due_date, return_date, fine)
                SELECT bt.id, b.id,
                       bt.borrow_date + interval '14 days',
                       CASE WHEN random() < 0.7 THEN bt.borrow_date + interval '7 days' END,
                       0
                FROM (SELECT id, borrow_date, row_number() OVER (ORDER BY id) AS rn
                      FROM borrow_transaction) bt
                CROSS JOIN generate_series(0, 1) AS k
                JOIN (SELECT id, row_number() OVER (ORDER BY id) AS rn FROM book) b
                  ON b.rn = ((bt.rn * 2 + k) % 100000) + 1
                """);

            jdbc.execute("ANALYZE book");
            jdbc.execute("ANALYZE member");
            jdbc.execute("ANALYZE borrow_transaction");
            jdbc.execute("ANALYZE borrow_record");
        });


        // Log The Results Finaly
        log.info("PERF SEED DONE in {} ms: books={}, members={}, transactions={}, records={}",
                System.currentTimeMillis() - start,
                jdbc.queryForObject("SELECT COUNT(*) FROM book", Long.class),
                jdbc.queryForObject("SELECT COUNT(*) FROM member", Long.class),
                jdbc.queryForObject("SELECT COUNT(*) FROM borrow_transaction", Long.class),
                jdbc.queryForObject("SELECT COUNT(*) FROM borrow_record", Long.class));
    }
}