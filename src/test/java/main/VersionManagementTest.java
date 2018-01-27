package main;

import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.update.VersionManagement;

import java.sql.SQLException;

public class VersionManagementTest {

    public static void main(String[] args) {
        try {
            VersionManagement.getInstance().runUpdates(new Context());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
