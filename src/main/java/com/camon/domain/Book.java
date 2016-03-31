package com.camon.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by user on 2016-03-31.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private int id;

    private String writer;

    private String title;

    private String publisher;

    private String publishDate;



}
