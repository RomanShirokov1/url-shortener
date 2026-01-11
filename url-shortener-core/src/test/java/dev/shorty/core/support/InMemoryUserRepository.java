package dev.shorty.core.support;

import dev.shorty.core.model.UserId;
import dev.shorty.core.port.UserRepository;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {
  private UserId userId;

  public InMemoryUserRepository() {}

  public InMemoryUserRepository(UserId userId) {
    this.userId = userId;
  }

  @Override
  public Optional<UserId> load() {
    return Optional.ofNullable(userId);
  }

  @Override
  public void save(UserId userId) {
    this.userId = userId;
  }

  public UserId getUserId() {
    return userId;
  }
}
