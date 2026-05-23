package com.hanielcota.essentials.config;

import java.util.List;
import java.util.Map;

public record ReloadReport(int total, Map<String, String> failures) {

  public ReloadReport {
    failures = Map.copyOf(failures);
  }

  public int succeeded() {
    return this.total - this.failures.size();
  }

  public List<String> failedNames() {
    return List.copyOf(this.failures.keySet());
  }
}
