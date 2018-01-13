package main;

import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.update.VersionManagement;

public class VersionManagementTest {

    public static void main(String[] args) {
        VersionManagement.getInstance().runUpdates(new Context());
    }

}
