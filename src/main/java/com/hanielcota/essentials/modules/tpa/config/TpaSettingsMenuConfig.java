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
    @Comment("Slot of the auto-accept-favorites toggle.") int autoAcceptSlot,
    @Comment("Name of the auto-accept toggle. Placeholder: {state}.") String autoAcceptName,
    @Comment("Lore of the auto-accept toggle. Placeholder: {state}.") List<String> autoAcceptLore,
    @Comment("Slot of the notification sound toggle.") int soundsSlot,
    @Comment("Name of the sound toggle. Placeholder: {state}.") String soundsName,
    @Comment("Lore of the sound toggle. Placeholder: {state}.") List<String> soundsLore,
    @Comment("Slot of the allow-cross-world toggle.") int allowCrossWorldSlot,
    @Comment("Name of the cross-world toggle. Placeholder: {state}.") String allowCrossWorldName,
    @Comment("Lore of the cross-world toggle. Placeholder: {state}.")
        List<String> allowCrossWorldLore,
    @Comment("Slot of the notify-when-favorited toggle.") int notifyWhenFavoritedSlot,
    @Comment("Name of the notify-when-favorited toggle. Placeholder: {state}.")
        String notifyWhenFavoritedName,
    @Comment("Lore of the notify-when-favorited toggle. Placeholder: {state}.")
        List<String> notifyWhenFavoritedLore,
    @Comment("Slot of the read-only cooldown info item.") int cooldownSlot,
    @Comment("Material of the cooldown info item.") Material cooldownIcon,
    @Comment("Name of the cooldown info item.") String cooldownName,
    @Comment("Lore of the cooldown info item.") List<String> cooldownLore,
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
        "Configurações de TPA",
        6,
        11,
        13,
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
        15,
        "<yellow>Auto-aceitar favoritos: {state}",
        List.of(
            "<gray>Pedidos dos seus favoritos",
            "<gray>são aceitos sem perguntar.",
            "",
            "<yellow>Clique para alternar."),
        20,
        "<yellow>Sons de notificação: {state}",
        List.of(
            "<gray>Toca um som quando você",
            "<gray>recebe um pedido de TPA.",
            "",
            "<yellow>Clique para alternar."),
        22,
        "<yellow>TPA entre mundos: {state}",
        List.of(
            "<gray>Aceita pedidos de jogadores",
            "<gray>que estão em outros mundos.",
            "",
            "<yellow>Clique para alternar."),
        24,
        "<yellow>Avisar ao ser favoritado: {state}",
        List.of(
            "<gray>Recebe uma mensagem no chat",
            "<gray>quando alguém te favorita.",
            "",
            "<yellow>Clique para alternar."),
        39,
        Material.CLOCK,
        "Cooldown",
        List.of(
            "<gray>Tempo de espera entre comandos:",
            "<white>5 segundos</white>.",
            "",
            "<dark_gray>(configurado pelo servidor)"),
        41,
        Material.BARRIER,
        "<red>Jogadores bloqueados",
        List.of("<gray>Quem não pode te enviar pedidos.", "", "<yellow>Clique para abrir."),
        49,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."),
        "<green>ligado",
        "<red>desligado");
  }
}
