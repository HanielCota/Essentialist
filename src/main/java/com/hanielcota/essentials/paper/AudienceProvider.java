package com.hanielcota.essentials.paper;

import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;

public interface AudienceProvider {

  Audience console();

  Audience broadcast();

  Audience player(@NonNull UUID id);
}
