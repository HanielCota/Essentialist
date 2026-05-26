package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record PlaceholderConfig(
    @Comment(
            "PAPI placeholder string evaluated for the <prefix> tag in chat formats. Empty"
                + " disables. Example: %vault_prefix% or %luckperms_prefix%.")
        String prefixPlaceholder,
    @Comment(
            "PAPI placeholder string evaluated for the <suffix> tag in chat formats. Empty"
                + " disables.")
        String suffixPlaceholder) {

  public static PlaceholderConfig defaults() {
    return new PlaceholderConfig("%vault_prefix%", "%vault_suffix%");
  }
}
