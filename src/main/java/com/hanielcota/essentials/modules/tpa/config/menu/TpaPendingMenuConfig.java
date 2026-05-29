package com.hanielcota.essentials.modules.tpa.config.menu;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TpaPendingMenuConfig(
    @Comment("Pending TPA requests menu title.") String title,
    @Comment("Pending TPA requests menu rows (1-6).") int rows,
    @Comment("Slots used by pending request items.") List<Integer> contentSlots,
    @Comment("Material of each pending request item.") Material requestIcon,
    @Comment("Use the requester skin on pending request items.") boolean requestUsePlayerHead,
    @Comment("Custom head texture when requestIcon is PLAYER_HEAD and player skin is disabled.")
        String requestHeadTexture,
    @Comment(
            "Pending request item name. Placeholders: {player}, {type}, {seconds}, "
                + "{origin_world}, {distance}.")
        String requestName,
    @Comment(
            "Pending request item lore. Placeholders: {player}, {type}, {seconds}, "
                + "{origin_world}, {distance}.")
        List<String> requestLore,
    @Comment("Material of the placeholder shown when there are no pending requests.")
        Material emptyIcon,
    @Comment("Name of the placeholder shown when there are no pending requests.") String emptyName,
    @Comment("Lore of the placeholder shown when there are no pending requests.")
        List<String> emptyLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Slot of the accept-all button.") int acceptAllSlot,
    @Comment("Material of the accept-all button.") Material acceptAllIcon,
    @Comment("Name of the accept-all button. Placeholder: {pending}.") String acceptAllName,
    @Comment("Lore of the accept-all button. Placeholder: {pending}.") List<String> acceptAllLore,
    @Comment("Slot of the deny-all button.") int denyAllSlot,
    @Comment("Material of the deny-all button.") Material denyAllIcon,
    @Comment("Name of the deny-all button. Placeholder: {pending}.") String denyAllName,
    @Comment("Lore of the deny-all button. Placeholder: {pending}.") List<String> denyAllLore,
    @Comment("Label for a request where the requester wants to come to you.") String typeTpa,
    @Comment("Label for a request where the requester wants you to go to them.") String typeTpaHere,
    @Comment(
            "Placeholder shown in {origin_world} / {distance} when the requester logged out or is"
                + " in another world.")
        String unknownPlaceholder,
    @Comment("Format for the {distance} value when known. Placeholder: {meters}.")
        String distanceFormat,
    @Comment("Previous/next page navigation buttons.") NavigationButtonsConfig navigation) {

  public static TpaPendingMenuConfig defaults() {
    return new TpaPendingMenuConfig(
        "Pending requests",
        6,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34),
        Material.PLAYER_HEAD,
        true,
        "",
        "{player}",
        List.of(
            "{type}.",
            "In {origin_world}, {distance} from you.",
            "",
            "Expires in {seconds}s.",
            "",
            "Click to choose an action."),
        Material.BARRIER,
        "No requests around here",
        List.of(
            "You have no requests right now.",
            "",
            "When someone summons you or asks to come to you, it shows up here."),
        49,
        Material.ARROW,
        "Back",
        List.of("Back to the TPA menu."),
        47,
        Material.LIME_DYE,
        "Accept all ({pending})",
        List.of("Accepts all {pending} pending requests at once.", "", "Click to accept."),
        51,
        Material.RED_DYE,
        "Deny all ({pending})",
        List.of("Denies all {pending} pending requests at once.", "", "Click to deny."),
        "Wants to come to you",
        "Wants you to go to them",
        "—",
        "{meters}m",
        NavigationButtonsConfig.defaults(47, 51));
  }
}
