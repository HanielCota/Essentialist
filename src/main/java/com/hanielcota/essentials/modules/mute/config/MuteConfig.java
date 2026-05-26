package com.hanielcota.essentials.modules.mute.config;

import java.util.List;
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
    @Comment(
            "Commands a muted player cannot run (names without the leading slash). Matched case-"
                + "insensitively against the first token, with any namespace prefix stripped (so "
                + "\"me\" also catches \"minecraft:me\").")
        List<String> blockedCommands) {

  public static MuteConfig defaults() {
    return new MuteConfig(
        "<green>Você silenciou <gold>{player}</gold> permanentemente.",
        "<green>Você silenciou <gold>{player}</gold> por <gold>{time}</gold>.",
        "<red>Você foi silenciado permanentemente.",
        "<red>Você foi silenciado por <gold>{time}</gold>.",
        "<green>Você removeu o silêncio de <gold>{player}</gold>.",
        "<green>O seu silêncio foi removido.",
        "<red>Você está silenciado permanentemente.",
        "<red>Você está silenciado. Tempo restante: <gold>{time}</gold>.",
        "<red>Você não pode silenciar a si mesmo.",
        "<red><gold>{player}</gold> não está silenciado.",
        "<red><gold>{player}</gold> não pode ser silenciado.",
        "<red>Duração inválida: <gold>{duration}</gold>.",
        List.of("me", "tell", "w", "whisper", "msg", "say", "r", "reply"));
  }

  public boolean isBlockedCommand(@NonNull String commandName) {
    for (var blocked : this.blockedCommands) {
      if (blocked.equalsIgnoreCase(commandName)) {
        return true;
      }
    }
    return false;
  }

  public String formatMutedSender(@NonNull String player) {
    return mutedSender.replace("{player}", player);
  }

  public String formatMutedSenderTimed(@NonNull String player, @NonNull String time) {
    var withPlayer = mutedSenderTimed.replace("{player}", player);

    return withPlayer.replace("{time}", time);
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
