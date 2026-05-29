package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.config.KitDefinitionConfig;
import com.hanielcota.essentials.modules.kit.repository.KitUsageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Admin operations: snapshot-create, delete and reload of kit definitions. */
@RequiredArgsConstructor
public final class KitAdminService {

  private static final String DEFAULT_CATEGORY = "general";

  private final KitConfigStore store;
  private final KitCatalog catalog;
  private final KitUsageRepository usage;

  /**
   * Snapshots the admin's main inventory as kit {@code rawId}; returns the saved item count (0 =
   * empty).
   */
  public int create(@NonNull Player admin, @NonNull String rawId) {
    var id = rawId.toLowerCase(Locale.ROOT);
    var items = nonEmptyContents(admin);
    if (items.isEmpty()) {
      return 0;
    }

    var encoded = KitItemCodec.encode(items);
    var definition = mergeOrCreate(id, rawId, encoded);

    this.store.putKit(id, definition);
    this.catalog.rebuild();

    return encoded.size();
  }

  public boolean delete(@NonNull String rawId) {
    var id = rawId.toLowerCase(Locale.ROOT);
    var removed = this.store.removeKit(id);

    if (removed) {
      this.catalog.rebuild();
      this.usage.deleteKit(id);
    }

    return removed;
  }

  public int reload() {
    this.store.load();
    this.catalog.rebuild();

    return this.catalog.size();
  }

  private KitDefinitionConfig mergeOrCreate(
      @NonNull String id, @NonNull String rawId, @NonNull List<String> encoded) {
    var existing = this.store.kits().get(id);
    if (existing != null) {
      return existing.withItems(encoded);
    }

    return KitDefinitionConfig.of(
        rawId, Material.CHEST, DEFAULT_CATEGORY, 0, false, "", false, encoded);
  }

  private static List<ItemStack> nonEmptyContents(@NonNull Player admin) {
    var inventory = admin.getInventory();
    var contents = inventory.getStorageContents();

    var items = new ArrayList<ItemStack>(contents.length);
    for (var item : contents) {
      if (item == null || item.getType().isAir()) {
        continue;
      }
      items.add(item);
    }

    return items;
  }
}
