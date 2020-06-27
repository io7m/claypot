/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.claypot.tests;

import ch.qos.logback.classic.Level;
import com.io7m.claypot.core.internal.CLPLogLevelConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CLPLogLevelConverterTest
{
  @Test
  public void conversionsOK()
  {
    final var converter = new CLPLogLevelConverter();
    assertEquals("debug", converter.convert("debug").getName());
    assertEquals("error", converter.convert("error").getName());
    assertEquals("info", converter.convert("info").getName());
    assertEquals("trace", converter.convert("trace").getName());
    assertEquals("warn", converter.convert("warn").getName());
  }

  @Test
  public void conversionsOKTyped()
  {
    final var converter = new CLPLogLevelConverter();
    assertEquals(Level.DEBUG, converter.convert("debug").toLevel());
    assertEquals(Level.ERROR, converter.convert("error").toLevel());
    assertEquals(Level.INFO, converter.convert("info").toLevel());
    assertEquals(Level.TRACE, converter.convert("trace").toLevel());
    assertEquals(Level.WARN, converter.convert("warn").toLevel());
  }

  @Test
  public void toStringOK()
  {
    final var converter = new CLPLogLevelConverter();
    assertTrue(converter.toString().contains("CLPLogLevelConverter"));
  }
}
