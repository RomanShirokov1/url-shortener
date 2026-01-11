package dev.shorty.core.usecase;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.port.LinkRepository;
import dev.shorty.core.port.NotificationPort;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class OpenLink {
  public enum Status {
    OK,
    NOT_FOUND,
    EXPIRED,
    LIMIT_REACHED
  }

  public static class Result {
    private final Status status;
    private final Link link;

    private Result(Status status, Link link) {
      this.status = status;
      this.link = link;
    }

    public static Result ok(Link link) {
      return new Result(Status.OK, link);
    }

    public static Result notFound() {
      return new Result(Status.NOT_FOUND, null);
    }

    public static Result expired(Link link) {
      return new Result(Status.EXPIRED, link);
    }

    public static Result limitReached(Link link) {
      return new Result(Status.LIMIT_REACHED, link);
    }

    public Status getStatus() {
      return status;
    }

    public Link getLink() {
      return link;
    }
  }

  private final LinkRepository linkRepository;
  private final NotificationPort notificationPort;

  public OpenLink(LinkRepository linkRepository, NotificationPort notificationPort) {
    this.linkRepository = Objects.requireNonNull(linkRepository, "linkRepository");
    this.notificationPort = Objects.requireNonNull(notificationPort, "notificationPort");
  }

  public Result open(String rawShortCode) {
    if (rawShortCode == null || rawShortCode.trim().isEmpty()) {
      return Result.notFound();
    }
    if (!ShortCode.isValid(rawShortCode, 8)) {
      notificationPort.notifyNotFound(ShortCode.fromString(rawShortCode));
      return Result.notFound();
    }
    ShortCode shortCode = ShortCode.fromString(rawShortCode);
    Optional<Link> stored = linkRepository.findByShortCode(shortCode);
    if (stored.isEmpty()) {
      notificationPort.notifyNotFound(shortCode);
      return Result.notFound();
    }

    Link link = stored.get();
    Instant now = Instant.now();
    if (link.isExpired(now)) {
      linkRepository.delete(shortCode);
      notificationPort.notifyExpired(link);
      return Result.expired(link);
    }
    if (link.isLimitReached()) {
      notificationPort.notifyLimitReached(link);
      return Result.limitReached(link);
    }

    link.setClickCount(link.getClickCount() + 1);
    linkRepository.save(link);
    return Result.ok(link);
  }
}
