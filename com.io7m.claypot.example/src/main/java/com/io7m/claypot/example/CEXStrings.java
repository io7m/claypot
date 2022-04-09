/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.claypot.example;

import com.io7m.claypot.core.CLPAbstractStrings;
import com.io7m.claypot.core.CLPStringsType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ResourceBundle;

/**
 * The default provider of strings.
 */

public final class CEXStrings extends CLPAbstractStrings
{
  private CEXStrings(
    final ResourceBundle inResources)
  {
    super(inResources);
  }

  /**
   * @return A new string provider
   */

  public static CLPStringsType create()
  {
    try (var stream = CEXStrings.class.getResourceAsStream(
      "/com/io7m/claypot/example/Example.xml")) {
      return new CEXStrings(ofXML(stream));
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
