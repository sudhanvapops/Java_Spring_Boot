-- Active: 1742912045376@@127.0.0.1@5432@multibag

-- USe database multibag in posgres

CREATE TABLE books (
    id BIGINT PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

CREATE TABLE copies (
    id BIGINT PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books(id)
);

CREATE TABLE reviews (
    id BIGINT PRIMARY KEY,
    comment VARCHAR(255) NOT NULL,
    book_id BIGINT NOT NULL REFERENCES books(id)
);

-- The relationships are:
-- - One book → many copies.
-- - One book → many reviews.
-- - Each copy and review belongs to one book.



INSERT INTO books (id, title)
VALUES (1, 'Clean Code');

INSERT INTO copies (id, book_id)
VALUES
    (101, 1),
    (102, 1),
    (103, 1);

INSERT INTO reviews (id, comment, book_id)
VALUES
    (201, 'Excellent book', 1),
    (202, 'Very useful', 1);


SELECT
    b.id AS book_id,
    b.title,
    c.id AS copy_id,
    r.id AS review_id,
    r.comment
FROM books b
JOIN copies c ON c.book_id = b.id
JOIN reviews r ON r.book_id = b.id;