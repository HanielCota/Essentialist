package com.hanielcota.essentials.modules.invsee.domain;

import java.util.Arrays;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

/** Snapshot of the editable /invsee slots used to detect stale writebacks. */
public record InvseeSnapshot(ItemStack[] contents) {

  private static final int EDITABLE_SLOTS = InvseeLayout.FIRST_LOCKED_SLOT;

  public InvseeSnapshot {
    contents = cloneContents(contents);
  }

  public static InvseeSnapshot fromPlayer(@NonNull Player player) {
    var inv = player.getInventory();
    var contents = new ItemStack[EDITABLE_SLOTS];
    var storage = inv.getStorageContents();

    for (var slot = 0; slot < InvseeLayout.STORAGE_SLOTS; slot++) {
      contents[slot] = cloneItem(storage[slot]);
    }

    contents[InvseeLayout.HELMET_SLOT] = cloneItem(inv.getHelmet());
    contents[InvseeLayout.CHESTPLATE_SLOT] = cloneItem(inv.getChestplate());
    contents[InvseeLayout.LEGGINGS_SLOT] = cloneItem(inv.getLeggings());
    contents[InvseeLayout.BOOTS_SLOT] = cloneItem(inv.getBoots());
    contents[InvseeLayout.OFFHAND_SLOT] = cloneItem(inv.getItemInOffHand());

    return new InvseeSnapshot(contents);
  }

  public static InvseeSnapshot fromView(@NonNull Inventory view) {
    var contents = new ItemStack[EDITABLE_SLOTS];
    for (var slot = 0; slot < EDITABLE_SLOTS; slot++) {
      contents[slot] = cloneItem(view.getItem(slot));
    }

    return new InvseeSnapshot(contents);
  }

  public boolean matches(@NonNull Player player) {
    var current = fromPlayer(player);
    return sameContents(this.contents, current.contents);
  }

  public void writeTo(@NonNull Player target) {
    var inv = target.getInventory();

    var storage = Arrays.copyOfRange(this.contents, 0, InvseeLayout.STORAGE_SLOTS);
    inv.setStorageContents(cloneContents(storage));

    inv.setHelmet(cloneItem(this.contents[InvseeLayout.HELMET_SLOT]));
    inv.setChestplate(cloneItem(this.contents[InvseeLayout.CHESTPLATE_SLOT]));
    inv.setLeggings(cloneItem(this.contents[InvseeLayout.LEGGINGS_SLOT]));
    inv.setBoots(cloneItem(this.contents[InvseeLayout.BOOTS_SLOT]));
    inv.setItemInOffHand(cloneItem(this.contents[InvseeLayout.OFFHAND_SLOT]));
  }

  public ItemStack itemAt(int slot) {
    return cloneItem(this.contents[slot]);
  }

  private static ItemStack[] cloneContents(@NonNull ItemStack[] source) {
    var copy = new ItemStack[source.length];
    for (var i = 0; i < source.length; i++) {
      copy[i] = cloneItem(source[i]);
    }

    return copy;
  }

  private static @Nullable ItemStack cloneItem(@Nullable ItemStack item) {
    if (isEmpty(item)) {
      return null;
    }

    return item.clone();
  }

  private static boolean sameContents(@NonNull ItemStack[] left, @NonNull ItemStack[] right) {
    if (left.length != right.length) {
      return false;
    }

    for (var i = 0; i < left.length; i++) {
      if (!sameItem(left[i], right[i])) {
        return false;
      }
    }

    return true;
  }

  private static boolean sameItem(@Nullable ItemStack left, @Nullable ItemStack right) {
    if (isEmpty(left) && isEmpty(right)) {
      return true;
    }
    if (isEmpty(left) || isEmpty(right)) {
      return false;
    }

    return left.getAmount() == right.getAmount() && left.isSimilar(right);
  }

  private static boolean isEmpty(@Nullable ItemStack item) {
    return item == null || item.getType().isAir();
  }
}
