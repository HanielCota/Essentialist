package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.service.ServiceRegistry;

/** Read-only view of the in-flight bootstrap handed to every {@link BootstrapStage}. */
public record StageContext(EssentialsPlugin plugin, ServiceRegistry services) {}
