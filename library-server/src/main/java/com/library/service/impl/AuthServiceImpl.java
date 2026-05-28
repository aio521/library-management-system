package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.BusinessException;
import com.library.dto.LoginDTO;
import com.library.entity.Menu;
import com.library.entity.User;
import com.library.mapper.MenuMapper;
import com.library.mapper.UserMapper;
import com.library.security.JwtTokenProvider;
import com.library.service.AuthService;
import com.library.utils.RedisUtil;
import com.library.vo.LoginVO;
import com.library.vo.MenuVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final MenuMapper menuMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    public AuthServiceImpl(UserMapper userMapper, MenuMapper menuMapper,
                           JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder, RedisUtil redisUtil) {
        this.userMapper = userMapper;
        this.menuMapper = menuMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, loginDTO.getUsername()));
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), roles);

        List<Menu> menus = menuMapper.selectByUserId(user.getId());
        List<MenuVO> menuTree = buildMenuTree(menus);

        return LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles)
                .menus(menuTree)
                .build();
    }

    @Override
    public void logout(String token) {
        long ttl = 86400000;
        redisUtil.set("blacklist:" + token, "1", ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public LoginVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        List<String> roles = userMapper.selectRoleCodesByUserId(userId);
        List<Menu> menus = menuMapper.selectByUserId(userId);
        List<MenuVO> menuTree = buildMenuTree(menus);

        return LoginVO.builder()
                .token(null)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles)
                .menus(menuTree)
                .build();
    }

    private List<MenuVO> buildMenuTree(List<Menu> menus) {
        Map<Long, List<Menu>> parentMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        return buildChildren(0L, parentMap);
    }

    private List<MenuVO> buildChildren(Long parentId, Map<Long, List<Menu>> parentMap) {
        List<MenuVO> result = new ArrayList<>();
        List<Menu> children = parentMap.get(parentId);
        if (children == null) return result;

        for (Menu menu : children) {
            MenuVO vo = new MenuVO();
            vo.setId(menu.getId());
            vo.setName(menu.getName());
            vo.setPath(menu.getPath());
            vo.setComponent(menu.getComponent());
            vo.setIcon(menu.getIcon());
            vo.setParentId(menu.getParentId());
            vo.setSort(menu.getSort());
            vo.setChildren(buildChildren(menu.getId(), parentMap));
            result.add(vo);
        }
        return result;
    }
}
