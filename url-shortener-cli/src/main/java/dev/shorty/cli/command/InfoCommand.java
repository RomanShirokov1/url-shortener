package dev.shorty.cli.command;

import dev.shorty.core.model.Link;
import dev.shorty.core.usecase.GetLinkInfo;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "info", description = "Show link details for current user")
public class InfoCommand implements Runnable {
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  private final GetLinkInfo getLinkInfo;

  @Parameters(index = "0", description = "Short code")
  private String shortCode;

  public InfoCommand(GetLinkInfo getLinkInfo) {
    this.getLinkInfo = Objects.requireNonNull(getLinkInfo, "getLinkInfo");
  }

  @Override
  public void run() {
    Optional<Link> link = getLinkInfo.getForCurrentUser(shortCode);
    if (link.isEmpty()) {
      System.out.println("Ссылка не найдена для текущего пользователя.");
      return;
    }
    Link value = link.get();
    System.out.println("Короткий код: " + value.getShortCode());
    System.out.println("Оригинал: " + value.getOriginalUrl());
    System.out.println("Создано: " + FORMATTER.format(value.getCreatedAt()));
    System.out.println("Когда истекает: " + FORMATTER.format(value.getExpiresAt()));
    System.out.println("Переходы: " + value.getClickCount() + "/" + value.getMaxClicks());
  }
}
