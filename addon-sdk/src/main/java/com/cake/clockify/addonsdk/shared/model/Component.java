package com.cake.clockify.addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Component {
    @NonNull
    private String type;

    @NonNull
    private String accessLevel;

    @NonNull
    private String path;

    private String label;

    private String iconPath;

    private Integer width;

    private Integer height;

    private Map<String, Object> options;
}

