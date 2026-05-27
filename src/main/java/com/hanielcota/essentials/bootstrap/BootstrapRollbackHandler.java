package com.hanielcota.essentials.bootstrap;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.database.connection.DatabaseProvider;
import java.util.function.Consumer;

/**
 * Tears down already-registered infrastructure when a bootstrap stage fails. Extracted from {@link
 * EssentialsBootstrap} so rollback knowledge of concrete service types lives in one place.
 */
final class BootstrapRollbackHandler {

  private BootstrapRollbackHandler() {}

  static void rollback(StageContext context, RuntimeException cause) {
    var services = context.services();
    var core = services.find(EssentialsCore.class).orElse(null);

    if (core != null) {
      suppressAndRun(cause, core::shutdown);
      return;
    }

    var databaseHandle = services.find(DatabaseProvider.class);
    Consumer<DatabaseProvider> closeDatabase = db -> suppressAndRun(cause, db::close);
    databaseHandle.ifPresent(closeDatabase);

    var menuHandle = services.find(MenuService.class);
    Consumer<MenuService> shutdownMenu = menu -> suppressAndRun(cause, menu::shutdown);
    menuHandle.ifPresent(shutdownMenu);
  }

  private static void suppressAndRun(RuntimeException primary, Runnable cleanup) {
    try {
      cleanup.run();
    } catch (RuntimeException e) {
      primary.addSuppressed(e);
    }
  }
}
