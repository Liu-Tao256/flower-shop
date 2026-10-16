package com.flower.controller;

import com.flower.entity.Address;
import com.flower.service.AddressService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }

    @GetMapping
    public Result<List<Address>> getAddressList(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(addressService.getAddressList(userId));
    }

    @GetMapping("/default")
    public Result<Address> getDefaultAddress(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        Address address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }

    @GetMapping("/{id}")
    public Result<Address> getAddressById(@PathVariable Long id) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            return Result.error("地址不存在");
        }
        return Result.success(address);
    }

    @PostMapping
    public Result<?> addAddress(@RequestBody Address address, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        address.setUserId(userId);
        addressService.addAddress(address);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<?> updateAddress(@PathVariable Long id, @RequestBody Address address, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        address.setId(id);
        address.setUserId(userId);
        addressService.updateAddress(address);
        return Result.success("修改成功");
    }

    @PutMapping("/{id}/default")
    public Result<?> setDefaultAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        addressService.setDefaultAddress(userId, id);
        return Result.success("设置成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        addressService.deleteAddress(id);
        return Result.success("删除成功");
    }
}