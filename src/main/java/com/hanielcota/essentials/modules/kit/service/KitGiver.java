package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.domain.Kit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.Nullable;

/**
 * Delivers a kit: armor is equipped into empty armor slots (occupied slots fall back to the
 * inventory), the off-hand likewise, and the rest goes to the main inventory. When {@code
 * dropOverflow} is off and it would not all fit, nothing is given ({@link
 * GiveResult#REJECTED_FULL}) so items are never silently lost.
 */
public final class KitGiver {

  private static final int ARMOR_SLOTS = 4;

  public GiveResult give(@NonNull Player player, @NonNull Kit kit, boolean dropOverflow) {
    var inventory = player.getInventory();

    var toInventory = new ArrayList<ItemStack>();
    for (var item : kit.storage()) {
      toInventory.add(item.clone());
    }

    var equipArmor = resolveArmor(inventory, kit.armor(), toInventory);
    var equipOffhand = resolveOffhand(inventory, kit.offhand(), toInventory);

    if (!dropOverflow && !fits(player, toInventory)) {
      return GiveResult.REJECTED_FULL;
    }

    applyArmor(inventory, equipArmor);
    if (equipOffhand != null) {
      inventory.setItemInOffHand(equipOffhand);
    }

    var leftover = inventory.addItem(toInventory.toArray(ItemStack[]::new));
    if (leftover.isEmpty()) {
      return GiveResult.GIVEN;
    }
    if (!dropOverflow) {
      return GiveResult.GIVEN;
    }

    dropAll(player, leftover.values());
    return GiveResult.OVERFLOW_DROPPED;
  }

  // Armor pieces to equip into currently-empty slots; pieces for occupied slots join toInventory.
  private static ItemStack[] resolveArmor(
      @NonNull PlayerInventory inventory,
      @NonNull List<ItemStack> kitArmor,
      @NonNull List<ItemStack> toInventory) {
    var current = inventory.getArmorContents();
    var equip = new ItemStack[ARMOR_SLOTS];

    for (var slot = 0; slot < ARMOR_SLOTS; slot++) {
      var piece = slot < kitArmor.size() ? kitArmor.get(slot) : null;
      if (piece == null) {
        continue;
      }

      if (isEmpty(current[slot])) {
        equip[slot] = piece.clone();
      } else {
        toInventory.add(piece.clone());
      }
    }

    return equip;
  }

  private static @Nullable ItemStack resolveOffhand(
      @NonNull PlayerInventory inventory,
      @Nullable ItemStack offhand,
      @NonNull List<ItemStack> toInventory) {
    if (offhand == null) {
      return null;
    }

    if (isEmpty(inventory.getItemInOffHand())) {
      return offhand.clone();
    }

    toInventory.add(offhand.clone());
    return null;
  }

  private static void applyArmor(@NonNull PlayerInventory inventory, @NonNull ItemStack[] equip) {
    if (equip[0] != null) {
      inventory.setBoots(equip[0]);
    }
    if (equip[1] != null) {
      inventory.setLeggings(equip[1]);
    }
    if (equip[2] != null) {
      inventory.setChestplate(equip[2]);
    }
    if (equip[3] != null) {
      inventory.setHelmet(equip[3]);
    }
  }

  // Non-destructive fit check: replay the add into a throwaway inventory seeded with a copy of the
  // player's storage so stacking rules are honoured without touching the live inventory.
  private static boolean fits(@NonNull Player player, @NonNull List<ItemStack> items) {
    var storage = player.getInventory().getStorageContents();

    var probe = Bukkit.createInventory(null, storage.length);
    probe.setContents(cloneContents(storage));

    var leftover = probe.addItem(cloneAll(items));
    return leftover.isEmpty();
  }

  private static void dropAll(@NonNull Player player, @NonNull Collection<ItemStack> items) {
    var world = player.getWorld();
    var location = player.getLocation();

    for (var item : items) {
      world.dropItemNaturally(location, item);
    }
  }

  private static boolean isEmpty(@Nullable ItemStack item) {
    return item == null || item.getType().isAir();
  }

  private static ItemStack[] cloneAll(@NonNull List<ItemStack> items) {
    return items.stream().map(ItemStack::clone).toArray(ItemStack[]::new);
  }

  private static ItemStack[] cloneContents(@NonNull ItemStack[] contents) {
    var copy = new ItemStack[contents.length];
    for (var i = 0; i < contents.length; i++) {
      copy[i] = contents[i] == null ? null : contents[i].clone();
    }

    return copy;
  }

  public enum GiveResult {
    GIVEN,
    OVERFLOW_DROPPED,
    REJECTED_FULL
  }
}
