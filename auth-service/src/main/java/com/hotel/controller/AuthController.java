package com.hotel.controller;

import com.hotel.dto.LoginDTO;
import com.hotel.dto.UsuarioRegistroDTO;
import com.hotel.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthController extends HttpServlet {
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public AuthController() {
        this.authService = new AuthService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/login".equals(pathInfo)) {
                handleLogin(request, response);
            } else if ("/register".equals(pathInfo)) {
                handleRegister(request, response);
            } else {
                enviarError(response, "Ruta no encontrada", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            enviarError(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            LoginDTO loginDTO = objectMapper.readValue(request.getReader(), LoginDTO.class);
            Map<String, Object> result = authService.login(loginDTO);
            enviarRespuesta(response, result);
        } catch (Exception e) {
            enviarError(response, "Error en la autenticación: " + e.getMessage(), 
                       HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            // Cambiar a UsuarioRegistroDTO
            UsuarioRegistroDTO registroDTO = objectMapper.readValue(request.getReader(), UsuarioRegistroDTO.class);
            Map<String, Object> result = authService.register(registroDTO);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            enviarRespuesta(response, result);
        } catch (Exception e) {
            enviarError(response, "Error en el registro: " + e.getMessage(), 
                       HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/me".equals(pathInfo)) {
                handleGetCurrentUser(request, response);
            } else {
                enviarError(response, "Ruta no encontrada", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            enviarError(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetCurrentUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Map<String, Object> userData = authService.getCurrentUser(token);
                enviarRespuesta(response, userData);
            } else {
                enviarError(response, "Token no proporcionado", HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            enviarError(response, "Error al obtener usuario actual: " + e.getMessage(), 
                       HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void enviarRespuesta(HttpServletResponse response, Object dato) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), dato);
    }

    private void enviarError(HttpServletResponse response, String mensaje, int status) 
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", mensaje);
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}