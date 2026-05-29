package com.hanielcota.essentials.modules.essentials.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** The static "how it works" guide item shown in the module-control menu. */
@ConfigSerializable
public record ModulesInfoConfig(
    @Comment("Slot (0-based) of the guide item.") int slot,
    @Comment("Material of the guide item.") Material material,
    @Comment("Name of the guide item.") String name,
    @Comment("Lore of the guide item (explains how the menu works).") List<String> lore) {

  public static ModulesInfoConfig defaults() {
    return new ModulesInfoConfig(
        4,
        Material.BOOK,
        "<gold><bold>Painel de Módulos",
        List.of(
            "<gray>Ative ou desative os módulos do servidor.",
            "",
            "<yellow>➜ <gray>Clique num item para <white>alternar<gray>.",
            "<green>● <gray>Verde = ativado.",
            "<red>● <gray>Vermelho = desativado.",
            "<yellow>➜ <gray>Use o <white>filtro</white> para trocar de categoria.",
            "",
            "<gold>⚠ <gray>As mudanças aplicam no <white>próximo reinício<gray>.",
            "<dark_gray>Tudo é salvo em modules.yml."));
  }
}
