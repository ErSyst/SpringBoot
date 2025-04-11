package com.example.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/books") // Изменен путь на /books
public class BookController {

    // Хранилище теперь для книг (Book), а не задач (Task)
    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    // CREATE: Создать новую книгу
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) { // Принимаем и возвращаем Book
        long id = counter.incrementAndGet();
        book.setId(id);
        books.put(id, book); // Сохраняем книгу
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    // READ: Получить все книги
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() { // Возвращаем список книг
        return ResponseEntity.ok(new ArrayList<>(books.values())); // Получаем значения из хранилища книг
    }

    // READ: Получить книгу по ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) { // Возвращаем книгу
        Book book = books.get(id); // Ищем книгу по ID
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE: Обновить книгу по ID
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) { // Принимаем и возвращаем Book
        Book existingBook = books.get(id); // Ищем существующую книгу
        if (existingBook != null) {
            // Обновляем поля существующей книги данными из запроса
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
            books.put(id, existingBook); // Сохраняем обновленную книгу
            return ResponseEntity.ok(existingBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: Удалить книгу по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Book removedBook = books.remove(id); // Удаляем книгу из хранилища
        if (removedBook != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
