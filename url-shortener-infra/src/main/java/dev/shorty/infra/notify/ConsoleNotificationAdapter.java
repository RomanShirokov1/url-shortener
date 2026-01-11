package dev.shorty.infra.notify;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.port.NotificationPort;

public class ConsoleNotificationAdapter implements NotificationPort {
  @Override
  public void notifyExpired(Link link) {
    System.out.println("Ссылка истекла и удалена: " + link.getShortCode());
  }

  @Override
  public void notifyLimitReached(Link link) {
    System.out.println("Достигнут лимит переходов: " + link.getShortCode());
  }

  @Override
  public void notifyNotFound(ShortCode shortCode) {
    System.out.println("Ссылка не найдена: " + shortCode);
  }
}
