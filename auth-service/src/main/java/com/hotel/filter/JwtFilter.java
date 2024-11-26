package com.hotel.filter;

import com.hotel.util.JwtUtil;
import io.jsonwebtoken.Claims;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class JwtFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Permitir solicitudes OPTIONS (CORS)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Rutas públicas
        String path = httpRequest.getRequestURI();
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"mensaje\":\"Token no proporcionado\"}");
            httpResponse.setContentType("application/json");
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = JwtUtil.validateToken(token);
            request.setAttribute("claims", claims);
            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"mensaje\":\"Token inválido\"}");
            httpResponse.setContentType("application/json");
        }
    }

    private boolean isPublicPath(String path) {
        System.out.println("Checking path: " + path);
        return  path.endsWith("/api/roles") ||
                path.endsWith("/api/auth/login") ||
                path.endsWith("/api/auth/register");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}