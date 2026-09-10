package com.airtribe.librarysystem.strategy;

import com.airtribe.librarysystem.model.Book;

import java.util.ArrayList;
import java.util.List;

public class AuthorSearchStrategy implements BookSearchStrategy {

    @Override
    public List<Book> search(List<Book> books, String query) {
        List<Book> results = new ArrayList<>();
        String needle = query.toLowerCase();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(needle)) {
                results.add(book);
            }
        }
        return results;
    }
}
