package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reserve")
public class Reserve {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long readerId;
    private Long bookId;
    private LocalDateTime reserveDate;
    private LocalDate expireDate;
    private Integer status;
}
