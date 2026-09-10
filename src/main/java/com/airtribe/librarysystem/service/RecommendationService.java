package com.airtribe.librarysystem.service;

import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Branch;
import com.airtribe.librarysystem.model.Patron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Recommends books by looking at which author shows up most often in a
 * patron's borrowing history, then suggesting other titles by that author
 * that the patron has not already read.
 */
public class RecommendationService {

    public List<Book> recommend(Patron patron, Branch branch, int limit) {
        List<Book> history = patron.getBorrowingHistory();
        if (history.isEmpty()) {
            return popularAvailableBooks(branch, limit);
        }

        Map<String, Integer> authorFrequency = new HashMap<>();
        for (Book book : history) {
            authorFrequency.merge(book.getAuthor(), 1, Integer::sum);
        }

        String favoriteAuthor = null;
        int highestCount = 0;
        for (Map.Entry<String, Integer> entry : authorFrequency.entrySet()) {
            if (entry.getValue() > highestCount) {
                highestCount = entry.getValue();
                favoriteAuthor = entry.getKey();
            }
        }

        Set<String> alreadyRead = new HashSet<>();
        for (Book book : history) {
            alreadyRead.add(book.getIsbn());
        }

        List<Book> recommendations = new ArrayList<>();
        for (Book book : branch.getAllBooks()) {
            if (recommendations.size() >= limit) {
                break;
            }
            if (book.getAuthor().equals(favoriteAuthor) && !alreadyRead.contains(book.getIsbn())) {
                recommendations.add(book);
            }
        }
        return recommendations;
    }

    private List<Book> popularAvailableBooks(Branch branch, int limit) {
        List<Book> available = new ArrayList<>();
        for (Book book : branch.getAllBooks()) {
            if (available.size() >= limit) {
                break;
            }
            if (book.isAvailable()) {
                available.add(book);
            }
        }
        return available;
    }
}
