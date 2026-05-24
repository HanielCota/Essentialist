package com.hanielcota.essentials.modules.homes.config.messages;

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
    @Comment("/sethome when the given material name is invalid. Placeholders: {material}.")
        String invalidMaterial,
    @Comment("Shown on /home start when a delay is configured. Placeholders: {name}, {seconds}.")
        String teleporting,
    @Comment("Shown after /home completes successfully. Placeholders: {name}.") String teleported,
    @Comment("Shown when the delayed teleport is cancelled by damage or disconnect.")
        String cancelled,
    @Comment("Shown when the teleport itself fails.") String failed,
    @Comment("Title of the delete-confirmation dialog.") String deleteConfirmTitle,
    @Comment("Centre item of the delete dialog.") String deleteConfirmPrompt,
    @Comment("Label of the yes button in the delete dialog.") String deleteConfirmYes,
    @Comment("Label of the no button in the delete dialog.") String deleteConfirmNo,
    @Comment("Confirmation after the icon changes. Placeholders: {name}, {material}.")
        String materialUpdated,
    @Comment(
            "Chat instruction shown after shift+click. Placeholders: {name}, {seconds}, {timeout}.")
        String renamePrompt,
    @Comment("Shown when the player types 'cancel' (or its alias) to abort the rename.")
        String renameCancelled,
    @Comment("Shown when the rename input window expires. Placeholders: {seconds}.")
        String renameTimeout,
    @Comment("Shown when a home name is empty, too long, or contains unsafe characters.")
        String renameInvalid,
    @Comment("Shown when the new name is already used. Placeholders: {name}.") String renameTaken,
    @Comment("Shown when the home disappeared during the rename window. Placeholders: {name}.")
        String renameLost,
    @Comment("Rename confirmation. Placeholders: {old}, {new}.") String renamed) {

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
        "<red>Material inválido: <gold>{material}</gold>.",
        "<yellow>Teleportando para <gold>{name}</gold> em <gold>{seconds}s</gold>. "
            + "Não tome dano.",
        "<green>Você foi teleportado para <gold>{name}</gold>.",
        "<red>Teleporte cancelado.",
        "<red>O teleporte não pôde ser concluído.",
        "<dark_red>Excluir esta home?",
        "<red>Excluir esta home?",
        "<green>Sim, excluir",
        "<red>Cancelar",
        "<green>Ícone de <gold>{name}</gold> alterado para <gold>{material}</gold>.",
        "<yellow>Digite no chat o novo nome para <gold>{name}</gold> "
            + "(ou <gold>cancel</gold>). Você tem <gold>{seconds}s</gold>.",
        "<yellow>Renomeação cancelada.",
        "<red>O tempo para renomear (<gold>{seconds}s</gold>) expirou.",
        "<red>Nome inválido. Use 1-32 caracteres: letras, números, _ ou -.",
        "<red>Você já tem uma home chamada <gold>{name}</gold>.",
        "<red>A home <gold>{name}</gold> desapareceu antes da renomeação.",
        "<green>Home <gold>{old}</gold> renomeada para <gold>{new}</gold>.");
  }

  public String invalidName() {
    return renameInvalid;
  }
}
