package com.flower.service;

import com.flower.dto.*;
import com.flower.entity.User;
import com.flower.mapper.UserMapper;
import com.flower.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> register(RegisterDTO dto) {
        User existUser = userMapper.findByPhone(dto.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已注册");
        }

        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : "花友" + dto.getPhone().substring(7));
        user.setAvatar("/default-avatar.png");
        user.setRole("user");
        user.setStatus("active");
        user.setPoints(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        return generateToken(user);
    }

    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.findByPhone(dto.getPhone());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账号已被封禁");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return generateToken(user);
    }

    public User getUserById(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    public void updateProfile(Long userId, UserProfileDTO dto) {
        User user = new User();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setAvatar(dto.getAvatar());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateProfile(user);
    }

    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userMapper.findById(userId);
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updatePassword(user);
    }

    public void changePhone(Long userId, String newPhone) {
        User existUser = userMapper.findByPhone(newPhone);
        if (existUser != null && !existUser.getId().equals(userId)) {
            throw new RuntimeException("手机号已被其他账号使用");
        }
        User user = new User();
        user.setId(userId);
        user.setPhone(newPhone);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updatePhone(user);
    }

    // 统计用户总数（管理后台用）
    public int count() {
        return userMapper.count();
    }

    private Map<String, Object> generateToken(User user) {
        String token = JwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("role", user.getRole());
        result.put("points", user.getPoints());
        return result;
    }
}