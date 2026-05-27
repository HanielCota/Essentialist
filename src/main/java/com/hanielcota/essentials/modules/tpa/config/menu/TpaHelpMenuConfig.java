package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the {@code /tpa} hub — shown when the player runs {@code /tpa} without arguments.
 */
@ConfigSerializable
public record TpaHelpMenuConfig(
    @Comment("/tpa hub title.") String title,
    @Comment("/tpa hub rows (1-6).") int rows,
    @Comment("Slot of the player profile item.") int profileSlot,
    @Comment("Use the viewer's skin on the profile item.") boolean profileUsePlayerHead,
    @Comment("Fallback material of the profile item.") Material profileIcon,
    @Comment("Custom head texture for the profile item when not using the viewer's skin.")
        String profileHeadTexture,
    @Comment(
            "Name of the profile item. Placeholders: {player}, {sent}, {received}, {pending}, "
                + "{accept_rate}, {avg_accept}, {most_contacted}.")
        String profileName,
    @Comment(
            "Lore of the profile item. Placeholders: {player}, {sent}, {received}, {receive_tpa},"
                + " {receive_tpahere}, {accept_rate}, {avg_accept}, {most_contacted}.")
        List<String> profileLore,
    @Comment("Label used in the profile lore when there is no data yet for a stat.")
        String statsFallback,
    @Comment("Slot of the /tpa explainer.") int tpaSlot,
    @Comment("Material of the /tpa explainer.") Material tpaIcon,
    @Comment("Custom head texture for the /tpa explainer when material is PLAYER_HEAD.")
        String tpaHeadTexture,
    @Comment("Name of the /tpa explainer.") String tpaName,
    @Comment("Lore of the /tpa explainer.") List<String> tpaLore,
    @Comment("Slot of the pending requests shortcut.") int pendingSlot,
    @Comment("Material of the pending requests shortcut.") Material pendingIcon,
    @Comment("Custom head texture for the pending shortcut when material is PLAYER_HEAD.")
        String pendingHeadTexture,
    @Comment("Name of the pending requests shortcut. Placeholder: {pending}.") String pendingName,
    @Comment("Lore of the pending requests shortcut. Placeholder: {pending}.")
        List<String> pendingLore,
    @Comment("Slot of the history shortcut — clicking opens /tpahistory.") int historySlot,
    @Comment("Material of the history shortcut.") Material historyIcon,
    @Comment("Custom head texture for the history shortcut when material is PLAYER_HEAD.")
        String historyHeadTexture,
    @Comment("Name of the history shortcut.") String historyName,
    @Comment("Lore of the history shortcut.") List<String> historyLore,
    @Comment("Slot of the settings shortcut — clicking opens TPA settings.") int settingsSlot,
    @Comment("Material of the settings shortcut.") Material settingsIcon,
    @Comment("Custom head texture for the settings shortcut when material is PLAYER_HEAD.")
        String settingsHeadTexture,
    @Comment("Name of the settings shortcut.") String settingsName,
    @Comment("Lore of the settings shortcut.") List<String> settingsLore,
    @Comment("Slot of the favorites shortcut — clicking opens the favorites menu.")
        int favoritesSlot,
    @Comment("Material of the favorites shortcut.") Material favoritesIcon,
    @Comment("Custom head texture for the favorites shortcut when material is PLAYER_HEAD.")
        String favoritesHeadTexture,
    @Comment("Name of the favorites shortcut. Placeholder: {favorites}.") String favoritesName,
    @Comment("Lore of the favorites shortcut. Placeholder: {favorites}.")
        List<String> favoritesLore,
    @Comment(
            "Slot of the outgoing-request shortcut — shown when there is a pending /tpa to cancel.")
        int outgoingSlot,
    @Comment("Material of the outgoing shortcut when there is a pending request.")
        Material outgoingIcon,
    @Comment("Use the target's skin on the outgoing shortcut head.") boolean outgoingUsePlayerHead,
    @Comment("Custom head texture for the outgoing shortcut when material is PLAYER_HEAD.")
        String outgoingHeadTexture,
    @Comment("Name of the outgoing shortcut. Placeholders: {target}, {type}, {seconds}.")
        String outgoingName,
    @Comment("Lore of the outgoing shortcut. Placeholders: {target}, {type}, {seconds}.")
        List<String> outgoingLore,
    @Comment("Label used in {type} when the outgoing request is a /tpa.") String outgoingTypeTpa,
    @Comment("Label used in {type} when the outgoing request is a /tpahere.")
        String outgoingTypeTpaHere,
    @Comment("Material of the outgoing shortcut when there is no pending request.")
        Material outgoingIdleIcon,
    @Comment("Name of the outgoing shortcut when idle.") String outgoingIdleName,
    @Comment("Lore of the outgoing shortcut when idle.") List<String> outgoingIdleLore,
    @Comment("Label used for enabled settings.") String enabledLabel,
    @Comment("Label used for disabled settings.") String disabledLabel) {

  public static TpaHelpMenuConfig defaults() {
    return new TpaHelpMenuConfig(
        "TPA",
        5,
        4,
        true,
        Material.PLAYER_HEAD,
        "",
        "{player}",
        List.of("Suas estatísticas de teleporte.", "", "Clique para abrir."),
        "—",
        19,
        Material.BOOK,
        "",
        "Como funciona",
        List.of(
            "Comandos, exemplos e perguntas frequentes do sistema de teleporte.",
            "",
            "Clique para abrir."),
        21,
        Material.BELL,
        "",
        "Pedidos recebidos",
        List.of("Você tem {pending} pedido(s) esperando sua resposta.", "", "Clique para ver."),
        23,
        Material.CLOCK,
        "",
        "Histórico",
        List.of("Veja seus últimos teleportes.", "", "Clique para abrir."),
        25,
        Material.REDSTONE,
        "",
        "Configurações",
        List.of(
            "Ajuste suas preferências: quem pode te chamar, sons, aceitar favoritos automaticamente"
                + " e mais.",
            "",
            "Clique para ajustar."),
        28,
        Material.EMERALD,
        "",
        "Favoritos",
        List.of(
            "Seus jogadores favoritos.",
            "Você tem {favorites} salvo(s).",
            "",
            "Clique para abrir."),
        32,
        Material.PLAYER_HEAD,
        true,
        "",
        "Pedido enviado",
        List.of(
            "Você pediu para {type} {target}.",
            "",
            "Expira em {seconds}s.",
            "",
            "Clique para cancelar."),
        "ir até",
        "chamar",
        Material.LIGHT_GRAY_DYE,
        "Sem pedido em aberto",
        List.of("Você não tem nenhum TPA aguardando resposta agora."),
        "sim",
        "não");
  }
}
