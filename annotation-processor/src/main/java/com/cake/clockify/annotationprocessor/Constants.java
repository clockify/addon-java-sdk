package com.cake.clockify.annotationprocessor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String DELIMITER_NAME_PARTS = "_";

    public static final String REGEX_METHOD_NAME_SPLIT = "[.\\-_]";
    public static final String REGEX_UPPER_CASE_SPLIT = "(?=\\p{Upper})";

    public static final String CLOCKIFY_MODEL_PACKAGE = "com.cake.clockify.addonsdk.clockify.model";
    public static final String CLOCKIFY_MANIFESTS_DIR = "clockify-manifests";
    public static final List<String> CLOCKIFY_MANIFESTS = List.of(
            CLOCKIFY_MANIFESTS_DIR + "/1.2.json",
            CLOCKIFY_MANIFESTS_DIR + "/1.3.json",
            CLOCKIFY_MANIFESTS_DIR + "/1.4.json"
    );

    public static final String CLOCKIFY_PREFIX = "Clockify";
    public static final String CLOCKIFY_MANIFEST_INTERFACE = "ClockifyManifest";
    public static final String CLOCKIFY_PATH_INTERFACE = "ClockifyResource";
}
