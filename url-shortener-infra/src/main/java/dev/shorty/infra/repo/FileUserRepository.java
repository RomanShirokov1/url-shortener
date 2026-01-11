package dev.shorty.infra.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shorty.core.model.UserId;
import dev.shorty.core.port.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class FileUserRepository implements UserRepository {
  private final Path filePath;
  private final ObjectMapper mapper = new ObjectMapper();

  public FileUserRepository(Path filePath) {
    this.filePath = Objects.requireNonNull(filePath, "filePath");
  }

  @Override
  public Optional<UserId> load() {
    if (!Files.exists(filePath)) {
      return Optional.empty();
    }
    try (InputStream input = Files.newInputStream(filePath)) {
      UserRecord record = mapper.readValue(input, UserRecord.class);
      if (record == null || record.userId() == null || record.userId().isBlank()) {
        return Optional.empty();
      }
      return Optional.of(UserId.fromString(record.userId()));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read user file: " + filePath, e);
    }
  }

  @Override
  public void save(UserId userId) {
    try {
      Files.createDirectories(filePath.getParent());
      try (OutputStream output = Files.newOutputStream(filePath)) {
        mapper
            .writerWithDefaultPrettyPrinter()
            .writeValue(output, new UserRecord(userId.toString()));
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write user file: " + filePath, e);
    }
  }

  private record UserRecord(String userId) {}
}
