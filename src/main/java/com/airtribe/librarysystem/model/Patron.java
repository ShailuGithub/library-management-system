package com.airtribe.librarysystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Patron {

    private final String patronId;
    private String name;
    private String email;
    private final List<Book> borrowedBooks = new ArrayList<>();
    private final List<Book> borrowingHistory = new ArrayList<>();

    public Patron(String patronId, String name, String email) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
    }

    public String getPatronId() {
        return patronId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Book> getBorrowedBooks() {
        return new ArrayList<>(borrowedBooks);
    }

    public List<Book> getBorrowingHistory() {
        return new ArrayList<>(borrowingHistory);
    }

    public boolean hasBorrowed(Book book) {
        return borrowedBooks.contains(book);
    }

    /**
     * Recorded by LendingService when a checkout succeeds.
     */
    public void recordCheckout(Book book) {
        borrowedBooks.add(book);
        borrowingHistory.add(book);
    }

    /**
     * Recorded by LendingService when a return succeeds.
     */
    public void recordReturn(Book book) {
        if (!borrowedBooks.remove(book)) {
            throw new IllegalStateException(name + " does not currently have \"" + book.getTitle() + "\" checked out");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Patron)) {
            return false;
        }
        Patron patron = (Patron) o;
        return patronId.equals(patron.patronId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patronId);
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %s, %s)", name, patronId, email);
    }
}
