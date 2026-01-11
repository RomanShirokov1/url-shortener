package dev.shorty.cli.command;

import dev.shorty.core.model.Link;
import dev.shorty.core.usecase.EditLink;
import java.util.Objects;
import java.util.Optional;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "edit", description = "Edit link settings for current user")
public class EditCommand implements Runnable {
  private final EditLink editLink;

  @Parameters(index = "0", description = "Short code")
  private String shortCode;

  @Option(
      names = {"-m", "--max-clicks"},
      required = true,
      description = "New max clicks")
  private int maxClicks;

  public EditCommand(EditLink editLink) {
    this.editLink = Objects.requireNonNull(editLink, "editLink");
  }

  @Override
  public void run() {
    Optional<Link> link = editLink.updateMaxClicks(shortCode, maxClicks);
    if (link.isEmpty()) {
      System.out.println("Ссылка не найдена для текущего пользователя.");
      return;
    }
    System.out.println("Обновлен лимит переходов: " + link.get().getMaxClicks());
  }
}
