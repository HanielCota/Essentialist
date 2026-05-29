package com.hanielcota.essentials.modules.kit.domain;

import lombok.NonNull;
import org.bukkit.Material;

/** A kit category as shown in the category menu, resolved from the static config. */
public record KitCategory(
    @NonNull String id, @NonNull String displayName, @NonNull Material icon, int order) {}
