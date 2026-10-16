package com.flower.controller;

import com.flower.dto.*;
import com.flower.entity.User;
import com.flower.service.UserService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        try {
            return Result.success(userService.register(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO dto) {
        try {
            return Result.success(userService.login(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<?> getInfo(HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody UserProfileDTO dto, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        userService.updateProfile(userId, dto);
        return Result.success("修改成功");
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody ChangePasswordDTO dto, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        try {
            userService.changePassword(userId, dto);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/phone")
    public Result<?> changePhone(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        String newPhone = params.get("phone");
        if (newPhone == null || !newPhone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }
        userService.changePhone(userId, newPhone);
        return Result.success("手机号修改成功");
    }
}
