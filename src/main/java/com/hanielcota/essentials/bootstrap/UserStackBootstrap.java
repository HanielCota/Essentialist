package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.user.DefaultUserSessionService;
import com.hanielcota.essentials.user.UserSessionService;
import com.hanielcota.essentials.user.listener.UserSessionListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class UserStackBootstrap implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "user-stack";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var sessions = new DefaultUserSessionService();
    context.services().register(UserSessionService.class, sessions);

    var server = this.plugin.getServer();
    var pluginManager = server.getPluginManager();
    var sessionListener = new UserSessionListener(sessions);

    pluginManager.registerEvents(sessionListener, this.plugin);
  }
}
