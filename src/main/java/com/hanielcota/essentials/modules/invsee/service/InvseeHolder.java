package com.hanielcota.essentials.modules.invsee.service;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

/** Marks an inventory as an /invsee view and remembers whose inventory it mirrors. */
public final class InvseeHolder implements InventoryHolder {

  private final UUID targetId;
  private Inventory inventory;

  public InvseeHolder(UUID targetId) {
    this.targetId = Objects.requireNonNull(targetId, "targetId");
  }

  public UUID targetId() {
    return targetId;
  }

  /** Set once, right after the inventory is created. */
  public void inventory(Inventory inventory) {
    this.inventory = Objects.requireNonNull(inventory, "inventory");
  }

  @Override
  public @NonNull Inventory getInventory() {
    return inventory;
  }
}
