package com.hanielcota.essentials.modules.invsee.service;

import com.hanielcota.essentials.util.ComponentUtils;
import java.util.Objects;
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

  private static ItemStack filler() {
    var item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    var meta = item.getItemMeta();
    meta.displayName(Component.empty());
    item.setItemMeta(meta);
    return item;
  }

  /** Builds the 45-slot invsee GUI populated with {@code target}'s inventory. */
  public Inventory createView(Player target, String title) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(title, "title");

    var holder = new InvseeHolder(target.getUniqueId());
    var view = Bukkit.createInventory(holder, SIZE, ComponentUtils.mini(title));
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
    return view;
  }

  /** Writes the editable slots of {@code view} back into {@code target}'s inventory. */
  public void sync(Player target, Inventory view) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(view, "view");

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
