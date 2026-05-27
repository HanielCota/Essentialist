package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the favorite-action sub-menu — two TPA buttons (visit / summon) plus remove and
 * back. Opens when the player clicks a favorite head.
 *
 * <p>The {@code title} is resolved once at menu registration (MenuFramework v1.2.0 limitation), so
 * it cannot contain a per-viewer {@code {player}} placeholder — the target name appears in the head
 * item instead.
 */
@ConfigSerializable
public record TpaFavoriteActionMenuConfig(
    @Comment("Favorite action menu title (static, no per-viewer placeholders).") String title,
    @Comment("Favorite action menu rows (1-6).") int rows,
    @Comment("Slot of the player head shown at the top.") int targetSlot,
    @Comment("Material of the target item.") Material targetIcon,
    @Comment("Use the favorited player's skin on the target item.") boolean targetUsePlayerHead,
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
    @Comment("Slot of the remove-favorite button.") int removeSlot,
    @Comment("Material of the remove-favorite button.") Material removeIcon,
    @Comment("Name of the remove-favorite button. Placeholder: {player}.") String removeName,
    @Comment("Lore of the remove-favorite button. Placeholder: {player}.") List<String> removeLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaFavoriteActionMenuConfig defaults() {
    return new TpaFavoriteActionMenuConfig(
        "Favorito",
        5,
        13,
        Material.PLAYER_HEAD,
        true,
        "",
        "{player}",
        List.of("Escolha uma ação abaixo."),
        30,
        Material.ENDER_PEARL,
        "Ir até {player}",
        List.of("Pede teleporte para visitar {player}.", "", "Clique para enviar o pedido."),
        31,
        Material.COMPASS,
        "Chamar {player}",
        List.of("Pede para {player} vir até você.", "", "Clique para enviar o pedido."),
        32,
        Material.RED_DYE,
        "Remover dos favoritos",
        List.of("Tira {player} da sua lista de favoritos.", "", "Clique para remover."),
        40,
        Material.ARROW,
        "Voltar",
        List.of("Volta para a lista de favoritos."));
  }
}
