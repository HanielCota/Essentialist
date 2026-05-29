package com.hanielcota.essentials.modules.kit.domain;

import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A claimable kit, fully resolved (items already deserialized). The persisted definition lives in
 * {@code kits.yml}; this is the in-memory shape the catalog hands to the menus and the claim flow.
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
    @NonNull List<ItemStack> items) {

  public boolean hasPermission() {
    return !this.permission.isBlank();
  }

  public boolean hasCooldown() {
    return this.cooldownSeconds > 0;
  }
}
