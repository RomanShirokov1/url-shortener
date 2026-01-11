package dev.shorty.infra.config;

import dev.shorty.core.port.ConfigPort;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

public class PropertiesConfig implements ConfigPort {
  private static final String LINKS_PATH_KEY = "shorty.links.path";
  private static final String USER_PATH_KEY = "shorty.user.path";
  private static final String DEFAULT_MAX_CLICKS_KEY = "shorty.defaultMaxClicks";
  private static final String LINK_TTL_HOURS_KEY = "shorty.linkTtlHours";

  private final Properties properties = new Properties();

  public PropertiesConfig() {
    try (InputStream input =
        PropertiesConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
      if (input != null) {
        properties.load(input);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load application.properties", e);
    }
  }

  @Override
  public Duration getLinkTtl() {
    int hours = getInt(LINK_TTL_HOURS_KEY, 24);
    return Duration.ofHours(hours);
  }

  @Override
  public int getDefaultMaxClicks() {
    return getInt(DEFAULT_MAX_CLICKS_KEY, 3);
  }

  @Override
  public Path getLinksFilePath() {
    return Path.of(properties.getProperty(LINKS_PATH_KEY, "data/links.json"));
  }

  @Override
  public Path getUserFilePath() {
    return Path.of(properties.getProperty(USER_PATH_KEY, "data/user.json"));
  }

  private int getInt(String key, int fallback) {
    String value = properties.getProperty(key);
    if (value == null) {
      return fallback;
    }
    return Integer.parseInt(value.trim());
  }
}
