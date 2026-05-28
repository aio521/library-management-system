package com.library.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private Integer status;
    private List<String> roles;
    private LocalDateTime createTime;
}
