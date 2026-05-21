package com.hanielcota.essentials.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ReloadReport(int total, Map<String, String> failures) {

  public ReloadReport {
    Objects.requireNonNull(failures, "failures");
    failures = Map.copyOf(failures);
  }

  public int succeeded() {
    return total - failures.size();
  }

  public List<String> failedNames() {
    return List.copyOf(failures.keySet());
  }
}
