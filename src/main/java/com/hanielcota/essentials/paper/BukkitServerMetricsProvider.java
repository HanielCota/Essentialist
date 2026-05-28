package com.hanielcota.essentials.paper;

import java.lang.management.ManagementFactory;
import org.bukkit.Bukkit;

/**
 * Production {@link ServerMetricsProvider} backed by Paper's static APIs ({@code Bukkit.*}, {@code
 * Runtime.getRuntime()}, {@code ManagementFactory.*}). Wired once in {@code CoreServicesBootstrap}.
 */
public final class BukkitServerMetricsProvider implements ServerMetricsProvider {

  private static final long BYTES_PER_MB = 1024L * 1024L;

  @Override
  public double tps() {
    var rawTps = Bukkit.getTPS()[0];
    return Math.min(20.0, rawTps);
  }

  @Override
  public int onlinePlayerCount() {
    return Bukkit.getOnlinePlayers().size();
  }

  @Override
  public int maxPlayers() {
    return Bukkit.getMaxPlayers();
  }

  @Override
  public String version() {
    return Bukkit.getVersion();
  }

  @Override
  public int worldCount() {
    return Bukkit.getWorlds().size();
  }

  @Override
  public long memoryUsedMb() {
    var runtime = Runtime.getRuntime();
    var totalBytes = runtime.totalMemory();
    var freeBytes = runtime.freeMemory();
    return (totalBytes - freeBytes) / BYTES_PER_MB;
  }

  @Override
  public long memoryMaxMb() {
    return Runtime.getRuntime().maxMemory() / BYTES_PER_MB;
  }

  @Override
  public long uptimeMillis() {
    return ManagementFactory.getRuntimeMXBean().getUptime();
  }
}
