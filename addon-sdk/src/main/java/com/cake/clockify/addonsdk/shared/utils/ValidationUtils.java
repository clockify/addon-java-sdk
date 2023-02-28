package com.cake.clockify.addonsdk.shared.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {
    public static boolean isValidManifestPath(String path) {
        return path != null
                && path.length() > 0
                && path.charAt(0) == '/'
                && path.charAt(path.length() - 1) != '/';
    }

    public static boolean isValidBaseUrl(String url) {
        if (url == null) {
            return false;
        }

        try {
            URI uri = new URL(url).toURI();
            return uri.getQuery() == null;
        } catch (Exception e) {
            return false;
        }
    }
}
