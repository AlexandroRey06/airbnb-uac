package com.hotel.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.hotel.exception.DatabaseException;

public class DatabaseConnection {
    // Configuración para múltiples bases de datos
    private static final Map<DatabaseType, String> DB_URLS = new HashMap<>();
    private static final Map<DatabaseType, String> DB_USERS = new HashMap<>();
    private static final Map<DatabaseType, String> DB_PASSWORDS = new HashMap<>();

    static {
        // Primera base de datos
        DB_URLS.put(DatabaseType.PRIMARY_DB, "jdbc:postgresql://hotel-db.c982saawuip8.us-east-1.rds.amazonaws.com:5432/hotel_db");
        DB_USERS.put(DatabaseType.PRIMARY_DB, "postgres");
        DB_PASSWORDS.put(DatabaseType.PRIMARY_DB, "Hotel123");

        // Segunda base de datos
        DB_URLS.put(DatabaseType.SECONDARY_DB, "jdbc:postgresql://hotel-db-backup.c982saawuip8.us-east-1.rds.amazonaws.com:5432/hotel_db_backup");
        DB_USERS.put(DatabaseType.SECONDARY_DB, "postgres");
        DB_PASSWORDS.put(DatabaseType.SECONDARY_DB, "Hotel123");
    }

    private static DatabaseConnection instance;
    private final Map<DatabaseType, Connection> connections;

    private DatabaseConnection() {
        connections = new HashMap<>();
        try {
            Class.forName("org.postgresql.Driver");
            // Inicializar conexiones para todas las bases de datos
            for (DatabaseType type : DatabaseType.values()) {
                connections.put(type, DriverManager.getConnection(
                    DB_URLS.get(type),
                    DB_USERS.get(type),
                    DB_PASSWORDS.get(type)
                ));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DatabaseException("Error al conectar con las bases de datos", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection(DatabaseType type) {
        return connections.get(type);
    }
}