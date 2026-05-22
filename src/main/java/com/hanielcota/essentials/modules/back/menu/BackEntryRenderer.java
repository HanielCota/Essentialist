package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ItemRenderer;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.util.Numbers;
import com.hanielcota.essentials.util.Placeholders;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
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
    var time = snap.timeFormatter().format(moment);

    var values =
        Map.<String, Object>of(
            "world", worldName,
            "x", Numbers.compact(loc.getX()),
            "y", Numbers.compact(loc.getY()),
            "z", Numbers.compact(loc.getZ()),
            "time", time);
    var lore =
        snap.itemLore().stream()
            .map(line -> Placeholders.format(line, values))
            .toArray(String[]::new);

    return ItemTemplate.builder(snap.itemMaterial())
        .name(snap.formatItemName(humanIndex))
        .lore(lore)
        .glow(snap.itemGlow())
        .italic(false)
        .build();
  }
}
