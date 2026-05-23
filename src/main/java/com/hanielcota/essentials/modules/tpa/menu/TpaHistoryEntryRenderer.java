package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ItemRenderer;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import com.hanielcota.essentials.util.Numbers;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.bukkit.Material;
import org.jspecify.annotations.NonNull;

/** Renders one {@link TpaHistoryEntry} as the target player's head in the history menu. */
public record TpaHistoryEntryRenderer(ConfigHandle<TpaConfig> config)
    implements ItemRenderer<TpaHistoryEntry> {

  private static final String UNKNOWN = "—";

  @Override
  public @NonNull ItemTemplate render(@NonNull TpaHistoryEntry entry, int humanIndex) {
    var settings = config.value().menu();
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

  private String[] buildLore(
      List<String> template,
      String targetName,
      String type,
      String status,
      String world,
      String x,
      String y,
      String z,
      String time) {
    var lore = new String[template.size()];
    for (var i = 0; i < template.size(); i++) {
      lore[i] = formatLine(template.get(i), targetName, type, status, world, x, y, z, time);
    }
    return lore;
  }

  private static String formatLine(
      String line,
      String targetName,
      String type,
      String status,
      String world,
      String x,
      String y,
      String z,
      String time) {
    return line.replace("{target}", targetName)
        .replace("{type}", type)
        .replace("{status}", status)
        .replace("{world}", world)
        .replace("{x}", x)
        .replace("{y}", y)
        .replace("{z}", z)
        .replace("{time}", time);
  }
}
