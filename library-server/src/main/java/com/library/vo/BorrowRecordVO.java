package com.library.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BorrowRecordVO {
    private Long id;
    private Long readerId;
    private String readerName;
    private String readerNo;
    private Long stockId;
    private String barcode;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime borrowDate;
    private LocalDate dueDate;
    private LocalDateTime returnDate;
    private Integer renewCount;
    private Integer status;
}
