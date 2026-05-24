package com.hanielcota.essentials.modules.vanish.config;

import com.hanielcota.essentials.config.MessagePair;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.util.Numbers;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record VanishConfig(
    @Comment("Shown to the player when they enter vanish.") String enabled,
    @Comment("Placeholders: {player}.") String enabledOther,
    @Comment("Shown to the player when they leave vanish.") String disabled,
    @Comment("Placeholders: {player}.") String disabledOther,
    @Comment("Shown when the target player is not online.") String targetNotFound,
    @Comment("/vanish list menu title.") String menuTitle,
    @Comment("Rows in the /vanish list menu (clamped to 1-6).") int menuRows,
    @Comment("Slots used by vanish entries. Leave empty to use every row except the last.")
        List<Integer> menuContentSlots,
    @Comment("Previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Slot of the static info item shown on every page.") int infoSlot,
    @Comment("Material of the static info item.") Material infoMaterial,
    @Comment("Name of the static info item.") String infoName,
    @Comment("Lore of the static info item.") List<String> infoLore,
    @Comment("Item name for each vanished player. Placeholder: {player}.") String itemName,
    @Comment("Item lore for each vanished player. Placeholders: {player}, {world}, {x}, {y}, {z}.")
        List<String> itemLore,
    @Comment("Material of the placeholder shown when no one is vanished.") Material emptyMaterial,
    @Comment("Name of the placeholder shown when no one is vanished.") String emptyName,
    @Comment("Lore of the placeholder shown when no one is vanished.") List<String> emptyLore,
    @Comment(
            "Shown after clicking a head and teleporting. Placeholders: {player}, {world}, {x},"
                + " {y}, {z}.")
        String teleported,
    @Comment("Shown when the clicked player is no longer online. Placeholder: {player}.")
        String teleportTargetGone,
    @Comment("Shown when the teleport call itself fails.") String teleportFailed) {

  private static final int MIN_ROWS = 1;
  private static final String PLAYER_PLACEHOLDER = "{player}";

  public static VanishConfig defaults() {
    return new VanishConfig(
        "<gray>You are now <green>vanished</green>.",
        "<gray>You vanished <gold>{player}</gold>.",
        "<gray>You are no longer vanished.",
        "<gray>You unvanished <gold>{player}</gold>.",
        "<red>That player is not online.",
        "<dark_gray>Vanished players",
        6,
        List.of(
            11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43),
        NavigationButtonsConfig.defaults(48, 50),
        10,
        Material.ENDER_EYE,
        "<yellow>Vanished players",
        List.of(
            "<gray>Players currently hidden from the server.",
            "",
            "<gray>Click a head to teleport to them.",
            "",
            "<dark_gray>» <gray>State resets when the server stops."),
        "<yellow>{player}",
        List.of(
            "<gray>World: <white>{world}",
            "<gray>Coordinates: <white>{x}, {y}, {z}",
            "",
            "<yellow>Click to teleport."),
        Material.BARRIER,
        "<red>No one is vanished",
        List.of(
            "<gray>Nobody is currently hidden.",
            "",
            "<yellow>/vanish [player]",
            "<dark_gray>» <gray>toggles vanish for a player."),
        "<green>Teleported to <gold>{player}</gold> at <gold>{world} {x}, {y}, {z}</gold>.",
        "<red><gold>{player}</gold> is no longer online.",
        "<red>Teleport failed.");
  }

  public MessagePair toggle(boolean vanished) {
    return vanished
        ? new MessagePair(enabled, enabledOther)
        : new MessagePair(disabled, disabledOther);
  }

  public int effectiveRows() {
    return MenuLayouts.clampRows(menuRows);
  }

  /** Content slots minus the info slot, so the static info item never collides with a head. */
  public List<Integer> effectiveContentSlots() {
    var rows = effectiveRows();
    var info = effectiveInfoSlot();
    List<Integer> sanitized;
    if (menuContentSlots.isEmpty()) {
      var count = rows > MIN_ROWS ? (rows - 1) * 9 : 9;
      sanitized = MenuLayouts.fallbackContentSlots(rows, count);
    } else {
      sanitized = MenuLayouts.sanitizeSlots(menuContentSlots, rows);
    }
    if (!sanitized.contains(info)) {
      return sanitized;
    }
    return sanitized.stream().filter(slot -> slot != info).toList();
  }

  public int effectiveInfoSlot() {
    return MenuLayouts.sanitizeSlot(infoSlot, effectiveRows(), 10);
  }

  public String formatItemName(@NonNull String player) {
    return itemName.replace(PLAYER_PLACEHOLDER, player);
  }

  public List<String> formatItemLore(
      @NonNull String player, @NonNull String world, double x, double y, double z) {
    var xStr = Numbers.compact(x);
    var yStr = Numbers.compact(y);
    var zStr = Numbers.compact(z);

    return itemLore.stream()
        .map(line -> formatLine(line, player, world, xStr, yStr, zStr))
        .toList();
  }

  public String formatTeleported(
      @NonNull String player, @NonNull String world, double x, double y, double z) {
    return formatLine(
        teleported, player, world, Numbers.compact(x), Numbers.compact(y), Numbers.compact(z));
  }

  public String formatTeleportTargetGone(@NonNull String player) {
    return teleportTargetGone.replace(PLAYER_PLACEHOLDER, player);
  }

  private static String formatLine(
      @NonNull String template,
      @NonNull String player,
      @NonNull String world,
      @NonNull String x,
      @NonNull String y,
      @NonNull String z) {
    return template
        .replace(PLAYER_PLACEHOLDER, player)
        .replace("{world}", world)
        .replace("{x}", x)
        .replace("{y}", y)
        .replace("{z}", z);
  }
}
