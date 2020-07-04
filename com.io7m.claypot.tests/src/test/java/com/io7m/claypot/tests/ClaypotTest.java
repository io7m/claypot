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

import com.io7m.claypot.core.CLPApplicationConfiguration;
import com.io7m.claypot.core.CLPCommandType;
import com.io7m.claypot.core.Claypot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class ClaypotTest
{
  private static final Logger LOG = LoggerFactory.getLogger(Claypot.class);
  private Logger spyLog;

  @BeforeEach
  public void setup()
  {
    this.spyLog = mock(Logger.class, delegatesTo(LOG));
  }

  @Test
  public void noArgumentsShowsUsage()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);
    claypot.execute(new String[]{});

    assertEquals(1, claypot.exitCode());
    final var captor = ArgumentCaptor.forClass(String.class);
    verify(this.spyLog).info(eq("{}"), captor.capture());

    final var argument = captor.getValue();
    assertTrue(argument.contains("Usage: cex"));
    assertTrue(argument.contains("Commands:"));
  }

  @Test
  public void noArgumentsUnrecognizedLogLevel()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);
    claypot.execute(new String[]{"--verbose", "who-knows"});

    assertEquals(1, claypot.exitCode());
    final var captor0 = ArgumentCaptor.forClass(String.class);
    final var captor1 = ArgumentCaptor.forClass(String.class);

    verify(this.spyLog).error(
      eq("{}{}: {}"),
      any(),
      captor0.capture(),
      captor1.capture()
    );

    final var arg0 = captor0.getValue();
    final var arg1 = captor1.getValue();
    assertTrue(arg1.contains("Unrecognized log level: who-knows"));
  }

  @Test
  public void helpShowsDetailedUsage()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);
    claypot.execute(new String[]{"help"});

    assertEquals(0, claypot.exitCode());
    final var captor = ArgumentCaptor.forClass(String.class);
    verify(this.spyLog).info(eq("{}"), captor.capture());

    final var argument = captor.getValue();
    assertTrue(argument.contains("Usage: cex"));
    assertTrue(argument.contains("Usage: help [options]"));
  }

  @Test
  public void helpShowsExtendedUsage()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);
    claypot.execute(new String[]{"help", "help"});

    assertEquals(0, claypot.exitCode());
    final var captor = ArgumentCaptor.forClass(StringBuilder.class);
    verify(this.spyLog).info(eq("{}"), captor.capture());

    final var argument = captor.getValue();
    assertTrue(argument.toString().contains("The \"help\" command, executed without arguments, shows"));
  }

  @Test
  public void helpUnrecognizedParameter()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);
    claypot.execute(new String[]{"help", "--unrecognized"});

    assertEquals(1, claypot.exitCode());
    final var captor = ArgumentCaptor.forClass(String.class);
    verify(this.spyLog).error(eq("Unknown command: {}"), captor.capture());

    final var argument = captor.getValue();
    assertTrue(argument.contains("--unrecognized"));
  }

  @Test
  public void commandCrashes()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .addCommands(CrashCommand::new)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);
    claypot.execute(new String[]{"crash"});

    assertEquals(1, claypot.exitCode());
    final var captor1 = ArgumentCaptor.forClass(String.class);
    final var captor2 = ArgumentCaptor.forClass(String.class);
    verify(this.spyLog, new Times(2))
      .error(
        eq("{}{}: {}"),
        any(),
        captor1.capture(),
        captor2.capture());

    final var argument = captor1.getValue();
    assertTrue(argument.contains("java.io.IOException"));
  }

  @Test
  public void commandCrashesVerbose()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .addCommands(CrashCommand::new)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);
    claypot.execute(new String[]{"crash", "--verbose", "debug"});

    assertEquals(1, claypot.exitCode());

    final var captor1 = ArgumentCaptor.forClass(String.class);
    final var captor2 = ArgumentCaptor.forClass(String.class);
    verify(this.spyLog, new Times(2))
      .error(
        eq("{}{}: {}"),
        any(),
        captor1.capture(),
        captor2.capture());

    verify(this.spyLog, new Times(2))
      .debug(eq("{}"), captor2.capture());

    final var arg0 = captor1.getValue();
    assertTrue(arg0.contains("java.io.IOException"));

    final var arg1 = captor2.getValue();
    assertTrue(arg1.contains("Stacktrace of java.io.IOException:"));
  }

  @Test
  public void commandDuplicate()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .addCommands(EmptyCommand::new)
        .addCommands(EmptyCommand::new)
        .build();

    Assertions.assertThrows(IllegalStateException.class, () -> {
      Claypot.create(applicationConfiguration);
    });
  }

  @Test
  public void commandNames()
  {
    final var applicationConfiguration =
      CLPApplicationConfiguration.builder()
        .setProgramName("cex")
        .setLogger(this.spyLog)
        .addCommands(CrashCommand::new)
        .addCommands(EmptyCommand::new)
        .build();

    final var claypot = Claypot.create(applicationConfiguration);

    final Map<String, CLPCommandType> commands = claypot.commands();
    assertEquals(3, commands.size());
    assertEquals(CrashCommand.class, commands.get("crash").getClass());
    assertEquals(EmptyCommand.class, commands.get("empty").getClass());
  }
}
