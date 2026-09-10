package com.airtribe.librarysystem.service;

import com.airtribe.librarysystem.exception.BookNotAvailableException;
import com.airtribe.librarysystem.exception.BookNotFoundException;
import com.airtribe.librarysystem.exception.BranchNotFoundException;
import com.airtribe.librarysystem.exception.PatronNotFoundException;
import com.airtribe.librarysystem.model.Book;
import com.airtribe.librarysystem.model.Branch;
import com.airtribe.librarysystem.model.Patron;
import com.airtribe.librarysystem.strategy.SearchType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Singleton pattern: a single LibraryService instance is the front door for
 * every branch, so all callers share one consistent view of branches,
 * patrons and in-flight reservations instead of wiring the sub-services
 * together themselves.
 */
public final class LibraryService {

    private static final Logger LOGGER = Logger.getLogger(LibraryService.class.getName());
    private static final LibraryService INSTANCE = new LibraryService();

    private final Map<String, Branch> branches = new LinkedHashMap<>();
    private final CatalogService catalogService = new CatalogService();
    private final PatronService patronService = new PatronService();
    private final ReservationService reservationService = new ReservationService();
    private final LendingService lendingService = new LendingService(patronService, reservationService);
    private final RecommendationService recommendationService = new RecommendationService();

    private LibraryService() {
    }

    public static LibraryService getInstance() {
        return INSTANCE;
    }

    public ReservationService getReservationService() {
        return reservationService;
    }

    public void registerBranch(Branch branch) {
        branches.put(branch.getBranchId(), branch);
        LOGGER.info("Registered branch " + branch);
    }

    public Branch findBranch(String branchId) throws BranchNotFoundException {
        Branch branch = branches.get(branchId);
        if (branch == null) {
            throw new BranchNotFoundException("No branch found with ID " + branchId);
        }
        return branch;
    }

    public List<Branch> getAllBranches() {
        return new ArrayList<>(branches.values());
    }

    public void addBook(String branchId, Book book) throws BranchNotFoundException {
        catalogService.addBook(findBranch(branchId), book);
    }

    public void removeBook(String branchId, String isbn) throws BranchNotFoundException, BookNotFoundException {
        catalogService.removeBook(findBranch(branchId), isbn);
    }

    public Book updateBook(String branchId, String isbn, String title, String author, Integer publicationYear)
            throws BranchNotFoundException, BookNotFoundException {
        return catalogService.updateBook(findBranch(branchId), isbn, title, author, publicationYear);
    }

    public List<Book> searchBooks(String branchId, SearchType type, String query) throws BranchNotFoundException {
        return catalogService.search(findBranch(branchId), type, query);
    }

    public Patron registerPatron(String name, String email) {
        return patronService.registerPatron(name, email);
    }

    public Book checkoutBook(String branchId, String patronId, String isbn)
            throws BranchNotFoundException, BookNotFoundException, BookNotAvailableException, PatronNotFoundException {
        return lendingService.checkoutBook(findBranch(branchId), patronId, isbn);
    }

    public Book returnBook(String branchId, String patronId, String isbn)
            throws BranchNotFoundException, BookNotFoundException, PatronNotFoundException {
        return lendingService.returnBook(findBranch(branchId), patronId, isbn);
    }

    public void reserveBook(String branchId, String patronId, String isbn)
            throws BranchNotFoundException, BookNotFoundException, PatronNotFoundException {
        Branch branch = findBranch(branchId);
        Patron patron = patronService.findPatron(patronId);
        Book book = branch.getBook(isbn);
        reservationService.reserveBook(branchId, book, patron);
    }

    /**
     * Multi-branch extension: moves copies of a title from one branch to
     * another, e.g. to rebalance stock. Only copies sitting on the shelf
     * (not checked out) can be transferred.
     */
    public void transferBook(String fromBranchId, String toBranchId, String isbn, int quantity)
            throws BranchNotFoundException, BookNotFoundException {
        Branch fromBranch = findBranch(fromBranchId);
        Branch toBranch = findBranch(toBranchId);
        Book source = fromBranch.getBook(isbn);

        source.removeCopies(quantity);
        Book transferred = new Book(source.getIsbn(), source.getTitle(), source.getAuthor(),
                source.getPublicationYear(), quantity);
        toBranch.addBook(transferred);

        if (source.getTotalCopies() == 0) {
            fromBranch.removeBook(isbn);
        }
        LOGGER.info(String.format("Transferred %d copies of \"%s\" from %s to %s",
                quantity, source.getTitle(), fromBranch.getName(), toBranch.getName()));
    }

    public List<Book> recommendBooks(String branchId, String patronId, int limit)
            throws BranchNotFoundException, PatronNotFoundException {
        Branch branch = findBranch(branchId);
        Patron patron = patronService.findPatron(patronId);
        return recommendationService.recommend(patron, branch, limit);
    }
}
