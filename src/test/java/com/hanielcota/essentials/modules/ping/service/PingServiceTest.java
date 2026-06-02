package com.hanielcota.essentials.modules.ping.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ping.config.PingConfig;
import org.junit.jupiter.api.Test;

class PingServiceTest {

  private static PingService service(int goodMax, int mediumMax) {
    var config = new PingConfig("own {ping}", "other {ping}", goodMax, mediumMax);
    var handle =
        new ConfigHandle<PingConfig>() {
          @Override
          public String name() {
            return "ping";
          }

          @Override
          public PingConfig value() {
            return config;
          }
        };
    return new PingService(handle);
  }

  @Test
  void formatUsesGreenForLowPing() {
    var service = service(100, 250);
    var result = service.format(50);

    assertTrue(result.startsWith("<green>"));
  }

  @Test
  void formatUsesGreenForBoundaryPing() {
    var service = service(100, 250);
    var result = service.format(100);

    assertTrue(result.startsWith("<green>"));
  }

  @Test
  void formatUsesYellowForMediumPing() {
    var service = service(100, 250);
    var result = service.format(150);

    assertTrue(result.contains("<yellow>"));
  }

  @Test
  void formatUsesYellowForMediumBoundary() {
    var service = service(100, 250);
    var result = service.format(250);

    assertTrue(result.contains("<yellow>"));
  }

  @Test
  void formatUsesRedForHighPing() {
    var service = service(100, 250);
    var result = service.format(300);

    assertTrue(result.contains("<red>"));
  }

  @Test
  void formatClampsNegativeToZero() {
    var service = service(100, 250);
    var result = service.format(-1);

    assertTrue(result.contains("0ms"));
  }

  @Test
  void formatIncludesPingValue() {
    var service = service(100, 250);
    var result = service.format(42);

    assertTrue(result.contains("42ms"));
  }
}
