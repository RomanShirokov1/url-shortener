package dev.shorty.core.usecase;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.port.LinkRepository;
import dev.shorty.core.port.UserRepository;
import java.util.Objects;
import java.util.Optional;

public class EditLink {
  private final LinkRepository linkRepository;
  private final UserRepository userRepository;

  public EditLink(LinkRepository linkRepository, UserRepository userRepository) {
    this.linkRepository = Objects.requireNonNull(linkRepository, "linkRepository");
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
  }

  public Optional<Link> updateMaxClicks(String rawShortCode, int maxClicks) {
    if (maxClicks <= 0) {
      throw new IllegalArgumentException("maxClicks must be positive");
    }
    ShortCode shortCode = ShortCode.fromString(rawShortCode);
    Optional<UserId> userId = userRepository.load();
    if (userId.isEmpty()) {
      return Optional.empty();
    }

    Optional<Link> link =
        linkRepository
            .findByShortCode(shortCode)
            .filter(existing -> existing.isOwnedBy(userId.get()));
    link.ifPresent(
        existing -> {
          existing.setMaxClicks(maxClicks);
          linkRepository.save(existing);
        });
    return link;
  }
}
