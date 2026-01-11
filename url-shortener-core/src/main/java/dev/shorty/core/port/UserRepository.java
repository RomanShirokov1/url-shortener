package dev.shorty.core.port;

import dev.shorty.core.model.UserId;
import java.util.Optional;

public interface UserRepository {
  Optional<UserId> load();

  void save(UserId userId);
}
