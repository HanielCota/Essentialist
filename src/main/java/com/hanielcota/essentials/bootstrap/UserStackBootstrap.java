package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.core.ShutdownRegistry;
import com.hanielcota.essentials.core.ShutdownStep;
import com.hanielcota.essentials.user.DefaultUserSessionService;
import com.hanielcota.essentials.user.UserSessionService;
import com.hanielcota.essentials.user.listener.UserSessionListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
final class UserStackBootstrap implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "user-stack";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var services = context.services();

    var sessions = new DefaultUserSessionService();
    services.register(UserSessionService.class, sessions);

    var server = this.plugin.getServer();
    var pluginManager = server.getPluginManager();
    var sessionListener = new UserSessionListener(sessions);

    pluginManager.registerEvents(sessionListener, this.plugin);

    // Explicit teardown so an in-process reload (PlugMan / repeated bootstrap in tests) does not
    // leak the listener against a stale plugin + service instance, matching the rest of bootstrap.
    var shutdownRegistry = services.resolve(ShutdownRegistry.class);
    var teardown =
        ShutdownStep.of("UserSessionListener", () -> HandlerList.unregisterAll(sessionListener));

    shutdownRegistry.register(teardown);
  }
}
