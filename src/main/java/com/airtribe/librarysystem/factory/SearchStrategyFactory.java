package com.airtribe.librarysystem.factory;

import com.airtribe.librarysystem.strategy.AuthorSearchStrategy;
import com.airtribe.librarysystem.strategy.BookSearchStrategy;
import com.airtribe.librarysystem.strategy.IsbnSearchStrategy;
import com.airtribe.librarysystem.strategy.SearchType;
import com.airtribe.librarysystem.strategy.TitleSearchStrategy;

/**
 * Factory pattern: hides the choice of concrete BookSearchStrategy behind a
 * single creation point, so callers only need to know the SearchType.
 */
public final class SearchStrategyFactory {

    private SearchStrategyFactory() {
    }

    public static BookSearchStrategy getStrategy(SearchType type) {
        switch (type) {
            case TITLE:
                return new TitleSearchStrategy();
            case AUTHOR:
                return new AuthorSearchStrategy();
            case ISBN:
                return new IsbnSearchStrategy();
            default:
                throw new IllegalArgumentException("Unsupported search type: " + type);
        }
    }
}
