package com.hanielcota.essentials.modules.socialspy.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.socialspy.config.SocialSpyConfig;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("socialspy")
@Permission("essentials.socialspy")
@PermissionForOther(".others")
@Cooldown(duration = "2s")
@Description("Ativa ou desativa a observação de mensagens privadas.")
@Syntax("/socialspy [jogador]")
public record SocialSpyCommand(ConfigHandle<SocialSpyConfig> config, SocialSpyService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var subjectId = subject.getUniqueId();
    var subjectName = subject.getName();
    var self = Senders.isSelf(sender, subject);

    var enabled = this.service.enter(subjectId);
    if (!enabled) {
      this.service.exit(subjectId);
    }

    var msg = renderToggle(snap, enabled, self, subjectName);

    sender.sendMessage(msg);
  }

  private static String renderToggle(
      @NonNull SocialSpyConfig snap, boolean enabled, boolean self, @NonNull String subjectName) {
    if (self) {
      return enabled ? snap.enabled() : snap.disabled();
    }

    return enabled ? snap.formatEnabledOther(subjectName) : snap.formatDisabledOther(subjectName);
  }
}
