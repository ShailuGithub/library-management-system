package com.airtribe.librarysystem.service;

import com.airtribe.librarysystem.exception.BookNotFoundException;
import com.airtribe.librarysystem.factory.SearchStrategyFactory;
import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Branch;
import com.airtribe.librarysystem.strategy.BookSearchStrategy;
import com.airtribe.librarysystem.strategy.SearchType;

import java.util.List;
import java.util.logging.Logger;

/**
 * Handles add/remove/update/search of books within a single branch's
 * catalog. Kept separate from Branch itself so the branch model stays a
 * plain data holder while this class owns the business rules and logging.
 */
public class CatalogService {

    private static final Logger LOGGER = Logger.getLogger(CatalogService.class.getName());

    public void addBook(Branch branch, Book book) {
        branch.addBook(book);
        LOGGER.info("Added " + book + " to branch " + branch.getName());
    }

    public void removeBook(Branch branch, String isbn) throws BookNotFoundException {
        Book book = branch.getBook(isbn);
        branch.removeBook(isbn);
        LOGGER.info("Removed \"" + book.getTitle() + "\" from branch " + branch.getName());
    }

    public Book updateBook(Branch branch, String isbn, String title, String author, Integer publicationYear)
            throws BookNotFoundException {
        Book book = branch.getBook(isbn);
        if (title != null) {
            book.setTitle(title);
        }
        if (author != null) {
            book.setAuthor(author);
        }
        if (publicationYear != null) {
            book.setPublicationYear(publicationYear);
        }
        LOGGER.info("Updated book " + isbn + " at branch " + branch.getName());
        return book;
    }

    public List<Book> search(Branch branch, SearchType type, String query) {
        BookSearchStrategy strategy = SearchStrategyFactory.getStrategy(type);
        return strategy.search(branch.getAllBooks(), query);
    }
}
