package com.hanielcota.essentials.modules.ban.config;

import com.hanielcota.essentials.shared.Placeholders;
import java.util.List;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record BanConfig(
    @Comment("Broadcast after a permanent ban. Placeholders: {player}, {issuer}, {reason}.")
        String banBroadcast,
    @Comment("Broadcast after a timed ban. Placeholders: {player}, {issuer}, {reason}, {time}.")
        String banBroadcastTimed,
    @Comment("Broadcast after an unban. Placeholders: {player}, {issuer}.") String unbanBroadcast,
    @Comment("Login-deny screen for a permanent ban. Placeholders: {reason}, {issuer}.")
        String kickPermanent,
    @Comment("Login-deny screen for a timed ban. Placeholders: {reason}, {issuer}, {time}.")
        String kickTimed,
    @Comment("Shown to staff after a ban. Placeholder: {player}.") String staffBanned,
    @Comment("Shown to staff after an unban. Placeholder: {player}.") String staffUnbanned,
    @Comment("Shown when the target is already banned. Placeholder: {player}.")
        String alreadyBanned,
    @Comment("Shown when /unban targets a player who isn't banned. Placeholder: {player}.")
        String notBanned,
    @Comment("Shown when the issuer tries to ban themselves.") String cannotBanSelf,
    @Comment("Shown when the target carries essentials.ban.exempt. Placeholder: {player}.")
        String exempt,
    @Comment("Shown when the console runs /ban (the menu needs a player).") String menuPlayerOnly,
    @Comment("Chat prompt asking the staff member to type a name.") String nickPrompt,
    @Comment("Shown when the staff member cancels the nick search.") String nickCancelled,
    @Comment("Shown when no player matches the typed name. Placeholder: {input}.")
        String nickUnknown,
    @Comment("Shown when confirm is clicked with no reason selected.") String selectReasonFirst,
    @Comment("Label used for a permanent ban in messages and menus.") String permanentLabel,
    BanMenuConfig menu,
    List<BanReasonOption> reasons,
    List<BanDurationOption> durations) {

  public static BanConfig defaults() {
    return new BanConfig(
        "<red><gold>{player}</gold> was banned by <gold>{issuer}</gold>. Reason:"
            + " <gold>{reason}</gold>.",
        "<red><gold>{player}</gold> was banned by <gold>{issuer}</gold> for <gold>{time}</gold>."
            + " Reason: <gold>{reason}</gold>.",
        "<green><gold>{player}</gold> was unbanned by <gold>{issuer}</gold>.",
        "<red>You are banned from this server.<newline><gray>Reason:"
            + " <white>{reason}<newline><gray>By: <white>{issuer}",
        "<red>You are banned from this server.<newline><gray>Reason:"
            + " <white>{reason}<newline><gray>Expires in: <white>{time}<newline><gray>By:"
            + " <white>{issuer}",
        "<green>Banned <gold>{player}</gold>.",
        "<green>Unbanned <gold>{player}</gold>.",
        "<red><gold>{player}</gold> is already banned.",
        "<red><gold>{player}</gold> is not banned.",
        "<red>You cannot ban yourself.",
        "<red><gold>{player}</gold> cannot be banned.",
        "<red>The ban menu can only be opened by players.",
        "<yellow>Type the player's name in chat, or <gold>cancel</gold> to abort.",
        "<gray>Ban search cancelled.",
        "<red>No player named <gold>{input}</gold> was found.",
        "<red>Select a reason before confirming.",
        "permanent",
        BanMenuConfig.defaults(),
        BanReasonOption.defaults(),
        BanDurationOption.defaults());
  }

  public String formatBanBroadcast(
      @NonNull String player, @NonNull String issuer, @NonNull String reason) {
    return Placeholders.format(banBroadcast, "player", player, "issuer", issuer, "reason", reason);
  }

  public String formatBanBroadcastTimed(
      @NonNull String player,
      @NonNull String issuer,
      @NonNull String reason,
      @NonNull String time) {
    return Placeholders.format(
        banBroadcastTimed, "player", player, "issuer", issuer, "reason", reason, "time", time);
  }

  public String formatUnbanBroadcast(@NonNull String player, @NonNull String issuer) {
    return Placeholders.format(unbanBroadcast, "player", player, "issuer", issuer);
  }

  public String formatKickPermanent(@NonNull String reason, @NonNull String issuer) {
    return Placeholders.format(kickPermanent, "reason", reason, "issuer", issuer);
  }

  public String formatKickTimed(
      @NonNull String reason, @NonNull String issuer, @NonNull String time) {
    return Placeholders.format(kickTimed, "reason", reason, "issuer", issuer, "time", time);
  }

  public String formatStaffBanned(@NonNull String player) {
    return staffBanned.replace("{player}", player);
  }

  public String formatStaffUnbanned(@NonNull String player) {
    return staffUnbanned.replace("{player}", player);
  }

  public String formatAlreadyBanned(@NonNull String player) {
    return alreadyBanned.replace("{player}", player);
  }

  public String formatNotBanned(@NonNull String player) {
    return notBanned.replace("{player}", player);
  }

  public String formatExempt(@NonNull String player) {
    return exempt.replace("{player}", player);
  }

  public String formatNickUnknown(@NonNull String input) {
    return nickUnknown.replace("{input}", input);
  }
}
