package com.hanielcota.essentials.modules.tpa.bootstrap;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.SqliteTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryTable;
import com.hanielcota.essentials.modules.tpa.repository.TpaBlockRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaBlockTable;
import com.hanielcota.essentials.modules.tpa.repository.TpaContactRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaContactTable;
import com.hanielcota.essentials.modules.tpa.repository.TpaFavoriteRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaFavoriteTable;
import com.hanielcota.essentials.modules.tpa.repository.TpaProfileRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaProfileTable;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaPersistenceBootstrap {

  private final @NonNull ModuleEnvironment env;
  private final @NonNull ModuleRegistrar registrar;

  public AsyncTpaHistory history() {
    var executor = this.env.service(SqlExecutor.class);
    var dialect = this.env.service(SqlDialect.class);
    var table = new TpaHistoryTable(dialect);
    table.install(executor);

    var sqliteBacked = new SqliteTpaHistory(executor);
    var writerFactory = this.env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("TpaHistory");
    this.registrar.closeable(writer);

    return new AsyncTpaHistory(sqliteBacked, writer);
  }

  public TpaProfileService profiles() {
    var executor = this.env.service(SqlExecutor.class);
    var table = profileTable(executor);
    var repository = new TpaProfileRepository(executor, table);
    var writerFactory = this.env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("TpaProfiles");
    this.registrar.closeable(writer);

    var service = new TpaProfileService(repository, writer);
    var entries = repository.listAll();
    service.loadAll(entries);

    return service;
  }

  public TpaBlockService blocks() {
    var executor = this.env.service(SqlExecutor.class);
    var table = blockTable(executor);
    var repository = new TpaBlockRepository(executor, table);
    var writerFactory = this.env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("TpaBlocks");
    this.registrar.closeable(writer);

    var service = new TpaBlockService(repository, writer);
    var entries = repository.listAll();
    service.loadAll(entries);

    return service;
  }

  public TpaFavoriteService favorites() {
    var executor = this.env.service(SqlExecutor.class);
    var table = favoriteTable(executor);
    var repository = new TpaFavoriteRepository(executor, table);
    var writerFactory = this.env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("TpaFavorites");
    this.registrar.closeable(writer);

    var service = new TpaFavoriteService(repository, writer);
    var entries = repository.listAll();
    service.loadAll(entries);

    return service;
  }

  public TpaContactService contacts() {
    var executor = this.env.service(SqlExecutor.class);
    var table = contactTable(executor);
    var repository = new TpaContactRepository(executor, table);
    var writerFactory = this.env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("TpaContacts");
    this.registrar.closeable(writer);

    var service = new TpaContactService(repository, writer);
    var entries = repository.listAll();
    service.loadAll(entries);

    return service;
  }

  private TpaProfileTable profileTable(@NonNull SqlExecutor executor) {
    var dialect = this.env.service(SqlDialect.class);
    var table = new TpaProfileTable(dialect);
    table.install(executor);

    return table;
  }

  private TpaBlockTable blockTable(@NonNull SqlExecutor executor) {
    var dialect = this.env.service(SqlDialect.class);
    var table = new TpaBlockTable(dialect);
    table.install(executor);

    return table;
  }

  private TpaFavoriteTable favoriteTable(@NonNull SqlExecutor executor) {
    var dialect = this.env.service(SqlDialect.class);
    var table = new TpaFavoriteTable(dialect);
    table.install(executor);

    return table;
  }

  private TpaContactTable contactTable(@NonNull SqlExecutor executor) {
    var dialect = this.env.service(SqlDialect.class);
    var table = new TpaContactTable(dialect);
    table.install(executor);

    return table;
  }
}
