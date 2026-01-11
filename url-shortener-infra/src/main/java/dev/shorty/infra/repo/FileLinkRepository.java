package dev.shorty.infra.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.shorty.core.model.Link;
import dev.shorty.core.model.OriginalUrl;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.port.LinkRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FileLinkRepository implements LinkRepository {
  private static final TypeReference<List<Link>> LIST_TYPE = new TypeReference<>() {};

  private final Path filePath;
  private final ObjectMapper mapper;

  public FileLinkRepository(Path filePath) {
    this.filePath = Objects.requireNonNull(filePath, "filePath");
    this.mapper = new ObjectMapper();
    this.mapper.registerModule(new JavaTimeModule());
    this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  public Optional<Link> findByShortCode(ShortCode shortCode) {
    return readAll().stream().filter(link -> link.getShortCode().equals(shortCode)).findFirst();
  }

  @Override
  public Optional<Link> findByUserAndOriginal(UserId userId, OriginalUrl originalUrl) {
    return readAll().stream()
        .filter(link -> link.getUserId().equals(userId))
        .filter(link -> link.getOriginalUrl().equals(originalUrl))
        .findFirst();
  }

  @Override
  public List<Link> findByUser(UserId userId) {
    List<Link> result = new ArrayList<>();
    for (Link link : readAll()) {
      if (link.getUserId().equals(userId)) {
        result.add(link);
      }
    }
    return result;
  }

  @Override
  public List<Link> findAll() {
    return readAll();
  }

  @Override
  public void save(Link link) {
    List<Link> links = readAll();
    boolean updated = false;
    for (int i = 0; i < links.size(); i++) {
      if (links.get(i).getShortCode().equals(link.getShortCode())) {
        links.set(i, link);
        updated = true;
        break;
      }
    }
    if (!updated) {
      links.add(link);
    }
    writeAll(links);
  }

  @Override
  public void delete(ShortCode shortCode) {
    List<Link> links = readAll();
    links.removeIf(link -> link.getShortCode().equals(shortCode));
    writeAll(links);
  }

  private List<Link> readAll() {
    if (!Files.exists(filePath)) {
      return new ArrayList<>();
    }
    try (InputStream input = Files.newInputStream(filePath)) {
      return mapper.readValue(input, LIST_TYPE);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read links file: " + filePath, e);
    }
  }

  private void writeAll(List<Link> links) {
    try {
      Files.createDirectories(filePath.getParent());
      try (OutputStream output = Files.newOutputStream(filePath)) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(output, links);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write links file: " + filePath, e);
    }
  }
}
