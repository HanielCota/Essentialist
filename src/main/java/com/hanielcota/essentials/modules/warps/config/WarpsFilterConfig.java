package com.hanielcota.essentials.modules.warps.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Look and labels of the warps-menu filter button (the cycling sort/filter control). */
@ConfigSerializable
public record WarpsFilterConfig(
    @Comment("Slot (0-based) of the filter button.") int slot,
    @Comment("Material of the filter button.") Material material,
    @Comment("Filter button name. Placeholder: {state} (the active filter label).") String name,
    @Comment("Filter button lore. {options} expands to the list with the active one marked.")
        List<String> lore,
    @Comment("Marker appended to the active option in {options}.") String activeMarker,
    @Comment("Label for the alphabetical (default) filter.") String labelDefault,
    @Comment("Label for the most-players filter.") String labelMostPlayers,
    @Comment("Label for the least-players filter.") String labelLeastPlayers,
    @Comment("Label for the most-liked filter.") String labelMostLiked,
    @Comment("Label for the favorites-only filter.") String labelFavorites,
    @Comment("Label for the PVP-only filter.") String labelPvp) {

  public static WarpsFilterConfig defaults() {
    return new WarpsFilterConfig(
        45,
        Material.HOPPER,
        "<yellow>Filtro: <white>{state}",
        List.of(
            "<gray>Ordena e filtra as warps.", "", "{options}", "", "<green>Clique para alternar."),
        " <green>◀",
        "Padrão (A-Z)",
        "Mais jogadores",
        "Menos jogadores",
        "Mais curtidas",
        "Favoritas",
        "PVP ativo");
  }
}
