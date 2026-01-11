package dev.shorty.core.usecase;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.port.LinkRepository;
import dev.shorty.core.port.UserRepository;
import java.util.Objects;
import java.util.Optional;

public class GetLinkInfo {
  private final LinkRepository linkRepository;
  private final UserRepository userRepository;

  public GetLinkInfo(LinkRepository linkRepository, UserRepository userRepository) {
    this.linkRepository = Objects.requireNonNull(linkRepository, "linkRepository");
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
  }

  public Optional<Link> getForCurrentUser(String rawShortCode) {
    ShortCode shortCode = ShortCode.fromString(rawShortCode);
    Optional<UserId> userId = userRepository.load();
    if (userId.isEmpty()) {
      return Optional.empty();
    }
    return linkRepository.findByShortCode(shortCode).filter(link -> link.isOwnedBy(userId.get()));
  }
}
