package com.apon.taalmaatjes.backend.database.jooq;

import com.apon.taalmaatjes.backend.log.Log;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.JDBCUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Context {
    private Connection connection;
    private DSLContext create;
    private Configuration configuration;

    public Context() {
        createNewConnection();
    }

    private void createNewConnection() {
        // Create a new connection to the database.
        String userName = "";
        String password = "";
        String url = "jdbc:h2:file:./Taalmaatjes-db";

        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try {
            connection = DriverManager.getConnection(url, userName, password);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } catch (SQLException e) {
            Log.error("Could not create connection.", e);
            return;
        }

        create = DSL.using(connection, SQLDialect.H2);
        configuration = DSL.using(connection, JDBCUtils.dialect(connection)).configuration();
    }

    public boolean commit() {
        try {
            connection.commit();
            return true;
        } catch (SQLException e) {
            Log.error("Could not commit connection.", e);
            return false;
        }
    }

    public boolean rollback() {
        try {
            connection.rollback();
            return true;
        } catch (SQLException e) {
            Log.error("Could not rollback connection.", e);
            return false;
        }
    }

    public boolean close() {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            Log.error("Could not close connection.", e);
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public DSLContext getCreate() {
        return create;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
