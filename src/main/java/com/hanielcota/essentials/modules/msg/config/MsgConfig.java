package com.hanielcota.essentials.modules.msg.config;

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
        "<gray>[<gold>You<gray> -> <gold>{target}<gray>] <white>{message}",
        "<gray>[<gold>{sender}<gray> -> <gold>You<gray>] <white>{message}",
        "<red>Please provide a message.",
        "<red>You cannot send a message to yourself.",
        "<red>The player <gold>{player}</gold> is not online.",
        "<red>You haven't talked to anyone yet.",
        "<red>The player <gold>{player}</gold> is no longer online.");
  }
}
