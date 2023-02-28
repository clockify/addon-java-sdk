package com.cake.clockify.addonsdk.shared.utils;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static final Gson GSON = new Gson();

    public static <T> T uncheckedCast(Object o) {
        return (T) o;
    }

    public static String trimTrailingSlash(String s) {
        if (s.charAt(s.length() - 1) == '/') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }
}
