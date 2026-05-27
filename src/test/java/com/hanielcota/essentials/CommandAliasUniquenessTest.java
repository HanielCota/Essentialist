package com.hanielcota.essentials;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class CommandAliasUniquenessTest {

  private static final Pattern COMMAND_VALUE =
      Pattern.compile("@Command\\s*\\(\\s*(?:value\\s*=\\s*)?\"([^\"]+)\"");
  private static final Pattern ALIASES_ARRAY =
      Pattern.compile("aliases\\s*=\\s*\\{([^}]*)}", Pattern.DOTALL);
  private static final Pattern ALIASES_SINGLE = Pattern.compile("aliases\\s*=\\s*\"([^\"]+)\"");
  private static final Pattern ALIAS = Pattern.compile("@Alias\\s*\\(\\s*\"([^\"]+)\"");
  private static final Pattern QUOTED = Pattern.compile("\"([^\"]+)\"");
  private static final Pattern SUBCOMMAND_ARRAY =
      Pattern.compile("@Subcommand\\s*\\(\\s*\\{([^}]*)}", Pattern.DOTALL);

  @Test
  void commandAliasesAreGloballyUnique() throws IOException {
    var occurrences = new HashMap<String, List<String>>();

    try (var paths = Files.walk(commandRoot())) {
      var files =
          paths
              .filter(Files::isRegularFile)
              .filter(path -> path.toString().endsWith(".java"))
              .toList();

      for (var file : files) {
        collectAliases(file, occurrences);
      }
    }

    var duplicates =
        occurrences.entrySet().stream().filter(entry -> entry.getValue().size() > 1).toList();

    assertTrue(duplicates.isEmpty(), () -> "Duplicate command aliases: " + duplicates);
  }

  private static void collectAliases(Path file, Map<String, List<String>> occurrences)
      throws IOException {
    var source = Files.readString(file);
    var location = commandRoot().relativize(file).toString().replace('\\', '/');

    collectMatches(COMMAND_VALUE, source, location, occurrences);
    collectMatches(ALIASES_SINGLE, source, location, occurrences);
    collectArrayAliases(source, location, occurrences);
    collectMatches(ALIAS, source, location, occurrences);
    collectSubcommandArrayAliases(source, location, occurrences);
  }

  private static void collectArrayAliases(
      String source, String location, Map<String, List<String>> occurrences) {
    var matcher = ALIASES_ARRAY.matcher(source);

    while (matcher.find()) {
      var aliases = matcher.group(1);
      collectMatches(QUOTED, aliases, location, occurrences);
    }
  }

  private static void collectSubcommandArrayAliases(
      String source, String location, Map<String, List<String>> occurrences) {
    var matcher = SUBCOMMAND_ARRAY.matcher(source);

    while (matcher.find()) {
      var raw = matcher.group(1);
      var quoted = QUOTED.matcher(raw);
      var first = true;

      while (quoted.find()) {
        if (first) {
          first = false;
          continue;
        }

        var alias = quoted.group(1);
        var locations = occurrences.computeIfAbsent(alias, ignored -> new ArrayList<>());

        locations.add(location);
      }
    }
  }

  private static void collectMatches(
      Pattern pattern, String source, String location, Map<String, List<String>> occurrences) {
    var matcher = pattern.matcher(source);

    while (matcher.find()) {
      var alias = matcher.group(1);
      var locations = occurrences.computeIfAbsent(alias, ignored -> new ArrayList<>());

      locations.add(location);
    }
  }

  private static Path commandRoot() {
    return Path.of("src", "main", "java", "com", "hanielcota", "essentials", "modules");
  }
}
