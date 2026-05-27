package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the target-action sub-menu opened by {@code /tpa <nick>} and {@code /tpahere
 * <nick>}: target head, two TPA buttons (visit / summon), favorite toggle, and close.
 */
@ConfigSerializable
public record TpaTargetActionMenuConfig(
    @Comment("Target action menu title. Placeholder: {player}.") String title,
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
    @Comment("Slot of the close button.") int closeSlot,
    @Comment("Material of the close button.") Material closeIcon,
    @Comment("Name of the close button.") String closeName,
    @Comment("Lore of the close button.") List<String> closeLore) {

  public static TpaTargetActionMenuConfig defaults() {
    return new TpaTargetActionMenuConfig(
        "TPA: {player}",
        3,
        4,
        Material.PLAYER_HEAD,
        true,
        "",
        "<gold>{player}",
        List.of("<gray>Escolha como teleportar."),
        11,
        Material.ENDER_PEARL,
        "<yellow>Ir até {player}",
        List.of(
            "<gray>Pede teleporte para visitar",
            "<gold>{player}</gold>.",
            "",
            "<yellow>Clique para enviar."),
        15,
        Material.COMPASS,
        "<yellow>Chamar {player}",
        List.of(
            "<gray>Pede para <gold>{player}</gold>",
            "<gray>vir até você.",
            "",
            "<yellow>Clique para enviar."),
        "<gold>★ Recomendado",
        21,
        Material.NETHER_STAR,
        "<aqua>Favoritar {player}",
        List.of(
            "<gray>Adiciona <gold>{player}</gold> aos",
            "<gray>seus favoritos.",
            "",
            "<yellow>Clique para favoritar."),
        21,
        Material.RED_DYE,
        "<red>Remover dos favoritos",
        List.of(
            "<gray>Tira <gold>{player}</gold> da",
            "<gray>sua lista de favoritos.",
            "",
            "<yellow>Clique para remover."),
        23,
        Material.BARRIER,
        "<yellow>Fechar",
        List.of("<gray>Fecha este menu."));
  }
}
