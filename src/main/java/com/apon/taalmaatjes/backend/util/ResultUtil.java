package com.apon.taalmaatjes.backend.util;

import com.apon.taalmaatjes.backend.api.returns.Result;

public class ResultUtil {

    public static Result createError(String errorMessage) {
        return createError(errorMessage, null);
    }

    public static Result createError(String errorMessage, Exception e) {
        Result result = new Result();
        result.setHasErrors(true);
        result.setErrorMessage(errorMessage);
        result.setE(e);

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