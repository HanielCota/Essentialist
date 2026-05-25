package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.Destination;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import com.hanielcota.essentials.util.Numbers;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;

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
    var withTarget = line.replace("{target}", targetName);
    var withType = withTarget.replace("{type}", type);
    var withStatus = withType.replace("{status}", status);
    var withWorld = withStatus.replace("{world}", world);
    var withX = withWorld.replace("{x}", x);
    var withY = withX.replace("{y}", y);
    var withZ = withY.replace("{z}", z);

    return withZ.replace("{time}", time);
  }

  private static Coordinates resolveCoordinates(@Nullable Destination destination) {
    if (destination == null) {
      return new Coordinates(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
    }

    var world = destination.world();
    var x = Numbers.compact(destination.x());
    var y = Numbers.compact(destination.y());
    var z = Numbers.compact(destination.z());

    return new Coordinates(world, x, y, z);
  }

  public @NonNull ItemTemplate render(@NonNull TpaHistoryEntry entry, int humanIndex) {
    var snap = this.config.value();
    var settings = snap.menu();

    var target = entry.target();
    var targetName = target.name();
    var targetId = target.id();

    var coords = resolveCoordinates(entry.destination());

    var instant = Instant.ofEpochMilli(entry.resolvedAt());
    var zone = ZoneId.systemDefault();
    var moment = LocalDateTime.ofInstant(instant, zone);

    var formatter = settings.timeFormatter();
    var time = formatter.format(moment);

    var typeLabel = settings.typeLabel(entry.type());
    var statusLabel = settings.statusLabel(entry.status());

    var loreTemplate = settings.itemLore();
    var lore =
        buildLore(
            loreTemplate,
            targetName,
            typeLabel,
            statusLabel,
            coords.world(),
            coords.x(),
            coords.y(),
            coords.z(),
            time);

    var itemName = settings.formatItemName(humanIndex, targetName);
    var glow = settings.itemGlow();

    var builder = ItemTemplate.builder(Material.PLAYER_HEAD);
    builder.head(targetId);
    builder.name(itemName);
    builder.lore(lore);
    builder.glow(glow);
    builder.italic(false);

    return builder.build();
  }

  /** The placeholder item shown when the history is empty. */
  public @NonNull ItemTemplate renderEmpty() {
    var snap = this.config.value();
    var settings = snap.menu();

    var material = settings.emptyMaterial();
    var emptyName = settings.emptyName();
    var loreArray = settings.emptyLore().toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder.name(emptyName);
    builder.lore(loreArray);
    builder.italic(false);

    return builder.build();
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
      var line = template.get(i);
      lore[i] = formatLine(line, targetName, type, status, world, x, y, z, time);
    }
    return lore;
  }

  private record Coordinates(String world, String x, String y, String z) {}
}
