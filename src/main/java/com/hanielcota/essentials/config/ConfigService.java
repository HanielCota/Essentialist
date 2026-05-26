package com.hanielcota.essentials.config;

import java.util.function.Supplier;
import lombok.NonNull;

public interface ConfigService {

  <T> ConfigHandle<T> load(
      @NonNull String name, @NonNull Class<T> type, @NonNull Supplier<T> defaults);

  ReloadReport reloadAll();
}
