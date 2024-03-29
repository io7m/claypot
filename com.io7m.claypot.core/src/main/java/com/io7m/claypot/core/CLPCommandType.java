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

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base type of commands.
 */

@ProviderType
public interface CLPCommandType
{
  /**
   * @return Extra help text for the command
   */

  default String extendedHelp() {
    return "";
  }

  /**
   * @return The name of the command
   */

  String name();

  /**
   * Execute the command.
   *
   * @return The resulting command status
   *
   * @throws Exception On errors.
   */

  Status execute()
    throws Exception;

  /**
   * The result of executing a command.
   */

  enum Status
  {
    /**
     * The command succeeded.
     */

    SUCCESS,

    /**
     * The command failed.
     */

    FAILURE;

    /**
     * @return The shell exit code
     */

    public int exitCode()
    {
      switch (this) {
        case SUCCESS:
          return 0;
        case FAILURE:
          return 1;
      }
      throw new IllegalStateException();
    }
  }
}
