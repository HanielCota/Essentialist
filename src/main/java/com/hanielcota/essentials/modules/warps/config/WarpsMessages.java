package com.hanielcota.essentials.modules.warps.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Every chat line the warps module can send. */
@ConfigSerializable
public record WarpsMessages(
    @Comment("/setwarp confirmation for a brand-new warp. Placeholders: {name}.") String warpSet,
    @Comment("/setwarp confirmation when overwriting an existing warp. Placeholders: {name}.")
        String warpUpdated,
    @Comment("Shown by /warp and /delwarp when the named warp is unknown. Placeholders: {name}.")
        String unknownWarp,
    @Comment("/delwarp confirmation. Placeholders: {name}.") String warpDeleted,
    @Comment(
            "Shown by /warp when the player lacks essentials.warp.use.<name>. "
                + "Placeholders: {name}.")
        String noPermission,
    @Comment("Shown by /warp when the target world is no longer loaded.") String worldGone,
    @Comment("/warps when the player has no usable warps.") String noWarps,
    @Comment("/warps header. Placeholders: {count}.") String listHeader,
    @Comment(
            "Clickable list item shown by /warps — one per warp. Placeholders: {name}, "
                + "{world}, {x}, {y}, {z}.")
        String listEntry,
    @Comment("Tooltip shown when hovering a /warps entry. Placeholders: {name}.")
        String listEntryHover,
    @Comment("Shown on /warp start when a delay is configured. Placeholders: {name}, {seconds}.")
        String teleporting,
    @Comment("Shown after /warp completes successfully. Placeholders: {name}.") String teleported,
    @Comment("Shown when the delayed teleport is cancelled by damage or disconnect.")
        String cancelled,
    @Comment("Shown when the teleport itself fails.") String failed,
    @Comment("/setwarp rejection for a malformed name. Placeholder: {max}.") String invalidName) {

  public static WarpsMessages defaults() {
    return new WarpsMessages(
        "<green>Warp <gold>{name}</gold> criada.",
        "<green>Warp <gold>{name}</gold> atualizada.",
        "<red>A warp <gold>{name}</gold> não existe.",
        "<yellow>Warp <gold>{name}</gold> removida.",
        "<red>Você não tem permissão para usar a warp <gold>{name}</gold>.",
        "<red>O mundo desta warp não está carregado.",
        "<red>Você não tem acesso a nenhuma warp.",
        "<gray>Warps disponíveis (<gold>{count}</gold><gray>):",
        "<gold>{name}</gold> <gray>— <white>{world} {x}, {y}, {z}",
        "<gray>Clique para ir até <gold>{name}</gold>.",
        "<yellow>Teleportando para <gold>{name}</gold> em <gold>{seconds}s</gold>. "
            + "Não tome dano.",
        "<green>Você foi teleportado para <gold>{name}</gold>.",
        "<red>Teleporte cancelado.",
        "<red>O teleporte não pôde ser concluído.",
        "<red>Nome de warp inválido. Use até {max} caracteres: letras, números, '_' ou '-'.");
  }
}
