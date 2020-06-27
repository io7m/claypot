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

package com.io7m.claypot.core;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.io7m.claypot.core.internal.CLPBriefUsageFormatter;
import com.io7m.claypot.core.internal.CLPCommandHelp;
import com.io7m.claypot.core.internal.CLPCommandRoot;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Objects;

/**
 * The main wrapper over {@link JCommander}.
 */

public final class Claypot
{
  private final CLPApplicationConfiguration configuration;
  private final JCommander commander;
  private final HashMap<String, CLPCommandType> commandMap;
  private final CLPStringsType strings;
  private int exitCode;

  private Claypot(
    final CLPApplicationConfiguration inConfiguration,
    final JCommander inCommander,
    final HashMap<String, CLPCommandType> inCommandMap,
    final CLPStringsType inStrings)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "inConfiguration");
    this.commander =
      Objects.requireNonNull(inCommander, "commander");
    this.commandMap =
      Objects.requireNonNull(inCommandMap, "commandMap");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
  }

  /**
   * Create a new wrapper based on the given configuration.
   *
   * @param configuration The application configuration
   *
   * @return A new wrapper
   */

  public static Claypot create(
    final CLPApplicationConfiguration configuration)
  {
    final var strings = CLPStrings.create();
    final var commander = new JCommander();
    final var context = new Context(commander, strings, configuration);

    commander.setProgramName(configuration.programName());
    commander.addObject(new CLPCommandRoot(context));

    final var constructors =
      configuration.commands();
    final var commandMap =
      new HashMap<String, CLPCommandType>(constructors.size() + 1);

    final var help = new CLPCommandHelp(context);
    commandMap.put(help.name(), help);

    for (final var constructor : constructors) {
      final var command = constructor.create(context);
      final var name = command.name();
      if (commandMap.containsKey(name)) {
        throw new IllegalStateException(
          strings.format("com.io7m.claypot.commandConflict", name)
        );
      }
      commandMap.put(name, command);
    }

    for (final var entry : commandMap.entrySet()) {
      commander.addCommand(entry.getKey(), entry.getValue());
    }

    return new Claypot(configuration, commander, commandMap, strings);
  }

  /**
   * @return The exit code resulting from the most recent {@link #execute(String[])}
   */

  public int exitCode()
  {
    return this.exitCode;
  }

  /**
   * Execute the wrapper for the given command-line arguments.
   *
   * @param args The command-line arguments
   */

  public void execute(
    final String[] args)
  {
    Objects.requireNonNull(args, "args");

    final var logger = this.configuration.logger();

    try {
      this.exitCode = 0;
      this.commander.parse(args);

      final String cmd = this.commander.getParsedCommand();
      if (cmd == null) {
        CLPBriefUsageFormatter.showBriefUsage(
          logger,
          this.configuration,
          this.commander
        );
        this.exitCode = 1;
        return;
      }

      final CLPCommandType command = this.commandMap.get(cmd);
      final CLPCommandType.Status status = command.execute();
      this.exitCode = status.exitCode();
    } catch (final ParameterException e) {
      logger.error("{}", e.getMessage());
      this.exitCode = 1;
    } catch (final Exception e) {
      this.logExceptionFriendly(logger, false, e);
      this.exitCode = 1;
    }
  }

  private void logExceptionFriendly(
    final Logger logger,
    final boolean isCause,
    final Throwable e)
  {
    if (e == null) {
      return;
    }

    logger.error(
      "{}{}: {}",
      isCause ? this.strings.format("com.io7m.claypot.causedBy") : "",
      e.getClass().getCanonicalName(),
      e.getMessage());

    if (logger.isDebugEnabled()) {
      final var trace = new StringBuilder(256);
      final String lineSeparator = System.lineSeparator();
      trace.append(lineSeparator);
      final var elements = e.getStackTrace();
      for (final var element : elements) {
        trace.append("  at ");

        final var moduleName = element.getModuleName();
        if (moduleName != null) {
          trace.append(moduleName);
          trace.append('/');
        } else {
          trace.append("<unnamed>/");
        }

        trace.append(element.getClassName());
        trace.append('.');
        trace.append(element.getMethodName());
        trace.append('(');
        trace.append(element.getFileName());
        trace.append(':');
        trace.append(element.getLineNumber());
        trace.append(')');
        trace.append(lineSeparator);
      }
      logger.debug(
        "{}",
        this.strings.format(
          "com.io7m.claypot.stackTraceOf",
          e.getClass().getCanonicalName(),
          trace
        ));
    }

    final var causes = e.getSuppressed();
    if (causes.length > 0) {
      for (final var cause : causes) {
        this.logExceptionFriendly(logger, true, cause);
      }
    }
    this.logExceptionFriendly(logger, true, e.getCause());
  }

  @Override
  public String toString()
  {
    return String.format(
      "[Claypot 0x%s]",
      Long.toUnsignedString(System.identityHashCode(this), 16)
    );
  }

  private static final class Context implements CLPCommandContextType
  {
    private final JCommander commander;
    private final CLPStringsType strings;
    private final CLPApplicationConfiguration configuration;

    private Context(
      final JCommander inCommander,
      final CLPStringsType inStrings,
      final CLPApplicationConfiguration inConfiguration)
    {
      this.commander =
        Objects.requireNonNull(inCommander, "commander");
      this.strings =
        Objects.requireNonNull(inStrings, "inStrings");
      this.configuration =
        Objects.requireNonNull(inConfiguration, "inConfiguration");
    }

    @Override
    public CLPStringsType strings()
    {
      return this.strings;
    }

    @Override
    public CLPApplicationConfiguration configuration()
    {
      return this.configuration;
    }

    @Override
    public JCommander commander()
    {
      return this.commander;
    }
  }
}
