package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sms_code")
public class SmsCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String code;
    private LocalDateTime expireTime;
    private Integer used;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
