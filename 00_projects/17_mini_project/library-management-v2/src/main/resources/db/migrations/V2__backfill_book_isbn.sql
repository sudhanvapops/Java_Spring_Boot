-- ============================================================
-- Flyway V2

-- Backfill isbn for the existing book rows
--
-- These are the real published ISBN-13s for the specific editions
--
-- "AND isbn IS NULL" makes this safe to re-run and won't clobber a
-- value someone already corrected by hand after this ran.
--
-- This does NOT cover rows created after this migration executes
-- (e.g. DataSeeder runs after Flyway migrate, so a fresh database)
-- ============================================================

UPDATE public.book
SET isbn = CASE id
    WHEN 1 THEN '9780062315007'
    WHEN 2 THEN '9780735211292'
    WHEN 3 THEN '9780132350884'
    WHEN 4 THEN '9781612680194'
    WHEN 5 THEN '9781585424337'
    WHEN 6 THEN '9793482167057'
END
WHERE id IN (1, 2, 3, 4, 5, 6)
  AND isbn IS NULL;
