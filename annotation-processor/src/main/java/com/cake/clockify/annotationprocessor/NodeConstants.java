package com.cake.clockify.annotationprocessor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeConstants {

    public static final String REF = "$ref";
    public static final String TYPE = "type";
    public static final String ANY_OF = "anyOf";
    public static final String DEFINITIONS = "definitions";
    public static final String PROPERTIES = "properties";
    public static final String REQUIRED = "required";
    public static final String ARRAY = "array";
    public static final String OBJECT = "object";
    public static final String STRING = "string";
    public static final String ENUM = "enum";
    public static final String ITEMS = "items";
    public static final String DESCRIPTION = "description";
}
