package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Appearance of the per-request action sub-menu — opens when the viewer clicks a pending request.
 * Surfaces Accept / Deny / Block explicitly instead of hiding them behind shift/right-click.
 */
@ConfigSerializable
public record TpaPendingActionMenuConfig(
    @Comment("Pending action menu title.") String title,
    @Comment("Pending action menu rows (1-6).") int rows,
    @Comment("Slot of the requester head shown at the top.") int targetSlot,
    @Comment("Material of the target item.") Material targetIcon,
    @Comment("Use the requester's skin on the target item.") boolean targetUsePlayerHead,
    @Comment("Custom head texture for the target item when material is PLAYER_HEAD.")
        String targetHeadTexture,
    @Comment("Name of the target item. Placeholders: {player}, {type}, {seconds}.")
        String targetName,
    @Comment("Lore of the target item. Placeholders: {player}, {type}, {seconds}.")
        List<String> targetLore,
    @Comment("Slot of the accept button.") int acceptSlot,
    @Comment("Material of the accept button.") Material acceptIcon,
    @Comment("Name of the accept button. Placeholder: {player}.") String acceptName,
    @Comment("Lore of the accept button. Placeholder: {player}.") List<String> acceptLore,
    @Comment("Slot of the deny button.") int denySlot,
    @Comment("Material of the deny button.") Material denyIcon,
    @Comment("Name of the deny button. Placeholder: {player}.") String denyName,
    @Comment("Lore of the deny button. Placeholder: {player}.") List<String> denyLore,
    @Comment("Slot of the block button.") int blockSlot,
    @Comment("Material of the block button.") Material blockIcon,
    @Comment("Name of the block button. Placeholder: {player}.") String blockName,
    @Comment("Lore of the block button. Placeholder: {player}.") List<String> blockLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Label for a request where the requester wants to come to you.") String typeTpa,
    @Comment("Label for a request where the requester wants you to go to them.")
        String typeTpaHere) {

  public static TpaPendingActionMenuConfig defaults() {
    return new TpaPendingActionMenuConfig(
        "Pedido pendente",
        5,
        13,
        Material.PLAYER_HEAD,
        true,
        "",
        "<gold>{player}",
        List.of(
            "<gray>{type}.",
            "<gray>Expira em <white>{seconds}s</white>.",
            "",
            "<gray>Escolha uma ação abaixo."),
        30,
        Material.LIME_DYE,
        "<green>Aceitar",
        List.of(
            "<gray>Aceita o pedido de <gold>{player}</gold>",
            "<gray>e teleporta na hora.",
            "",
            "<yellow>Clique para aceitar."),
        31,
        Material.RED_DYE,
        "<red>Recusar",
        List.of(
            "<gray>Recusa o pedido de <gold>{player}</gold>",
            "<gray>sem teleportar.",
            "",
            "<yellow>Clique para recusar."),
        32,
        Material.BARRIER,
        "<dark_red>Bloquear jogador",
        List.of(
            "<gray>Recusa o pedido e impede",
            "<gold>{player}</gold> <gray>de te enviar",
            "<gray>novos pedidos.",
            "",
            "<yellow>Clique para bloquear."),
        40,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna à lista de pedidos."),
        "Quer ir até você",
        "Quer chamar você");
  }
}
