package com.hanielcota.essentials.module.environment;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.ConfigService;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DefaultModuleEnvironment implements ModuleEnvironment {

  private final ModuleContext context;

  @Override
  public EssentialsPlugin plugin() {
    return this.context.plugin();
  }

  @Override
  public <T> T service(@NonNull Class<T> type) {
    var services = this.context.services();
    return services.resolve(type);
  }

  @Override
  public <T> Optional<T> findService(@NonNull Class<T> type) {
    var services = this.context.services();
    return services.find(type);
  }

  @Override
  public <T> ConfigHandle<T> config(
      @NonNull String name, @NonNull Class<T> type, @NonNull Supplier<T> defaults) {
    var configService = service(ConfigService.class);
    return configService.load(name, type, defaults);
  }
}
