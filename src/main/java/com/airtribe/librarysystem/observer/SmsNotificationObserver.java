package com.airtribe.librarysystem.observer;

import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Patron;

import java.util.logging.Logger;

public class SmsNotificationObserver implements BookAvailabilityObserver {

    private static final Logger LOGGER = Logger.getLogger(SmsNotificationObserver.class.getName());

    @Override
    public void onBookAvailable(Patron patron, Book book) {
        LOGGER.info(String.format("SMS sent to %s: \"%s\" is now available for pickup", patron.getName(), book.getTitle()));
    }
}
