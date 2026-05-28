package com.hanielcota.essentials.modules.homes.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesOptionsMenuConfig(
    @Comment(
            "Options menu title shown on right-click. Static — inventory titles can't be "
                + "personalised per viewer, so the home name appears in the info slot instead.")
        String title,
    @Comment("Options menu rows (1-6).") int rows,
    @Comment("Slot of the home info item in the options menu.") int homeSlot,
    @Comment("Slot of the teleport button.") int teleportSlot,
    @Comment("Material of the teleport button.") Material teleportMaterial,
    @Comment("Name of the teleport button. Placeholders: {name}.") String teleportName,
    @Comment("Lore of the teleport button.") List<String> teleportLore,
    @Comment("Slot of the rename button.") int renameSlot,
    @Comment("Material of the rename button.") Material renameMaterial,
    @Comment("Name of the rename button. Placeholders: {name}.") String renameName,
    @Comment("Lore of the rename button.") List<String> renameLore,
    @Comment("Slot of the change-icon button.") int iconSlot,
    @Comment("Material of the change-icon button.") Material iconMaterial,
    @Comment("Name of the change-icon button. Placeholders: {name}.") String iconName,
    @Comment("Lore of the change-icon button.") List<String> iconLore,
    @Comment("Slot of the delete button.") int deleteSlot,
    @Comment("Material of the delete button.") Material deleteMaterial,
    @Comment("Name of the delete button. Placeholders: {name}.") String deleteName,
    @Comment("Lore of the delete button.") List<String> deleteLore,
    @Comment("Slot of the back button.") int backSlot,
    @Comment("Material of the back button.") Material backMaterial,
    @Comment("Name of the back button.") String backName,
    @Comment("Lore of the back button.") List<String> backLore,
    @Comment("Slot of the pin / unpin button.") int pinSlot,
    @Comment("Material of the pin button (shown when the home is not pinned).")
        Material pinMaterial,
    @Comment("Name of the pin button. Placeholders: {name}.") String pinName,
    @Comment("Lore of the pin button. Placeholders: {name}.") List<String> pinLore,
    @Comment("Material of the unpin button (shown when the home is pinned).")
        Material unpinMaterial,
    @Comment("Name of the unpin button. Placeholders: {name}.") String unpinName,
    @Comment("Lore of the unpin button. Placeholders: {name}.") List<String> unpinLore) {

  public static HomesOptionsMenuConfig defaults() {
    return new HomesOptionsMenuConfig(
        "<dark_gray>Opções da home",
        3,
        4,
        11,
        Material.ENDER_PEARL,
        "<green>Teleportar",
        List.of("<gray>Vai até <gold>{name}</gold>."),
        12,
        Material.NAME_TAG,
        "<yellow>Renomear",
        List.of(
            "<gray>Troca o nome de <gold>{name}</gold>.",
            "",
            "<yellow>Clique e digite o novo nome no chat."),
        14,
        Material.PAINTING,
        "<aqua>Trocar ícone",
        List.of("<gray>Escolhe um novo ícone para <gold>{name}</gold>."),
        15,
        Material.BARRIER,
        "<red>Deletar",
        List.of("<gray>Remove <gold>{name}</gold> permanentemente."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna à lista de homes."),
        13,
        Material.NETHER_STAR,
        "<gold>Fixar no topo",
        List.of("<gray>Move <gold>{name}</gold> para o topo da lista."),
        Material.NETHER_STAR,
        "<gold>★ Desfixar",
        List.of("<gray>Remove o destaque de <gold>{name}</gold>."));
  }
}
