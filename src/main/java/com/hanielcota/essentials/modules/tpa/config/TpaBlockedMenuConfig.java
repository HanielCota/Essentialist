package com.hanielcota.essentials.modules.tpa.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TpaBlockedMenuConfig(
    @Comment("Blocked players menu title.") String title,
    @Comment("Blocked players menu rows (1-6).") int rows,
    @Comment("Slots used by blocked player items.") List<Integer> contentSlots,
    @Comment("Material of each blocked player item.") Material blockedIcon,
    @Comment("Use the blocked player's skin on blocked player items.") boolean blockedUsePlayerHead,
    @Comment("Custom head texture when blockedIcon is PLAYER_HEAD and player skin is disabled.")
        String blockedHeadTexture,
    @Comment("Blocked player item name. Placeholder: {player}.") String blockedName,
    @Comment("Blocked player item lore. Placeholder: {player}.") List<String> blockedLore,
    @Comment("Material of the placeholder shown when no players are blocked.") Material emptyIcon,
    @Comment("Name of the placeholder shown when no players are blocked.") String emptyName,
    @Comment("Lore of the placeholder shown when no players are blocked.") List<String> emptyLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaBlockedMenuConfig defaults() {
    return new TpaBlockedMenuConfig(
        "<dark_aqua>Jogadores bloqueados",
        3,
        List.of(10, 11, 12, 13, 14, 15, 16),
        Material.PLAYER_HEAD,
        true,
        "",
        "<red>{player}",
        List.of(
            "<gray>Este jogador não pode",
            "<gray>enviar pedidos de TPA para você.",
            "",
            "<yellow>Clique para desbloquear."),
        Material.BARRIER,
        "<green>Ninguém bloqueado",
        List.of("<gray>Use <yellow>/tpablock <jogador>", "<gray>para bloquear pedidos de alguém."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna às configurações de TPA."));
  }
}
