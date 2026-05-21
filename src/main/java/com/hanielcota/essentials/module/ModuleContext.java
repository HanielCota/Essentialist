package com.hanielcota.essentials.module;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.service.ServiceRegistry;

public record ModuleContext(EssentialsPlugin plugin, ServiceRegistry services) {}
