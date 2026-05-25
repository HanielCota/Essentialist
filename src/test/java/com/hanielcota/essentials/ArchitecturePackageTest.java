package com.hanielcota.essentials;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
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

  @Test
  void persistenceTypesDoNotLiveInServicePackages() throws IOException {
    var violations =
        Files.walk(mainJavaRoot())
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".java"))
            .filter(ArchitecturePackageTest::isPersistenceTypeInServicePackage)
            .map(ArchitecturePackageTest::relativePath)
            .toList();

    assertTrue(violations.isEmpty(), () -> "Persistence types in service packages: " + violations);
  }

  @Test
  void domainValueTypesDoNotLiveInServicePackages() throws IOException {
    var violations =
        Files.walk(mainJavaRoot())
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".java"))
            .filter(ArchitecturePackageTest::isDomainValueTypeInServicePackage)
            .map(ArchitecturePackageTest::relativePath)
            .toList();

    assertTrue(violations.isEmpty(), () -> "Domain value types in service packages: " + violations);
  }

  private static boolean isPersistenceTypeInServicePackage(Path path) {
    var typeName = fileName(path).replace(".java", "");

    return relativePath(path).contains("/service/") && PERSISTENCE_TYPES.contains(typeName);
  }

  private static boolean isDomainValueTypeInServicePackage(Path path) {
    var typeName = fileName(path).replace(".java", "");

    return relativePath(path).contains("/service/") && DOMAIN_VALUE_TYPES.contains(typeName);
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
}
