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
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.io7m.claypot.core.CLPAbstractCommand;
import com.io7m.claypot.core.CLPCommandContextType;
import com.io7m.claypot.core.CLPCommandType;

import java.util.ArrayList;
import java.util.List;

import static com.io7m.claypot.core.CLPCommandType.Status.FAILURE;
import static com.io7m.claypot.core.CLPCommandType.Status.SUCCESS;
import static com.io7m.claypot.core.internal.CLPBriefUsageFormatter.showBriefUsage;

@Parameters(commandDescription = "Show detailed help messages for commands.")
public final class CLPCommandHelp extends CLPAbstractCommand
{
  @Parameter(description = "command")
  private List<String> commandNames = new ArrayList<>();

  /**
   * Construct a command.
   *
   * @param inContext The command context
   */

  public CLPCommandHelp(
    final CLPCommandContextType inContext)
  {
    super(inContext);
  }

  @Override
  public String extendedHelp()
  {
    return this.strings()
      .format(
        "com.io7m.claypot.helpExtended",
        this.commander().getProgramName()
      );
  }

  @Override
  protected Status executeActual()
  {
    final var logger = this.logger();

    if (this.commandNames.isEmpty()) {
      final var console = new CLPStringBuilderConsole();
      final var commander = this.commander();
      commander.setUsageFormatter(new DefaultUsageFormatter(commander));
      commander.setConsole(console);
      commander.usage();
      logger.info("{}", console.builder().toString());
      return SUCCESS;
    }

    final var commandName = this.commandNames.get(0);
    final var commander = this.commander();
    final var commands = commander.getCommands();
    final var subCommander = commands.get(commandName);
    if (subCommander == null) {
      logger.error("Unknown command: {}", commandName);
      showBriefUsage(logger, this.commander());
      return FAILURE;
    }

    final var console = new CLPStringBuilderConsole();
    subCommander.setUsageFormatter(new DefaultUsageFormatter(subCommander));
    subCommander.setConsole(console);
    subCommander.usage();

    final var commandObject = (CLPCommandType) subCommander.getObjects().get(0);
    final var help = commandObject.extendedHelp().trim();
    final var builder = console.builder();
    if (!help.isBlank()) {
      help.lines()
        .map(CLPCommandHelp::indentLine)
        .forEach(line -> {
          builder.append(line);
          builder.append('\n');
        });
    }

    logger.info("{}", builder);
    return SUCCESS;
  }

  private static String indentLine(
    final String text)
  {
    if (text.trim().isEmpty()) {
      return "";
    }
    return "  " + text;
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CLPCommandHelp 0x%s]",
      Long.toUnsignedString(System.identityHashCode(this), 16)
    );
  }

  @Override
  public String name()
  {
    return "help";
  }
}
