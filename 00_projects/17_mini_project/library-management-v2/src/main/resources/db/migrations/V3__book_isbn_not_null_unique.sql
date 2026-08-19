-- flyway:executeInTransaction=false
-- ============================================================
-- Flyway V3
-- Make book.isbn NOT NULL and UNIQUE
--
-- Staged into small, low-lock steps instead of one blunt ALTER,
-- because on a large/live table:
--   - "ALTER COLUMN isbn SET NOT NULL" directly takes an ACCESS
--     EXCLUSIVE lock (blocks ALL reads/writes on book) for as
--     long as it takes to scan every row.
--   - "ADD CONSTRAINT ... UNIQUE" (the shorthand form) builds its
--     backing index while holding that same table-wide lock.
-- Neither matters at this table's current size, but this is the
-- pattern to reach for once it does - so it's used here too.
-- ============================================================

ALTER TABLE public.book
    ALTER COLUMN isbn SET NOT NULL;

ALTER TABLE public.book
    ADD CONSTRAINT uk_book_isbn UNIQUE (isbn);