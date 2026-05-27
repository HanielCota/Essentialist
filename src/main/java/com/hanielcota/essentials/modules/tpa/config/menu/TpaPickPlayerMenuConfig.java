package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the player picker opened from the hub's TPA slot — paginated heads of every online
 * player (the viewer is filtered out). Clicking a head stashes the target selection and switches to
 * the target-action menu.
 */
@ConfigSerializable
public record TpaPickPlayerMenuConfig(
    @Comment("Picker title (static, no per-viewer placeholders).") String title,
    @Comment("Picker rows (1-6).") int rows,
    @Comment("Slots used by online-player heads.") List<Integer> contentSlots,
    @Comment("Material of each player item.") Material playerIcon,
    @Comment("Use the listed player's skin on player items.") boolean playerUsePlayerHead,
    @Comment("Custom head texture when playerIcon is PLAYER_HEAD and player skin is disabled.")
        String playerHeadTexture,
    @Comment("Player item name. Placeholder: {player}.") String playerName,
    @Comment("Player item lore. Placeholder: {player}.") List<String> playerLore,
    @Comment("Material of the placeholder shown when nobody else is online.") Material emptyIcon,
    @Comment("Name of the placeholder shown when nobody else is online.") String emptyName,
    @Comment("Lore of the placeholder shown when nobody else is online.") List<String> emptyLore,
    @Comment("Slot of the back-to-hub button.") int backSlot,
    @Comment("Material of the back-to-hub button.") Material backIcon,
    @Comment("Name of the back-to-hub button.") String backName,
    @Comment("Lore of the back-to-hub button.") List<String> backLore,
    @Comment("Slot of the filter-cycle button.") int filterSlot,
    @Comment("Material of the filter button.") Material filterIcon,
    @Comment("Name of the filter button. Placeholder: {filter}.") String filterName,
    @Comment(
            "Lore of the filter button. Use {filter} for the current label and {options} to expand"
                + " the full list of filter states with the active one marked.")
        List<String> filterLore,
    @Comment("Label used in {filter} when no filter is applied.") String filterLabelAll,
    @Comment("Label used in {filter} when filtering by favorites.") String filterLabelFavorites,
    @Comment("Label used in {filter} when filtering by same world.") String filterLabelSameWorld,
    @Comment("Label used in {filter} when filtering by recent contacts.") String filterLabelRecent,
    @Comment("Suffix appended to the active option in the {options} expansion.")
        String filterActiveMarker) {

  public static TpaPickPlayerMenuConfig defaults() {
    return new TpaPickPlayerMenuConfig(
        "<gold>Escolha um jogador",
        6,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34),
        Material.PLAYER_HEAD,
        true,
        "",
        "<gold>{player}",
        List.of("<gray>Clique para abrir as ações", "<gray>de TPA com este jogador."),
        Material.BARRIER,
        "<red>Ninguém online",
        List.of("<gray>Não há nenhum outro jogador", "<gray>online no momento."),
        49,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."),
        45,
        Material.HOPPER,
        "<gold>Filtro: <yellow>{filter}",
        List.of(
            "<gray>Filtra a lista de jogadores",
            "<gray>mostrados no menu.",
            "",
            "{options}",
            "",
            "<yellow>Clique para alternar."),
        "Todos",
        "Favoritos",
        "Mesmo mundo",
        "Recentes",
        " <green>✓");
  }
}
