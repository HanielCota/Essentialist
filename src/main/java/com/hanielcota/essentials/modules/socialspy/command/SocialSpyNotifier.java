package com.hanielcota.essentials.modules.socialspy.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.socialspy.config.SocialSpyConfig;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Picks the correct toggle line for {@code /socialspy}: caller-vs-other on top of enabled-vs-
 * disabled. Sends it back to the actor.
 */
@RequiredArgsConstructor
public final class SocialSpyNotifier {

  private final ConfigHandle<SocialSpyConfig> config;

  public void sendToggle(
      @NonNull CommandActor sender, boolean enabled, boolean self, @NonNull String subjectName) {
    var snap = this.config.value();
    var msg = render(snap, enabled, self, subjectName);

    sender.sendMessage(msg);
  }

  private static String render(
      @NonNull SocialSpyConfig snap, boolean enabled, boolean self, @NonNull String subjectName) {
    if (self) {
      return enabled ? snap.enabled() : snap.disabled();
    }

    return enabled ? snap.formatEnabledOther(subjectName) : snap.formatDisabledOther(subjectName);
  }
}
