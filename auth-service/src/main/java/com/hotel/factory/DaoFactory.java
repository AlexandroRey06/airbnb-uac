package com.hotel.factory;

import com.hotel.dao.*;

public class DaoFactory {
    
    public static UsuarioDao createUsuarioDao(DatabaseType type) {
        return new UsuarioDaoImpl(DatabaseConnection.getInstance().getConnection(type));
    }

    public static RolDao createRolDao(DatabaseType type) {
        return new RolDaoImpl(DatabaseConnection.getInstance().getConnection(type));
    }

    public static UsuarioDao createReplicatedUsuarioDao() {
        return new ReplicatedUsuarioDaoImpl(
            createUsuarioDao(DatabaseType.PRIMARY_DB),
            createUsuarioDao(DatabaseType.SECONDARY_DB)
        );
    }

    public static RolDao createReplicatedRolDao() {
        return new ReplicatedRolDaoImpl(
            createRolDao(DatabaseType.PRIMARY_DB),
            createRolDao(DatabaseType.SECONDARY_DB)
        );
    }
}