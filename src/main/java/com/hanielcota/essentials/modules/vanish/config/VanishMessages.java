package com.hanielcota.essentials.modules.vanish.config;

import com.hanielcota.essentials.util.Numbers;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * Renders vanish menu messages: player heads, lore, teleport-success line and target-gone line.
 * Multi-placeholder interpolation with {@link Numbers#compact} for coordinate compaction lives here
 * so the config record stays a plain data holder.
 */
public final class VanishMessages {

  private static final String PLAYER_PLACEHOLDER = "{player}";

  private VanishMessages() {}

  public static String itemName(@NonNull VanishConfig snap, @NonNull String player) {
    return snap.itemName().replace(PLAYER_PLACEHOLDER, player);
  }

  public static List<String> itemLore(
      @NonNull VanishConfig snap,
      @NonNull String player,
      @NonNull String world,
      double x,
      double y,
      double z) {
    var xStr = Numbers.compact(x);
    var yStr = Numbers.compact(y);
    var zStr = Numbers.compact(z);

    var template = snap.itemLore();
    var lines = new ArrayList<String>(template.size());
    for (var line : template) {
      var formatted = formatLine(line, player, world, xStr, yStr, zStr);
      lines.add(formatted);
    }

    return lines;
  }

  public static String teleported(
      @NonNull VanishConfig snap,
      @NonNull String player,
      @NonNull String world,
      double x,
      double y,
      double z) {
    var xStr = Numbers.compact(x);
    var yStr = Numbers.compact(y);
    var zStr = Numbers.compact(z);

    return formatLine(snap.teleported(), player, world, xStr, yStr, zStr);
  }

  public static String teleportTargetGone(@NonNull VanishConfig snap, @NonNull String player) {
    return snap.teleportTargetGone().replace(PLAYER_PLACEHOLDER, player);
  }

  private static String formatLine(
      @NonNull String template,
      @NonNull String player,
      @NonNull String world,
      @NonNull String x,
      @NonNull String y,
      @NonNull String z) {
    var withPlayer = template.replace(PLAYER_PLACEHOLDER, player);
    var withWorld = withPlayer.replace("{world}", world);
    var withX = withWorld.replace("{x}", x);
    var withY = withX.replace("{y}", y);

    return withY.replace("{z}", z);
  }
}
