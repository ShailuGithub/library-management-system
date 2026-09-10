package com.airtribe.librarysystem.strategy;

import com.airtribe.librarysystem.model.Book;

import java.util.List;

/**
 * Strategy pattern: lets LibraryService pick a search algorithm (by title,
 * author or ISBN) at runtime without hardcoding the matching logic.
 */
public interface BookSearchStrategy {

    List<Book> search(List<Book> books, String query);
}
