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
        "Comandos",
        List.of(
            "/tpa <jogador>",
            "Pede para ir até o jogador.",
            "",
            "/tpahere <jogador>",
            "Pede para o jogador vir até você.",
            "",
            "/tpaccept [jogador]",
            "Aceita um pedido pendente.",
            "",
            "/tpdeny [jogador]",
            "Recusa um pedido pendente.",
            "",
            "/tpacancel",
            "Cancela um pedido que você enviou."),
        13,
        Material.MAP,
        "Exemplos",
        List.of(
            "Visitar alguém:",
            "1. Você usa /tpa Notch",
            "2. Notch recebe o pedido no chat",
            "3. Notch clica em ACEITAR",
            "4. Você teleporta até ele",
            "",
            "Chamar alguém:",
            "1. Você usa /tpahere Notch",
            "2. Notch clica em ACEITAR",
            "3. Notch teleporta até você"),
        16,
        Material.BOOK,
        "Perguntas frequentes",
        List.of(
            "Quanto tempo dura um pedido?",
            "60 segundos por padrão.",
            "",
            "Posso bloquear alguém?",
            "Sim. Use /tpablock <jogador>.",
            "",
            "O outro recusou, e agora?",
            "Você recebe um aviso no chat e pode tentar novamente depois.",
            "",
            "Posso ver meus teleportes?",
            "Sim. Use /tpahistory para abrir o histórico."),
        22,
        Material.ARROW,
        "Voltar",
        List.of("Volta para o menu de TPA."));
  }
}
