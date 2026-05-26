package com.hanielcota.essentials.modules.tpa.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Notification settings sub-menu — controls sound and chat-alert toggles. Opened from {@code
 * TpaSettingsMenu}.
 */
@ConfigSerializable
public record TpaNotificationSettingsMenuConfig(
    @Comment("Notification settings menu title.") String title,
    @Comment("Notification settings menu rows (1-6).") int rows,
    @Comment("Slot of the notification sound toggle.") int soundsSlot,
    @Comment("Slot of the notify-when-favorited toggle.") int notifyWhenFavoritedSlot,
    @Comment("Material used when a toggle is enabled.") Material enabledIcon,
    @Comment("Material used when a toggle is disabled.") Material disabledIcon,
    @Comment("Name of the sound toggle. Placeholder: {state}.") String soundsName,
    @Comment("Lore of the sound toggle. Placeholder: {state}.") List<String> soundsLore,
    @Comment("Name of the notify-when-favorited toggle. Placeholder: {state}.")
        String notifyWhenFavoritedName,
    @Comment("Lore of the notify-when-favorited toggle. Placeholder: {state}.")
        List<String> notifyWhenFavoritedLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Label used when a toggle is enabled.") String enabledLabel,
    @Comment("Label used when a toggle is disabled.") String disabledLabel) {

  public static TpaNotificationSettingsMenuConfig defaults() {
    return new TpaNotificationSettingsMenuConfig(
        "Notificações",
        3,
        10,
        12,
        Material.LIME_DYE,
        Material.GRAY_DYE,
        "<yellow>Sons de notificação: {state}",
        List.of(
            "<gray>Toca um som quando você",
            "<gray>recebe um pedido de TPA.",
            "",
            "<yellow>Clique para alternar."),
        "<yellow>Avisar ao ser favoritado: {state}",
        List.of(
            "<gray>Recebe uma mensagem no chat",
            "<gray>quando alguém te favorita.",
            "",
            "<yellow>Clique para alternar."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna às configurações."),
        "<green>ligado",
        "<red>desligado");
  }
}
