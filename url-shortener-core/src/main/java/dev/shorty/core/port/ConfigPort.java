package dev.shorty.core.port;

import java.nio.file.Path;
import java.time.Duration;

public interface ConfigPort {
  Duration getLinkTtl();

  int getDefaultMaxClicks();

  Path getLinksFilePath();

  Path getUserFilePath();
}
