package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ItemRenderer;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record BackEntryRenderer(ConfigHandle<BackConfig> config)
    implements ItemRenderer<HistoryEntry> {

  public BackEntryRenderer {
    Objects.requireNonNull(config, "config");
  }

  @Override
  public @NonNull ItemTemplate render(HistoryEntry entry, int humanIndex) {
    var snap = config.value();
    var loc = entry.location();
    var world = loc.getWorld() != null ? loc.getWorld().getName() : "?";

    return ItemTemplate.builder(snap.itemMaterial())
        .name(snap.formatItemName(humanIndex))
        .lore(
            snap.formatItemLoreLocation(world, loc.getX(), loc.getY(), loc.getZ()),
            snap.itemLoreClick())
        .glow(snap.itemGlow())
        .build();
  }
}
