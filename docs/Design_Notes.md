# Design Notes

## Assumptions

- No persistence, database, or external API — everything lives in memory
  for the duration of the `Main` run, per the assignment brief.
- A "copy" of a book is tracked as a count (`totalCopies` /
  `availableCopies`) on `Book` rather than as individual physical-copy
  objects, since the brief doesn't require per-copy identity (e.g. copy
  barcodes or condition tracking).
- A reservation only makes sense while a book is fully checked out;
  `ReservationService.reserveBook` rejects a reservation on a book that
  still has a copy on the shelf and tells the caller to check it out
  directly instead.
- Returning a copy does not auto-checkout it to the next patron in the
  reservation queue — it only fires a notification. Auto-assigning it
  would remove the patron's chance to decline, and isn't asked for in the
  brief.

## Package layout

```
com.airtribe.librarysystem
├── model      Book, Patron, Branch — plain data holders with the small
│              set of invariants that only make sense to enforce on the
│              object itself (e.g. a Book can't go over its total copies)
├── exception  Checked exceptions for the four "not found / not
│              available" failure cases
├── strategy   BookSearchStrategy + one implementation per SearchType
├── factory    SearchStrategyFactory — the only place that maps a
│              SearchType to a concrete strategy
├── observer   BookAvailabilityObserver + two notification channels
├── service    CatalogService, PatronService, LendingService,
│              ReservationService, RecommendationService, and the
│              LibraryService facade that wires them together
├── util       IdGenerator (static counters for patron/branch IDs)
└── ui         Main — a scripted demo of every flow
```

## Why these design patterns

- **Strategy (`strategy` package)** — searching by title, author, or ISBN
  is the same shape of operation (filter a list of books by a predicate)
  with different matching logic. Strategy keeps `CatalogService` free of
  `if/else` chains on search type and makes adding a new search field
  (e.g. by genre) a one-class addition.
- **Factory (`SearchStrategyFactory`)** — pairs naturally with Strategy:
  callers only deal in `SearchType`, an enum, and never construct a
  `BookSearchStrategy` themselves. This keeps the choice of concrete
  strategy in one place.
- **Observer (`observer` package` + `ReservationService`)** — when a
  reserved book is returned, an arbitrary number of interested parties
  (email, SMS, in the future maybe a push-notification service) need to
  hear about it without `ReservationService` knowing how each channel
  works. New channels are added by implementing
  `BookAvailabilityObserver` and registering an instance — no change to
  `ReservationService` or `LendingService`.
- **Singleton (`LibraryService`)** — the assignment models a single
  library system spanning multiple branches; `LibraryService.getInstance()`
  ensures every part of the app (and `Main`) shares the same set of
  branches, patrons, and reservation queues instead of accidentally
  constructing parallel, inconsistent copies of the sub-services.

## SOLID principles

- **Single Responsibility** — each service owns exactly one concern:
  `CatalogService` (books), `PatronService` (patrons),
  `LendingService` (checkout/return), `ReservationService`
  (reservation queues + notifications), `RecommendationService`
  (suggestions). `LibraryService` only coordinates; it contains no
  business logic of its own.
- **Open/Closed** — new search fields are added as new
  `BookSearchStrategy` implementations, and new notification channels as
  new `BookAvailabilityObserver` implementations, without modifying the
  classes that consume them.
- **Liskov Substitution** — any `BookSearchStrategy` or
  `BookAvailabilityObserver` implementation can be swapped in without
  the caller (`CatalogService`, `ReservationService`) needing to know
  which concrete type it's holding.
- **Interface Segregation** — `BookSearchStrategy` and
  `BookAvailabilityObserver` each expose a single method; implementers
  are never forced to implement behaviour they don't need.
- **Dependency Inversion** — `ReservationService` depends on the
  `BookAvailabilityObserver` interface, not on `EmailNotificationObserver`
  or `SmsNotificationObserver` directly; `CatalogService` depends on
  `BookSearchStrategy`, not on any one search implementation.

## Optional extensions implemented

- **Multi-branch support** — `Branch` owns its own catalog map;
  `LibraryService.transferBook` moves a given quantity of copies from one
  branch's catalog to another's, refusing to move copies that are
  currently checked out (only shelf copies are `removeCopies`-eligible).
- **Reservation system** — `ReservationService` queues patrons per
  `(branchId, isbn)` and notifies the next one in line via the Observer
  pattern when `LendingService.returnBook` brings a copy back into stock.
- **Recommendation system** — `RecommendationService` finds the author
  that appears most often in a patron's borrowing history and suggests
  other titles by that author, at that branch, the patron hasn't already
  borrowed. Falls back to available titles at the branch for a patron
  with no history yet.
