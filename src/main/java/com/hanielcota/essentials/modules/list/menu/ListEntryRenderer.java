package com.hanielcota.essentials.modules.list.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.list.config.ListConfig;
import com.hanielcota.essentials.modules.list.domain.PlayerEntry;
import com.hanielcota.essentials.shared.PlayerHeadTextures;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

/** Renders the player entries and the empty state shown when no players are visible. */
public record ListEntryRenderer(ConfigHandle<ListConfig> config) {

  public ItemTemplate render(@NonNull PlayerEntry entry) {
    var snap = this.config.value();
    var playerName = entry.name();
    var groupName = entry.groupDisplayName();

    var itemName = snap.formatItemName(playerName, groupName);
    var lore = snap.formatItemLore(playerName, groupName);
    var loreArray = lore.toArray(String[]::new);
    var material = entry.material();
    var builder = ItemTemplate.builder(material);

    if (material == Material.PLAYER_HEAD) {
      PlayerHeadTextures.applyTo(builder, entry.id());
    }

    builder = builder.name(itemName);
    builder = builder.lore(loreArray);
    builder = builder.flags(ItemFlag.HIDE_ATTRIBUTES);
    builder = builder.italic(false);

    return builder.build();
  }

  public ItemTemplate renderEmpty() {
    var snap = this.config.value();
    var material = snap.emptyMaterial();
    var name = snap.emptyName();
    var lore = snap.emptyLore();
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder = builder.name(name);
    builder = builder.lore(loreArray);
    builder = builder.flags(ItemFlag.HIDE_ATTRIBUTES);
    builder = builder.italic(false);

    return builder.build();
  }
}
