package com.hanielcota.essentials.modules.tpa.config;

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
    @Comment("Name of the profile item. Placeholders: {player}, {sent}, {received}.")
        String profileName,
    @Comment(
            "Lore of the profile item. Placeholders: {player}, {sent}, {received}, "
                + "{receive_tpa}, {receive_tpahere}.")
        List<String> profileLore,
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
    @Comment("Label used for enabled settings.") String enabledLabel,
    @Comment("Label used for disabled settings.") String disabledLabel) {

  public static TpaHelpMenuConfig defaults() {
    return new TpaHelpMenuConfig(
        "<dark_aqua>TPA",
        3,
        13,
        true,
        Material.PLAYER_HEAD,
        "",
        "<gold>{player}",
        List.of(
            "<gray>Pedidos enviados: <white>{sent}",
            "<gray>Pedidos recebidos: <white>{received}",
            "<gray>Pedidos pendentes: <white>{pending}",
            "",
            "<gray>Deixar virem até você: {receive_tpa}",
            "<gray>Deixar chamarem você: {receive_tpahere}",
            "",
            "<dark_gray>Use /tpa <jogador> para",
            "<dark_gray>pedir para ir até alguém."),
        10,
        Material.ENDER_PEARL,
        "",
        "<yellow>Pedidos de teleporte",
        List.of(
            "<gray>Ir até alguém: você pede",
            "<gray>permissão para teleportar até",
            "<gray>outro jogador.",
            "",
            "<gray>Chamar alguém: você pede",
            "<gray>para o jogador vir até você.",
            "",
            "<dark_gray>O alvo aceita ou recusa",
            "<dark_gray>pela mensagem no chat.",
            "",
            "<dark_gray>Exemplos: <gray>/tpa Notch",
            "<dark_gray>          <gray>/tpahere Notch"),
        12,
        Material.PAPER,
        "",
        "<yellow>Pedidos pendentes",
        List.of(
            "<gray>Você tem <white>{pending}</white> pedido(s)",
            "<gray>aguardando resposta.",
            "",
            "<yellow>Clique para abrir."),
        15,
        Material.BOOK,
        "",
        "<gold>Histórico",
        List.of(
            "<gray>Abre os seus últimos",
            "<gray>pedidos de teleporte.",
            "",
            "<yellow>Clique para abrir."),
        16,
        Material.COMPARATOR,
        "",
        "<aqua>Configurações",
        List.of(
            "<gray>Escolha se outros jogadores",
            "<gray>podem ir até você ou chamar",
            "<gray>você até eles.",
            "",
            "<yellow>Clique para configurar."),
        14,
        Material.NETHER_STAR,
        "",
        "<gold>Favoritos",
        List.of(
            "<gray>Atalhos rápidos de TPA para",
            "<gray>seus jogadores favoritos.",
            "<gray>Salvos: <white>{favorites}",
            "",
            "<yellow>Clique para abrir."),
        "<green>Ligado",
        "<red>Desligado");
  }
}
