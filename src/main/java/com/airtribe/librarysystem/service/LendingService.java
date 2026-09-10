package com.airtribe.librarysystem.service;

import com.airtribe.librarysystem.exception.BookNotAvailableException;
import com.airtribe.librarysystem.exception.BookNotFoundException;
import com.airtribe.librarysystem.exception.PatronNotFoundException;
import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Branch;
import com.airtribe.librarysystem.model.Patron;

import java.util.logging.Logger;

public class LendingService {

    private static final Logger LOGGER = Logger.getLogger(LendingService.class.getName());

    private final PatronService patronService;
    private final ReservationService reservationService;

    public LendingService(PatronService patronService, ReservationService reservationService) {
        this.patronService = patronService;
        this.reservationService = reservationService;
    }

    public Book checkoutBook(Branch branch, String patronId, String isbn)
            throws BookNotFoundException, BookNotAvailableException, PatronNotFoundException {
        Patron patron = patronService.findPatron(patronId);
        Book book = branch.getBook(isbn);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("\"" + book.getTitle() + "\" has no available copies right now");
        }
        book.checkout();
        patron.recordCheckout(book);
        LOGGER.info(patron.getName() + " checked out \"" + book.getTitle() + "\" from branch " + branch.getName());
        return book;
    }

    public Book returnBook(Branch branch, String patronId, String isbn)
            throws BookNotFoundException, PatronNotFoundException {
        Patron patron = patronService.findPatron(patronId);
        Book book = branch.getBook(isbn);
        patron.recordReturn(book);
        book.returnCopy();
        LOGGER.info(patron.getName() + " returned \"" + book.getTitle() + "\" to branch " + branch.getName());
        reservationService.notifyNextInQueue(branch.getBranchId(), book);
        return book;
    }
}
