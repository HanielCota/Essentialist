package com.hanielcota.essentials.modules.kit;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.kit.command.KitClaimNotifier;
import com.hanielcota.essentials.modules.kit.command.KitCommand;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.listener.FirstJoinKitListener;
import com.hanielcota.essentials.modules.kit.listener.KitMenuCleanupListener;
import com.hanielcota.essentials.modules.kit.listener.KitUsageCacheListener;
import com.hanielcota.essentials.modules.kit.menu.KitCategoryClickHandler;
import com.hanielcota.essentials.modules.kit.menu.KitCategoryMenu;
import com.hanielcota.essentials.modules.kit.menu.KitListClickHandler;
import com.hanielcota.essentials.modules.kit.menu.KitListMenu;
import com.hanielcota.essentials.modules.kit.menu.KitMenuState;
import com.hanielcota.essentials.modules.kit.menu.KitPreviewClickHandler;
import com.hanielcota.essentials.modules.kit.menu.KitPreviewMenu;
import com.hanielcota.essentials.modules.kit.menu.presentation.KitEntryRenderer;
import com.hanielcota.essentials.modules.kit.repository.CachedKitUsageRepository;
import com.hanielcota.essentials.modules.kit.repository.KitUsageCache;
import com.hanielcota.essentials.modules.kit.repository.KitUsageTable;
import com.hanielcota.essentials.modules.kit.repository.SqlKitUsageRepository;
import com.hanielcota.essentials.modules.kit.service.KitAdminService;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.modules.kit.service.KitClaimService;
import com.hanielcota.essentials.modules.kit.service.KitConfigStore;
import com.hanielcota.essentials.modules.kit.service.KitCooldownService;
import com.hanielcota.essentials.modules.kit.service.KitGiver;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.NonNull;

/**
 * Menu-driven kits: categories, paginated lists, item preview, claim with cooldown and one-time.
 */
public final class KitModule extends AbstractModule {

  public KitModule() {
    super("kit");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var menus = env.service(MenuService.class);

    var usage = wireUsageStorage(env, registrar);
    var cooldowns = new KitCooldownService(usage, System::currentTimeMillis);

    var definitions = wireDefinitions(env, registrar);
    var store = definitions.store();
    var catalog = definitions.catalog();

    // The store owns kit.yml and is itself the config handle (it both reads and rewrites the file).
    ConfigHandle<KitConfig> config = store;

    var giver = new KitGiver();
    var claimService = new KitClaimService(config, cooldowns, giver);
    var admin = new KitAdminService(store, catalog, usage);

    wireMenus(config, catalog, cooldowns, claimService, registrar);

    registrar.listener(new FirstJoinKitListener(catalog, claimService));
    registrar.command(new KitCommand(config, menus, admin));
  }

  private CachedKitUsageRepository wireUsageStorage(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var sqlExecutor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);

    var table = new KitUsageTable(dialect);
    table.install(sqlExecutor);

    var sqlRepository = new SqlKitUsageRepository(sqlExecutor, table);
    var cache = new KitUsageCache();
    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var asyncWriter = writerFactory.create("Kits");
    var repository = new CachedKitUsageRepository(sqlRepository, asyncWriter, cache);

    registrar.closeable(repository);
    registrar.listener(new KitUsageCacheListener(repository));

    return repository;
  }

  private Definitions wireDefinitions(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    // Single kit.yml under the config dir (CoreServicesBootstrap roots module configs at
    // <dataFolder>/modules). The module owns the file directly because it rewrites it on create.
    var configDir = env.plugin().getDataFolder().toPath().resolve("modules");
    var file = configDir.resolve("kit.yml");

    var ioExecutor = newIoExecutor();
    registrar.closeable(ioExecutor::shutdown);

    var store = new KitConfigStore(file, ioExecutor);
    store.load();

    var catalog = new KitCatalog(store);
    catalog.rebuild();

    return new Definitions(store, catalog);
  }

  private record Definitions(KitConfigStore store, KitCatalog catalog) {}

  private void wireMenus(
      @NonNull ConfigHandle<KitConfig> config,
      @NonNull KitCatalog catalog,
      @NonNull KitCooldownService cooldowns,
      @NonNull KitClaimService claimService,
      @NonNull ModuleRegistrar registrar) {
    var state = new KitMenuState();
    var entryRenderer = new KitEntryRenderer(config, cooldowns);
    var notifier = new KitClaimNotifier(config, cooldowns);

    var categoryClicks = new KitCategoryClickHandler(state);
    var listClicks = new KitListClickHandler(state);
    var previewClicks = new KitPreviewClickHandler(state, catalog, claimService, notifier);

    registrar.menu(new KitCategoryMenu(config, catalog, categoryClicks));
    registrar.menu(new KitListMenu(config, catalog, entryRenderer, state, listClicks));
    registrar.menu(new KitPreviewMenu(config, catalog, state, previewClicks));
    registrar.listener(new KitMenuCleanupListener(state));
  }

  private static ExecutorService newIoExecutor() {
    return Executors.newSingleThreadExecutor(KitModule::ioThread);
  }

  private static Thread ioThread(@NonNull Runnable runnable) {
    var thread = new Thread(runnable, "essentials-kit-io");
    thread.setDaemon(true);

    return thread;
  }
}
