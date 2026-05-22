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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.jspecify.annotations.NonNull;

/** Renders one {@link TpaHistoryEntry} as the target player's head in the history menu. */
public record TpaHistoryEntryRenderer(ConfigHandle<TpaConfig> config)
    implements ItemRenderer<TpaHistoryEntry> {

  private static final String UNKNOWN = "—";

  @Override
  public @NonNull ItemTemplate render(@NonNull TpaHistoryEntry entry, int humanIndex) {
    Objects.requireNonNull(entry, "entry");

    var settings = config.value().menu();
    var target = entry.target();
    var destination = entry.destination();

    // Inicializa tudo com o valor padrão (Fail-Safe)
    var worldName = UNKNOWN;
    var x = UNKNOWN;
    var y = UNKNOWN;
    var z = UNKNOWN;

    // Um único bloco condicional limpo e focado
    if (destination != null) {
      worldName = destination.world();
      x = Numbers.compact(destination.x());
      y = Numbers.compact(destination.y());
      z = Numbers.compact(destination.z());
    }

    var moment =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.resolvedAt()), ZoneId.systemDefault());
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

    List<String> list = new ArrayList<>();
    for (String line : template) {
      String replace =
          line.replace("{target}", targetName)
              .replace("{type}", type)
              .replace("{status}", status)
              .replace("{world}", world)
              .replace("{x}", x)
              .replace("{y}", y)
              .replace("{z}", z)
              .replace("{time}", time);
      list.add(replace);
    }
    return list.toArray(new String[0]);
  }
}
