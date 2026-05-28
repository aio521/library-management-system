package com.library.config;

import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String placeholder = "TO_BE_REPLACED_BY_INIT_RUNNER";

        User admin = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (admin != null && placeholder.equals(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode("admin123"));
            userMapper.updateById(admin);
        }

        User librarian = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "librarian"));
        if (librarian != null && placeholder.equals(librarian.getPassword())) {
            librarian.setPassword(passwordEncoder.encode("admin123"));
            userMapper.updateById(librarian);
        }
    }
}
