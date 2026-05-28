package com.library.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReaderVO {
    private Long id;
    private String readerNo;
    private String name;
    private Integer gender;
    private String idCard;
    private String dept;
    private String phone;
    private Integer maxBorrow;
    private Integer borrowDays;
    private Integer status;
    private String cardNo;
    private Integer currentBorrowCount;
    private LocalDateTime createTime;
}
