package com.airtribe.librarysystem.ui;

import com.airtribe.librarysystem.exception.BookNotAvailableException;
import com.airtribe.librarysystem.exception.BookNotFoundException;
import com.airtribe.librarysystem.exception.BranchNotFoundException;
import com.airtribe.librarysystem.exception.PatronNotFoundException;
import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Branch;
import com.airtribe.librarysystem.model.Patron;
import com.airtribe.librarysystem.observer.EmailNotificationObserver;
import com.airtribe.librarysystem.observer.SmsNotificationObserver;
import com.airtribe.librarysystem.service.LibraryService;
import com.airtribe.librarysystem.strategy.SearchType;
import com.airtribe.librarysystem.util.IdGenerator;

import java.util.List;

public class Main {

    public static void main(String[] args) throws BranchNotFoundException, BookNotFoundException,
            PatronNotFoundException, BookNotAvailableException {

        LibraryService library = LibraryService.getInstance();
        library.getReservationService().addObserver(new EmailNotificationObserver());
        library.getReservationService().addObserver(new SmsNotificationObserver());

        String downtown = IdGenerator.getNextBranchId();
        String uptown = IdGenerator.getNextBranchId();
        library.registerBranch(new Branch(downtown, "Downtown Branch", "12 MG Road"));
        library.registerBranch(new Branch(uptown, "Uptown Branch", "8 Lake View"));

        library.addBook(downtown, new Book("978-0132350884", "Clean Code", "Robert C. Martin", 2008, 2));
        library.addBook(downtown, new Book("978-0201633610", "Design Patterns", "Erich Gamma", 1994, 1));
        library.addBook(downtown, new Book("978-0134685991", "Effective Java", "Joshua Bloch", 2018, 1));
        library.addBook(downtown, new Book("978-0137081073", "The Clean Coder", "Robert C. Martin", 2011, 1));
        library.addBook(uptown, new Book("978-0596007126", "Head First Design Patterns", "Eric Freeman", 2004, 1));

        Patron shailesh = library.registerPatron("Shailesh", "shailu580@gmail.com");
        Patron aditi = library.registerPatron("Aditi", "aditi@example.com");

        System.out.println("--- Search by title: \"design\" ---");
        List<Book> results = library.searchBooks(downtown, SearchType.TITLE, "design");
        results.forEach(System.out::println);

        System.out.println("\n--- Checkout flow ---");
        library.checkoutBook(downtown, shailesh.getPatronId(), "978-0201633610");
        System.out.println(shailesh.getName() + " checked out \"Design Patterns\"");

        System.out.println("\n--- Reservation flow (book already checked out) ---");
        try {
            library.checkoutBook(downtown, aditi.getPatronId(), "978-0201633610");
        } catch (BookNotAvailableException e) {
            System.out.println(aditi.getName() + " could not check out: " + e.getMessage());
            library.reserveBook(downtown, aditi.getPatronId(), "978-0201633610");
            System.out.println(aditi.getName() + " reserved the book instead");
        }

        System.out.println("\n--- Return flow triggers reservation notification ---");
        library.returnBook(downtown, shailesh.getPatronId(), "978-0201633610");

        System.out.println("\n--- Multi-branch transfer ---");
        library.checkoutBook(downtown, shailesh.getPatronId(), "978-0132350884");
        System.out.println("Downtown \"Clean Code\" before transfer: "
                + library.findBranch(downtown).getBook("978-0132350884"));
        library.transferBook(downtown, uptown, "978-0132350884", 1);
        System.out.println("Downtown \"Clean Code\" after transfer: "
                + library.findBranch(downtown).getBook("978-0132350884"));
        System.out.println("Uptown \"Clean Code\" after transfer: "
                + library.findBranch(uptown).getBook("978-0132350884"));

        System.out.println("\n--- Recommendations for " + shailesh.getName() + " ---");
        List<Book> recommendations = library.recommendBooks(downtown, shailesh.getPatronId(), 3);
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations yet.");
        } else {
            recommendations.forEach(System.out::println);
        }
    }
}
