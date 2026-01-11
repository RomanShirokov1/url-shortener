package dev.shorty.core.support;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.port.NotificationPort;
import java.util.ArrayList;
import java.util.List;

public class CapturingNotificationPort implements NotificationPort {
  private final List<ShortCode> notFound = new ArrayList<>();
  private final List<Link> expired = new ArrayList<>();
  private final List<Link> limitReached = new ArrayList<>();

  @Override
  public void notifyExpired(Link link) {
    expired.add(link);
  }

  @Override
  public void notifyLimitReached(Link link) {
    limitReached.add(link);
  }

  @Override
  public void notifyNotFound(ShortCode shortCode) {
    notFound.add(shortCode);
  }

  public List<ShortCode> getNotFound() {
    return notFound;
  }

  public List<Link> getExpired() {
    return expired;
  }

  public List<Link> getLimitReached() {
    return limitReached;
  }
}
