package com.hanielcota.essentials.modules.vanish.menu;

import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.shared.Numbers;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VanishMessageRenderer {

  private static final String PLAYER_PLACEHOLDER = "{player}";

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
    var xStr = Numbers.display(x);
    var yStr = Numbers.display(y);
    var zStr = Numbers.display(z);

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
    var xStr = Numbers.display(x);
    var yStr = Numbers.display(y);
    var zStr = Numbers.display(z);

    return formatLine(snap.messages().teleported(), player, world, xStr, yStr, zStr);
  }

  public static String teleportTargetGone(@NonNull VanishConfig snap, @NonNull String player) {
    return snap.messages().teleportTargetGone().replace(PLAYER_PLACEHOLDER, player);
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
