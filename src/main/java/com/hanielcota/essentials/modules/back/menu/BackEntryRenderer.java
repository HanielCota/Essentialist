package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.shared.Numbers;
import com.hanielcota.essentials.shared.Placeholders;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import lombok.NonNull;

public record BackEntryRenderer(ConfigHandle<BackConfig> config) {

  public @NonNull ItemTemplate render(@NonNull HistoryEntry entry, int humanIndex) {
    var snap = this.config.value();
    var location = entry.location();

    var entryWorld = location.getWorld();
    var worldName = entryWorld != null ? entryWorld.getName() : "?";

    var createdAt = entry.createdAt();
    var instant = Instant.ofEpochMilli(createdAt);
    var zone = ZoneId.systemDefault();
    var moment = LocalDateTime.ofInstant(instant, zone);

    var formatter = snap.timeFormatter();
    var time = formatter.format(moment);

    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();
    var xStr = Numbers.display(x);
    var yStr = Numbers.display(y);
    var zStr = Numbers.display(z);

    var cause = entry.cause();
    var causeLabel = snap.causeLabel(cause);

    var loreTemplate = snap.itemLore();
    var lore = new String[loreTemplate.size()];
    for (var i = 0; i < loreTemplate.size(); i++) {
      var line = loreTemplate.get(i);
      lore[i] = formatLine(line, worldName, xStr, yStr, zStr, time, causeLabel);
    }

    var material = snap.itemMaterial();
    var itemName = snap.formatItemName(humanIndex);
    var glow = snap.itemGlow();

    return ItemTemplate.builder(material)
        .name(itemName)
        .lore(lore)
        .glow(glow)
        .italic(false)
        .build();
  }

  public @NonNull ItemTemplate renderEmpty() {
    var snap = this.config.value();

    var material = snap.emptyMaterial();
    var name = snap.emptyName();
    var loreArray = snap.emptyLore().toArray(String[]::new);

    return ItemTemplate.builder(material).name(name).lore(loreArray).italic(false).build();
  }

  private String formatLine(
      @NonNull String line,
      @NonNull String world,
      @NonNull String x,
      @NonNull String y,
      @NonNull String z,
      @NonNull String time,
      @NonNull String cause) {
    var values = Map.of("world", world, "x", x, "y", y, "z", z, "time", time, "cause", cause);

    return Placeholders.format(line, values);
  }
}
