package dev.shorty.core.usecase;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.UserId;
import dev.shorty.core.port.LinkRepository;
import dev.shorty.core.port.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ListLinks {
  private final LinkRepository linkRepository;
  private final UserRepository userRepository;

  public ListLinks(LinkRepository linkRepository, UserRepository userRepository) {
    this.linkRepository = Objects.requireNonNull(linkRepository, "linkRepository");
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
  }

  public List<Link> listForCurrentUser() {
    Optional<UserId> userId = userRepository.load();
    if (userId.isEmpty()) {
      return Collections.emptyList();
    }
    return linkRepository.findByUser(userId.get());
  }
}
