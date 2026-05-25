package com.hanielcota.essentials.modules.clearchat.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.clearchat.config.ClearChatConfig;
import com.hanielcota.essentials.modules.clearchat.service.ClearChatService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;

@Command("clearchat")
@Permission("essentials.clearchat")
@Cooldown(duration = "3s")
@Description("Limpa o chat de todos os jogadores online.")
@Syntax("/clearchat")
public record ClearChatCommand(ConfigHandle<ClearChatConfig> config, ClearChatService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var actorName = actor.name();
    var announcement = snap.formatAnnouncement(actorName);
    var lines = snap.effectiveLines();

    this.service.clearChat(lines, announcement);

    // Players see the broadcast above; the console does not, so echo it there.
    if (!actor.isPlayer()) {
      actor.sendMessage(announcement);
    }
  }
}
