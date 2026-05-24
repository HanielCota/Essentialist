package com.hanielcota.essentials.modules.invsee.service;

import com.hanielcota.essentials.util.ComponentUtils;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class InvseeService {

  public static final int SIZE = 45;
  public static final int STORAGE_SLOTS = 36;
  public static final int HELMET_SLOT = 36;
  public static final int CHESTPLATE_SLOT = 37;
  public static final int LEGGINGS_SLOT = 38;
  public static final int BOOTS_SLOT = 39;
  public static final int OFFHAND_SLOT = 40;
  public static final int FIRST_LOCKED_SLOT = 41;

  private static final ItemStack FILLER = filler();

  // target -> viewer. Single viewer per target — every {@link #sync} bulk-writes the
  // view back onto the target, so two viewers with snapshot-stale views would clobber
  // each other's edits (item loss).
  private final ConcurrentMap<UUID, UUID> activeByTarget = new ConcurrentHashMap<>();

  private static ItemStack filler() {
    var item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    var meta = item.getItemMeta();
    meta.displayName(Component.empty());
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Builds the 45-slot invsee GUI populated with {@code target}'s inventory.
   *
   * <p>Returns empty when another viewer already holds {@code target} — concurrent views with stale
   * snapshots would corrupt the target on writeback. Caller must call {@link #release(UUID, UUID)}
   * when the view closes.
   */
  public Optional<Inventory> createView(
      @NonNull Player viewer, @NonNull Player target, @NonNull String title) {

    var targetId = target.getUniqueId();
    var viewerId = viewer.getUniqueId();
    // Any existing holder — even the same viewer — denies the new view. The previous view's
    // InventoryCloseEvent will fire `release(...)` for the old holder; allowing two open views
    // from the same viewer would let the old close handler free the lock while the new view is
    // still live and editing.
    var existing = this.activeByTarget.putIfAbsent(targetId, viewerId);
    if (existing != null) {
      return Optional.empty();
    }

    var holder = new InvseeHolder(targetId);
    var titleComponent = ComponentUtils.mini(title);
    var view = Bukkit.createInventory(holder, SIZE, titleComponent);
    holder.inventory(view);

    PlayerInventory source = target.getInventory();
    ItemStack[] storage = source.getStorageContents();
    for (int slot = 0; slot < STORAGE_SLOTS; slot++) {
      view.setItem(slot, storage[slot]);
    }
    view.setItem(HELMET_SLOT, source.getHelmet());
    view.setItem(CHESTPLATE_SLOT, source.getChestplate());
    view.setItem(LEGGINGS_SLOT, source.getLeggings());
    view.setItem(BOOTS_SLOT, source.getBoots());
    view.setItem(OFFHAND_SLOT, source.getItemInOffHand());
    for (int slot = FIRST_LOCKED_SLOT; slot < SIZE; slot++) {
      view.setItem(slot, FILLER);
    }
    return Optional.of(view);
  }

  /** Releases the viewer lock on {@code targetId} if held by {@code viewerId}. */
  public void release(@NonNull UUID targetId, @NonNull UUID viewerId) {
    this.activeByTarget.remove(targetId, viewerId);
  }

  /** Releases the viewer lock on {@code targetId} unconditionally (target quit/died). */
  public void releaseTarget(@NonNull UUID targetId) {
    this.activeByTarget.remove(targetId);
  }

  /** Writes the editable slots of {@code view} back into {@code target}'s inventory. */
  public void sync(@NonNull Player target, @NonNull Inventory view) {

    PlayerInventory inv = target.getInventory();
    ItemStack[] storage = new ItemStack[STORAGE_SLOTS];
    for (int slot = 0; slot < STORAGE_SLOTS; slot++) {
      storage[slot] = view.getItem(slot);
    }
    inv.setStorageContents(storage);
    inv.setHelmet(view.getItem(HELMET_SLOT));
    inv.setChestplate(view.getItem(CHESTPLATE_SLOT));
    inv.setLeggings(view.getItem(LEGGINGS_SLOT));
    inv.setBoots(view.getItem(BOOTS_SLOT));
    inv.setItemInOffHand(view.getItem(OFFHAND_SLOT));
  }
}
