package com.hanielcota.essentials.command;

import io.github.hanielcota.commandframework.core.SuggestionProvider;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.Locale;
import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;

/**
 * Provides tab-completion suggestions for enchantment names. Accepts {@link RegistryAccess} as a
 * parameter so callers can inject alternate registries (e.g. for testing without a running server).
 */
public final class EnchantmentSuggestions {

  private EnchantmentSuggestions() {}

  public static SuggestionProvider<Enchantment> provider(@NonNull RegistryAccess registryAccess) {
    return context -> {
      var currentInput = context.currentInput();
      var input = currentInput.toLowerCase(Locale.ROOT);

      var names = new ArrayList<String>();
      var enchantmentRegistry = registryAccess.getRegistry(RegistryKey.ENCHANTMENT);

      for (var enchantment : enchantmentRegistry) {
        var namespacedKey = enchantment.getKey();
        var name = namespacedKey.getKey();

        if (name.startsWith(input)) {
          names.add(name);
        }
      }

      return names;
    };
  }
}
