package com.hanielcota.essentials.modules.ban.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Visual configuration for the three ban menus. Titles are intentionally colourless. */
@ConfigSerializable
public record BanMenuConfig(
    @Comment("Title of the player-picker menu.") String pickerTitle,
    @Comment("Rows (1-6) of the player-picker menu.") int pickerRows,
    @Comment("Title of the ban-options menu.") String optionsTitle,
    @Comment("Rows (1-6) of the ban-options menu.") int optionsRows,
    @Comment("Title of the ban-list menu.") String listTitle,
    @Comment("Rows (1-6) of the ban-list menu.") int listRows,
    @Comment("Button: open the nick search (offline ban).") String searchName,
    @Comment("Button: open the nick search — lore.") List<String> searchLore,
    @Comment("Button: confirm the ban.") String confirmName,
    @Comment("Button: confirm the ban — lore. Placeholders: {player}, {duration}, {reason}.")
        List<String> confirmLore,
    @Comment("Button: go back to the picker.") String backName,
    @Comment("Header item over the selected target. Placeholder: {player}.") String targetName,
    @Comment("Header item over the selected target — lore.") List<String> targetLore,
    @Comment("Selected duration/reason marker appended to the chosen button's lore.")
        String selectedMarker,
    @Comment("Ban-list entry name. Placeholder: {player}.") String entryName,
    @Comment("Ban-list entry lore. Placeholders: {reason}, {issuer}, {expires}.")
        List<String> entryLore) {

  public static BanMenuConfig defaults() {
    return new BanMenuConfig(
        "Ban — select a player",
        6,
        "Ban — options",
        6,
        "Active bans",
        6,
        "<green>Search by name",
        List.of("<gray>Ban an offline player", "<yellow>Click to type a name"),
        "<green>Confirm ban",
        List.of(
            "<gray>Player: <white>{player}",
            "<gray>Duration: <white>{duration}",
            "<gray>Reason: <white>{reason}",
            "",
            "<yellow>Click to ban"),
        "<gray>Back",
        "<red>{player}",
        List.of("<gray>Pick a duration and a reason below"),
        "<green>✔ Selected",
        "<red>{player}",
        List.of(
            "<gray>Reason: <white>{reason}",
            "<gray>By: <white>{issuer}",
            "<gray>Expires: <white>{expires}",
            "",
            "<yellow>Click to unban"));
  }
}
