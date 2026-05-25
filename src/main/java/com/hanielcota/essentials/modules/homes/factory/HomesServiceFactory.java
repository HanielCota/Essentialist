package com.hanielcota.essentials.modules.homes.factory;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.listener.HomesCacheListener;
import com.hanielcota.essentials.modules.homes.repository.CachedHomeRepository;
import com.hanielcota.essentials.modules.homes.repository.HomeCache;
import com.hanielcota.essentials.modules.homes.repository.SqlHomeRepository;
import com.hanielcota.essentials.modules.homes.repository.SqlHomeTable;
import com.hanielcota.essentials.modules.homes.service.HomeLimitResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import java.util.function.IntSupplier;
import lombok.NonNull;
import org.bukkit.event.Listener;

public final class HomesServiceFactory {

  public HomesServiceComponents create(
      @NonNull ConfigHandle<HomesConfig> config, @NonNull SqlExecutor executor) {

    SqlHomeTable.install(executor);

    var sqlRepository = new SqlHomeRepository(executor);
    var cache = new HomeCache();

    var asyncWriter = new DefaultAsyncDatabaseWriter("Essentialist-Homes");
    var repository = new CachedHomeRepository(sqlRepository, asyncWriter, cache);

    IntSupplier defaultLimit = () -> config.value().defaultLimit();
    var limits = new HomeLimitResolver(defaultLimit);

    var service = new HomeService(repository, limits);
    var cacheListener = new HomesCacheListener(repository);

    return new HomesServiceComponents(service, repository, cacheListener);
  }

  public record HomesServiceComponents(
      HomeService service, AutoCloseable closeable, Listener cacheListener) {}
}
