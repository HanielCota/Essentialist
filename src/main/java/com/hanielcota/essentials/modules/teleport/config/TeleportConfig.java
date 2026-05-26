package com.hanielcota.essentials.modules.teleport.config;

import com.hanielcota.essentials.shared.Numbers;
import com.hanielcota.essentials.shared.Placeholders;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TeleportConfig(
    @Comment("/tp <player> sender message. Placeholders: {player}.") String toPlayer,
    @Comment("/tp <player> destination notification. Placeholders: {player}.") String teleportedTo,
    @Comment("Self-target error.") String selfTarget,
    @Comment("/tp move <from> <to> sender message. Placeholders: {from}, {to}.") String moveSender,
    @Comment("/tp move <from> <to> notification to the moved player. Placeholders: {sender}.")
        String moveNotify,
    @Comment("/tp pos <x> <y> <z> sender message. Placeholders: {x}, {y}, {z}.") String toPos,
    @Comment("/tphere <player> sender message. Placeholders: {player}.") String broughtPlayer,
    @Comment("/tphere <player> notification to the moved player. Placeholders: {sender}.")
        String broughtBy,
    @Comment("Shown when a teleport could not be completed.") String teleportFailed,
    @Comment("Shown by /tp when coordinates are invalid or outside the world limits.")
        String invalidPosition,
    @Comment("Shown by /tp when the named player is not online. Placeholders: {player}.")
        String playerNotFound,
    @Comment("Shown when /tp is invoked from the console without target arguments.")
        String mustBePlayer,
    @Comment("Shown when /tp <from> <to> is used without the .others permission.")
        String noPermissionOthers,
    @Comment("Shown by /tpcancel when the player has no warm-up teleport in progress.")
        String cancelNoPending) {

  public static TeleportConfig defaults() {
    return new TeleportConfig(
        "<green>Teleported to <gold>{player}</gold>.",
        "<yellow><gold>{player}</gold> teleported to you.",
        "<red>You are already there.",
        "<green>Teleported <gold>{from}</gold> to <gold>{to}</gold>.",
        "<yellow>You were teleported by <gold>{sender}</gold>.",
        "<green>Teleported to <gold>{x}, {y}, {z}</gold>.",
        "<green>Brought <gold>{player}</gold> to you.",
        "<yellow>Teleported to <gold>{sender}</gold>.",
        "<red>The teleport could not be completed.",
        "<red>Those coordinates are outside the world limits.",
        "<red>Player <gold>{player}</gold> is not online.",
        "<red>Only players can teleport. Use /tp <from> <to> from the console.",
        "<red>You don't have permission to teleport other players.",
        "<red>You don't have any teleport in progress.");
  }

  public String formatToPlayer(@NonNull String player) {
    return toPlayer.replace("{player}", player);
  }

  public String formatTeleportedTo(@NonNull String player) {
    return teleportedTo.replace("{player}", player);
  }

  public String formatMoveSender(@NonNull String from, @NonNull String to) {
    return Placeholders.format(moveSender, "from", from, "to", to);
  }

  public String formatMoveNotify(@NonNull String sender) {
    return moveNotify.replace("{sender}", sender);
  }

  public String formatToPos(double x, double y, double z) {
    var compactX = Numbers.display(x);
    var compactY = Numbers.display(y);
    var compactZ = Numbers.display(z);

    return Placeholders.format(toPos, "x", compactX, "y", compactY, "z", compactZ);
  }

  public String formatBroughtPlayer(@NonNull String player) {
    return broughtPlayer.replace("{player}", player);
  }

  public String formatBroughtBy(@NonNull String sender) {
    return broughtBy.replace("{sender}", sender);
  }

  public String formatPlayerNotFound(@NonNull String player) {
    return playerNotFound.replace("{player}", player);
  }
}
