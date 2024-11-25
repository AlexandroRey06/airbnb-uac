package com.hotel.controller;

import com.hotel.dto.RolDTO;
import com.hotel.service.RolService;
import com.hotel.service.RolServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RolController extends HttpServlet {
    private final RolService rolService;
    private final ObjectMapper objectMapper;

    public RolController() {
        this.rolService = new RolServiceImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<RolDTO> roles = rolService.obtenerTodos();
                enviarRespuesta(response, roles);
            } else {
                String[] splits = pathInfo.split("/");
                if (splits.length == 2) {
                    Long id = Long.parseLong(splits[1]);
                    RolDTO rol = rolService.obtenerPorId(id);
                    enviarRespuesta(response, rol);
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
            RolDTO rolDTO = objectMapper.readValue(request.getReader(), RolDTO.class);
            RolDTO nuevoRol = rolService.crear(rolDTO);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            enviarRespuesta(response, nuevoRol);
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
            
            RolDTO rolDTO = objectMapper.readValue(request.getReader(), RolDTO.class);
            RolDTO rolActualizado = rolService.actualizar(id, rolDTO);
            
            enviarRespuesta(response, rolActualizado);
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
            
            rolService.eliminar(id);
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