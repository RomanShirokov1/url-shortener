package dev.shorty.cli;

import dev.shorty.cli.command.CleanupCommand;
import dev.shorty.cli.command.CreateCommand;
import dev.shorty.cli.command.DeleteCommand;
import dev.shorty.cli.command.EditCommand;
import dev.shorty.cli.command.InfoCommand;
import dev.shorty.cli.command.InitCommand;
import dev.shorty.cli.command.ListCommand;
import dev.shorty.cli.command.OpenCommand;
import dev.shorty.core.model.Link;
import dev.shorty.core.model.UserId;
import dev.shorty.core.usecase.CleanupExpired;
import dev.shorty.core.usecase.CreateLink;
import dev.shorty.core.usecase.DeleteLink;
import dev.shorty.core.usecase.EditLink;
import dev.shorty.core.usecase.GetLinkInfo;
import dev.shorty.core.usecase.ListLinks;
import dev.shorty.core.usecase.OpenLink;
import dev.shorty.infra.config.PropertiesConfig;
import dev.shorty.infra.notify.ConsoleNotificationAdapter;
import dev.shorty.infra.repo.FileLinkRepository;
import dev.shorty.infra.repo.FileUserRepository;
import java.awt.Desktop;
import java.net.URI;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "shorty", mixinStandardHelpOptions = true)
public class Main implements Runnable {
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  public static void main(String[] args) {
    Main main = new Main();
    if (args.length == 0) {
      main.runInteractive();
      return;
    }
    CommandLine commandLine = new CommandLine(main);
    commandLine.addSubcommand("init", main.initCommand);
    commandLine.addSubcommand("create", main.createCommand);
    commandLine.addSubcommand("open", main.openCommand);
    commandLine.addSubcommand("list", main.listCommand);
    commandLine.addSubcommand("info", main.infoCommand);
    commandLine.addSubcommand("edit", main.editCommand);
    commandLine.addSubcommand("delete", main.deleteCommand);
    commandLine.addSubcommand("cleanup", main.cleanupCommand);
    System.exit(commandLine.execute(args));
  }

  private final InitCommand initCommand;
  private final CreateCommand createCommand;
  private final OpenCommand openCommand;
  private final ListCommand listCommand;
  private final InfoCommand infoCommand;
  private final EditCommand editCommand;
  private final DeleteCommand deleteCommand;
  private final CleanupCommand cleanupCommand;
  private final CreateLink createLink;
  private final OpenLink openLink;
  private final ListLinks listLinks;
  private final GetLinkInfo getLinkInfo;
  private final EditLink editLink;
  private final DeleteLink deleteLink;
  private final CleanupExpired cleanupExpired;
  private final FileUserRepository userRepository;

