package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.config.KitDefinitionConfig;
import com.hanielcota.essentials.modules.kit.domain.Kit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** In-memory view of the resolved kits (items already deserialized), rebuilt from the store. */
@RequiredArgsConstructor
public final class KitCatalog {

  private final KitDefinitionStore store;

  private volatile Map<String, Kit> kits = Map.of();

  public void rebuild() {
    var definitions = this.store.all();

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

  private static Kit toKit(@NonNull String id, @NonNull KitDefinitionConfig definition) {
    var items = KitItemCodec.decode(definition.items());

    return new Kit(
        id,
        definition.displayName(),
        definition.icon(),
        definition.category(),
        definition.cooldownSeconds(),
        definition.oneTime(),
        definition.permission(),
        definition.firstJoin(),
        items);
  }
}
