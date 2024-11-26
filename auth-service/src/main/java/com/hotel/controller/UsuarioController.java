package com.hotel.controller;

import com.hotel.dto.UsuarioDTO;
import com.hotel.dto.UsuarioRegistroDTO;
import com.hotel.service.UsuarioService;
import com.hotel.service.UsuarioServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UsuarioController extends HttpServlet {
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;

    public UsuarioController() {
        // Aquí deberías usar inyección de dependencias
        this.usuarioService = new UsuarioServiceImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<UsuarioDTO> usuarios = usuarioService.obtenerTodos();
                enviarRespuesta(response, usuarios);
            } else {
                String[] splits = pathInfo.split("/");
                if (splits.length == 2) {
                    Long id = Long.parseLong(splits[1]);
                    UsuarioDTO usuario = usuarioService.obtenerPorId(id);
                    enviarRespuesta(response, usuario);
                }
            }
        } catch (Exception e) {
            ExceptionHandlerServlet.handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            UsuarioRegistroDTO registroDTO = objectMapper.readValue(request.getReader(), UsuarioRegistroDTO.class);
            UsuarioDTO nuevoUsuario = usuarioService.crear(registroDTO);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            enviarRespuesta(response, nuevoUsuario);
        } catch (Exception e) {
            ExceptionHandlerServlet.handleException(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String[] pathParts = request.getPathInfo().split("/");
            Long id = Long.parseLong(pathParts[1]);
            
            UsuarioDTO usuarioDTO = objectMapper.readValue(request.getReader(), UsuarioDTO.class);
            UsuarioDTO usuarioActualizado = usuarioService.actualizar(id, usuarioDTO);
            
            enviarRespuesta(response, usuarioActualizado);
        } catch (Exception e) {
            ExceptionHandlerServlet.handleException(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String[] pathParts = request.getPathInfo().split("/");
            Long id = Long.parseLong(pathParts[1]);
            
            usuarioService.eliminar(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            ExceptionHandlerServlet.handleException(response, e);
        }
    }

    private void enviarRespuesta(HttpServletResponse response, Object dato) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), dato);
    }
}