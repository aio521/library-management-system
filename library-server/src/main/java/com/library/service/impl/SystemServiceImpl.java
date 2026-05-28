package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.BusinessException;
import com.library.dto.PageDTO;
import com.library.entity.*;
import com.library.mapper.*;
import com.library.service.SystemService;
import com.library.vo.UserVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SystemServiceImpl implements SystemService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final OperationLogMapper operationLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public SystemServiceImpl(UserMapper userMapper, RoleMapper roleMapper,
                             MenuMapper menuMapper, OperationLogMapper operationLogMapper,
                             PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.operationLogMapper = operationLogMapper;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<UserVO> userPage(PageDTO dto) {
        Page<User> page = new Page<>(dto.getPage(), dto.getPageSize());
        Page<User> userPage = userMapper.selectPage(page,
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime));

        Page<UserVO> voPage = new Page<>(dto.getPage(), dto.getPageSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(u -> {
            UserVO vo = new UserVO();
            vo.setId(u.getId());
            vo.setUsername(u.getUsername());
            vo.setRealName(u.getRealName());
            vo.setPhone(u.getPhone());
            vo.setStatus(u.getStatus());
            vo.setCreateTime(u.getCreateTime());
            vo.setRoles(userMapper.selectRoleCodesByUserId(u.getId()));
            return vo;
        }).toList());
        return voPage;
    }

    @Override
    @Transactional
    public User createUser(User user, List<Long> roleIds) {
        Long exists = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (exists > 0) throw new BusinessException("用户名已存在");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        userMapper.insert(user);

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                jdbcTemplate.update(
                        "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", user.getId(), roleId);
            }
        }
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user, List<Long> roleIds) {
        User existing = userMapper.selectById(user.getId());
        if (existing == null) throw new BusinessException("用户不存在");

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        userMapper.updateById(user);

        if (roleIds != null) {
            jdbcTemplate.update("DELETE FROM user_role WHERE user_id = ?", user.getId());
            for (Long roleId : roleIds) {
                jdbcTemplate.update(
                        "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", user.getId(), roleId);
            }
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM user_role WHERE user_id = ?", id);
        userMapper.deleteById(id);
    }

    @Override
    public List<Role> roleList() {
        return roleMapper.selectList(null);
    }

    @Override
    public Role createRole(Role role) {
        roleMapper.insert(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        roleMapper.updateById(role);
        return role;
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        jdbcTemplate.update("DELETE FROM role_menu WHERE role_id = ?", id);
        jdbcTemplate.update("DELETE FROM user_role WHERE role_id = ?", id);
        roleMapper.deleteById(id);
    }

    @Override
    public List<Menu> menuTree() {
        List<Menu> all = menuMapper.selectList(
                new LambdaQueryWrapper<Menu>().orderByAsc(Menu::getSort));

        Map<Long, List<Menu>> parentMap = new HashMap<>();
        for (Menu menu : all) {
            parentMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
        }

        return buildMenuChildren(0L, parentMap);
    }

    private List<Menu> buildMenuChildren(Long parentId, Map<Long, List<Menu>> parentMap) {
        List<Menu> result = new ArrayList<>();
        List<Menu> children = parentMap.get(parentId);
        if (children == null) return result;

        for (Menu menu : children) {
            menu.setChildren(buildMenuChildren(menu.getId(), parentMap));
            result.add(menu);
        }
        return result;
    }

    @Override
    @Transactional
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        jdbcTemplate.update("DELETE FROM role_menu WHERE role_id = ?", roleId);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                jdbcTemplate.update(
                        "INSERT INTO role_menu (role_id, menu_id) VALUES (?, ?)", roleId, menuId);
            }
        }
    }

    @Override
    public Page<OperationLog> logPage(PageDTO dto) {
        Page<OperationLog> page = new Page<>(dto.getPage(), dto.getPageSize());
        return operationLogMapper.selectPage(page,
                new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreateTime));
    }
}
