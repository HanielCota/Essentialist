package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the favorites menu — paginated list of saved players plus an "add favorite" button
 * that opens a chat prompt, a sort toggle and a "top contacted" suggestions section.
 */
@ConfigSerializable
public record TpaFavoritesMenuConfig(
    @Comment("Favorites menu title.") String title,
    @Comment("Favorites menu rows (1-6).") int rows,
    @Comment("Slots used by favorite player items.") List<Integer> contentSlots,
    @Comment("Material of each favorite player item.") Material favoriteIcon,
    @Comment("Use the favorited player's skin on favorite items.") boolean favoriteUsePlayerHead,
    @Comment("Custom head texture when favoriteIcon is PLAYER_HEAD and player skin is disabled.")
        String favoriteHeadTexture,
    @Comment("Favorite item name. Placeholders: {player}, {status}.") String favoriteName,
    @Comment("Favorite item lore. Placeholders: {player}, {status}.") List<String> favoriteLore,
    @Comment("Label used in {status} when the favorited player is online.") String statusOnline,
    @Comment("Label used in {status} when the favorited player is offline.") String statusOffline,
    @Comment("Material of the placeholder shown when the player has no favorites yet.")
        Material emptyIcon,
    @Comment("Name of the placeholder shown when the player has no favorites yet.")
        String emptyName,
    @Comment("Lore of the placeholder shown when the player has no favorites yet.")
        List<String> emptyLore,
    @Comment("Slot of the add-favorite button.") int addSlot,
    @Comment("Material of the add-favorite button.") Material addIcon,
    @Comment("Custom head texture for the add-favorite button when material is PLAYER_HEAD.")
        String addHeadTexture,
    @Comment("Name of the add-favorite button.") String addName,
    @Comment("Lore of the add-favorite button.") List<String> addLore,
    @Comment("Slot of the ordering-cycle button.") int orderingSlot,
    @Comment("Material of the ordering button.") Material orderingIcon,
    @Comment("Name of the ordering button. Placeholder: {state}.") String orderingName,
    @Comment(
            "Lore of the ordering button. Use {state} for the current label and {options} to expand"
                + " the full list of ordering states with the active one marked.")
        List<String> orderingLore,
    @Comment("Label used in {state} when ordering by name.") String orderingStateName,
    @Comment("Label used in {state} when ordering by recent contact.") String orderingStateRecent,
    @Comment("Label used in {state} when ordering by online-first.")
        String orderingStateOnlineFirst,
    @Comment("Suffix appended to the active option in the {options} expansion.")
        String orderingActiveMarker,
    @Comment("Maximum number of top-contact suggestions to inject when there is room.")
        int maxSuggestions,
    @Comment(
            "Material of each suggestion item. Suggestions appear after the player's own favorites "
                + "when the player has any TPA contact history with non-favorited players.")
        Material suggestionIcon,
    @Comment("Use the suggested player's skin on suggestion items.")
        boolean suggestionUsePlayerHead,
    @Comment("Custom head texture for suggestions when material is PLAYER_HEAD.")
        String suggestionHeadTexture,
    @Comment("Suggestion item name. Placeholders: {player}, {status}, {count}.")
        String suggestionName,
    @Comment("Suggestion item lore. Placeholders: {player}, {status}, {count}.")
        List<String> suggestionLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaFavoritesMenuConfig defaults() {
    return new TpaFavoritesMenuConfig(
        "Favorites",
        6,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34),
        Material.PLAYER_HEAD,
        true,
        "",
        "{player} ({status})",
        List.of("Quick shortcut to teleport with this player.", "", "Click to choose an action."),
        "online",
        "offline",
        Material.BARRIER,
        "Your list is empty",
        List.of(
            "You don't have any favorites yet.",
            "",
            "Use the green button to save your first player."),
        47,
        Material.EMERALD,
        "",
        "Add favorite",
        List.of("Click and type the player's name in chat.", "", "Type cancel to abort."),
        49,
        Material.HOPPER,
        "Sort: {state}",
        List.of("Defines how your list is shown.", "", "{options}", "", "Click to change."),
        "Name",
        "Recent",
        "Online first",
        " ◀",
        6,
        Material.PLAYER_HEAD,
        true,
        "",
        "Suggestion: {player}",
        List.of(
            "Status: {status}", "You've teleported {count}x already.", "", "Click to favorite."),
        51,
        Material.ARROW,
        "Back",
        List.of("Back to the TPA menu."));
  }
}
