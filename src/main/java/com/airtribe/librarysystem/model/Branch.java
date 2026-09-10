package com.airtribe.librarysystem.model;

import com.airtribe.librarysystem.exception.BookNotFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Branch {

    private final String branchId;
    private String name;
    private String address;
    private final Map<String, Book> catalog = new LinkedHashMap<>();

    public Branch(String branchId, String name, String address) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addBook(Book book) {
        Book existing = catalog.get(book.getIsbn());
        if (existing != null) {
            existing.addCopies(book.getTotalCopies());
        } else {
            catalog.put(book.getIsbn(), book);
        }
    }

    public void removeBook(String isbn) throws BookNotFoundException {
        Book book = getBook(isbn);
        if (book.getBorrowedCopies() > 0) {
            throw new IllegalStateException(
                    "Cannot remove \"" + book.getTitle() + "\"; " + book.getBorrowedCopies() + " copies are still checked out");
        }
        catalog.remove(isbn);
    }

    public Book getBook(String isbn) throws BookNotFoundException {
        Book book = catalog.get(isbn);
        if (book == null) {
            throw new BookNotFoundException("No book with ISBN " + isbn + " found at branch " + name);
        }
        return book;
    }

    public boolean hasBook(String isbn) {
        return catalog.containsKey(isbn);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(catalog.values());
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %s, %s) - %d titles", name, branchId, address, catalog.size());
    }
}
