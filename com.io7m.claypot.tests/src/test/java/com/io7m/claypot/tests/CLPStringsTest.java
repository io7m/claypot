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

import com.io7m.claypot.core.CLPAbstractStrings;
import com.io7m.claypot.core.CLPStrings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CLPStringsTest
{
  @Test
  public void missingResource()
  {
    final var ex = assertThrows(UncheckedIOException.class, Wrong::create);
    assertTrue(ex.getMessage().contains("CRASH!"));
  }

  @Test
  public void resourceBundleExists()
  {
    final var strings = CLPStrings.create();
    assertNotNull(strings.resources());
    assertEquals("Commands", strings.format("com.io7m.claypot.commands"));
  }

  final static class Wrong extends CLPAbstractStrings
  {
    Wrong(
      final ResourceBundle inResources)
    {
      super(inResources);
    }

    public static CLPAbstractStrings create()
    {
      return new Wrong(ofXML(new InputStream()
      {
        @Override
        public int read()
          throws IOException
        {
          throw new IOException("CRASH!");
        }
      }));
    }
  }
}
