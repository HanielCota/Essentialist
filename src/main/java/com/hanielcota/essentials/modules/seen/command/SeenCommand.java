package com.hanielcota.essentials.modules.seen.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.seen.config.SeenConfig;
import com.hanielcota.essentials.modules.seen.service.SeenService;
import com.hanielcota.essentials.util.DurationFormatter;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@Command("seen")
@Permission("essentials.seen")
@Cooldown(duration = "1s")
@Description("Mostra a última vez em que o jogador esteve online.")
@Syntax("/seen <jogador>")
public record SeenCommand(ConfigHandle<SeenConfig> config, SeenService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @Arg("jogador") String jogador) {
    var snap = this.config.value();
    var query = jogador.strip();
    var target = this.service.findPlayer(query).orElse(null);

    if (target == null) {
      var neverMsg = snap.formatNeverSeen(query);
      sender.sendError(neverMsg);
      return;
    }

    var displayName = resolveName(target, query);
    var now = Instant.now();
    var line = renderLine(snap, target, displayName, now);

    sender.sendMessage(line);
  }

  private static String renderLine(
      @NonNull SeenConfig snap,
      @NonNull OfflinePlayer target,
      @NonNull String displayName,
      @NonNull Instant now) {
    if (target.isOnline()) {
      var loginMillis = target.getLastLogin();
      var duration = sinceMillis(loginMillis, now);

      return snap.formatOnline(displayName, duration);
    }

    var seenMillis = target.getLastSeen();
    var duration = sinceMillis(seenMillis, now);

    return snap.formatOffline(displayName, duration);
  }

  private static String sinceMillis(long sourceMillis, @NonNull Instant now) {
    if (sourceMillis <= 0L) {
      return DurationFormatter.format(Duration.ZERO);
    }

    var source = Instant.ofEpochMilli(sourceMillis);
    var elapsed = Duration.between(source, now);

    return DurationFormatter.format(elapsed);
  }

  private static String resolveName(@NonNull OfflinePlayer target, @NonNull String fallback) {
    var stored = target.getName();

    return stored != null ? stored : fallback;
  }
}
