package com.hotel.util;

import org.mindrot.jbcrypt.BCrypt;

//Single Responsibility Principle (Principio de Responsabilidad Única)
//Solo se encarga de la encriptación de contraseñas
public class PasswordUtil {
    // Fuerza del trabajo para el hash (mayor número = más seguro pero más lento)
    private static final int WORKLOAD = 12;

    public static String hashPassword(String plainTextPassword) {
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plainTextPassword, salt);
    }

    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}