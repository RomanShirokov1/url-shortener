package dev.shorty.core.usecase;

import dev.shorty.core.model.Link;
import dev.shorty.core.port.LinkRepository;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class CleanupExpired {
  private final LinkRepository linkRepository;

  public CleanupExpired(LinkRepository linkRepository) {
    this.linkRepository = Objects.requireNonNull(linkRepository, "linkRepository");
  }

  public int cleanup() {
    Instant now = Instant.now();
    List<Link> links = linkRepository.findAll();
    int removed = 0;
    for (Link link : links) {
      if (link.isExpired(now)) {
        linkRepository.delete(link.getShortCode());
        removed++;
      }
    }
    return removed;
  }
}
