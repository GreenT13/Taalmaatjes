package com.apon.taalmaatjes.backend.util;

public class ObjectUtil {

    @SafeVarargs
    public static <T> T coalesce(T ...items) {
        for(T i : items) if(i != null) return i;
        return null;
    }

}
