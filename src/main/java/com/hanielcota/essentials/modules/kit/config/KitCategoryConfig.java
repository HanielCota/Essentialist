package com.hanielcota.essentials.modules.kit.config;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Display metadata for one kit category (referenced by id from each kit definition). */
@ConfigSerializable
public record KitCategoryConfig(
    @Comment("Display name shown in the category menu (MiniMessage).") String displayName,
    @Comment("Icon shown for this category.") Material icon,
    @Comment("Sort order in the category menu (ascending).") int order) {}
