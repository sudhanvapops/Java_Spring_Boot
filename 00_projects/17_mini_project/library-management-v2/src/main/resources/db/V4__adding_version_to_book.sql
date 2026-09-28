-- Adding Version TO Book for Optimistic Locking


ALTER TABLE public.book
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
