package com.hanielcota.essentials.modules.leaves.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record LeavesConfig(
    @Comment(
            "Master switch for the leaf-decay protection. Set to false to fully disable it — the"
                + " listener is not registered, so it cannot interfere with other plugins. Takes"
                + " effect on server start / module reload.")
        boolean enabled,
    @Comment(
            "How the worlds list is interpreted. WHITELIST = protection is active ONLY in the"
                + " listed worlds. BLACKLIST = active everywhere EXCEPT the listed worlds. The"
                + " default (BLACKLIST + empty list) keeps leaves from decaying in every world.")
        WorldMode worldMode,
    @Comment("Worlds the worldMode applies to. Use the exact world folder name.")
        List<String> worlds) {

  public enum WorldMode {
    WHITELIST,
    BLACKLIST
  }

  public static LeavesConfig defaults() {
    return new LeavesConfig(true, WorldMode.BLACKLIST, List.of());
  }

  public boolean appliesTo(String worldName) {
    var listed = worlds.contains(worldName);
    return worldMode == WorldMode.WHITELIST ? listed : !listed;
  }
}
