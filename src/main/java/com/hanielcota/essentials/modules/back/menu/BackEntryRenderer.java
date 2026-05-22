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
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record BackEntryRenderer(ConfigHandle<BackConfig> config)
    implements ItemRenderer<HistoryEntry> {

  public BackEntryRenderer {
    Objects.requireNonNull(config, "config");
  }

  @Override
  public @NonNull ItemTemplate render(@NonNull HistoryEntry entry, int humanIndex) {
    var snap = config.value();
    var loc = entry.location();
    var entryWorld = loc.getWorld();
    var worldName = entryWorld != null ? entryWorld.getName() : "?";

    var moment =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.createdAt()), ZoneId.systemDefault());
    var time = DateTimeFormatter.ofPattern(snap.timeFormat()).format(moment);

    var lore =
        snap.itemLore().stream()
            .map(
                line ->
                    line.replace("{world}", worldName)
                        .replace("{x}", Numbers.compact(loc.getX()))
                        .replace("{y}", Numbers.compact(loc.getY()))
                        .replace("{z}", Numbers.compact(loc.getZ()))
                        .replace("{time}", time))
            .toArray(String[]::new);

    return ItemTemplate.builder(snap.itemMaterial())
        .name(snap.formatItemName(humanIndex))
        .lore(lore)
        .glow(snap.itemGlow())
        .italic(false)
        .build();
  }
}
