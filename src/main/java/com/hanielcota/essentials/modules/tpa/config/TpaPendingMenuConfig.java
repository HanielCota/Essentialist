package com.hanielcota.essentials.modules.tpa.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TpaPendingMenuConfig(
    @Comment("Pending TPA requests menu title.") String title,
    @Comment("Pending TPA requests menu rows (1-6).") int rows,
    @Comment("Slots used by pending request items.") List<Integer> contentSlots,
    @Comment("Material of each pending request item.") Material requestIcon,
    @Comment("Use the requester skin on pending request items.") boolean requestUsePlayerHead,
    @Comment("Custom head texture when requestIcon is PLAYER_HEAD and player skin is disabled.")
        String requestHeadTexture,
    @Comment("Pending request item name. Placeholders: {player}, {type}, {seconds}.")
        String requestName,
    @Comment("Pending request item lore. Placeholders: {player}, {type}, {seconds}.")
        List<String> requestLore,
    @Comment("Material of the placeholder shown when there are no pending requests.")
        Material emptyIcon,
    @Comment("Name of the placeholder shown when there are no pending requests.") String emptyName,
    @Comment("Lore of the placeholder shown when there are no pending requests.")
        List<String> emptyLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Label for a request where the requester wants to come to you.") String typeTpa,
    @Comment("Label for a request where the requester wants you to go to them.")
        String typeTpaHere) {

  public static TpaPendingMenuConfig defaults() {
    return new TpaPendingMenuConfig(
        "<dark_aqua>Pedidos pendentes",
        3,
        List.of(10, 11, 12, 13, 14, 15, 16),
        Material.PLAYER_HEAD,
        true,
        "",
        "<yellow>{player}",
        List.of(
            "<gray>Pedido: <white>{type}",
            "<gray>Expira em: <white>{seconds}s",
            "",
            "<green>Clique esquerdo para aceitar.",
            "<red>Clique direito para recusar."),
        Material.BARRIER,
        "<red>Nenhum pedido pendente",
        List.of("<gray>Quando alguém pedir teleporte", "<gray>para você, o pedido aparece aqui."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna ao menu de TPA."),
        "Quer ir até você",
        "Quer chamar você");
  }
}
