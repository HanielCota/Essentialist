package com.hanielcota.essentials;

import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.bootstrap.EssentialsBootstrap;
import com.hanielcota.essentials.core.EssentialsCore;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public final class EssentialsPlugin extends JavaPlugin {

  private EssentialsCore core;

  @Override
  public void onEnable() {
    try {
      this.core = new EssentialsBootstrap(this).start();
    } catch (RuntimeException e) {
      getLogger().log(Level.SEVERE, "Failed to enable Essentials", e);
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    if (this.core == null) {
      return;
    }
    try {
      this.core.shutdown();
    } catch (RuntimeException e) {
      getLogger().log(Level.SEVERE, "Error during Essentials shutdown", e);
    } finally {
      this.core = null;
    }
  }

  public EssentialsApi api() {
    if (this.core == null) {
      throw new IllegalStateException("Essentials is not enabled");
    }
    return this.core;
  }
}
