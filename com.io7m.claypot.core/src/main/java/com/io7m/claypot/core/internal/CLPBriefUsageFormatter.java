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

package com.io7m.claypot.core.internal;

import com.beust.jcommander.DefaultUsageFormatter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.ProgramName;
import com.beust.jcommander.Parameters;
import com.io7m.claypot.core.CLPStrings;
import com.io7m.claypot.core.CLPStringsType;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public final class CLPBriefUsageFormatter extends DefaultUsageFormatter
{
  private final JCommander commander;
  private final CLPStringsType strings;

  public CLPBriefUsageFormatter(
    final JCommander inCommander)
  {
    super(inCommander);

    this.commander =
      Objects.requireNonNull(inCommander, "commander");
    this.strings =
      CLPStrings.create();
  }

  public static void showBriefUsage(
    final Logger logger,
    final JCommander commander)
  {
    Objects.requireNonNull(logger, "logger");
    Objects.requireNonNull(commander, "commander");

    final var console = new CLPStringBuilderConsole();
    commander.setUsageFormatter(new CLPBriefUsageFormatter(commander));
    commander.setConsole(console);
    commander.usage();
    logger.info("{}", console.builder().toString());
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
    out.append("  ");
    out.append(this.strings.format(
      "com.io7m.claypot.help",
      this.commander.getProgramName()
    ).trim());
    out.append('\n');
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
    out.append(indent);
    out.append("  ");
    out.append(this.strings.format("com.io7m.claypot.commands"));
    out.append(":\n");

    final var rawCommands = this.commander.getRawCommands();
    final var commandNames = new ArrayList<>(rawCommands.keySet());
    commandNames.sort(Comparator.comparing(ProgramName::getDisplayName));

    var longest = 0;
    for (final var commandName : commandNames) {
      longest = Math.max(commandName.getName().length(), longest);
    }
    longest += 4;
    
    for (final var commandName : commandNames) {
      final var commands = rawCommands.get(commandName);
      final Object arg = commands.getObjects().get(0);
      final Parameters p = arg.getClass().getAnnotation(Parameters.class);

      if (p == null || !p.hidden()) {
        final String lineFormat = 
          String.format("    %%-%ds %%s", Integer.valueOf(longest));
        final String description =
          String.format(
            lineFormat,
            commandName.getDisplayName(),
            this.getCommandDescription(commandName.getName())
          );

        out.append(description);
        out.append('\n');
      }
    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CLPBriefUsageFormatter 0x%s]",
      Long.toUnsignedString(System.identityHashCode(this), 16)
    );
  }
}
