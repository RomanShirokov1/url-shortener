package dev.shorty.cli.command;

import dev.shorty.core.usecase.DeleteLink;
import java.util.Objects;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "delete", description = "Delete a link for current user")
public class DeleteCommand implements Runnable {
  private final DeleteLink deleteLink;

  @Parameters(index = "0", description = "Short code")
  private String shortCode;

  public DeleteCommand(DeleteLink deleteLink) {
    this.deleteLink = Objects.requireNonNull(deleteLink, "deleteLink");
  }

  @Override
  public void run() {
    boolean deleted = deleteLink.deleteForCurrentUser(shortCode);
    if (deleted) {
      System.out.println("Удалено: " + shortCode);
    } else {
      System.out.println("Ссылка не найдена для текущего пользователя.");
    }
  }
}
