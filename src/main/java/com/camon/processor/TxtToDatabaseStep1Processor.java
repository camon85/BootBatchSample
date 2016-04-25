package com.camon.processor;

import com.camon.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by user on 2016-03-31.
 */
public class TxtToDatabaseStep1Processor implements ItemProcessor<Book, Book> {

    private static final Logger log = LoggerFactory.getLogger(TxtToDatabaseStep1Processor.class);

    @Override
    public Book process(final Book book) throws Exception {
        log.info("### TxtToDatabaseStep1Processor ###");
        log.info(book.toString());
        return book;
    }

}
