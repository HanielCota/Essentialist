package com.hanielcota.essentials.integrations;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class IntegrationRegistry {

  private final Map<String, Integration> integrations = new LinkedHashMap<>();

  public void register(Integration integration) {
    Objects.requireNonNull(integration, "integration");
    String name = Objects.requireNonNull(integration.name(), "integration.name()");
    integrations.putIfAbsent(name, integration);
  }

  public Optional<Integration> get(String name) {
    return Optional.ofNullable(integrations.get(Objects.requireNonNull(name, "name")));
  }

  public Collection<Integration> all() {
    return List.copyOf(integrations.values());
  }

  public void disableAll() {
    for (Integration integration : integrations.values()) {
      if (integration.isAvailable()) {
        integration.disable();
      }
    }
  }
}
