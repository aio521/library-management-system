package com.library.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private List<String> roles;
    private List<MenuVO> menus;
}
