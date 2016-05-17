package com.drake1804.f1feedler.utils;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public class AppUtils {

    public static boolean validEmail(String email) {
        return email.matches("[A-Za-z0-9._%+-][A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    }

}
