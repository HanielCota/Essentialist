package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.user.DefaultUserSessionService;
import com.hanielcota.essentials.user.UserSessionService;
import com.hanielcota.essentials.user.listener.UserSessionListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class UserStackBootstrap {

  private final EssentialsPlugin plugin;

  void register(@NonNull ServiceRegistry services) {
    var sessions = new DefaultUserSessionService();
    services.register(UserSessionService.class, sessions);

    var server = this.plugin.getServer();
    var pluginManager = server.getPluginManager();
    var sessionListener = new UserSessionListener(sessions);

    pluginManager.registerEvents(sessionListener, this.plugin);
  }
}
