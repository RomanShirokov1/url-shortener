package dev.shorty.cli.command;

import dev.shorty.core.usecase.CleanupExpired;
import java.util.Objects;
import picocli.CommandLine.Command;

@Command(name = "cleanup", description = "Remove expired links")
public class CleanupCommand implements Runnable {
  private final CleanupExpired cleanupExpired;

  public CleanupCommand(CleanupExpired cleanupExpired) {
    this.cleanupExpired = Objects.requireNonNull(cleanupExpired, "cleanupExpired");
  }

  @Override
  public void run() {
    int removed = cleanupExpired.cleanup();
    System.out.println("Удалено истекших ссылок: " + removed);
  }
}
