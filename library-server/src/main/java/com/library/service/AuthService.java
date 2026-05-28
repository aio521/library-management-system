package com.library.service;

import com.library.dto.LoginDTO;
import com.library.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO loginDTO);
    void logout(String token);
    LoginVO getUserInfo(Long userId);
}
