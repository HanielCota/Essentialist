package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record AntiSpamConfig(
    @Comment(
            "Block sending the exact same message twice in a row. Stripped, case-sensitive"
                + " comparison; whitespace and case both matter. Players with chat.bypassantispam"
                + " skip this check.")
        boolean blockRepeated,
    @Comment(
            "Shown when a channel cooldown blocks the message. Placeholder: {seconds} — the"
                + " whole number of seconds (rounded up) the sender must still wait.")
        String cooldownWarning,
    @Comment("Shown when blockRepeated cancels a duplicate message. Empty drops silently.")
        String repeatedWarning) {

  public static AntiSpamConfig defaults() {
    return new AntiSpamConfig(
        true,
        "<red>Aguarde <gold>{seconds}s</gold> antes de enviar outra mensagem.",
        "<red>Você acabou de enviar essa mensagem.");
  }

  public String formatCooldownWarning(long seconds) {
    var secondsStr = String.valueOf(seconds);

    return cooldownWarning.replace("{seconds}", secondsStr);
  }
}
