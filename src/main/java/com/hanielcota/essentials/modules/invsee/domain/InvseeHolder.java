package com.hanielcota.essentials.modules.invsee.domain;

import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/** Marks an inventory as an /invsee view and remembers whose inventory it mirrors. */
@RequiredArgsConstructor
public final class InvseeHolder implements InventoryHolder {

  private final UUID targetId;
  private Inventory inventory;

  public UUID targetId() {
    return this.targetId;
  }

  /** Set once, right after the inventory is created. */
  public void inventory(@NonNull Inventory inventory) {
    this.inventory = inventory;
  }

  @Override
  public @NonNull Inventory getInventory() {
    return this.inventory;
  }
}
