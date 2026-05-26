package com.hanielcota.essentials.modules.tpa.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the favorites menu — paginated list of saved players plus an "add favorite" button
 * that opens a chat prompt.
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
    @Comment("Favorite item name. Placeholder: {player}.") String favoriteName,
    @Comment("Favorite item lore. Placeholder: {player}.") List<String> favoriteLore,
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
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaFavoritesMenuConfig defaults() {
    return new TpaFavoritesMenuConfig(
        "<dark_aqua>Favoritos",
        3,
        List.of(10, 11, 12, 13, 14, 15, 16),
        Material.PLAYER_HEAD,
        true,
        "",
        "<gold>{player}",
        List.of(
            "<gray>Atalho rápido de TPA.",
            "",
            "<yellow>Clique para escolher",
            "<yellow>ir até ele ou chamar."),
        Material.BARRIER,
        "<red>Sem favoritos",
        List.of(
            "<gray>Você ainda não tem favoritos.",
            "",
            "<yellow>Clique no botão de adicionar",
            "<yellow>para salvar um jogador."),
        18,
        Material.WRITABLE_BOOK,
        "",
        "<green>Adicionar favorito",
        List.of(
            "<gray>Clique e digite o nick do",
            "<gray>jogador no chat.",
            "",
            "<dark_gray>Digite <gray>cancelar <dark_gray>para abortar."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."));
  }
}
