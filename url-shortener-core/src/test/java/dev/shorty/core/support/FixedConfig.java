package dev.shorty.core.support;

import dev.shorty.core.port.ConfigPort;
import java.nio.file.Path;
import java.time.Duration;

public class FixedConfig implements ConfigPort {
  private final Duration linkTtl;
  private final int defaultMaxClicks;

  public FixedConfig(Duration linkTtl, int defaultMaxClicks) {
    this.linkTtl = linkTtl;
    this.defaultMaxClicks = defaultMaxClicks;
  }

  @Override
  public Duration getLinkTtl() {
    return linkTtl;
  }

  @Override
  public int getDefaultMaxClicks() {
    return defaultMaxClicks;
  }

  @Override
  public Path getLinksFilePath() {
    return Path.of("build/test-links.json");
  }

  @Override
  public Path getUserFilePath() {
    return Path.of("build/test-user.json");
  }
}
