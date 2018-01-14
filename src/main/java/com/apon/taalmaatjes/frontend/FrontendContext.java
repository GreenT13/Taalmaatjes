package com.apon.taalmaatjes.frontend;

import com.apon.taalmaatjes.backend.database.jooq.Context;
import org.jooq.Configuration;

public class FrontendContext {
    private static FrontendContext ourInstance = new FrontendContext();

    public static FrontendContext getInstance() {
        return ourInstance;
    }

    private Context context;

    private FrontendContext() {
        context = new Context();
    }

    public Context getContext() {
        return context;
    }
}
