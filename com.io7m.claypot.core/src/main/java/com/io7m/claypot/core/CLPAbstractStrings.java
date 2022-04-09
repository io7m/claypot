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

package com.io7m.claypot.core;

import com.io7m.claypot.core.internal.CLPXMLResourceBundle;
import org.osgi.annotation.versioning.ProviderType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * An abstract implementation of the {@link CLPStringsType} interface.
 */

@ProviderType
public abstract class CLPAbstractStrings implements CLPStringsType
{
  private final ResourceBundle resources;

  protected CLPAbstractStrings(
    final ResourceBundle inResources)
  {
    this.resources = Objects.requireNonNull(inResources, "inResources");
  }

  @Override
  public final ResourceBundle resources()
  {
    return this.resources;
  }

  @Override
  public final String format(
    final String id,
    final Object... args)
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(args, "args");
    return MessageFormat.format(this.resources.getString(id), args);
  }

  protected static ResourceBundle ofXMLResource(
    final Class<?> clazz,
    final String resource)
  {
    try (var stream = clazz.getResourceAsStream(resource)) {
      return ofXML(stream);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected static ResourceBundle ofXML(
    final InputStream stream)
  {
    try {
      return new CLPXMLResourceBundle(
        Objects.requireNonNull(stream, "stream")
      );
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

