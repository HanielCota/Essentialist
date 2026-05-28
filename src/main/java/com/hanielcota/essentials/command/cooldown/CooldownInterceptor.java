package com.hanielcota.essentials.command.cooldown;

import com.hanielcota.essentials.config.ConfigHandle;
import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
import io.github.hanielcota.commandframework.core.ParsedParameter;
import io.github.hanielcota.commandframework.core.RichCommandInterceptor;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Config-driven command cooldowns, replacing the framework's compile-time {@code @Cooldown}
 * annotation. The duration per command root comes from {@link CooldownsConfig}, so admins can tune
 * it without recompiling. Only players are rate-limited (the console never is), and the cooldown is
 * consumed only after a successful execution.
 */
@RequiredArgsConstructor
public final class CooldownInterceptor implements RichCommandInterceptor {

  private final @NonNull ConfigHandle<CooldownsConfig> config;
  private final @NonNull CommandCooldownService service;

  @Override
  public @NonNull CommandResult before(
      @NonNull CommandContext context, @NonNull List<ParsedParameter<?>> parameters) {
    var actor = context.actor();
    if (!actor.isPlayer()) {
      return CommandResult.success();
    }

    var snap = this.config.value();
    var command = context.route().root();
    var seconds = snap.secondsFor(command);
    if (seconds <= 0) {
      return CommandResult.success();
    }

    var durationMillis = seconds * 1000L;
    var now = System.currentTimeMillis();
    var actorId = actor.uniqueId();
    var remaining = this.service.remainingMillis(actorId, command, durationMillis, now);

    if (remaining <= 0) {
      return CommandResult.success();
    }

    var remainingSeconds = (remaining + 999L) / 1000L;
    var cooldownMsg = snap.formatMessage(remainingSeconds);

    return CommandResult.failure(CommandStatus.COOLDOWN, cooldownMsg);
  }

  @Override
  public @NonNull CommandResult after(
      @NonNull CommandContext context, @NonNull CommandResult result) {
    if (!result.isSuccess()) {
      return result;
    }

    var actor = context.actor();
    if (!actor.isPlayer()) {
      return result;
    }

    var snap = this.config.value();
    var command = context.route().root();
    if (snap.secondsFor(command) <= 0) {
      return result;
    }

    var now = System.currentTimeMillis();
    var actorId = actor.uniqueId();
    this.service.record(actorId, command, now);

    return result;
  }
}
