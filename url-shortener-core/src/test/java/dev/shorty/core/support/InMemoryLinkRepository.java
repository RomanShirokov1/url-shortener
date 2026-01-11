package dev.shorty.core.support;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.OriginalUrl;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.port.LinkRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLinkRepository implements LinkRepository {
  private final List<Link> links = new ArrayList<>();

  @Override
  public Optional<Link> findByShortCode(ShortCode shortCode) {
    return links.stream().filter(link -> link.getShortCode().equals(shortCode)).findFirst();
  }

  @Override
  public Optional<Link> findByUserAndOriginal(UserId userId, OriginalUrl originalUrl) {
    return links.stream()
        .filter(link -> link.getUserId().equals(userId))
        .filter(link -> link.getOriginalUrl().equals(originalUrl))
        .findFirst();
  }

  @Override
  public List<Link> findByUser(UserId userId) {
    List<Link> result = new ArrayList<>();
    for (Link link : links) {
      if (link.getUserId().equals(userId)) {
        result.add(link);
      }
    }
    return result;
  }

  @Override
  public List<Link> findAll() {
    return new ArrayList<>(links);
  }

  @Override
  public void save(Link link) {
    for (int i = 0; i < links.size(); i++) {
      if (links.get(i).getShortCode().equals(link.getShortCode())) {
        links.set(i, link);
        return;
      }
    }
    links.add(link);
  }

  @Override
  public void delete(ShortCode shortCode) {
    links.removeIf(link -> link.getShortCode().equals(shortCode));
  }

  public int size() {
    return links.size();
  }
}
