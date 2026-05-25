package com.hanielcota.essentials.modules.homes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class CachedHomeRepositoryTest {

  private static Home home(UUID owner, String name) {
    return new Home(owner, name, "world", 1, 2, 3, 0, 0, Material.RED_BED, 1);
  }

  private static List<String> names(List<Home> homes) {
    return homes.stream().map(Home::name).toList();
  }

  @Test
  void loadForPullsFromDelegateAndPopulatesCache() {
    var owner = UUID.randomUUID();
    var delegate = new RecordingHomeRepository(home(owner, "base"));
    var writer = new RecordingWriter();
    var repository = new CachedHomeRepository(delegate, writer, new HomeCache());

    repository.loadFor(owner);

    assertEquals(1, delegate.listCalls);
    assertTrue(repository.find(owner, "BASE").isPresent());
    assertEquals(1, repository.count(owner));
    assertEquals(List.of("base"), names(repository.list(owner)));
    assertEquals(0, delegate.findCalls + delegate.countCalls);
  }

  @Test
  void servesReadsFromMemoryAndQueuesWrites() {
    var owner = UUID.randomUUID();
    var delegate = new RecordingHomeRepository(home(owner, "base"));
    var writer = new RecordingWriter();
    var cache = new HomeCache();
    cache.loadFor(owner, List.of(home(owner, "base")));
    var repository = new CachedHomeRepository(delegate, writer, cache);

    repository.save(home(owner, "mine"));

    assertTrue(repository.find(owner, "mine").isPresent());
    assertEquals(1, writer.pending());
    assertEquals(0, delegate.saveCalls);

    writer.runAll();

    assertEquals(1, delegate.saveCalls);
  }

  @Test
  void renameAndDeleteChangeCacheBeforeQueuedWriteRuns() {
    var owner = UUID.randomUUID();
    var delegate = new RecordingHomeRepository(home(owner, "base"));
    var writer = new RecordingWriter();
    var cache = new HomeCache();
    cache.loadFor(owner, List.of(home(owner, "base")));
    var repository = new CachedHomeRepository(delegate, writer, cache);

    assertTrue(repository.rename(owner, "BASE", "main"));
    assertFalse(repository.find(owner, "base").isPresent());
    assertTrue(repository.find(owner, "MAIN").isPresent());

    assertTrue(repository.delete(owner, "main"));
    assertEquals(0, repository.count(owner));
    assertEquals(2, writer.pending());

    writer.runAll();

    assertEquals(1, delegate.renameCalls);
    assertEquals(1, delegate.deleteCalls);
  }

  @Test
  void evictForDropsCacheImmediatelyWithoutBlockingPendingWrites() {
    var owner = UUID.randomUUID();
    var delegate = new RecordingHomeRepository(home(owner, "base"));
    var writer = new RecordingWriter();
    var cache = new HomeCache();
    cache.loadFor(owner, List.of(home(owner, "base")));
    var repository = new CachedHomeRepository(delegate, writer, cache);

    repository.save(home(owner, "second"));
    repository.evictFor(owner);

    assertFalse(repository.find(owner, "second").isPresent());
    assertEquals(1, writer.pending());

    writer.runAll();

    assertEquals(1, delegate.saveCalls);
  }

  @Test
  void quickReconnectAfterEvictKeepsFreshlyLoadedBucket() {
    var owner = UUID.randomUUID();
    var delegate = new RecordingHomeRepository(home(owner, "base"));
    var writer = new RecordingWriter();
    var cache = new HomeCache();
    cache.loadFor(owner, List.of(home(owner, "base")));
    var repository = new CachedHomeRepository(delegate, writer, cache);

    // Quit -> evict, then immediate reconnect repopulates the bucket. The previous bug deferred
    // eviction through the writer queue: the deferred eviction would fire after loadFor and
    // wipe the fresh bucket. We assert the fresh bucket survives.
    repository.evictFor(owner);
    repository.loadFor(owner);
    writer.runAll();

    assertTrue(repository.find(owner, "base").isPresent());
    assertEquals(1, repository.count(owner));
  }

  private static final class RecordingWriter implements AsyncDatabaseWriter {

    private final Queue<Runnable> tasks = new ArrayDeque<>();

    @Override
    public java.util.concurrent.CompletableFuture<Void> submit(String operation, Runnable work) {
      tasks.add(work);
      return java.util.concurrent.CompletableFuture.completedFuture(null);
    }

    int pending() {
      return tasks.size();
    }

    void runAll() {
      while (!tasks.isEmpty()) {
        tasks.remove().run();
      }
    }

    @Override
    public void close() {}
  }

  private static final class RecordingHomeRepository implements HomeRepository {

    private final List<Home> homes = new ArrayList<>();
    private int findCalls;
    private int listCalls;
    private int countCalls;
    private int saveCalls;
    private int renameCalls;
    private int deleteCalls;

    private RecordingHomeRepository(Home... homes) {
      this.homes.addAll(List.of(homes));
    }

    @Override
    public Optional<Home> find(UUID owner, String name) {
      findCalls++;
      return Optional.empty();
    }

    @Override
    public List<Home> list(UUID owner) {
      listCalls++;
      return List.copyOf(this.homes);
    }

    @Override
    public int count(UUID owner) {
      countCalls++;
      return 0;
    }

    @Override
    public void save(Home home) {
      saveCalls++;
    }

    @Override
    public boolean delete(UUID owner, String name) {
      deleteCalls++;
      return true;
    }

    @Override
    public boolean rename(UUID owner, String oldName, String newName) {
      renameCalls++;
      return true;
    }

    @Override
    public boolean updateMaterial(UUID owner, String name, Material material) {
      return true;
    }
  }
}
