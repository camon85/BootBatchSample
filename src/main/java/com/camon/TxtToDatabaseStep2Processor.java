package com.camon;

import com.camon.domain.Book;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by user on 2016-03-31.
 */
public class TxtToDatabaseStep2Processor implements ItemProcessor<Book, Book> {

    private static final Logger log = LoggerFactory.getLogger(TxtToDatabaseStep2Processor.class);

    @Override
    public Book process(final Book book) throws Exception {
        log.info("### TxtToDatabaseStep2Processor ###");
        log.info(new Gson().toJson(book));

        return book;
    }
}
