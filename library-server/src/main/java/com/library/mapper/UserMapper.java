package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT r.role_code FROM user_role ur JOIN role r ON ur.role_id = r.id WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodesByUserId(Long userId);
}
