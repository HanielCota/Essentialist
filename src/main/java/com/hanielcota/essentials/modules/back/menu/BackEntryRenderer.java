package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ItemRenderer;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.util.Numbers;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record BackEntryRenderer(ConfigHandle<BackConfig> config)
    implements ItemRenderer<HistoryEntry> {

  @Override
  public @NonNull ItemTemplate render(@NonNull HistoryEntry entry, int humanIndex) {
    Objects.requireNonNull(entry, "entry");

    var snap = config.value();
    var loc = entry.location();

    var entryWorld = loc.getWorld();
    var worldName = entryWorld != null ? entryWorld.getName() : "?";

    var moment =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.createdAt()), ZoneId.systemDefault());
    var time = snap.timeFormatter().format(moment);

    var xStr = Numbers.compact(loc.getX());
    var yStr = Numbers.compact(loc.getY());
    var zStr = Numbers.compact(loc.getZ());

    var loreTemplate = snap.itemLore();
    var lore = new String[loreTemplate.size()];

    for (var i = 0; i < loreTemplate.size(); i++) {
      lore[i] =
          loreTemplate
              .get(i)
              .replace("{world}", worldName)
              .replace("{x}", xStr)
              .replace("{y}", yStr)
              .replace("{z}", zStr)
              .replace("{time}", time);
    }

    return ItemTemplate.builder(snap.itemMaterial())
        .name(snap.formatItemName(humanIndex))
        .lore(lore)
        .glow(snap.itemGlow())
        .italic(false)
        .build();
  }
}
