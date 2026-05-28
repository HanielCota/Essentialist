package com.hanielcota.essentials.paper;

/**
 * Read surface for server-wide runtime metrics: TPS, player counts, version, world count, memory
 * and uptime. Lets renderers depend on an injected provider instead of reaching into {@code
 * Bukkit.*}, {@code Runtime.*} and {@code ManagementFactory.*} globals.
 */
public interface ServerMetricsProvider {

  double tps();

  int onlinePlayerCount();

  int maxPlayers();

  String version();

  int worldCount();

  long memoryUsedMb();

  long memoryMaxMb();

  long uptimeMillis();
}
