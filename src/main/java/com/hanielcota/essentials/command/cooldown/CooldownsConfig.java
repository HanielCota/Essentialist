package com.hanielcota.essentials.command.cooldown;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Central, admin-tunable command cooldowns. Replaces the per-command compile-time {@code @Cooldown}
 * annotation. Each command is keyed by its root name (the {@code @Command} value); commands absent
 * from {@link #commands} fall back to {@link #defaultSeconds}. A value of 0 disables the cooldown.
 */
@ConfigSerializable
public record CooldownsConfig(
    @Comment("Cooldown in seconds for any command not listed below. 0 disables it.")
        int defaultSeconds,
    @Comment("Shown while a command is on cooldown. Placeholder: {seconds}.") String message,
    @Comment("Per-command cooldown in seconds, keyed by the root command name. 0 disables it.")
        Map<String, Integer> commands) {

  private static final String[] ONE_SECOND = {
    "r",
    "realname",
    "seen",
    "setspawn",
    "tpablock",
    "tpacancel",
    "tpaccept",
    "tpaunblock",
    "tpcancel",
    "tpdeny",
    "vanish",
    "afk",
    "broadcast",
    "msg"
  };

  private static final String[] TWO_SECONDS = {
    "bigorna",
    "cortador",
    "delwarp",
    "home",
    "rebolo",
    "setwarp",
    "socialspy",
    "spawn",
    "tear",
    "warp",
    "list",
    "cartografia",
    "forjamento",
    "bancada"
  };

  private static final String[] FIVE_SECONDS = {
    "alimentar",
    "back",
    "compactar",
    "curar",
    "derreter",
    "fly",
    "limpar",
    "luz",
    "matar",
    "reparar",
    "tpa",
    "tpahere"
  };

  public static CooldownsConfig defaults() {
    var commands = new LinkedHashMap<String, Integer>();

    putAll(commands, ONE_SECOND, 1);
    putAll(commands, TWO_SECONDS, 2);
    putAll(commands, FIVE_SECONDS, 5);

    var message = "<red>Aguarde <gold>{seconds}s</gold> antes de usar este comando novamente.";

    return new CooldownsConfig(3, message, commands);
  }

  private static void putAll(
      @NonNull Map<String, Integer> target, @NonNull String[] commands, int seconds) {
    for (var command : commands) {
      target.put(command, seconds);
    }
  }

  public int secondsFor(@NonNull String command) {
    return this.commands.getOrDefault(command, this.defaultSeconds);
  }

  public String formatMessage(long seconds) {
    var secondsText = Long.toString(seconds);
    return this.message.replace("{seconds}", secondsText);
  }
}
