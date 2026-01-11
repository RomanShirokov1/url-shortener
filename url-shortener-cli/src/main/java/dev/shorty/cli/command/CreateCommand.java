package dev.shorty.cli.command;

import dev.shorty.core.model.Link;
import dev.shorty.core.usecase.CreateLink;
import java.util.Objects;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "create", description = "Create a short link")
public class CreateCommand implements Runnable {
  private final CreateLink createLink;

  @Option(
      names = {"-u", "--url"},
      required = true,
      description = "Original URL")
  private String url;

  public CreateCommand(CreateLink createLink) {
    this.createLink = Objects.requireNonNull(createLink, "createLink");
  }

  @Override
  public void run() {
    Link link = createLink.create(url);
    System.out.println("Короткий код: " + link.getShortCode());
    System.out.println("Оригинал: " + link.getOriginalUrl());
    System.out.println("Переходы: " + link.getClickCount() + "/" + link.getMaxClicks());
  }
}
