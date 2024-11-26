package com.hotel.controller;

import com.hotel.exception.DatabaseException;
import com.hotel.exception.EntityNotFoundException;
import com.hotel.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/error-handler")
public class ExceptionHandlerServlet extends HttpServlet {

   public static void handleException(HttpServletResponse response, Exception e) throws IOException {
       ErrorResponse errorResponse;
       int statusCode;

       if (e instanceof ValidationException) {
           statusCode = HttpServletResponse.SC_BAD_REQUEST; // 400
           errorResponse = new ErrorResponse("Error de validación", e.getMessage());
       }
       else if (e instanceof EntityNotFoundException) {
           statusCode = HttpServletResponse.SC_NOT_FOUND; // 404
           errorResponse = new ErrorResponse("Entidad no encontrada", e.getMessage());
       }
       else if (e instanceof DatabaseException) {
           statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
           errorResponse = new ErrorResponse("Error de base de datos", e.getMessage());
       }
       else {
           statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
           errorResponse = new ErrorResponse("Error interno del servidor", "Ha ocurrido un error inesperado");
       }

       response.setStatus(statusCode);
       response.setContentType("application/json");
       response.setCharacterEncoding("UTF-8");
       new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
   }
}