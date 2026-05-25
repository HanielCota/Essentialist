package com.hanielcota.essentials.modules.give.domain;

public record GiveResult(int requested, int given, int leftover) {

  public static GiveResult of(int requested, int leftover) {
    return new GiveResult(requested, requested - leftover, leftover);
  }

  public boolean noneGiven() {
    return this.given == 0;
  }

  public boolean partial() {
    return this.leftover > 0;
  }
}
