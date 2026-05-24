package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.util.Numbers;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.NonNull;

public record BackEntryRenderer(ConfigHandle<BackConfig> config) {

  public @NonNull ItemTemplate render(@NonNull HistoryEntry entry, int humanIndex) {
    var snap = this.config.value();
    var location = entry.location();

    var entryWorld = location.getWorld();
    var worldName = entryWorld != null ? entryWorld.getName() : "?";

    var instant = Instant.ofEpochMilli(entry.createdAt());
    var moment = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

    var time = snap.timeFormatter().format(moment);

    var xStr = Numbers.compact(location.getX());
    var yStr = Numbers.compact(location.getY());
    var zStr = Numbers.compact(location.getZ());

    var loreTemplate = snap.itemLore();
    var lore = new String[loreTemplate.size()];

    for (var i = 0; i < loreTemplate.size(); i++) {
      lore[i] = formatLine(loreTemplate.get(i), worldName, xStr, yStr, zStr, time);
    }

    return ItemTemplate.builder(snap.itemMaterial())
        .name(snap.formatItemName(humanIndex))
        .lore(lore)
        .glow(snap.itemGlow())
        .italic(false)
        .build();
  }

  private String formatLine(
      @NonNull String line,
      @NonNull String world,
      @NonNull String x,
      @NonNull String y,
      @NonNull String z,
      @NonNull String time) {
    return line.replace("{world}", world)
        .replace("{x}", x)
        .replace("{y}", y)
        .replace("{z}", z)
        .replace("{time}", time);
  }
}
