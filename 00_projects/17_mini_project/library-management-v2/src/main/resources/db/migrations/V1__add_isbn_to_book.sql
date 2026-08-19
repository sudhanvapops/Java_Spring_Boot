-- ============================================================
-- Flyway V1
-- Added isbn to book (nullable for now)
--
-- Added nullable so existing rows don't need a backfill up front.
-- A follow-up migration will backfill isbn for existing books and
-- then i will add NOT NULL / UNIQUE constraints once every row has a value.
-- ============================================================

ALTER TABLE public.book
    ADD COLUMN isbn VARCHAR(20);
