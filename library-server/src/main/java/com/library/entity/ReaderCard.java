package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("reader_card")
public class ReaderCard {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long readerId;
    private String cardNo;
    private LocalDate issueDate;
    private LocalDate expireDate;
    private Integer status;
}
