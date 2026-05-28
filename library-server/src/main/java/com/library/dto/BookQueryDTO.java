package com.library.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookQueryDTO extends PageDTO {
    private String isbn;
    private String title;
    private String author;
    private Long categoryId;
}
