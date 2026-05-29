package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.config.KitDefinitionConfig;
import com.hanielcota.essentials.modules.kit.repository.KitUsageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
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
   * Snapshots the admin's full kit (main inventory + worn armor + off-hand) as kit {@code rawId};
   * returns the total item count saved (0 means everything was empty). A {@code cooldownSeconds} of
   * {@code -1} keeps the existing cooldown (or 0 for a new kit); {@code >= 0} sets it.
   */
  public int create(@NonNull Player admin, @NonNull String rawId, long cooldownSeconds) {
    var id = rawId.toLowerCase(Locale.ROOT);
    var inventory = admin.getInventory();

    var storage = KitItemCodec.encode(nonEmpty(inventory.getStorageContents()));
    var armor = KitItemCodec.encodePositional(inventory.getArmorContents());
    var offhand = KitItemCodec.encode(List.of(inventory.getItemInOffHand()));

    var total = storage.size() + countPresent(armor) + offhand.size();
    if (total == 0) {
      return 0;
    }

    var definition = mergeOrCreate(id, rawId, storage, armor, offhand, cooldownSeconds);
    this.store.putKit(id, definition);
    this.catalog.rebuild();

    return total;
  }

  /**
   * Applies a metadata change to an existing kit; returns {@code false} when the kit is unknown.
   */
  public boolean edit(@NonNull String rawId, @NonNull UnaryOperator<KitDefinitionConfig> mutation) {
    var id = rawId.toLowerCase(Locale.ROOT);
    var existing = this.store.kits().get(id);
    if (existing == null) {
      return false;
    }

    var updated = mutation.apply(existing);
    this.store.putKit(id, updated);
    this.catalog.rebuild();

    return true;
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
      @NonNull String id,
      @NonNull String rawId,
      @NonNull List<String> storage,
      @NonNull List<String> armor,
      @NonNull List<String> offhand,
      long cooldownSeconds) {
    var existing = this.store.kits().get(id);
    var base =
        existing != null
            ? existing.withContents(storage, armor, offhand)
            : KitDefinitionConfig.of(
                rawId,
                Material.CHEST,
                DEFAULT_CATEGORY,
                0,
                false,
                "",
                false,
                storage,
                armor,
                offhand);

    if (cooldownSeconds < 0) {
      return base;
    }

    return base.withCooldownSeconds(cooldownSeconds);
  }

  private static List<ItemStack> nonEmpty(@NonNull ItemStack[] contents) {
    var items = new ArrayList<ItemStack>(contents.length);
    for (var item : contents) {
      if (item == null || item.getType().isAir()) {
        continue;
      }
      items.add(item);
    }

    return items;
  }

  private static int countPresent(@NonNull List<String> positional) {
    var present = 0;
    for (var entry : positional) {
      if (!entry.isBlank()) {
        present++;
      }
    }

    return present;
  }
}
