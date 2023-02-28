package com.cake.clockify.addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class SettingsTab<S extends Setting, G extends SettingsGroup<S>> {
    @NonNull
    private String id;
    @NonNull
    private String name;

    private SettingHeader header;
    private List<S> settings;
    private List<G> groups;
}
