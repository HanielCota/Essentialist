package com.hanielcota.essentials.modules.kit.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * The {@code kits.yml} file: every kit definition keyed by its id. Mutated by /kit create/delete.
 */
@ConfigSerializable
public record KitDefinitionsConfig(
    @Comment("Kit definitions keyed by id. Add kits in-game with /kit create <name>.")
        Map<String, KitDefinitionConfig> kits) {

  public KitDefinitionsConfig {
    kits = kits == null ? new LinkedHashMap<>() : new LinkedHashMap<>(kits);
  }

  public static KitDefinitionsConfig defaults() {
    // A fillable template: run "/kit create starter" holding the items, then flip the flags here.
    var starter =
        KitDefinitionConfig.of(
            "<green>Starter", Material.CHEST, "general", 0, false, "", false, java.util.List.of());

    var sample = new LinkedHashMap<String, KitDefinitionConfig>();
    sample.put("starter", starter);

    return new KitDefinitionsConfig(sample);
  }
}
