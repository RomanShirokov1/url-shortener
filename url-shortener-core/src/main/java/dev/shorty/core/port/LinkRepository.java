package dev.shorty.core.port;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.OriginalUrl;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
  Optional<Link> findByShortCode(ShortCode shortCode);

  Optional<Link> findByUserAndOriginal(UserId userId, OriginalUrl originalUrl);

  List<Link> findByUser(UserId userId);

  List<Link> findAll();

  void save(Link link);

  void delete(ShortCode shortCode);
}
