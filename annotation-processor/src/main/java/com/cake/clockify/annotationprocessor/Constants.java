package com.cake.clockify.annotationprocessor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String MANIFEST_FILE = "clockify-manifest-v1.2.json";
    public static final String DELIMITER_NAME_PARTS = "_";

    public static final String REGEX_METHOD_NAME_SPLIT = "[.\\-_]";
    public static final String REGEX_UPPER_CASE_SPLIT = "(?=\\p{Upper})";

    public static final String CLOCKIFY_PREFIX = "Clockify";
}
