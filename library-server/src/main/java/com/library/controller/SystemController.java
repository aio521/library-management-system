package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.PageDTO;
import com.library.entity.*;
import com.library.service.SystemService;
import com.library.vo.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/users")
    public Result<Page<UserVO>> userPage(PageDTO dto) {
        return Result.success(systemService.userPage(dto));
    }

    @PostMapping("/users")
    public Result<User> createUser(@RequestBody Map<String, Object> body) {
        User user = new User();
        user.setUsername((String) body.get("username"));
        user.setPassword((String) body.get("password"));
        user.setRealName((String) body.get("realName"));
        user.setPhone((String) body.get("phone"));
        @SuppressWarnings("unchecked")
        List<Integer> roleIdInts = (List<Integer>) body.get("roleIds");
        List<Long> roleIds = roleIdInts != null ? roleIdInts.stream().map(Long::valueOf).toList() : null;
        return Result.success(systemService.createUser(user, roleIds));
    }

    @PutMapping("/users/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User user = new User();
        user.setId(id);
        user.setUsername((String) body.get("username"));
        user.setPassword((String) body.get("password"));
        user.setRealName((String) body.get("realName"));
        user.setPhone((String) body.get("phone"));
        user.setStatus((Integer) body.get("status"));
        @SuppressWarnings("unchecked")
        List<Integer> roleIdInts = (List<Integer>) body.get("roleIds");
        List<Long> roleIds = roleIdInts != null ? roleIdInts.stream().map(Long::valueOf).toList() : null;
        return Result.success(systemService.updateUser(user, roleIds));
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        systemService.deleteUser(id);
        return Result.success();
    }

    @GetMapping("/roles")
    public Result<List<Role>> roleList() {
        return Result.success(systemService.roleList());
    }

    @PostMapping("/roles")
    public Result<Role> createRole(@RequestBody Role role) {
        return Result.success(systemService.createRole(role));
    }

    @PutMapping("/roles/{id}")
    public Result<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return Result.success(systemService.updateRole(role));
    }

    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        systemService.deleteRole(id);
        return Result.success();
    }

    @GetMapping("/menus")
    public Result<List<Menu>> menuTree() {
        return Result.success(systemService.menuTree());
    }

    @PostMapping("/roles/{id}/menus")
    public Result<Void> assignRoleMenus(@PathVariable Long id, @RequestBody Map<String, List<Integer>> body) {
        @SuppressWarnings("unchecked")
        List<Integer> menuIdInts = body.get("menuIds");
        List<Long> menuIds = menuIdInts != null ? menuIdInts.stream().map(Long::valueOf).toList() : null;
        systemService.assignRoleMenus(id, menuIds);
        return Result.success();
    }

    @GetMapping("/logs")
    public Result<Page<OperationLog>> logPage(PageDTO dto) {
        return Result.success(systemService.logPage(dto));
    }
}
