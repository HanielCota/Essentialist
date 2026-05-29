package com.hanielcota.essentials.module.environment;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.service.ServiceRegistry;

/**
 * The shared plugin handle and service registry passed into each module's enable phase. Modules
 * normally consume the higher-level {@link ModuleEnvironment} / {@code ModuleRegistrar} views built
 * from this context rather than reaching for the raw collaborators here.
 */
public record ModuleContext(EssentialsPlugin plugin, ServiceRegistry services) {}
