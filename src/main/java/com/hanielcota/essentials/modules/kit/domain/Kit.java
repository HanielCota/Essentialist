package com.hanielcota.essentials.modules.kit.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * A claimable kit, fully resolved (items already deserialized). {@code armor} is a positional list
 * of four slots (boots, leggings, chestplate, helmet) whose entries may be {@code null}; {@code
 * offhand} is {@code null} when the kit has none.
 */
public record Kit(
    @NonNull String id,
    @NonNull String displayName,
    @NonNull Material icon,
    @NonNull String category,
    long cooldownSeconds,
    boolean oneTime,
    @NonNull String permission,
    boolean firstJoin,
    @NonNull List<ItemStack> storage,
    @NonNull List<ItemStack> armor,
    @Nullable ItemStack offhand,
    boolean dailyReset) {

  public boolean hasPermission() {
    return !this.permission.isBlank();
  }

  /** Whether the kit gates re-claims at all (a rolling cooldown or a daily reset). */
  public boolean hasCooldownGate() {
    return this.dailyReset || this.cooldownSeconds > 0;
  }

  public boolean isEmpty() {
    return this.storage.isEmpty()
        && this.armor.stream().allMatch(Objects::isNull)
        && this.offhand == null;
  }

  /** Every item the kit grants, for the read-only preview (storage, then armor, then off-hand). */
  public List<ItemStack> previewItems() {
    var items = new ArrayList<ItemStack>(this.storage);

    for (var piece : this.armor) {
      if (piece != null) {
        items.add(piece);
      }
    }
    if (this.offhand != null) {
      items.add(this.offhand);
    }

    return items;
  }
}
