package com.hanielcota.essentials;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ArchitecturePackageTest {

  private static final Set<String> DOMAIN_VALUE_TYPES =
      Set.of("SpawnLocation", "PlayerEntry", "Resolved", "SeenLine", "GiveResult");

  private static final Set<String> PERSISTENCE_TYPES =
      Set.of(
          "WarpStore",
          "WarpTable",
          "WarpCache",
          "SpawnStore",
          "SpawnTable",
          "MuteStore",
          "MuteTable",
          "NickStore",
          "NickTable");

  // Pattern: `import com.hanielcota.essentials.modules.<module>.` — captures the imported module.
  private static final Pattern CROSS_MODULE_IMPORT =
      Pattern.compile("^import com\\.hanielcota\\.essentials\\.modules\\.(\\w+)\\.");

  // Matches `implements XxxApi` either unqualified or fully-qualified (or in a comma-separated
  // implements clause). Captures the api type name in group 1.
  private static final Pattern IMPLEMENTS_PUBLIC_API =
      Pattern.compile(
          "implements\\s+(?:[\\w.]+\\.)?(HomesApi|WarpsApi|MutesApi|NicksApi|VanishApi|TeleportsApi)\\b");

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
    // Allowed cross-module imports: anything in another module's `service`, `domain`, `model`,
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
              "Cross-module imports must go through service/domain/model packages: " + violations);
    }
  }

  @Test
  void onlyCoreApiPackageImplementsPublicApiInterfaces() throws IOException {
    try (var paths = walkMainJava()) {
      var violations =
          paths
              .filter(ArchitecturePackageTest::implementsPublicApiFacade)
              .filter(ArchitecturePackageTest::livesOutsideCoreApi)
              .map(ArchitecturePackageTest::relativePath)
              .toList();

      assertTrue(
          violations.isEmpty(),
          () ->
              "Only com.hanielcota.essentials.core.api may implement public *Api interfaces: "
                  + violations);
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
        || importLine.contains(".model.")
        || importLine.contains(".history.");
  }

  private static boolean implementsPublicApiFacade(Path path) {
    var body = readString(path);
    return IMPLEMENTS_PUBLIC_API.matcher(body).find();
  }

  private static boolean livesOutsideCoreApi(Path path) {
    var rel = relativePath(path);
    return !rel.startsWith("com/hanielcota/essentials/core/api/");
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
