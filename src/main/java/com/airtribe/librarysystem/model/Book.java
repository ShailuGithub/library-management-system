package com.airtribe.librarysystem.model;

import java.util.Objects;

public class Book {

    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, int publicationYear, int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getBorrowedCopies() {
        return totalCopies - availableCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void addCopies(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Number of copies to add must be positive");
        }
        totalCopies += count;
        availableCopies += count;
    }

    public void removeCopies(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Number of copies to remove must be positive");
        }
        if (count > availableCopies) {
            throw new IllegalStateException(
                    "Cannot remove " + count + " copies of \"" + title + "\"; only " + availableCopies
                            + " are on the shelf (rest are checked out)");
        }
        totalCopies -= count;
        availableCopies -= count;
    }

    public void checkout() {
        if (!isAvailable()) {
            throw new IllegalStateException("No available copies of \"" + title + "\" to check out");
        }
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies >= totalCopies) {
            throw new IllegalStateException("All copies of \"" + title + "\" are already on the shelf");
        }
        availableCopies++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("\"%s\" by %s (%d) [ISBN: %s] - %d/%d available",
                title, author, publicationYear, isbn, availableCopies, totalCopies);
    }
}
