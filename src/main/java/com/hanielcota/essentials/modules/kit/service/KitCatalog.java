package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.config.KitDefinitionConfig;
import com.hanielcota.essentials.modules.kit.domain.Kit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** In-memory view of the resolved kits (items already deserialized), rebuilt from the config. */
@RequiredArgsConstructor
public final class KitCatalog {

  private final ConfigHandle<KitConfig> config;

  private volatile Map<String, Kit> kits = Map.of();

  public void rebuild() {
    var definitions = this.config.value().kits();

    var resolved = new LinkedHashMap<String, Kit>(definitions.size());
    for (var entry : definitions.entrySet()) {
      var kit = toKit(entry.getKey(), entry.getValue());
      resolved.put(entry.getKey(), kit);
    }

    this.kits = Map.copyOf(resolved);
  }

  public Optional<Kit> find(@NonNull String id) {
    return Optional.ofNullable(this.kits.get(id));
  }

  public List<Kit> all() {
    return List.copyOf(this.kits.values());
  }

  public List<Kit> byCategory(@NonNull String categoryId) {
    return this.kits.values().stream().filter(kit -> kit.category().equals(categoryId)).toList();
  }

  public List<Kit> firstJoinKits() {
    return this.kits.values().stream().filter(Kit::firstJoin).toList();
  }

  public int size() {
    return this.kits.size();
  }

  private static final int ARMOR_SLOTS = 4;

  private static Kit toKit(@NonNull String id, @NonNull KitDefinitionConfig definition) {
    var storage = KitItemCodec.decode(definition.items());
    var armor = KitItemCodec.decodePositional(definition.armor(), ARMOR_SLOTS);

    var offhandItems = KitItemCodec.decode(definition.offhand());
    var offhand = offhandItems.isEmpty() ? null : offhandItems.getFirst();

    return new Kit(
        id,
        definition.displayName(),
        definition.icon(),
        definition.category(),
        definition.cooldownSeconds(),
        definition.oneTime(),
        definition.permission(),
        definition.firstJoin(),
        storage,
        armor,
        offhand,
        definition.dailyReset());
  }
}
