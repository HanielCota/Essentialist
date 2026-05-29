package com.hanielcota.essentials.modules.mute.config;

import com.hanielcota.essentials.shared.Placeholders;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record MuteConfig(
    @Comment("Shown to the issuer after a permanent mute. Placeholder: {player}.")
        String mutedSender,
    @Comment("Shown to the issuer after a timed mute. Placeholders: {player}, {time}.")
        String mutedSenderTimed,
    @Comment("Shown to the target after a permanent mute.") String mutedTarget,
    @Comment("Shown to the target after a timed mute. Placeholder: {time}.")
        String mutedTargetTimed,
    @Comment("Shown to the issuer after a successful /unmute. Placeholder: {player}.")
        String unmutedSender,
    @Comment("Shown to the target after a successful /unmute.") String unmutedTarget,
    @Comment("Cancellation message for a permanently muted player on chat.") String chatBlocked,
    @Comment("Cancellation message for a timed-muted player on chat. Placeholder: {time}.")
        String chatBlockedTimed,
    @Comment("Shown when the issuer tries to mute themselves.") String cannotMuteSelf,
    @Comment("Shown when /unmute is called on a player who isn't muted. Placeholder: {player}.")
        String notMuted,
    @Comment("Shown when the target carries essentials.mute.exempt. Placeholder: {player}.")
        String exempt,
    @Comment("Shown when the duration argument cannot be parsed. Placeholder: {duration}.")
        String invalidDuration,
    MuteBlockedCommands blockedCommands) {

  public static MuteConfig defaults() {
    return new MuteConfig(
        "<green>You muted <gold>{player}</gold> permanently.",
        "<green>You muted <gold>{player}</gold> for <gold>{time}</gold>.",
        "<red>You have been muted permanently.",
        "<red>You have been muted for <gold>{time}</gold>.",
        "<green>You unmuted <gold>{player}</gold>.",
        "<green>You have been unmuted.",
        "<red>You are muted permanently.",
        "<red>You are muted. Time remaining: <gold>{time}</gold>.",
        "<red>You cannot mute yourself.",
        "<red><gold>{player}</gold> is not muted.",
        "<red><gold>{player}</gold> cannot be muted.",
        "<red>Invalid duration: <gold>{duration}</gold>.",
        MuteBlockedCommands.defaults());
  }

  public String formatMutedSender(@NonNull String player) {
    return mutedSender.replace("{player}", player);
  }

  public String formatMutedSenderTimed(@NonNull String player, @NonNull String time) {
    return Placeholders.format(mutedSenderTimed, "player", player, "time", time);
  }

  public String formatMutedTargetTimed(@NonNull String time) {
    return mutedTargetTimed.replace("{time}", time);
  }

  public String formatChatBlockedTimed(@NonNull String time) {
    return chatBlockedTimed.replace("{time}", time);
  }

  public String formatUnmutedSender(@NonNull String player) {
    return unmutedSender.replace("{player}", player);
  }

  public String formatNotMuted(@NonNull String player) {
    return notMuted.replace("{player}", player);
  }

  public String formatExempt(@NonNull String player) {
    return exempt.replace("{player}", player);
  }

  public String formatInvalidDuration(@NonNull String duration) {
    return invalidDuration.replace("{duration}", duration);
  }
}
