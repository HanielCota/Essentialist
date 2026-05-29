package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Help/tutorial sub-menu opened from the "How it works" slot in {@code TpaHelpMenu}. Holds three
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
        "How TPA works",
        3,
        10,
        Material.WRITABLE_BOOK,
        "Commands",
        List.of(
            "/tpa <player>",
            "Request to go to the player.",
            "",
            "/tpahere <player>",
            "Request the player to come to you.",
            "",
            "/tpaccept [player]",
            "Accept a pending request.",
            "",
            "/tpdeny [player]",
            "Deny a pending request.",
            "",
            "/tpacancel",
            "Cancel a request you sent."),
        13,
        Material.MAP,
        "Examples",
        List.of(
            "Visit someone:",
            "1. You run /tpa Notch",
            "2. Notch gets the request in chat",
            "3. Notch clicks ACCEPT",
            "4. You teleport to them",
            "",
            "Summon someone:",
            "1. You run /tpahere Notch",
            "2. Notch clicks ACCEPT",
            "3. Notch teleports to you"),
        16,
        Material.BOOK,
        "Frequently asked questions",
        List.of(
            "How long does a request last?",
            "60 seconds by default.",
            "",
            "Can I block someone?",
            "Yes. Use /tpablock <player>.",
            "",
            "They denied it, now what?",
            "You get a chat notice and can try again later.",
            "",
            "Can I see my teleports?",
            "Yes. Use /tpahistory to open the history."),
        22,
        Material.ARROW,
        "Back",
        List.of("Back to the TPA menu."));
  }
}
