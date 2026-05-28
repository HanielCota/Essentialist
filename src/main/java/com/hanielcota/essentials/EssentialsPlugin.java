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
    var bootstrap = createBootstrap();

    try {
      this.core = bootstrap.start();
    } catch (RuntimeException e) {
      var logger = getLogger();
      logger.log(Level.SEVERE, "Failed to enable Essentials", e);

      var server = getServer();
      var pluginManager = server.getPluginManager();
      pluginManager.disablePlugin(this);
    }
  }

  protected EssentialsBootstrap createBootstrap() {
    return new EssentialsBootstrap(this);
  }

  @Override
  public void onDisable() {
    if (this.core == null) {
      return;
    }

    try {
      this.core.shutdown();
    } catch (RuntimeException e) {
      var logger = getLogger();
      logger.log(Level.SEVERE, "Error during Essentials shutdown", e);
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
