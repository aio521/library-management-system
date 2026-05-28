package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    @Select("SELECT m.* FROM menu m JOIN role_menu rm ON m.id = rm.menu_id WHERE rm.role_id = #{roleId} ORDER BY m.sort")
    List<Menu> selectByRoleId(Long roleId);

    @Select("SELECT DISTINCT m.* FROM menu m JOIN role_menu rm ON m.id = rm.menu_id " +
            "JOIN user_role ur ON rm.role_id = ur.role_id WHERE ur.user_id = #{userId} ORDER BY m.sort")
    List<Menu> selectByUserId(Long userId);
}
