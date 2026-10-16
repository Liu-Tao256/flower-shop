package com.flower.interceptor;

import com.flower.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 放行不需要登录的接口
        if (path.contains("/user/login") ||
                path.contains("/user/register") ||
                path.contains("/products") ||
                path.contains("/plant/identify") ||
                path.contains("/plant/top-rank") ||
                path.contains("/plant/gallery") ||
                path.contains("/news")) {
            return true;
        }

        // 验证token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }

        token = token.substring(7);
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期\"}");
            return false;
        }

        // 管理员接口权限检查
        String role = JwtUtil.getRoleFromToken(token);
        if (path.contains("/admin") && !"admin".equals(role)) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
            return false;
        }

        return true;
    }
}