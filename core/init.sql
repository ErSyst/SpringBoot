SET client_encoding = 'UTF8';

CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(50) UNIQUE,
    publication_year INT
);

DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM books LIMIT 1) THEN
      INSERT INTO books(title, author, isbn, publication_year) VALUES
      ('The Lord of the Rings', 'J.R.R. Tolkien', '978-0618260274', 1954),
      ('Pride and Prejudice', 'Jane Austen', '978-0141439518', 1813),
      ('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 1960);
   END IF;
END $$;
