package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_stock")
public class BookStock {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private String barcode;
    private String location;
    private Integer status;
    private String rfidTag;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
