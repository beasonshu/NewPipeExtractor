package org.schabi.newpipe.extractor;

import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import java.io.IOException;
import java.util.List;

/**
 * Collectors are used to simplify the collection of information
 * from extractors
 * @param <I> the item type
 * @param <E> the extractor type
 */
public interface Collector<I, E> {

    /**
     * Try to add an extractor to the collection
     * @param extractor the extractor to add
     */
    void commit(E extractor);

    /**
     * Try to extract the item from an extractor without adding it to the collection
     * @param extractor the extractor to use
     * @return the item
     * @throws ParsingException thrown if there is an error extracting the
     *                          <b>required</b> fields of the item.
     */
    I extract(E extractor) throws ExtractionException, IOException;

    /**
     * Get all items
     * @return the items
     */
    List<I> getItems();

    /**
     * Get all errors
     * @return the errors
     */
    List<Throwable> getErrors();

    /**
     * Reset all collected items and errors
     */
    void reset();
}
