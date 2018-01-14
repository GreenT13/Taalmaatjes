package base;

import com.apon.taalmaatjes.backend.database.generated.tables.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.JDBCUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ContextForTests {
    private static ContextForTests ourInstance = new ContextForTests();

    public static ContextForTests getInstance() {
        return ourInstance;
    }

    private Connection connection;
    private DSLContext create;
    private Configuration configuration;

    private ContextForTests() {
        initializeConnectionInMemoryDb();
        initializeDb();
    }

    private void initializeDb() {
        // Initialize connection and database.
        initializeConnectionInMemoryDb();
        createDbCopy();

        // Make sure creation of the database is not rollbacked.
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeConnectionInMemoryDb() {
        // Create a new connection to the database.
        String url = "jdbc:h2:mem:testDbTaalmaatjes";

        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try {
            connection = DriverManager.getConnection(url, "", "");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        create = DSL.using(connection, SQLDialect.H2);
        configuration = DSL.using(connection, JDBCUtils.dialect(connection)).configuration();
    }

    private void createDbCopy() {
        List<Table<?>> allTables = Arrays.asList(Scriptlog.SCRIPTLOG,
                Student.STUDENT,
                Volunteer.VOLUNTEER,
                Volunteerinstance.VOLUNTEERINSTANCE,
                Volunteermatch.VOLUNTEERMATCH);

        for (Table<?> table : allTables) {
            for (Query query : create.ddl(table).queries()) {
                query.execute();
            }
        }
    }

    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
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
