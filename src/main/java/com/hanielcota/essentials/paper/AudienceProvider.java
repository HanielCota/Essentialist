package com.hanielcota.essentials.paper;

import java.util.UUID;
import net.kyori.adventure.audience.Audience;

public interface AudienceProvider {

  Audience console();

  Audience broadcast();

  Audience player(UUID id);
}
