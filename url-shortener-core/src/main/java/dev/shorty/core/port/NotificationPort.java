package dev.shorty.core.port;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.ShortCode;

public interface NotificationPort {
  void notifyExpired(Link link);

  void notifyLimitReached(Link link);

  void notifyNotFound(ShortCode shortCode);
}
