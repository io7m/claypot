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

package com.io7m.claypot.example;

import com.beust.jcommander.Parameters;
import com.io7m.claypot.core.CLPAbstractCommand;
import com.io7m.claypot.core.CLPCommandContextType;
import com.io7m.claypot.core.CLPStringsType;

/**
 * The red command.
 */

@Parameters(commandDescription = "Paint things red.")
public final class CEXRed extends CLPAbstractCommand
{
  private final CLPStringsType strings;

  /**
   * The red command.
   *
   * @param inContext The command context
   */

  public CEXRed(final CLPCommandContextType inContext)
  {
    super(inContext);
    this.strings = CEXStrings.create();
  }

  @Override
  protected Status executeActual()
  {
    return Status.SUCCESS;
  }

  @Override
  public String extendedHelp()
  {
    return this.strings.format("redExtendedHelp");
  }

  @Override
  public String name()
  {
    return "red";
  }
}
