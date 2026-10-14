
package com.booknest.demo.book;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookFileStorage storage;

    public BookService(BookFileStorage storage) {
        this.storage = storage;
    }

    // 1. Get all books
    public List<Book> getAllBooks() {
        return storage.loadBooks();
    }

    // 2. Find a book by its ID
    public Optional<Book> getBookById(int id) {
        return storage.loadBooks()
                .stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    // 3. Add a new book
    public Book addBook(Book newBook) {

        List<Book> books = storage.loadBooks();

        // Check whether the ISBN already exists
        boolean isbnExists = books.stream()
                .anyMatch(book ->
                        book.getIsbn().equalsIgnoreCase(newBook.getIsbn()));

        if (isbnExists) {
            throw new IllegalArgumentException(
                    "A book with this ISBN already exists.");
        }

        // Generate the next ID
        int nextId = 1;

        for (Book book : books) {
            if (book.getId() >= nextId) {
                nextId = book.getId() + 1;
            }
        }

        newBook.setId(nextId);
        books.add(newBook);

        storage.saveBooks(books);

        return newBook;
    }

    // 4. Update an existing book
    public Book updateBook(int id, Book updatedBook) {

        List<Book> books = storage.loadBooks();

        for (int i = 0; i < books.size(); i++) {
            Book existingBook = books.get(i);

            if (existingBook.getId() == id) {

                // Keep the original ID
                updatedBook.setId(id);

                // Prevent duplicate ISBNs
                boolean duplicateIsbn = books.stream()
                        .anyMatch(book ->
                                book.getId() != id
                                        && book.getIsbn().equalsIgnoreCase(
                                        updatedBook.getIsbn()));

                if (duplicateIsbn) {
                    throw new IllegalArgumentException(
                            "Another book already uses this ISBN.");
                }

                books.set(i, updatedBook);
                storage.saveBooks(books);

                return updatedBook;
            }
        }

        throw new IllegalArgumentException(
                "Book with ID " + id + " was not found.");
    }

    // 5. Delete a book
    public void deleteBook(int id) {

        List<Book> books = storage.loadBooks();

        boolean removed = books.removeIf(
                book -> book.getId() == id);

        if (!removed) {
            throw new IllegalArgumentException(
                    "Book with ID " + id + " was not found.");
        }

        storage.saveBooks(books);
    }

    // 6. Search books by title, author, or genre
    public List<Book> searchBooks(String keyword) {

        List<Book> results = new ArrayList<>();

        if (keyword == null || keyword.isBlank()) {
            return results;
        }

        String searchTerm = keyword.toLowerCase().trim();

        for (Book book : storage.loadBooks()) {

            if (book.getTitle().toLowerCase().contains(searchTerm)
                    || book.getAuthor().toLowerCase().contains(searchTerm)
                    || book.getGenre().toLowerCase().contains(searchTerm)) {

                results.add(book);
            }
        }

        return results;
    }
}
