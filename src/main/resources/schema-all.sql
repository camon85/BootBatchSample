DROP TABLE books IF EXISTS;

CREATE TABLE books  (
    id BIGINT,
    title VARCHAR(100),
    writer VARCHAR(100),
    publisher VARCHAR(50),
    publish_date VARCHAR(8)
);