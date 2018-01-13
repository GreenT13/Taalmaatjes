package base;

import com.apon.taalmaatjes.backend.database.generated.tables.*;
import com.apon.taalmaatjes.backend.database.mydao.ScriptlogMyDao;
import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.JDBCUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class BaseTest {
    protected static Connection connection;
    protected static DSLContext create;
    protected static Configuration configuration;
    protected static Dummy dummy;

    @Before
    @After
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void initializeDb() {
        // Initialize connection and database.
        initializeConnectionInMemoryDb();
        createDbCopy();

        // Make sure creation of the database is not rollbacked.
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initialize Dummy class for easy testing.
        dummy = new Dummy(configuration);
    }

    public static void initializeConnectionInMemoryDb() {
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

    public static void createDbCopy() {
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
}
