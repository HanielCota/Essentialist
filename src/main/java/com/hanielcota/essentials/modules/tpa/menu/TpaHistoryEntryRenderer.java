package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import com.hanielcota.essentials.util.Numbers;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;

/** Renders one {@link TpaHistoryEntry} as the target player's head in the history menu. */
public record TpaHistoryEntryRenderer(ConfigHandle<TpaConfig> config) {

  private static final String UNKNOWN = "—";

  private static String formatLine(
      @NonNull String line,
      @NonNull String targetName,
      @NonNull String type,
      @NonNull String status,
      @NonNull String world,
      @NonNull String x,
      @NonNull String y,
      @NonNull String z,
      @NonNull String time) {
    return line.replace("{target}", targetName)
        .replace("{type}", type)
        .replace("{status}", status)
        .replace("{world}", world)
        .replace("{x}", x)
        .replace("{y}", y)
        .replace("{z}", z)
        .replace("{time}", time);
  }

  public @NonNull ItemTemplate render(@NonNull TpaHistoryEntry entry, int humanIndex) {
    var settings = this.config.value().menu();
    var target = entry.target();
    var destination = entry.destination();

    var worldName = UNKNOWN;
    var x = UNKNOWN;
    var y = UNKNOWN;
    var z = UNKNOWN;
    if (destination != null) {
      worldName = destination.world();
      x = Numbers.compact(destination.x());
      y = Numbers.compact(destination.y());
      z = Numbers.compact(destination.z());
    }

    var instant = Instant.ofEpochMilli(entry.resolvedAt());
    var moment = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    var time = settings.timeFormatter().format(moment);

    var typeLabel = settings.typeLabel(entry.type());
    var statusLabel = settings.statusLabel(entry.status());
    var lore =
        buildLore(
            settings.itemLore(), target.name(), typeLabel, statusLabel, worldName, x, y, z, time);

    return ItemTemplate.builder(Material.PLAYER_HEAD)
        .head(target.id())
        .name(settings.formatItemName(humanIndex, target.name()))
        .lore(lore)
        .glow(settings.itemGlow())
        .italic(false)
        .build();
  }

  /** The placeholder item shown when the history is empty. */
  public @NonNull ItemTemplate renderEmpty() {
    var settings = this.config.value().menu();
    return ItemTemplate.builder(settings.emptyMaterial())
        .name(settings.emptyName())
        .lore(settings.emptyLore().toArray(String[]::new))
        .italic(false)
        .build();
  }

  private String[] buildLore(
      @NonNull List<String> template,
      @NonNull String targetName,
      @NonNull String type,
      @NonNull String status,
      @NonNull String world,
      @NonNull String x,
      @NonNull String y,
      @NonNull String z,
      @NonNull String time) {
    var lore = new String[template.size()];
    for (var i = 0; i < template.size(); i++) {
      lore[i] = formatLine(template.get(i), targetName, type, status, world, x, y, z, time);
    }
    return lore;
  }
}
