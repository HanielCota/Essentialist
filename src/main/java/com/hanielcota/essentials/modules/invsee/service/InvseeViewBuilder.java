package com.hanielcota.essentials.modules.invsee.service;

import com.hanielcota.essentials.modules.invsee.domain.InvseeHolder;
import com.hanielcota.essentials.modules.invsee.domain.InvseeLayout;
import com.hanielcota.essentials.modules.invsee.domain.InvseeSnapshot;
import com.hanielcota.essentials.shared.ComponentUtils;
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
    var view = Bukkit.createInventory(holder, InvseeLayout.SIZE, titleComponent);
    holder.inventory(view);

    var snapshot = InvseeSnapshot.fromPlayer(target);
    holder.snapshot(snapshot);

    for (var slot = 0; slot < InvseeLayout.STORAGE_SLOTS; slot++) {
      view.setItem(slot, snapshot.itemAt(slot));
    }

    view.setItem(InvseeLayout.HELMET_SLOT, snapshot.itemAt(InvseeLayout.HELMET_SLOT));
    view.setItem(InvseeLayout.CHESTPLATE_SLOT, snapshot.itemAt(InvseeLayout.CHESTPLATE_SLOT));
    view.setItem(InvseeLayout.LEGGINGS_SLOT, snapshot.itemAt(InvseeLayout.LEGGINGS_SLOT));
    view.setItem(InvseeLayout.BOOTS_SLOT, snapshot.itemAt(InvseeLayout.BOOTS_SLOT));
    view.setItem(InvseeLayout.OFFHAND_SLOT, snapshot.itemAt(InvseeLayout.OFFHAND_SLOT));

    for (var slot = InvseeLayout.FIRST_LOCKED_SLOT; slot < InvseeLayout.SIZE; slot++) {
      view.setItem(slot, FILLER.clone());
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
