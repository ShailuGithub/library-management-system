package com.airtribe.librarysystem.observer;

import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Patron;

import java.util.logging.Logger;

public class EmailNotificationObserver implements BookAvailabilityObserver {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationObserver.class.getName());

    @Override
    public void onBookAvailable(Patron patron, Book book) {
        LOGGER.info(String.format("Email sent to %s: \"%s\" is now available for pickup", patron.getEmail(), book.getTitle()));
    }
}
