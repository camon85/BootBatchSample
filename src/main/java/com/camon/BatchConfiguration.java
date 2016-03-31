package com.camon;

import com.camon.domain.Book;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by user on 2016-03-31.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public TxtToDatabaseJobExecutionListener txtToDatabaseJobExecutionListener() {
        return new TxtToDatabaseJobExecutionListener(new JdbcTemplate(dataSource));
    }

    //=================== start txtToDatabaseJob ===================
    @Bean
    public Job txtToDatabaseJob() {
        return jobBuilderFactory.get("txtToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .listener(txtToDatabaseJobExecutionListener())
                .flow(txtToDatabaseStep1())
                .next(txtToDatabaseStep2())
                .end()
                .build();
    }

    @Bean
    public Step txtToDatabaseStep1() {
        return stepBuilderFactory.get("txtToDatabaseStep1")
                .<Book, Book> chunk(10) // chunk size: commit interval
                .reader(txtToDatabaseStep1Reader())
                .processor(txtToDatabaseStep1Processor())
                .writer(txtToDatabaseStep1Writer())
                .build();
    }

    @Bean
    public FlatFileItemReader<Book> txtToDatabaseStep1Reader() {
        FlatFileItemReader<Book> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("book.txt"));
        reader.setLineMapper(new DefaultLineMapper<Book>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "id", "title", "writer", "publisher", "publishDate" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Book>() {{
                setTargetType(Book.class);
            }});
        }});
        return reader;
    }

    @Bean
    public TxtToDatabaseStep1Processor txtToDatabaseStep1Processor() {
        return new TxtToDatabaseStep1Processor();
    }

    @Bean
    public JdbcBatchItemWriter<Book> txtToDatabaseStep1Writer() {
        JdbcBatchItemWriter<Book> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO books (id, title, writer, publisher, publish_date) VALUES (:id, :title, :writer, :publisher, :publishDate)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Step txtToDatabaseStep2() {
        return stepBuilderFactory.get("txtToDatabaseStep2")
                .<Book, Book> chunk(5)
                .reader(txtToDatabaseStep2Reader())
                .processor(txtToDatabaseStep2Processor())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Book> txtToDatabaseStep2Reader() {
        JdbcCursorItemReader<Book> reader = new JdbcCursorItemReader<>();
        String sql = "SELECT id, title, writer, publisher, publish_date FROM books";
        reader.setSql(sql);
        reader.setDataSource(dataSource);
        reader.setRowMapper((rs, row) -> new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
        return reader;
    }

    @Bean
    public TxtToDatabaseStep2Processor txtToDatabaseStep2Processor() {
        return new TxtToDatabaseStep2Processor();
    }
    //=================== end txtToDatabaseJob ===================

    //=================== start databaseToConsoleJob ===================
    @Bean
    public Job databaseToConsoleJob() {
        return jobBuilderFactory.get("databaseToConsoleJob")
                .incrementer(new RunIdIncrementer())
//                .listener(txtToDatabaseJobExecutionListener()) // 특별히 전후 처리 할 게 없으면 없어도 됨
                .flow(databaseToConsoleJSetp1())
                .end()
                .build();
    }

    @Bean
    public Step databaseToConsoleJSetp1() {
        return stepBuilderFactory.get("databaseToConsoleJSetp1")
                .<Book, Book> chunk(1000)
                .reader(databaseToConsoleJSetp1Reader())
                .processor(databaseToConsoleStep1Processor()) // processor, writer 둘 중 하나만 있어도 문제 없음
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Book> databaseToConsoleJSetp1Reader() {
        JdbcCursorItemReader<Book> reader = new JdbcCursorItemReader<>();
        String sql = "SELECT id, title, writer, publisher, publish_date FROM books order by publisher";
        reader.setSql(sql);
        reader.setDataSource(dataSource);
        reader.setRowMapper((rs, row) -> new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
        return reader;
    }

    @Bean
    public DatabaseToConsoleStep1Processor databaseToConsoleStep1Processor() {
        return new DatabaseToConsoleStep1Processor();
    }
    //=================== end databaseToConsoleJob ===================

}
