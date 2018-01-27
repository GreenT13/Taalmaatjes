package com.apon.taalmaatjes.frontend.presentation;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageResource {
    // Should overwrite this variable in Taalmaatjes.java if you want to change the language.
    public static Locale locale = new Locale("nl");

    private static MessageResource ourInstance = new MessageResource();

    public static MessageResource getInstance() {
        return ourInstance;
    }

    ResourceBundle messageResource;

    private MessageResource() {
        messageResource = ResourceBundle.getBundle("MessageResources", locale);
    }

    public String getValue(String messageCode) {
        String message;
        try {
            message = messageResource.getString(messageCode);
        } catch (MissingResourceException e) {
            message = "???" + messageCode + "???";
        }
        return message;
    }
}
