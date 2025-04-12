package com.example.gateway.controller;

import com.example.core.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/gateway/books")
public class ApiGatewayController {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayController.class);

    private final RestTemplate restTemplate;
    private final String coreServiceUrl;

    public ApiGatewayController(RestTemplate restTemplate, @Value("${core.service.url}") String coreServiceUrl) {
        this.restTemplate = restTemplate;
        this.coreServiceUrl = coreServiceUrl;
        log.info("URL основного сервиса сконфигурирован: {}", this.coreServiceUrl);
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        String url = coreServiceUrl;
        log.info("GATEWAY: Перенаправление CREATE запроса для книги: {} на URL: {}", book, url);
        try {
            ResponseEntity<Book> response = restTemplate.postForEntity(url, book, Book.class);
            log.info("GATEWAY: Получен ответ от основного сервиса: Статус={}, Тело={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (HttpClientErrorException e) {
            log.error("GATEWAY: Ошибка вызова основного сервиса (CREATE): Статус={}, Тело={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("GATEWAY: Общая ошибка вызова основного сервиса (CREATE)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка шлюза во время создания: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        String url = coreServiceUrl;
        log.info("GATEWAY: Перенаправление GET ALL запроса на URL: {}", url);
        try {
            ResponseEntity<List<Book>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Book>>() {}
            );
            log.info("GATEWAY: Получен ответ от основного сервиса: Статус={}, Размер тела={}", response.getStatusCode(), response.getBody() != null ? response.getBody().size() : "null");
            return response;
        } catch (HttpClientErrorException e) {
            log.error("GATEWAY: Ошибка вызова основного сервиса (GET ALL): Статус={}, Тело={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("GATEWAY: Общая ошибка вызова основного сервиса (GET ALL)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка шлюза при получении всех книг: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        String url = coreServiceUrl + "/" + id;
        log.info("GATEWAY: Перенаправление GET by ID запроса для id: {} на URL: {}", id, url);
        try {
            ResponseEntity<Book> response = restTemplate.getForEntity(url, Book.class);
            log.info("GATEWAY: Получен ответ от основного сервиса: Статус={}, Тело={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (HttpClientErrorException e) {
            log.error("GATEWAY: Ошибка вызова основного сервиса (GET by ID {}): Статус={}, Тело={}", id, e.getStatusCode(), e.getResponseBodyAsString(), e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("GATEWAY: Общая ошибка вызова основного сервиса (GET by ID {})", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка шлюза при получении книги по ID: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        String url = coreServiceUrl + "/" + id;
        log.info("GATEWAY: Перенаправление UPDATE запроса для id: {} с телом: {} на URL: {}", id, updatedBook, url);
        try {
            ResponseEntity<Book> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(updatedBook),
                    Book.class
            );
            log.info("GATEWAY: Получен ответ от основного сервиса: Статус={}, Тело={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (HttpClientErrorException e) {
            log.error("GATEWAY: Ошибка вызова основного сервиса (UPDATE {}): Статус={}, Тело={}", id, e.getStatusCode(), e.getResponseBodyAsString(), e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("GATEWAY: Общая ошибка вызова основного сервиса (UPDATE {})", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка шлюза во время обновления: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        String url = coreServiceUrl + "/" + id;
        log.info("GATEWAY: Перенаправление DELETE запроса для id: {} на URL: {}", id, url);
        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            log.info("GATEWAY: Получен ответ от основного сервиса: Статус={}", response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).build();
        } catch (HttpClientErrorException e) {
            log.error("GATEWAY: Ошибка вызова основного сервиса (DELETE {}): Статус={}, Тело={}", id, e.getStatusCode(), e.getResponseBodyAsString(), e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("GATEWAY: Общая ошибка вызова основного сервиса (DELETE {})", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка шлюза во время удаления: " + e.getMessage());
        }
    }
}