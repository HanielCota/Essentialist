package com.hanielcota.essentials.modules.title.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TitleConfig(
    @Comment("Title fade-in time in ticks (20 ticks = 1 second).") int fadeInTicks,
    @Comment("Title stay time in ticks (20 ticks = 1 second).") int stayTicks,
    @Comment("Title fade-out time in ticks (20 ticks = 1 second).") int fadeOutTicks,
    @Comment("Shown to the sender after sending a title to themselves.") String sent,
    @Comment("Shown to the sender after sending a title to another player. Placeholders: {player}.")
        String sentOther,
    @Comment("Shown to the sender after /title broadcast. Placeholders: {count}.")
        String broadcasted,
    @Comment("Shown when targeting another player without essentials.title.others.")
        String noPermissionOther,
    @Comment("Shown when /title is used without any text.") String usage,
    @Comment(
            "Shown when the named target disconnected between parsing the command and dispatching"
                + " the title. Placeholders: {player}.")
        String targetOffline) {

  public static TitleConfig defaults() {
    return new TitleConfig(
        10,
        70,
        20,
        "<green>Title sent.",
        "<green>Title sent to <gold>{player}</gold>.",
        "<green>Title sent to <gold>{count}</gold> player(s).",
        "<red>You don't have permission to send titles to other players.",
        """
        <yellow>Usage: <gray>/title [player] "title" "subtitle"</gray> — the subtitle is optional.\
        """,
        "<red>The player <gold>{player}</gold> is no longer online.");
  }

  public MessagePair whenSent() {
    return new MessagePair(sent, sentOther);
  }

  public String formatBroadcasted(int count) {
    var countStr = Integer.toString(count);
    return broadcasted.replace("{count}", countStr);
  }

  public String formatTargetOffline(String playerName) {
    return targetOffline.replace("{player}", playerName);
  }
}
