package com.hotel.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hotel.exception.DatabaseException;

public class DatabaseConnection {
   private static final int MAX_POOL_SIZE = 5;
   private static final int INITIAL_POOL_SIZE = 2;
   private static final long CONNECTION_TIMEOUT = 10000; // 10 segundos

   private static final Map<DatabaseType, List<PooledConnection>> connectionPools = new HashMap<>();
   private static final Lock lock = new ReentrantLock();

   private static DatabaseConnection instance;

   // URLs y credenciales de las bases de datos
   private static final Map<DatabaseType, String> DB_URLS = new HashMap<>();
   private static final Map<DatabaseType, String> DB_USERS = new HashMap<>();
   private static final Map<DatabaseType, String> DB_PASSWORDS = new HashMap<>();

   static {
       // Configuración de bases de datos
       DB_URLS.put(DatabaseType.PRIMARY_DB, "jdbc:postgresql://hotel-db.c982saawuip8.us-east-1.rds.amazonaws.com:5432/hotel_db");
       DB_USERS.put(DatabaseType.PRIMARY_DB, "postgres");
       DB_PASSWORDS.put(DatabaseType.PRIMARY_DB, "Hotel123");

       DB_URLS.put(DatabaseType.SECONDARY_DB, "jdbc:postgresql://hotel-db-backup.c982saawuip8.us-east-1.rds.amazonaws.com:5432/hotel_db_backup");
       DB_USERS.put(DatabaseType.SECONDARY_DB, "postgres");
       DB_PASSWORDS.put(DatabaseType.SECONDARY_DB, "Hotel123");

       try {
           Class.forName("org.postgresql.Driver");
           // Inicializar pools para cada tipo de base de datos
           for (DatabaseType type : DatabaseType.values()) {
               List<PooledConnection> pool = new ArrayList<>();
               for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                   pool.add(createNewConnection(type));
               }
               connectionPools.put(type, pool);
           }
       } catch (Exception e) {
           throw new DatabaseException("Error initializing database pools", e);
       }
   }

   private static PooledConnection createNewConnection(DatabaseType type) throws SQLException {
       String url = DB_URLS.get(type);
       String user = DB_USERS.get(type);
       String password = DB_PASSWORDS.get(type);
       Connection conn = DriverManager.getConnection(url, user, password);
       return new PooledConnection(conn);
   }

   private DatabaseConnection() {
       // Constructor privado para singleton
   }

   public static DatabaseConnection getInstance() {
       if (instance == null) {
           synchronized (DatabaseConnection.class) {
               if (instance == null) {
                   instance = new DatabaseConnection();
               }
           }
       }
       return instance;
   }

   public Connection getConnection(DatabaseType type) {
       long startTime = System.currentTimeMillis();
       while (System.currentTimeMillis() - startTime < CONNECTION_TIMEOUT) {
           lock.lock();
           try {
               List<PooledConnection> pool = connectionPools.get(type);

               // Buscar una conexión disponible
               for (PooledConnection pooledConn : pool) {
                   if (!pooledConn.isInUse() && isConnectionValid(pooledConn.getConnection())) {
                       pooledConn.setInUse(true);
                       return pooledConn.getConnection();
                   }
               }

               // Si no hay conexiones disponibles y no alcanzamos el máximo, crear una nueva
               if (pool.size() < MAX_POOL_SIZE) {
                   PooledConnection newConn = createNewConnection(type);
                   newConn.setInUse(true);
                   pool.add(newConn);
                   return newConn.getConnection();
               }
           } catch (SQLException e) {
               System.err.println("Error getting connection: " + e.getMessage());
           } finally {
               lock.unlock();
           }

           // Esperar un poco antes de reintentar
           try {
               Thread.sleep(100);
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               throw new DatabaseException("Connection wait interrupted", e);
           }
       }

       throw new DatabaseException("Timeout waiting for database connection");
   }

   private boolean isConnectionValid(Connection conn) {
       try {
           return conn != null && !conn.isClosed() && conn.isValid(1);
       } catch (SQLException e) {
           return false;
       }
   }

   // Clase interna para manejar conexiones en el pool
   private static class PooledConnection {
       private final Connection connection;
       private boolean inUse;

       public PooledConnection(Connection connection) {
           this.connection = connection;
           this.inUse = false;
       }

       public Connection getConnection() {
           return connection;
       }

       public boolean isInUse() {
           return inUse;
       }

       public void setInUse(boolean inUse) {
           this.inUse = inUse;
       }
   }

   // Método para liberar una conexión
   public void releaseConnection(Connection conn, DatabaseType type) {
       lock.lock();
       try {
           List<PooledConnection> pool = connectionPools.get(type);
           for (PooledConnection pooledConn : pool) {
               if (pooledConn.getConnection() == conn) {
                   pooledConn.setInUse(false);
                   break;
               }
           }
       } finally {
           lock.unlock();
       }
   }

   // Método para cerrar todas las conexiones (útil al apagar la aplicación)
   public void closeAllConnections() {
       lock.lock();
       try {
           for (List<PooledConnection> pool : connectionPools.values()) {
               for (PooledConnection pooledConn : pool) {
                   try {
                       pooledConn.getConnection().close();
                   } catch (SQLException e) {
                       System.err.println("Error closing connection: " + e.getMessage());
                   }
               }
               pool.clear();
           }
           connectionPools.clear();
       } finally {
           lock.unlock();
       }
   }

   // Método para obtener el número de conexiones activas (útil para monitoreo)
   public int getActiveConnections(DatabaseType type) {
       lock.lock();
       try {
           List<PooledConnection> pool = connectionPools.get(type);
           return (int) pool.stream().filter(PooledConnection::isInUse).count();
       } finally {
           lock.unlock();
       }
   }
}