package com.airtribe.librarysystem.service;

import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Patron;
import com.airtribe.librarysystem.observer.BookAvailabilityObserver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Observer pattern (subject side): keeps a FIFO reservation queue per book
 * and notifies every registered BookAvailabilityObserver when a reserved
 * book comes back into stock.
 */
public class ReservationService {

    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    private final Map<String, Deque<Patron>> reservationQueues = new HashMap<>();
    private final List<BookAvailabilityObserver> observers = new ArrayList<>();

    public void addObserver(BookAvailabilityObserver observer) {
        observers.add(observer);
    }

    public void reserveBook(String branchId, Book book, Patron patron) {
        if (book.isAvailable()) {
            throw new IllegalStateException(
                    "\"" + book.getTitle() + "\" is currently on the shelf; check it out instead of reserving it");
        }
        String key = reservationKey(branchId, book.getIsbn());
        reservationQueues.computeIfAbsent(key, k -> new ArrayDeque<>()).addLast(patron);
        LOGGER.info(patron.getName() + " reserved \"" + book.getTitle() + "\" at branch " + branchId);
    }

    /**
     * Called by LendingService right after a copy is returned. Pops the next
     * waiting patron (if any) and notifies every observer.
     */
    public void notifyNextInQueue(String branchId, Book book) {
        String key = reservationKey(branchId, book.getIsbn());
        Deque<Patron> queue = reservationQueues.get(key);
        if (queue == null || queue.isEmpty() || !book.isAvailable()) {
            return;
        }
        Patron next = queue.pollFirst();
        for (BookAvailabilityObserver observer : observers) {
            observer.onBookAvailable(next, book);
        }
    }

    private String reservationKey(String branchId, String isbn) {
        return branchId + "::" + isbn;
    }
}
