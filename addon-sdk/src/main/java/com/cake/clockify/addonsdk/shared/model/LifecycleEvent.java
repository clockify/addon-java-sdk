package com.cake.clockify.addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class LifecycleEvent {
    @NonNull
    protected String type;
    @NonNull
    protected String path;
}
