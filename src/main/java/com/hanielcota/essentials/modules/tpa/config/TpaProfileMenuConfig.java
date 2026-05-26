package com.hanielcota.essentials.modules.tpa.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Stats screen — opened from the profile slot in {@code TpaHelpMenu}. Each stat lives in its own
 * slot so the lore stays focused instead of cramming everything in a single tooltip.
 */
@ConfigSerializable
public record TpaProfileMenuConfig(
    @Comment("Profile menu title. Placeholder: {player}.") String title,
    @Comment("Profile menu rows (1-6).") int rows,
    @Comment("Label used when a stat has no data yet.") String statsFallback,
    @Comment("Slot of the player head item.") int headSlot,
    @Comment("Use the viewer's skin on the head item.") boolean headUsePlayerHead,
    @Comment("Fallback material of the head item.") Material headIcon,
    @Comment("Custom head texture when headUsePlayerHead is false.") String headHeadTexture,
    @Comment("Name of the head item. Placeholder: {player}.") String headName,
    @Comment("Lore of the head item. Placeholder: {player}.") List<String> headLore,
    @Comment("Slot of the sent-requests stat.") int sentSlot,
    @Comment("Material of the sent-requests stat.") Material sentIcon,
    @Comment("Name of the sent stat. Placeholder: {sent}.") String sentName,
    @Comment("Lore of the sent stat. Placeholder: {sent}.") List<String> sentLore,
    @Comment("Slot of the received-requests stat.") int receivedSlot,
    @Comment("Material of the received-requests stat.") Material receivedIcon,
    @Comment("Name of the received stat. Placeholder: {received}.") String receivedName,
    @Comment("Lore of the received stat. Placeholders: {received}, {pending}.")
        List<String> receivedLore,
    @Comment("Slot of the accept-rate stat.") int acceptRateSlot,
    @Comment("Material of the accept-rate stat.") Material acceptRateIcon,
    @Comment("Name of the accept-rate stat. Placeholder: {accept_rate}.") String acceptRateName,
    @Comment("Lore of the accept-rate stat. Placeholder: {accept_rate}.")
        List<String> acceptRateLore,
    @Comment("Slot of the average-response stat.") int avgResponseSlot,
    @Comment("Material of the average-response stat.") Material avgResponseIcon,
    @Comment("Name of the average-response stat. Placeholder: {avg_accept}.")
        String avgResponseName,
    @Comment("Lore of the average-response stat. Placeholder: {avg_accept}.")
        List<String> avgResponseLore,
    @Comment("Slot of the most-contacted stat.") int mostContactedSlot,
    @Comment("Material of the most-contacted stat.") Material mostContactedIcon,
    @Comment("Name of the most-contacted stat. Placeholder: {most_contacted}.")
        String mostContactedName,
    @Comment("Lore of the most-contacted stat. Placeholder: {most_contacted}.")
        List<String> mostContactedLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaProfileMenuConfig defaults() {
    return new TpaProfileMenuConfig(
        "Perfil de {player}",
        3,
        "<gray>—",
        4,
        true,
        Material.PLAYER_HEAD,
        "",
        "<gold>{player}",
        List.of("<gray>Suas estatísticas de teleporte."),
        10,
        Material.PAPER,
        "<yellow>Pedidos enviados",
        List.of("<gray>Total: <white>{sent}", "", "<dark_gray>Pedidos que você enviou."),
        12,
        Material.CHEST,
        "<yellow>Pedidos recebidos",
        List.of(
            "<gray>Total: <white>{received}",
            "<gray>Aguardando: <white>{pending}",
            "",
            "<dark_gray>Pedidos que chegaram para você."),
        14,
        Material.EMERALD,
        "<yellow>Taxa de aceite",
        List.of("<gray>Você aceita <white>{accept_rate}", "<gray>dos pedidos que recebe."),
        16,
        Material.CLOCK,
        "<yellow>Tempo médio de resposta",
        List.of(
            "<gray>Você responde em média em",
            "<white>{avg_accept}",
            "",
            "<dark_gray>(do pedido até o aceite)"),
        20,
        Material.PLAYER_HEAD,
        "<yellow>Mais contactado",
        List.of("<gold>{most_contacted}", "", "<gray>Com quem você mais teleporta."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."));
  }
}
