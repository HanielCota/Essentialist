package com.hanielcota.essentials.modules.info.config;

import com.hanielcota.essentials.menu.MenuLayouts;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record InfoConfig(
    @Comment("Title of the /info menu.") String menuTitle,
    @Comment("/info menu rows (1-6).") int rows,
    @Comment("Slots used by /info content and the back button.") List<Integer> contentSlots,
    @Comment("Slots used by detail entries.") List<Integer> detailSlots,
    @Comment("Slot of the back button in detail views.") int backSlot,
    @Comment("Material of the back button.") Material backMaterial,
    @Comment("Name of the back button.") String backName,
    @Comment("Lore of the back button.") List<String> backLore,
    @Comment("Slot of the server category.") int serverSlot,
    @Comment("Material of the server category.") Material serverMaterial,
    @Comment("Name of the server category.") String serverName,
    @Comment("Lore of the server category.") List<String> serverLore,
    @Comment("Slot of the player category.") int playerSlot,
    @Comment("Material of the player category.") Material playerMaterial,
    @Comment("Name of the player category.") String playerName,
    @Comment("Lore of the player category.") List<String> playerLore,
    @Comment("Slot of the about category.") int aboutSlot,
    @Comment("Material of the about category.") Material aboutMaterial,
    @Comment("Name of the about category.") String aboutName,
    @Comment("Lore of the about category.") List<String> aboutLore) {

  public static InfoConfig defaults() {
    return new InfoConfig(
        "<dark_gray>Informações",
        4,
        List.of(9, 10, 11, 12, 13, 14, 15, 16, 17, 31),
        List.of(9, 10, 11, 12, 13, 14, 15, 16, 17),
        31,
        Material.ARROW,
        "<yellow>Voltar",
        List.of(),
        11,
        Material.COMMAND_BLOCK,
        "<yellow>Servidor",
        List.of("<gray>Status do servidor."),
        13,
        Material.PLAYER_HEAD,
        "<yellow>Jogador",
        List.of("<gray>Informações de um jogador."),
        15,
        Material.ENCHANTED_BOOK,
        "<yellow>Essentialist",
        List.of("<gray>Sobre o plugin."));
  }

  public int effectiveRows() {
    return MenuLayouts.clampRows(rows);
  }

  public List<Integer> effectiveContentSlots() {
    return MenuLayouts.sanitizeSlots(contentSlots, effectiveRows());
  }

  public List<Integer> effectiveDetailSlots() {
    return MenuLayouts.sanitizeSlots(detailSlots, effectiveRows());
  }

  public int effectiveBackSlot() {
    var fallback = MenuLayouts.slotCount(effectiveRows()) - 5;
    return MenuLayouts.sanitizeSlot(backSlot, effectiveRows(), fallback);
  }

  /**
   * Sanitized category slots. Out-of-range raw values would silently drop the category button
   * through SlotRenderer.renderDynamicPageSlots and leave the tab unreachable; the fallback keeps
   * the icon visible even on a misconfig.
   */
  public int effectiveServerSlot() {
    return MenuLayouts.sanitizeSlot(serverSlot, effectiveRows(), 11);
  }

  public int effectivePlayerSlot() {
    return MenuLayouts.sanitizeSlot(playerSlot, effectiveRows(), 13);
  }

  public int effectiveAboutSlot() {
    return MenuLayouts.sanitizeSlot(aboutSlot, effectiveRows(), 15);
  }
}
