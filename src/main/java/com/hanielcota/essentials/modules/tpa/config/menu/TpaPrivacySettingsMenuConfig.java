package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Privacy settings sub-menu — controls who can send the viewer TPA requests. Opened from {@code
 * TpaSettingsMenu}.
 */
@ConfigSerializable
public record TpaPrivacySettingsMenuConfig(
    @Comment("Privacy settings menu title.") String title,
    @Comment("Privacy settings menu rows (1-6).") int rows,
    @Comment("Slot of the incoming /tpa toggle.") int receiveTpaSlot,
    @Comment("Slot of the incoming /tpahere toggle.") int receiveTpaHereSlot,
    @Comment("Slot of the allow-cross-world toggle.") int allowCrossWorldSlot,
    @Comment("Material used when a toggle is enabled.") Material enabledIcon,
    @Comment("Material used when a toggle is disabled.") Material disabledIcon,
    @Comment("Name of the incoming /tpa toggle. Placeholder: {state}.") String receiveTpaName,
    @Comment("Lore of the incoming /tpa toggle. Placeholder: {state}.") List<String> receiveTpaLore,
    @Comment("Name of the incoming /tpahere toggle. Placeholder: {state}.")
        String receiveTpaHereName,
    @Comment("Lore of the incoming /tpahere toggle. Placeholder: {state}.")
        List<String> receiveTpaHereLore,
    @Comment("Name of the cross-world toggle. Placeholder: {state}.") String allowCrossWorldName,
    @Comment("Lore of the cross-world toggle. Placeholder: {state}.")
        List<String> allowCrossWorldLore,
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

  public static TpaPrivacySettingsMenuConfig defaults() {
    return new TpaPrivacySettingsMenuConfig(
        "Privacidade",
        3,
        10,
        12,
        14,
        Material.LIME_DYE,
        Material.GRAY_DYE,
        "<yellow>Receber /tpa: {state}",
        List.of(
            "<gray>Outros jogadores podem pedir",
            "<gray>para vir até você.",
            "",
            "<yellow>Clique para alternar."),
        "<yellow>Receber /tpahere: {state}",
        List.of(
            "<gray>Outros jogadores podem pedir",
            "<gray>para você ir até eles.",
            "",
            "<yellow>Clique para alternar."),
        "<yellow>TPA entre mundos: {state}",
        List.of(
            "<gray>Aceita pedidos de jogadores",
            "<gray>que estão em outros mundos.",
            "",
            "<yellow>Clique para alternar."),
        20,
        Material.IRON_BARS,
        "<red>Jogadores bloqueados",
        List.of("<gray>Quem não pode te enviar pedidos.", "", "<yellow>Clique para abrir."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna às configurações."),
        "<green>ligado",
        "<red>desligado");
  }
}
