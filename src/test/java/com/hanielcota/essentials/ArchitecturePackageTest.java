package com.hanielcota.essentials;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ArchitecturePackageTest {

  private static final Set<String> DOMAIN_VALUE_TYPES =
      Set.of("SpawnLocation", "PlayerEntry", "Resolved", "SeenLine", "GiveResult");

  private static final Set<String> PERSISTENCE_TYPES =
      Set.of(
          "WarpRepository",
          "SqlWarpRepository",
          "WarpTable",
          "WarpCache",
          "SpawnRepository",
          "SqlSpawnRepository",
          "SpawnTable",
          "MuteRepository",
          "SqlMuteRepository",
          "MuteTable",
          "NickRepository",
          "SqlNickRepository",
          "NickTable",
          "RequestRepository",
          "InMemoryRequestRepository",
          "TpaBlockRepository",
          "SqlTpaBlockRepository",
          "TpaContactRepository",
          "SqlTpaContactRepository",
          "TpaFavoriteRepository",
          "SqlTpaFavoriteRepository",
          "TpaProfileRepository",
          "SqlTpaProfileRepository");

  private static final Set<String> MODULE_ROOT_TYPES =
      Set.of("AbstractModule.java", "Module.java", "ModuleMetadata.java");

  // Pattern: `import com.hanielcota.essentials.modules.<module>.` — captures the imported module.
  private static final Pattern CROSS_MODULE_IMPORT =
      Pattern.compile("^import com\\.hanielcota\\.essentials\\.modules\\.(\\w+)\\.");

  // Matches `implements XxxApi` either unqualified or fully-qualified (or in a comma-separated
  // implements clause). Captures the api type name in group 1.
  private static final Pattern IMPLEMENTS_PUBLIC_API =
      Pattern.compile(
          "implements\\s+(?:[\\w.]+\\.)?(HomesApi|WarpsApi|MutesApi|NicksApi|VanishApi|TeleportsApi)\\b");

  private static final Map<String, Integer> TPA_COMPLEXITY_BUDGETS =
      Map.ofEntries(
          Map.entry("com/hanielcota/essentials/modules/tpa/TpaModule.java", 180),
          Map.entry(
              "com/hanielcota/essentials/modules/tpa/menu/favorites/TpaFavoritesMenu.java", 180),
          Map.entry(
              "com/hanielcota/essentials/modules/tpa/menu/pending/TpaPendingActionMenu.java", 180),
          Map.entry("com/hanielcota/essentials/modules/tpa/menu/TpaHelpMenu.java", 260),
          Map.entry("com/hanielcota/essentials/modules/tpa/menu/pending/TpaPendingMenu.java", 180),
          Map.entry(
              "com/hanielcota/essentials/modules/tpa/service/TeleportRequestService.java", 200),
          Map.entry("com/hanielcota/essentials/modules/tpa/menu/TpaHistoryMenu.java", 250),
          Map.entry(
              "com/hanielcota/essentials/modules/tpa/menu/favorites/TpaFavoriteActionMenu.java",
              240),
          Map.entry("com/hanielcota/essentials/modules/tpa/menu/TpaTargetActionMenu.java", 280),
          Map.entry("com/hanielcota/essentials/modules/tpa/menu/TpaPickPlayerMenu.java", 200),
          Map.entry("com/hanielcota/essentials/modules/tpa/menu/TpaBehaviorSettingsMenu.java", 230),
          Map.entry(
              "com/hanielcota/essentials/modules/tpa/menu/presentation/TpaFavoriteMenuRenderer.java",
              220),
          Map.entry("com/hanielcota/essentials/modules/tpa/menu/TpaProfileMenu.java", 220));

  @Test
  void persistenceTypesDoNotLiveInServicePackages() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isPersistenceTypeInServicePackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(), () -> "Persistence types in service packages: " + violations);
    }
  }

  @Test
  void persistenceNamedTypesDoNotLiveInServicePackages() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isNamedPersistenceTypeInServicePackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(), () -> "Named persistence types in service packages: " + violations);
    }
  }

  @Test
  void domainValueTypesDoNotLiveInServicePackages() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isDomainValueTypeInServicePackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(), () -> "Domain value types in service packages: " + violations);
    }
  }

  @Test
  void repositoriesDoNotImportBukkitApi() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isRepositoryFile)
              .filter(ArchitecturePackageTest::importsBukkitTypeOtherThanMaterialOrLocation)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(),
          () ->
              "Repository classes must not depend on Bukkit beyond Material/Location: "
                  + violations);
    }
  }

  @Test
  void modulesOnlyImportSiblingModulesViaServiceClasses() throws IOException {
    // Allowed cross-module imports: anything in another module's `service`, `domain`,
    // or `history` package (those are the inter-module API surfaces). Importing another module's
    // `command`, `listener`, `menu`, `repository`, `config` is a contract violation.
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isInsideModulesPackage)
              .flatMap(path -> illegalCrossModuleImports(path).stream())
              .toList();

      assertTrue(
          violations.isEmpty(),
          () ->
              "Cross-module imports must go through service/domain/history packages: "
                  + violations);
    }
  }

  @Test
  void onlyCoreApiPackageImplementsPublicApiInterfaces() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::implementsPublicApiFacade)
              .filter(ArchitecturePackageTest::livesOutsideCoreApiOrModuleServicePackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(),
          () ->
              "Only com.hanielcota.essentials.core.api or modules/*/service/ may implement public"
                  + " *Api interfaces: "
                  + violations);
    }
  }

  @Test
  void complexTpaEntryPointsStayWithinSizeBudget() {
    var violations =
        TPA_COMPLEXITY_BUDGETS.entrySet().stream()
            .filter(ArchitecturePackageTest::exceedsLineBudget)
            .map(ArchitecturePackageTest::formatLineBudgetViolation)
            .toList();

    assertTrue(violations.isEmpty(), () -> "Oversized TPA entry points: " + violations);
  }

  @Test
  void tpaBootstrapClassesLiveInBootstrapPackage() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isTpaBootstrapOutsideBootstrapPackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(),
          () -> "TPA bootstrap classes outside bootstrap package: " + violations);
    }
  }

  @Test
  void menuPresentationHelpersLiveInPresentationPackage() throws IOException {
    // For any module that already owns a `menu/presentation/` package, every renderer/browser/
    // stats-formatter under `menu/` must live inside `menu/presentation/`. Modules without an
    // existing `presentation/` (back/list/vanish/whitelist) keep their single EntryRenderer in
    // `menu/` — the sub-package is only justified once rendering helpers multiply.
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isMenuPresentationHelperOutsidePresentationPackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(),
          () -> "Menu presentation helpers outside presentation package: " + violations);
    }
  }

  @Test
  void databaseInfrastructureLivesInNamedSubpackages() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isDatabaseInfrastructureInRootPackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(),
          () -> "Database infrastructure should live in named subpackages: " + violations);
    }
  }

  @Test
  void moduleInternalsLiveInNamedSubpackages() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::isModuleInternalInRootPackage)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(),
          () -> "Module internals should live in named subpackages: " + violations);
    }
  }

  private static Stream<Path> walkMainJava() throws IOException {
    return Files.walk(mainJavaRoot())
        .filter(Files::isRegularFile)
        .filter(path -> path.toString().endsWith(".java"));
  }

  private static boolean isPersistenceTypeInServicePackage(Path path) {
    var typeName = fileName(path).replace(".java", "");

    return relativePath(path).contains("/service/") && PERSISTENCE_TYPES.contains(typeName);
  }

  private static boolean isNamedPersistenceTypeInServicePackage(Path path) {
    var typeName = fileName(path).replace(".java", "");
    var persistenceName =
        typeName.endsWith("Repository") || typeName.endsWith("Table") || typeName.endsWith("Cache");

    return relativePath(path).contains("/service/") && persistenceName;
  }

  private static boolean isDomainValueTypeInServicePackage(Path path) {
    var typeName = fileName(path).replace(".java", "");

    return relativePath(path).contains("/service/") && DOMAIN_VALUE_TYPES.contains(typeName);
  }

  private static boolean isRepositoryFile(Path path) {
    return relativePath(path).contains("/repository/");
  }

  private static boolean importsBukkitTypeOtherThanMaterialOrLocation(Path path) {
    var lines = readLines(path);
    return lines.stream()
        .anyMatch(
            line -> {
              var trimmed = line.trim();
              if (!trimmed.startsWith("import org.bukkit")) {
                return false;
              }
              // Material and Location are stable, narrow value types that the persistence layer
              // legitimately maps. Anything else (Player, World, Server, ...) leaks Bukkit
              // lifecycle into storage.
              return !trimmed.contains("org.bukkit.Material")
                  && !trimmed.contains("org.bukkit.Location");
            });
  }

  private static boolean isInsideModulesPackage(Path path) {
    return relativePath(path).startsWith("com/hanielcota/essentials/modules/");
  }

  private static String moduleOf(Path path) {
    var rel = relativePath(path);
    var afterPrefix = rel.substring("com/hanielcota/essentials/modules/".length());
    var nextSlash = afterPrefix.indexOf('/');
    return nextSlash < 0 ? afterPrefix : afterPrefix.substring(0, nextSlash);
  }

  private static List<String> illegalCrossModuleImports(Path path) {
    var ownModule = moduleOf(path);
    var lines = readLines(path);

    return lines.stream()
        .map(String::trim)
        .filter(
            line -> {
              var matcher = CROSS_MODULE_IMPORT.matcher(line);
              return matcher.find() && !matcher.group(1).equals(ownModule);
            })
        .filter(line -> !isPermittedCrossModuleImport(line))
        .map(line -> relativePath(path) + " → " + line)
        .toList();
  }

  private static boolean isPermittedCrossModuleImport(String importLine) {
    return importLine.contains(".service.")
        || importLine.contains(".domain.")
        || importLine.contains(".history.");
  }

  private static boolean implementsPublicApiFacade(Path path) {
    var body = readString(path);
    return IMPLEMENTS_PUBLIC_API.matcher(body).find();
  }

  private static boolean livesOutsideCoreApiOrModuleServicePackage(Path path) {
    var rel = relativePath(path);
    return !rel.startsWith("com/hanielcota/essentials/core/api/")
        && !(rel.startsWith("com/hanielcota/essentials/modules/") && rel.contains("/service/"));
  }

  private static String fileName(Path path) {
    return path.getFileName().toString();
  }

  private static Path mainJavaRoot() {
    return Path.of("src", "main", "java");
  }

  private static String relativePath(Path path) {
    return mainJavaRoot().relativize(path).toString().replace('\\', '/');
  }

  private static boolean isTpaBootstrapOutsideBootstrapPackage(Path path) {
    var rel = relativePath(path);
    var fileName = fileName(path);

    return rel.startsWith("com/hanielcota/essentials/modules/tpa/")
        && fileName.endsWith("Bootstrap.java")
        && !rel.contains("/bootstrap/");
  }

  private static boolean isMenuPresentationHelperOutsidePresentationPackage(Path path) {
    var rel = relativePath(path);
    var fileName = fileName(path);
    var presentationHelper =
        fileName.endsWith("Renderer.java")
            || fileName.endsWith("Browser.java")
            || fileName.endsWith("StatsFormatter.java");

    if (!presentationHelper) {
      return false;
    }
    if (!rel.contains("/menu/") || rel.contains("/menu/presentation/")) {
      return false;
    }

    return modulePresentationPackageExists(path);
  }

  private static boolean isDatabaseInfrastructureInRootPackage(Path path) {
    var rel = relativePath(path);

    if (!rel.startsWith("com/hanielcota/essentials/database/")) {
      return false;
    }

    var databasePrefix = "com/hanielcota/essentials/database/";
    var afterDatabasePackage = rel.substring(databasePrefix.length());

    return !afterDatabasePackage.contains("/");
  }

  private static boolean isModuleInternalInRootPackage(Path path) {
    var rel = relativePath(path);

    if (!rel.startsWith("com/hanielcota/essentials/module/")) {
      return false;
    }

    var modulePrefix = "com/hanielcota/essentials/module/";
    var afterModulePackage = rel.substring(modulePrefix.length());
    if (afterModulePackage.contains("/")) {
      return false;
    }

    return !MODULE_ROOT_TYPES.contains(afterModulePackage);
  }

  private static boolean modulePresentationPackageExists(Path path) {
    var rel = relativePath(path);
    var menuIndex = rel.indexOf("/menu/");
    var modulePrefix = rel.substring(0, menuIndex);
    var presentationDir = mainJavaRoot().resolve(modulePrefix + "/menu/presentation");

    return Files.isDirectory(presentationDir);
  }

  private static boolean exceedsLineBudget(Map.Entry<String, Integer> budget) {
    var path = mainJavaRoot().resolve(budget.getKey());
    var lineCount = readLines(path).size();

    return lineCount > budget.getValue();
  }

  private static String formatLineBudgetViolation(Map.Entry<String, Integer> budget) {
    var path = mainJavaRoot().resolve(budget.getKey());
    var lineCount = readLines(path).size();

    return budget.getKey() + " has " + lineCount + " lines; budget is " + budget.getValue();
  }

  // Architecture tests must surface IO failures (unreadable files, missing source root) instead of
  // silently masking them as "no violation found" — a swallowed exception would let real
  // violations slip past CI undetected.
  private static List<String> readLines(Path path) {
    try {
      return Files.readAllLines(path);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read " + path, e);
    }
  }

  private static String readString(Path path) {
    try {
      return Files.readString(path);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read " + path, e);
    }
  }
}
