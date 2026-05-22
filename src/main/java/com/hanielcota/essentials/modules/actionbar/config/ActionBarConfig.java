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
        "<red>Informe a mensagem da action bar.",
        "<red>Use /actionbar broadcast para enviar pelo console.",
        "<green>Action bar enviada.",
        "<green>Action bar enviada para <gold>{count}</gold> jogador(es).");
  }

  public String formatBroadcasted(int count) {
    return broadcasted.replace("{count}", Integer.toString(count));
  }
}
