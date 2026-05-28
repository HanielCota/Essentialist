package com.hanielcota.essentials.paper;

import java.util.Optional;
import lombok.NonNull;
import org.bukkit.World;

/**
 * Lookup surface for loaded worlds. Lets domain records and services request worlds via an injected
 * provider instead of {@code Bukkit.getWorld(name)} (which is static and untestable).
 */
public interface WorldLookup {

  Optional<World> world(@NonNull String name);
}
