package com.hanielcota.essentials.api;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.service.ServiceRegistry;

public interface EssentialsApi {

  EssentialsPlugin plugin();

  ServiceRegistry services();
}