  public Main() {
    PropertiesConfig config = new PropertiesConfig();
    this.userRepository = new FileUserRepository(config.getUserFilePath());
    FileLinkRepository linkRepository = new FileLinkRepository(config.getLinksFilePath());
    ConsoleNotificationAdapter notification = new ConsoleNotificationAdapter();

    this.createLink = new CreateLink(config, linkRepository, userRepository);
    this.openLink = new OpenLink(linkRepository, notification);
    this.listLinks = new ListLinks(linkRepository, userRepository);
    this.getLinkInfo = new GetLinkInfo(linkRepository, userRepository);
    this.editLink = new EditLink(linkRepository, userRepository);
    this.deleteLink = new DeleteLink(linkRepository, userRepository);
    this.cleanupExpired = new CleanupExpired(linkRepository);

    this.initCommand = new InitCommand(userRepository);
    this.createCommand = new CreateCommand(createLink);
    this.openCommand = new OpenCommand(openLink);
    this.listCommand = new ListCommand(listLinks);
    this.infoCommand = new InfoCommand(getLinkInfo);
    this.editCommand = new EditCommand(editLink);
    this.deleteCommand = new DeleteCommand(deleteLink);
    this.cleanupCommand = new CleanupCommand(cleanupExpired);
  }

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }

  private void runInteractive() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println();
      System.out.println("Меню команд:");
      System.out.println("1) инициализировать пользователя");
      System.out.println("2) создать короткую ссылку");
      System.out.println("3) открыть короткую ссылку");
      System.out.println("4) мои ссылки");
      System.out.println("5) информация о ссылке");
      System.out.println("6) изменить лимит переходов");
      System.out.println("7) удалить ссылку");
      System.out.println("8) очистить истекшие");
      System.out.println("0) выход");
      System.out.print("Выбор: ");

      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> initUser();
        case "2" -> createLink(scanner);
        case "3" -> openLink(scanner);
        case "4" -> listLinks();
        case "5" -> linkInfo(scanner);
        case "6" -> editLink(scanner);
        case "7" -> deleteLink(scanner);
        case "8" -> cleanupExpired();
        case "0" -> {
          System.out.println("До свидания.");
          return;
        }
        default -> System.out.println("Неизвестная команда.");
      }
    }
  }

  private void initUser() {
    Optional<UserId> existing = userRepository.load();
    if (existing.isPresent()) {
      System.out.println("Текущий пользователь: " + existing.get());
      return;
    }
    UserId created = UserId.newId();
    userRepository.save(created);
    System.out.println("Создан пользователь: " + created);
  }

  private void createLink(Scanner scanner) {
    System.out.print("Оригинальный URL: ");
    String url = scanner.nextLine().trim();
    if (url.isEmpty()) {
      System.out.println("URL обязателен.");
      return;
    }
    Link link = createLink.create(url);
    System.out.println("Короткий код: " + link.getShortCode());
    System.out.println("Оригинал: " + link.getOriginalUrl());
    System.out.println("Переходы: " + link.getClickCount() + "/" + link.getMaxClicks());
  }

  private void openLink(Scanner scanner) {
    System.out.print("Короткий код: ");
    String shortCode = scanner.nextLine().trim();
    OpenLink.Result result = openLink.open(shortCode);
    if (result.getStatus() != OpenLink.Status.OK) {
      return;
    }
    try {
      URI uri = result.getLink().getOriginalUrl().toUri();
      if (!Desktop.isDesktopSupported()) {
        System.out.println("Открытие браузера не поддерживается.");
        return;
      }
      Desktop.getDesktop().browse(uri);
      System.out.println("Открыто: " + uri);
    } catch (Exception e) {
      System.out.println("Не удалось открыть браузер: " + e.getMessage());
    }
  }

  private void listLinks() {
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

  private void linkInfo(Scanner scanner) {
    System.out.print("Короткий код: ");
    String shortCode = scanner.nextLine().trim();
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

  private void editLink(Scanner scanner) {
    System.out.print("Короткий код: ");
    String shortCode = scanner.nextLine().trim();
    System.out.print("Новый лимит переходов: ");
    String maxClicksValue = scanner.nextLine().trim();
    int maxClicks;
    try {
      maxClicks = Integer.parseInt(maxClicksValue);
    } catch (NumberFormatException e) {
      System.out.println("Некорректное число.");
      return;
    }
    Optional<Link> link = editLink.updateMaxClicks(shortCode, maxClicks);
    if (link.isEmpty()) {
      System.out.println("Ссылка не найдена для текущего пользователя.");
      return;
    }
    System.out.println("Обновлен лимит переходов: " + link.get().getMaxClicks());
  }

  private void deleteLink(Scanner scanner) {
    System.out.print("Короткий код: ");
    String shortCode = scanner.nextLine().trim();
    boolean deleted = deleteLink.deleteForCurrentUser(shortCode);
    if (deleted) {
      System.out.println("Удалено: " + shortCode);
    } else {
      System.out.println("Ссылка не найдена для текущего пользователя.");
    }
  }

  private void cleanupExpired() {
    int removed = cleanupExpired.cleanup();
    System.out.println("Удалено истекших ссылок: " + removed);
  }
}
