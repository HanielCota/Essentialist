package com.hanielcota.essentials.modules.homes.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Every chat line the homes module can send. */
@ConfigSerializable
public record HomesMessages(
    @Comment("Confirmation for a brand-new home created via /homes. Placeholders: {name}.")
        String homeSet,
    @Comment(
            "Shown when the player has no free slots left for a new home. Placeholders: {name}, "
                + "{limit}.")
        String limitReached,
    @Comment("Shown by /home when the named home is unknown. Placeholders: {name}.")
        String unknownHome,
    @Comment("/home when the player has no homes yet.") String noHomes,
    @Comment("Confirmation after a home is deleted from the menu. Placeholders: {name}.")
        String homeDeleted,
    @Comment("Shown by /home when the target world is no longer loaded.") String worldGone,
    @Comment("Shown on /home start when a delay is configured. Placeholders: {name}, {seconds}.")
        String teleporting,
    @Comment("Clickable suffix appended to the teleporting message that runs /tpcancel.")
        String cancelButton,
    @Comment("Tooltip shown when hovering the [Cancel] button of the teleporting message.")
        String cancelHover,
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
    @Comment("Rename confirmation. Placeholders: {old}, {new}.") String renamed,
    @Comment(
            "Chat instruction shown after clicking the + Nova home slot in /homes. "
                + "Placeholders: {seconds}.")
        String createPrompt,
    @Comment("Shown when the player types 'cancel' (or its alias) to abort the create flow.")
        String createCancelled,
    @Comment("Shown when the create input window expires. Placeholders: {seconds}.")
        String createTimeout,
    @Comment(
            "Shown when the chosen name is already used by an existing home. Placeholders: {name}.")
        String createAlreadyExists) {

  public static HomesMessages defaults() {
    return new HomesMessages(
        "<green>Home <gold>{name}</gold> set.",
        "<red>You have reached your home limit (<gold>{limit}</gold>). "
            + "Couldn't create <gold>{name}</gold>. Delete one from the menu to free a slot.",
        "<red>You don't have any home named <gold>{name}</gold>.",
        "<red>You don't have any homes yet. Open <gold>/homes</gold> and click the + button.",
        "<yellow>Home <gold>{name}</gold> removed.",
        "<red>The world of this home is not loaded.",
        "<yellow>Teleporting to <gold>{name}</gold> in <gold>{seconds}s</gold>. "
            + "Don't take damage.",
        "<red><u>[Cancel]</u>",
        "<gray>Click to cancel the teleport.",
        "<green>You were teleported to <gold>{name}</gold>.",
        "<red>Teleport cancelled.",
        "<red>The teleport could not be completed.",
        "<dark_red>Delete this home?",
        "<red>Delete this home?",
        "<green>Yes, delete",
        "<red>Cancel",
        "<green>Icon of <gold>{name}</gold> changed to <gold>{material}</gold>.",
        "<yellow>Type the new name for <gold>{name}</gold> in chat "
            + "(or <gold>cancel</gold>). You have <gold>{seconds}s</gold>.",
        "<yellow>Rename cancelled.",
        "<red>The rename window (<gold>{seconds}s</gold>) expired.",
        "<red>Invalid name. Use 1-32 characters: letters, digits, _ or -.",
        "<red>You already have a home named <gold>{name}</gold>.",
        "<red>Home <gold>{name}</gold> disappeared before the rename completed.",
        "<green>Home <gold>{old}</gold> renamed to <gold>{new}</gold>.",
        "<yellow>Type the name for your new home in chat "
            + "(or <gold>cancel</gold>). You have <gold>{seconds}s</gold>.",
        "<yellow>Create cancelled.",
        "<red>The create window (<gold>{seconds}s</gold>) expired.",
        "<red>You already have a home named <gold>{name}</gold>. Pick a different name.");
  }

  public String invalidName() {
    return renameInvalid;
  }
}
