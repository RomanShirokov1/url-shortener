package dev.shorty.core.usecase;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.OriginalUrl;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.port.ConfigPort;
import dev.shorty.core.port.LinkRepository;
import dev.shorty.core.port.UserRepository;
import java.time.Instant;
import java.util.Objects;

public class CreateLink {
  private static final int SHORT_CODE_LENGTH = 8;

  private final ConfigPort config;
  private final LinkRepository linkRepository;
  private final UserRepository userRepository;

  public CreateLink(ConfigPort config, LinkRepository linkRepository, UserRepository userRepository) {
    this.config = Objects.requireNonNull(config, "config");
    this.linkRepository = Objects.requireNonNull(linkRepository, "linkRepository");
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
  }

  public Link create(String rawUrl) {
    OriginalUrl originalUrl = OriginalUrl.fromString(rawUrl);
    UserId userId =
        userRepository.load().orElseGet(
            () -> {
              UserId created = UserId.newId();
              userRepository.save(created);
              return created;
            });

    return linkRepository
        .findByUserAndOriginal(userId, originalUrl)
        .map(
            existing -> {
              if (existing.isExpired(Instant.now())) {
                linkRepository.delete(existing.getShortCode());
                return createNewLink(userId, originalUrl);
              }
              return existing;
            })
        .orElseGet(() -> createNewLink(userId, originalUrl));
  }

  private Link createNewLink(UserId userId, OriginalUrl originalUrl) {
    Instant now = Instant.now();
    ShortCode shortCode = generateUniqueShortCode();
    int maxClicks = config.getDefaultMaxClicks();
    return persist(
        new Link(
            userId,
            originalUrl,
            shortCode,
            now,
            now.plus(config.getLinkTtl()),
            maxClicks,
            0));
  }

  private ShortCode generateUniqueShortCode() {
    ShortCode candidate = ShortCode.random(SHORT_CODE_LENGTH);
    while (linkRepository.findByShortCode(candidate).isPresent()) {
      candidate = ShortCode.random(SHORT_CODE_LENGTH);
    }
    return candidate;
  }

  private Link persist(Link link) {
    linkRepository.save(link);
    return link;
  }
}
