package com.camon;

import com.camon.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by user on 2016-03-31.
 */
public class TxtToDatabaseJobExecutionListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(TxtToDatabaseJobExecutionListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TxtToDatabaseJobExecutionListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("### TxtToDatabaseJobExecution STARTED ###");
        super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("### TxtToDatabaseJobExecution FINISHED ###");
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

            log.info("BatchStatus.COMPLETED");
            List<Book> results = jdbcTemplate.query("SELECT id, title, writer, publisher, publish_date FROM books", (rs, row) -> {
                return new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
            });

            for (Book book : results) {
                log.info("Found <" + book + "> in the database.");
            }

        } else if(jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("BatchStatus.FAILED");
        } else {
            log.info("BatchStatus ??");
        }
    }
}