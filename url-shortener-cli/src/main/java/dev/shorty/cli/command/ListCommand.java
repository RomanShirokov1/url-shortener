package dev.shorty.cli.command;

import dev.shorty.core.model.Link;
import dev.shorty.core.usecase.ListLinks;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import picocli.CommandLine.Command;

@Command(name = "list", description = "List links for current user")
public class ListCommand implements Runnable {
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  private final ListLinks listLinks;

  public ListCommand(ListLinks listLinks) {
    this.listLinks = Objects.requireNonNull(listLinks, "listLinks");
  }

  @Override
  public void run() {
    List<Link> links = listLinks.listForCurrentUser();
    if (links.isEmpty()) {
      System.out.println("Ссылок нет.");
      return;
    }
    for (Link link : links) {
      System.out.println("Короткий код: " + link.getShortCode());
      System.out.println("Оригинал: " + link.getOriginalUrl());
      System.out.println("Переходы: " + link.getClickCount() + "/" + link.getMaxClicks());
      System.out.println("Когда истекает: " + FORMATTER.format(link.getExpiresAt()));
      System.out.println("---");
    }
  }
}
