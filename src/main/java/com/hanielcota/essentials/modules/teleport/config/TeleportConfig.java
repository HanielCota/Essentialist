package com.hanielcota.essentials.modules.teleport.config;

import com.hanielcota.essentials.util.Numbers;
import com.hanielcota.essentials.util.Placeholders;
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
    @Comment("Shown by /tp pos when coordinates are outside the world limits.")
        String invalidPosition) {

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
        "<red>Those coordinates are outside the world limits.");
  }

  public String formatToPlayer(String player) {
    return toPlayer.replace("{player}", player);
  }

  public String formatTeleportedTo(String player) {
    return teleportedTo.replace("{player}", player);
  }

  public String formatMoveSender(String from, String to) {
    return Placeholders.format(moveSender, "from", from, "to", to);
  }

  public String formatMoveNotify(String sender) {
    return moveNotify.replace("{sender}", sender);
  }

  public String formatToPos(double x, double y, double z) {
    return Placeholders.format(
        toPos, "x", Numbers.compact(x), "y", Numbers.compact(y), "z", Numbers.compact(z));
  }

  public String formatBroughtPlayer(String player) {
    return broughtPlayer.replace("{player}", player);
  }

  public String formatBroughtBy(String sender) {
    return broughtBy.replace("{sender}", sender);
  }
}
