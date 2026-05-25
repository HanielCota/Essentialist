package com.hanielcota.essentials.modules.invsee.service;

import com.hanielcota.essentials.util.ComponentUtils;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Builds the 45-slot /invsee GUI populated with the target's storage, armor and offhand. Locked
 * slots are filled with a gray glass-pane filler. Stateless — every call produces a fresh
 * inventory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvseeViewBuilder {

  private static final ItemStack FILLER = filler();

  public static Inventory build(
      @NonNull Player target, @NonNull String title, @NonNull UUID targetId) {
    var holder = new InvseeHolder(targetId);
    var titleComponent = ComponentUtils.mini(title);
    var view = Bukkit.createInventory(holder, InvseeService.SIZE, titleComponent);
    holder.inventory(view);

    var source = target.getInventory();
    var storage = source.getStorageContents();
    for (var slot = 0; slot < InvseeService.STORAGE_SLOTS; slot++) {
      view.setItem(slot, storage[slot]);
    }

    var helmet = source.getHelmet();
    var chestplate = source.getChestplate();
    var leggings = source.getLeggings();
    var boots = source.getBoots();
    var offhand = source.getItemInOffHand();

    view.setItem(InvseeService.HELMET_SLOT, helmet);
    view.setItem(InvseeService.CHESTPLATE_SLOT, chestplate);
    view.setItem(InvseeService.LEGGINGS_SLOT, leggings);
    view.setItem(InvseeService.BOOTS_SLOT, boots);
    view.setItem(InvseeService.OFFHAND_SLOT, offhand);

    for (var slot = InvseeService.FIRST_LOCKED_SLOT; slot < InvseeService.SIZE; slot++) {
      view.setItem(slot, FILLER);
    }

    return view;
  }

  private static ItemStack filler() {
    var item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    var meta = item.getItemMeta();
    var emptyName = Component.empty();

    meta.displayName(emptyName);
    item.setItemMeta(meta);

    return item;
  }
}
