package com.library.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookVO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private String categoryName;
    private String edition;
    private String coverUrl;
    private String description;
    private Integer totalStock;
    private Integer availableStock;
    private LocalDateTime createTime;
}
