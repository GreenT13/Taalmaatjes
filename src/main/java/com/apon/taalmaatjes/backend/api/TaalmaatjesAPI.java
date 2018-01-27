package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.util.ResultUtil;

public class TaalmaatjesAPI {
    private static TaalmaatjesAPI ourInstance = new TaalmaatjesAPI();
    private final static String VERSION_NUMBER = "v0.1";
    private final static String RELEASE_DATE = "2018-01-27";

    public static TaalmaatjesAPI getInstance() {
        return ourInstance;
    }

    private TaalmaatjesAPI() { }

    /**
     * Get the version number.
     * @return
     */
    public Result getVersionNumber() {
        return ResultUtil.createOk(VERSION_NUMBER);
    }

    /**
     * Get the release date.
     * @return
     */
    public Result getReleaseDate() {
        return ResultUtil.createOk(RELEASE_DATE);
    }
}