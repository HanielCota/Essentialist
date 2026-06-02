package com.hanielcota.essentials.modules.title.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TitleLinesTest {

  @Test
  void parsePlainTextAsTitle() {
    var lines = TitleLines.parse("Hello World");

    assertEquals("Hello World", lines.title());
    assertEquals("", lines.subtitle());
  }

  @Test
  void parseQuotedTitleOnly() {
    var lines = TitleLines.parse("\"Hello World\"");

    assertEquals("Hello World", lines.title());
    assertEquals("", lines.subtitle());
  }

  @Test
  void parseQuotedTitleAndSubtitle() {
    var lines = TitleLines.parse("\"Title\" \"Subtitle\"");

    assertEquals("Title", lines.title());
    assertEquals("Subtitle", lines.subtitle());
  }

  @Test
  void parseEmptyInput() {
    var lines = TitleLines.parse("");

    assertEquals("", lines.title());
    assertEquals("", lines.subtitle());
  }

  @Test
  void parseOnlyWhitespace() {
    var lines = TitleLines.parse("   ");

    assertEquals("", lines.title());
    assertEquals("", lines.subtitle());
  }

  @Test
  void parseIgnoresExtraTextOutsideQuotes() {
    var lines = TitleLines.parse("\"Title\" middle \"Subtitle\" suffix");

    assertEquals("Title", lines.title());
    assertEquals("Subtitle", lines.subtitle());
  }

  @Test
  void parseUnclosedQuoteReturnsEverythingAfterOpen() {
    var lines = TitleLines.parse("\"Unclosed title");

    assertEquals("Unclosed title", lines.title());
    assertEquals("", lines.subtitle());
  }

  @Test
  void parseTripleQuotedSegments() {
    var lines = TitleLines.parse("\"First\" \"Second\" \"Third\"");

    assertEquals("First", lines.title());
    assertEquals("Second", lines.subtitle());
  }
}
