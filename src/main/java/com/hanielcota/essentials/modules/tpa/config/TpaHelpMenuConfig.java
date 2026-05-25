package com.hanielcota.essentials.modules.tpa.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the {@code /tpa} help menu — shown when the player runs {@code /tpa} without
 * arguments. Each entry is a non-interactive explainer slot, except {@link #historySlot()} which
 * opens the {@code /tpahistory} menu when clicked.
 */
@ConfigSerializable
public record TpaHelpMenuConfig(
    @Comment("/tpa help menu title.") String title,
    @Comment("/tpa help menu rows (1-6).") int rows,
    @Comment("Slot of the /tpa explainer.") int tpaSlot,
    @Comment("Material of the /tpa explainer.") Material tpaIcon,
    @Comment("Name of the /tpa explainer.") String tpaName,
    @Comment("Lore of the /tpa explainer.") List<String> tpaLore,
    @Comment("Slot of the /tpahere explainer.") int tpaHereSlot,
    @Comment("Material of the /tpahere explainer.") Material tpaHereIcon,
    @Comment("Name of the /tpahere explainer.") String tpaHereName,
    @Comment("Lore of the /tpahere explainer.") List<String> tpaHereLore,
    @Comment("Slot of the /tpaccept and /tpdeny explainer.") int acceptSlot,
    @Comment("Material of the accept/deny explainer.") Material acceptIcon,
    @Comment("Name of the accept/deny explainer.") String acceptName,
    @Comment("Lore of the accept/deny explainer.") List<String> acceptLore,
    @Comment("Slot of the history shortcut — clicking opens /tpahistory.") int historySlot,
    @Comment("Material of the history shortcut.") Material historyIcon,
    @Comment("Name of the history shortcut.") String historyName,
    @Comment("Lore of the history shortcut.") List<String> historyLore) {

  public static TpaHelpMenuConfig defaults() {
    return new TpaHelpMenuConfig(
        "<dark_aqua>Como usar o TPA",
        3,
        10,
        Material.ENDER_PEARL,
        "<yellow>/tpa <jogador>",
        List.of(
            "<gray>Pede para se teleportar",
            "<gray>até outro jogador.",
            "",
            "<dark_gray>Exemplo: <gray>/tpa Notch"),
        12,
        Material.ENDER_EYE,
        "<yellow>/tpahere <jogador>",
        List.of(
            "<gray>Pede que outro jogador",
            "<gray>se teleporte até você.",
            "",
            "<dark_gray>Exemplo: <gray>/tpahere Notch"),
        14,
        Material.PAPER,
        "<yellow>/tpaccept · /tpdeny",
        List.of(
            "<gray>Aceita ou recusa um pedido",
            "<gray>de teleporte pendente.",
            "",
            "<dark_gray>Dica: <gray>clique no botão",
            "<dark_gray>da mensagem que aparece."),
        16,
        Material.BOOK,
        "<gold>Histórico",
        List.of(
            "<gray>Abre os seus últimos",
            "<gray>pedidos de teleporte.",
            "",
            "<yellow>Clique para abrir."));
  }
}
