package com.hanielcota.essentials.modules.online.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record OnlineConfig(
    @Comment("/online menu title. Placeholder: {count}.") String menuTitle,
    @Comment("/online menu rows (1-6). Holds up to rows × 9 players.") int menuRows,
    @Comment("Display name of each player head. Placeholder: {player}.") String itemName,
    @Comment("Lore of each player head. Placeholders: {player}, {ping}, {world}.")
        List<String> itemLore) {

  public static OnlineConfig defaults() {
    return new OnlineConfig(
        "<dark_aqua>Jogadores online <gray>({count})",
        6,
        "<yellow>{player}",
        List.of("<gray>Ping: <white>{ping}ms", "<gray>Mundo: <white>{world}"));
  }

  public int sanitizedRows() {
    return Math.clamp(menuRows, 1, 6);
  }

  public String formatTitle(int count) {
    var countStr = Integer.toString(count);
    return menuTitle.replace("{count}", countStr);
  }

  public String formatItemName(String player) {
    Objects.requireNonNull(player, "player");
    return itemName.replace("{player}", player);
  }

  public List<String> formatItemLore(String player, int ping, String world) {
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(world, "world");

    var pingStr = Integer.toString(ping);
    var formattedLore = new ArrayList<String>(itemLore.size());

    for (var line : itemLore) {
      var updatedLine =
          line.replace("{player}", player).replace("{ping}", pingStr).replace("{world}", world);
      formattedLore.add(updatedLine);
    }

    return List.copyOf(formattedLore);
  }
}
