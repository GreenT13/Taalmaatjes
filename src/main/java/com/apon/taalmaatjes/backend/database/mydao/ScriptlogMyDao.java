package com.apon.taalmaatjes.backend.database.mydao;

import com.apon.taalmaatjes.backend.database.generated.tables.daos.ScriptlogDao;
import org.jooq.Configuration;

public class ScriptlogMyDao extends ScriptlogDao {

    public ScriptlogMyDao(Configuration configuration) {
        super(configuration);
    }

}
