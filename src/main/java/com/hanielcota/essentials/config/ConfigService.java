package com.hanielcota.essentials.config;

import java.util.function.Supplier;

public interface ConfigService {

  <T> ConfigHandle<T> load(String name, Class<T> type, Supplier<T> defaults);

  ReloadReport reloadAll();

  AutoCloseable onReload(Runnable callback);
}
