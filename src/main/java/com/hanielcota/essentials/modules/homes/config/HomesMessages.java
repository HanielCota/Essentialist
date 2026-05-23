package com.hanielcota.essentials.modules.homes.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Every chat line the homes module can send. */
@ConfigSerializable
public record HomesMessages(
    @Comment("/sethome confirmation for a brand-new home. Placeholders: {name}.") String homeSet,
    @Comment("/sethome confirmation when overwriting an existing home. Placeholders: {name}.")
        String homeUpdated,
    @Comment(
            "Shown by /sethome when the player has no free slots left. Placeholders: {name}, "
                + "{limit}.")
        String limitReached,
    @Comment("Shown by /home and /delhome when the named home is unknown. Placeholders: {name}.")
        String unknownHome,
    @Comment("/home when the player has no homes yet.") String noHomes,
    @Comment("/delhome confirmation. Placeholders: {name}.") String homeDeleted,
    @Comment("Shown by /home when the target world is no longer loaded.") String worldGone,
    @Comment("/homes header. Placeholders: {count}, {limit}.") String listHeader,
    @Comment(
            "Clickable list item shown by /homes — one per home. Placeholders: {name}, {world}, "
                + "{x}, {y}, {z}.")
        String listEntry,
    @Comment("Tooltip shown when hovering a /homes entry. Placeholders: {name}.")
        String listEntryHover,
    @Comment("Shown on /home start when a delay is configured. Placeholders: {name}, {seconds}.")
        String teleporting,
    @Comment("Shown after /home completes successfully. Placeholders: {name}.") String teleported,
    @Comment("Shown when the delayed teleport is cancelled by movement or damage.")
        String cancelled,
    @Comment("Shown when the teleport itself fails.") String failed) {

  public static HomesMessages defaults() {
    return new HomesMessages(
        "<green>Home <gold>{name}</gold> definida.",
        "<green>Home <gold>{name}</gold> atualizada.",
        "<red>Você já atingiu o seu limite de homes (<gold>{limit}</gold>). "
            + "Use /delhome para liberar espaço.",
        "<red>Você não tem nenhuma home chamada <gold>{name}</gold>.",
        "<red>Você ainda não tem nenhuma home. Use <gold>/sethome [nome]</gold>.",
        "<yellow>Home <gold>{name}</gold> removida.",
        "<red>O mundo desta home não está carregado.",
        "<gray>Suas homes (<gold>{count}</gold><gray>/<gold>{limit}</gold><gray>):",
        "<gold>{name}</gold> <gray>— <white>{world} {x}, {y}, {z}",
        "<gray>Clique para ir até <gold>{name}</gold>.",
        "<yellow>Teleportando para <gold>{name}</gold> em <gold>{seconds}s</gold>. "
            + "Não se mova nem tome dano.",
        "<green>Você foi teleportado para <gold>{name}</gold>.",
        "<red>Teleporte cancelado.",
        "<red>O teleporte não pôde ser concluído.");
  }
}
