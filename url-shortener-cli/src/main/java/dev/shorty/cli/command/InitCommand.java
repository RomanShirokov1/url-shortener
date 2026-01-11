package dev.shorty.cli.command;

import dev.shorty.core.model.UserId;
import dev.shorty.core.port.UserRepository;
import java.util.Objects;
import java.util.Optional;
import picocli.CommandLine.Command;

@Command(name = "init", description = "Initialize or show current user")
public class InitCommand implements Runnable {
  private final UserRepository userRepository;

  public InitCommand(UserRepository userRepository) {
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
  }

  @Override
  public void run() {
    Optional<UserId> userId = userRepository.load();
    if (userId.isPresent()) {
      System.out.println("Текущий пользователь: " + userId.get());
      return;
    }
    UserId created = UserId.newId();
    userRepository.save(created);
    System.out.println("Создан пользователь: " + created);
  }
}
