package com.hanielcota.essentials.modules.actionbar.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ActionBarConfig(
    @Comment("Shown when no message is given.") String usage,
    @Comment("Shown when the console runs /actionbar without the broadcast subcommand.")
        String playerOnly,
    @Comment("Shown after sending an action bar to yourself.") String sent,
    @Comment("Shown after a broadcast. Placeholder: {count}.") String broadcasted) {

  public static ActionBarConfig defaults() {
    return new ActionBarConfig(
        "<red>Provide the action bar message.",
        "<red>Use /actionbar broadcast to send it from the console.",
        "<green>Action bar sent.",
        "<green>Action bar sent to <gold>{count}</gold> player(s).");
  }

  public String formatBroadcasted(int count) {
    return broadcasted.replace("{count}", Integer.toString(count));
  }
}
