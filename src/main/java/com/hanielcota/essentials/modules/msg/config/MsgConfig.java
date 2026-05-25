package com.hanielcota.essentials.modules.msg.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record MsgConfig(
    @Comment("Shown to the sender. Placeholders: {sender}, {target}, {message}.")
        String outgoingFormat,
    @Comment("Shown to the target. Placeholders: {sender}, {target}, {message}.")
        String incomingFormat,
    @Comment("Shown when no message text was provided.") String emptyMessage,
    @Comment("Shown when the sender tries to message themselves.") String cannotMessageSelf,
    @Comment("Shown when the target is offline or hidden. Placeholder: {player}.")
        String targetUnavailable,
    @Comment("Shown when /r is used without a known last partner.") String noReplyPartner,
    @Comment("Shown when the last reply partner went offline. Placeholder: {player}.")
        String replyPartnerUnavailable) {

  public static MsgConfig defaults() {
    return new MsgConfig(
        "<gray>[<gold>Você<gray> -> <gold>{target}<gray>] <white>{message}",
        "<gray>[<gold>{sender}<gray> -> <gold>Você<gray>] <white>{message}",
        "<red>Informe a mensagem.",
        "<red>Você não pode enviar uma mensagem para si mesmo.",
        "<red>O jogador <gold>{player}</gold> não está online.",
        "<red>Você ainda não conversou com ninguém.",
        "<red>O jogador <gold>{player}</gold> não está mais online.");
  }

  public String formatOutgoing(
      @NonNull String sender, @NonNull String target, @NonNull String body) {
    return fill(outgoingFormat, sender, target, body);
  }

  public String formatIncoming(
      @NonNull String sender, @NonNull String target, @NonNull String body) {
    return fill(incomingFormat, sender, target, body);
  }

  public String formatTargetUnavailable(@NonNull String player) {
    return targetUnavailable.replace("{player}", player);
  }

  public String formatReplyPartnerUnavailable(@NonNull String player) {
    return replyPartnerUnavailable.replace("{player}", player);
  }

  private static String fill(
      @NonNull String template,
      @NonNull String sender,
      @NonNull String target,
      @NonNull String body) {
    var withSender = template.replace("{sender}", sender);
    var withTarget = withSender.replace("{target}", target);

    return withTarget.replace("{message}", body);
  }
}
