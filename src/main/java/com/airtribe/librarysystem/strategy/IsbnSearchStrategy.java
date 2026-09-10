package com.airtribe.librarysystem.strategy;

import com.airtribe.librarysystem.model.Book;

import java.util.ArrayList;
import java.util.List;

public class IsbnSearchStrategy implements BookSearchStrategy {

    @Override
    public List<Book> search(List<Book> books, String query) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getIsbn().equalsIgnoreCase(query)) {
                results.add(book);
            }
        }
        return results;
    }
}
