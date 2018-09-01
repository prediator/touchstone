package com.touchstone.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";
    
    public static final String Url = "http://18.209.29.75:3000/api";
    public static final String local = "http://localhost:9000/#/succ";
    public static final String live = "http://www.ridgelift.io/touchstone/#/succ";

    private Constants() {
    }
}
