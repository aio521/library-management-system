package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reader")
public class Reader {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String readerNo;
    @NotBlank(message = "姓名不能为空")
    private String name;
    private Integer gender;
    private String idCard;
    private String dept;
    private String phone;
    private Integer maxBorrow;
    private Integer borrowDays;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
