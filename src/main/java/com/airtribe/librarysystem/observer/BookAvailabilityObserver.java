package com.airtribe.librarysystem.observer;

import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Patron;

/**
 * Observer pattern: notified by ReservationService when a book a patron
 * reserved becomes available again, without ReservationService needing to
 * know how the notification is actually delivered.
 */
public interface BookAvailabilityObserver {

    void onBookAvailable(Patron patron, Book book);
}
