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

package com.io7m.claypot.core.internal;

import com.beust.jcommander.DefaultUsageFormatter;
import com.beust.jcommander.JCommander;

/**
 * A long usage formatter.
 */

public final class CLPLongUsageFormatter extends DefaultUsageFormatter
{
  /**
   * A long usage formatter.
   *
   * @param commander The <tt>jcommander</tt> instance
   */

  public CLPLongUsageFormatter(
    final JCommander commander)
  {
    super(commander);
  }

  @Override
  public void appendMainLine(
    final StringBuilder out,
    final boolean hasOptions,
    final boolean hasCommands,
    final int indentCount,
    final String indent)
  {
    super.appendMainLine(out, hasOptions, hasCommands, indentCount, indent);
    out.append('\n');
  }

  @Override
  public void appendCommands(
    final StringBuilder out,
    final int indentCount,
    final int descriptionIndent,
    final String indent)
  {
    out.append('\n');
    super.appendCommands(out, indentCount, descriptionIndent, indent);
  }
}
