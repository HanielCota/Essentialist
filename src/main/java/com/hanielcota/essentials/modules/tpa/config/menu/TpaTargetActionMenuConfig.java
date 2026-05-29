package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the target-action sub-menu opened by {@code /tpa <nick>} and {@code /tpahere
 * <nick>}: target head, two TPA buttons (visit / summon), favorite toggle, and back-to-hub.
 *
 * <p>The {@code title} is resolved once at menu registration (MenuFramework v1.2.0 limitation), so
 * it cannot contain a per-viewer {@code {player}} placeholder — the target name appears in the head
 * item instead.
 */
@ConfigSerializable
public record TpaTargetActionMenuConfig(
    @Comment("Target action menu title (static, no per-viewer placeholders).") String title,
    @Comment("Target action menu rows (1-6).") int rows,
    @Comment("Slot of the player head shown at the top.") int targetSlot,
    @Comment("Material of the target item.") Material targetIcon,
    @Comment("Use the target player's skin on the target item.") boolean targetUsePlayerHead,
    @Comment("Custom head texture for the target item when material is PLAYER_HEAD.")
        String targetHeadTexture,
    @Comment("Name of the target item. Placeholder: {player}.") String targetName,
    @Comment("Lore of the target item. Placeholder: {player}.") List<String> targetLore,
    @Comment("Slot of the /tpa button (go to them).") int tpaSlot,
    @Comment("Material of the /tpa button.") Material tpaIcon,
    @Comment("Name of the /tpa button. Placeholder: {player}.") String tpaName,
    @Comment("Lore of the /tpa button. Placeholder: {player}.") List<String> tpaLore,
    @Comment("Slot of the /tpahere button (summon them to you).") int tpaHereSlot,
    @Comment("Material of the /tpahere button.") Material tpaHereIcon,
    @Comment("Name of the /tpahere button. Placeholder: {player}.") String tpaHereName,
    @Comment("Lore of the /tpahere button. Placeholder: {player}.") List<String> tpaHereLore,
    @Comment("Line prepended to the lore of whichever button matches the typed command.")
        String recommendedTag,
    @Comment("Slot of the add-favorite button (shown when the target is not yet a favorite).")
        int favoriteAddSlot,
    @Comment("Material of the add-favorite button.") Material favoriteAddIcon,
    @Comment("Name of the add-favorite button. Placeholder: {player}.") String favoriteAddName,
    @Comment("Lore of the add-favorite button. Placeholder: {player}.")
        List<String> favoriteAddLore,
    @Comment("Slot of the remove-favorite button (shown when the target is already a favorite).")
        int favoriteRemoveSlot,
    @Comment("Material of the remove-favorite button.") Material favoriteRemoveIcon,
    @Comment("Name of the remove-favorite button. Placeholder: {player}.")
        String favoriteRemoveName,
    @Comment("Lore of the remove-favorite button. Placeholder: {player}.")
        List<String> favoriteRemoveLore,
    @Comment("Slot of the back-to-hub button (returns to /tpa menu).") int backSlot,
    @Comment("Material of the back-to-hub button.") Material backIcon,
    @Comment("Name of the back-to-hub button.") String backName,
    @Comment("Lore of the back-to-hub button.") List<String> backLore) {

  public static TpaTargetActionMenuConfig defaults() {
    return new TpaTargetActionMenuConfig(
        "TPA Actions",
        3,
        4,
        Material.PLAYER_HEAD,
        true,
        "",
        "{player}",
        List.of("Choose how to teleport."),
        11,
        Material.ENDER_PEARL,
        "Go to {player}",
        List.of("Request a teleport to visit {player}.", "", "Click to send the request."),
        15,
        Material.COMPASS,
        "Summon {player}",
        List.of("Ask {player} to come to you.", "", "Click to send the request."),
        "★ Recommended",
        21,
        Material.NETHER_STAR,
        "Favorite {player}",
        List.of("Adds {player} to your favorites.", "", "Click to favorite."),
        21,
        Material.RED_DYE,
        "Remove from favorites",
        List.of("Removes {player} from your favorites list.", "", "Click to remove."),
        23,
        Material.ARROW,
        "Back to main menu",
        List.of("Back to the TPA menu."));
  }
}
