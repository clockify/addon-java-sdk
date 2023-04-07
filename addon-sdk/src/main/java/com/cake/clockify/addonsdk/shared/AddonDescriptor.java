package com.cake.clockify.addonsdk.shared;

import lombok.NonNull;

public record AddonDescriptor(@NonNull String key,
                              @NonNull String name,
                              @NonNull String description,
                              @NonNull String baseUrl,
                              String iconPath) {
}