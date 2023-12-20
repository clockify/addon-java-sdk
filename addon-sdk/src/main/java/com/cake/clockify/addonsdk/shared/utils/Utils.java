package com.cake.clockify.addonsdk.shared.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static String trimTrailingSlash(String s) {
        if (s.charAt(s.length() - 1) == '/') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }
}
