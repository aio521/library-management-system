package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.PageDTO;
import com.library.entity.*;
import com.library.vo.UserVO;

import java.util.List;

public interface SystemService {
    Page<UserVO> userPage(PageDTO dto);
    User createUser(User user, List<Long> roleIds);
    User updateUser(User user, List<Long> roleIds);
    void deleteUser(Long id);
    List<Role> roleList();
    Role createRole(Role role);
    Role updateRole(Role role);
    void deleteRole(Long id);
    List<Menu> menuTree();
    void assignRoleMenus(Long roleId, List<Long> menuIds);
    Page<OperationLog> logPage(PageDTO dto);
}
