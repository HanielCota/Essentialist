package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Help/tutorial sub-menu opened from the "Como funciona" slot in {@code TpaHelpMenu}. Holds three
 * cards (commands, examples, FAQ) plus a back button.
 */
@ConfigSerializable
public record TpaHelpInfoMenuConfig(
    @Comment("Help info menu title.") String title,
    @Comment("Help info menu rows (1-6).") int rows,
    @Comment("Slot of the commands card.") int commandsSlot,
    @Comment("Material of the commands card.") Material commandsIcon,
    @Comment("Name of the commands card.") String commandsName,
    @Comment("Lore of the commands card.") List<String> commandsLore,
    @Comment("Slot of the examples card.") int examplesSlot,
    @Comment("Material of the examples card.") Material examplesIcon,
    @Comment("Name of the examples card.") String examplesName,
    @Comment("Lore of the examples card.") List<String> examplesLore,
    @Comment("Slot of the FAQ card.") int faqSlot,
    @Comment("Material of the FAQ card.") Material faqIcon,
    @Comment("Name of the FAQ card.") String faqName,
    @Comment("Lore of the FAQ card.") List<String> faqLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaHelpInfoMenuConfig defaults() {
    return new TpaHelpInfoMenuConfig(
        "Como funciona o TPA",
        3,
        10,
        Material.WRITABLE_BOOK,
        "<yellow>Comandos",
        List.of(
            "<yellow>/tpa <jogador>",
            "<gray>Pede para ir até o jogador.",
            "",
            "<yellow>/tpahere <jogador>",
            "<gray>Pede para o jogador vir.",
            "",
            "<yellow>/tpaccept [jogador]",
            "<gray>Aceita um pedido pendente.",
            "",
            "<yellow>/tpdeny [jogador]",
            "<gray>Recusa um pedido pendente.",
            "",
            "<yellow>/tpacancel",
            "<gray>Cancela seu pedido enviado."),
        13,
        Material.MAP,
        "<yellow>Exemplos",
        List.of(
            "<dark_gray>Visitar alguém:",
            "<gray>1. Você usa <yellow>/tpa Notch",
            "<gray>2. Notch recebe pedido no chat",
            "<gray>3. Notch clica em ACEITAR",
            "<gray>4. Você teleporta até ele",
            "",
            "<dark_gray>Chamar alguém:",
            "<gray>1. Você usa <yellow>/tpahere Notch",
            "<gray>2. Notch clica em ACEITAR",
            "<gray>3. Notch teleporta até você"),
        16,
        Material.BOOK,
        "<yellow>Perguntas frequentes",
        List.of(
            "<gold>Quanto tempo dura um pedido?",
            "<gray>60 segundos por padrão.",
            "",
            "<gold>Posso bloquear alguém?",
            "<gray>Sim. <yellow>/tpablock <jogador>",
            "",
            "<gold>O outro recusa, e agora?",
            "<gray>Você recebe aviso no chat e",
            "<gray>pode tentar novamente depois.",
            "",
            "<gold>Posso ver meus teleportes?",
            "<gray>Sim. <yellow>/tpahistory <gray>abre o menu."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."));
  }
}
