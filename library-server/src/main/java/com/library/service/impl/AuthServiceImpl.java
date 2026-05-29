package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.BusinessException;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.Menu;
import com.library.entity.Reader;
import com.library.entity.ReaderCard;
import com.library.entity.Role;
import com.library.entity.SmsCode;
import com.library.entity.User;
import com.library.mapper.MenuMapper;
import com.library.mapper.ReaderCardMapper;
import com.library.mapper.ReaderMapper;
import com.library.mapper.RoleMapper;
import com.library.mapper.SmsCodeMapper;
import com.library.mapper.UserMapper;
import com.library.security.JwtTokenProvider;
import com.library.service.AuthService;
import com.library.service.SmsProvider;
import com.library.utils.RedisUtil;
import com.library.vo.LoginVO;
import com.library.vo.MenuVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final SmsCodeMapper smsCodeMapper;
    private final SmsProvider smsProvider;
    private final ReaderMapper readerMapper;
    private final ReaderCardMapper readerCardMapper;
    private final RoleMapper roleMapper;
    private final JdbcTemplate jdbcTemplate;

    public AuthServiceImpl(UserMapper userMapper, MenuMapper menuMapper,
                           JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder, RedisUtil redisUtil,
                           SmsCodeMapper smsCodeMapper, SmsProvider smsProvider,
                           ReaderMapper readerMapper, ReaderCardMapper readerCardMapper,
                           RoleMapper roleMapper, JdbcTemplate jdbcTemplate) {
        this.userMapper = userMapper;
        this.menuMapper = menuMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
        this.smsCodeMapper = smsCodeMapper;
        this.smsProvider = smsProvider;
        this.readerMapper = readerMapper;
        this.readerCardMapper = readerCardMapper;
        this.roleMapper = roleMapper;
        this.jdbcTemplate = jdbcTemplate;
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

    @Override
    public void sendCode(String phone) {
        // 检查是否已注册
        Long exists = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, phone));
        if (exists > 0) {
            throw new BusinessException("该手机号已注册");
        }

        // 检查60秒内是否已发送
        long oneMinuteAgo = System.currentTimeMillis() - 60000;
        Long recentCount = smsCodeMapper.selectCount(
                new LambdaQueryWrapper<SmsCode>()
                        .eq(SmsCode::getPhone, phone)
                        .gt(SmsCode::getCreateTime, new java.sql.Timestamp(oneMinuteAgo).toLocalDateTime()));
        if (recentCount > 0) {
            throw new BusinessException("验证码已发送，请60秒后重试");
        }

        // 生成6位验证码
        String code = String.format("%06d", (int)(Math.random() * 1000000));

        // 保存验证码
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        smsCode.setUsed(0);
        smsCodeMapper.insert(smsCode);

        // 发送
        smsProvider.send(phone, code);
    }

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        // 校验验证码
        SmsCode validCode = smsCodeMapper.selectValid(dto.getPhone(), dto.getCode());
        if (validCode == null) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 标记已使用
        validCode.setUsed(1);
        smsCodeMapper.updateById(validCode);

        // 检查手机号是否已注册
        Long exists = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getPhone()));
        if (exists > 0) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        // 分配读者角色
        Role readerRole = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, "ROLE_READER"));
        if (readerRole != null) {
            jdbcTemplate.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)",
                    user.getId(), readerRole.getId());
        }

        // 创建读者
        Reader reader = new Reader();
        reader.setUserId(user.getId());
        reader.setReaderNo(
                org.springframework.util.StringUtils.hasText(dto.getReaderNo())
                        ? dto.getReaderNo()
                        : "XJ" + System.currentTimeMillis() % 100000000);
        reader.setName(dto.getName());
        reader.setDept(dto.getDept());
        reader.setPhone(dto.getPhone());
        reader.setMaxBorrow(5);
        reader.setBorrowDays(30);
        reader.setStatus(0);
        readerMapper.insert(reader);

        // 创建借阅证
        ReaderCard card = new ReaderCard();
        card.setReaderId(reader.getId());
        card.setCardNo("RD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", reader.getId() % 10000));
        card.setIssueDate(LocalDate.now());
        card.setExpireDate(LocalDate.now().plusYears(4));
        card.setStatus(0);
        readerCardMapper.insert(card);
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
