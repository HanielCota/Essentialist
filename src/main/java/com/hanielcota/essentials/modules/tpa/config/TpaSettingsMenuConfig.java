package com.hanielcota.essentials.modules.tpa.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TpaSettingsMenuConfig(
    @Comment("TPA settings menu title.") String title,
    @Comment("TPA settings menu rows (1-6).") int rows,
    @Comment("Slot of the incoming /tpa toggle.") int receiveTpaSlot,
    @Comment("Slot of the incoming /tpahere toggle.") int receiveTpaHereSlot,
    @Comment("Material used when a toggle is enabled.") Material enabledIcon,
    @Comment("Material used when a toggle is disabled.") Material disabledIcon,
    @Comment("Name of the incoming /tpa toggle. Placeholder: {state}.") String receiveTpaName,
    @Comment("Lore of the incoming /tpa toggle. Placeholder: {state}.") List<String> receiveTpaLore,
    @Comment("Name of the incoming /tpahere toggle. Placeholder: {state}.")
        String receiveTpaHereName,
    @Comment("Lore of the incoming /tpahere toggle. Placeholder: {state}.")
        List<String> receiveTpaHereLore,
    @Comment("Slot of the blocked players shortcut.") int blockedSlot,
    @Comment("Material of the blocked players shortcut.") Material blockedIcon,
    @Comment("Name of the blocked players shortcut.") String blockedName,
    @Comment("Lore of the blocked players shortcut.") List<String> blockedLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Label used when a toggle is enabled.") String enabledLabel,
    @Comment("Label used when a toggle is disabled.") String disabledLabel) {

  public static TpaSettingsMenuConfig defaults() {
    return new TpaSettingsMenuConfig(
        "<dark_aqua>Configurações de TPA",
        3,
        11,
        15,
        Material.LIME_DYE,
        Material.GRAY_DYE,
        "<yellow>Permitir virem até você: {state}",
        List.of(
            "<gray>Quando ligado, outros jogadores",
            "<gray>podem pedir para teleportar",
            "<gray>até sua posição.",
            "",
            "<yellow>Clique para alternar."),
        "<yellow>Permitir chamarem você: {state}",
        List.of(
            "<gray>Quando ligado, outros jogadores",
            "<gray>podem pedir para você ir",
            "<gray>até a posição deles.",
            "",
            "<yellow>Clique para alternar."),
        13,
        Material.BARRIER,
        "<red>Jogadores bloqueados",
        List.of(
            "<gray>Veja quem não pode enviar",
            "<gray>pedidos de TPA para você.",
            "",
            "<yellow>Clique para abrir."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."),
        "<green>Ligado",
        "<red>Desligado");
  }
}
