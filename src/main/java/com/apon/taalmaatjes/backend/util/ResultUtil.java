package com.apon.taalmaatjes.backend.util;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.log.Log;

public class ResultUtil {

    public static Result createError(String errorCode) {
        return createError(errorCode, null);
    }

    public static Result createError(String errorCode, Exception e) {
        Result result = new Result();
        result.setHasErrors(true);
        result.setErrorMessage(errorCode);
        result.setE(e);

        // Log the error so we can see what the user has done.
        Log.logError(errorCode, e);
        return result;
    }

    public static Result createOk(Object object) {
        Result result = new Result();
        result.setHasErrors(false);
        result.setResult(object);

        return result;
    }

    public static Result createOk() {
        return createOk(null);
    }
}