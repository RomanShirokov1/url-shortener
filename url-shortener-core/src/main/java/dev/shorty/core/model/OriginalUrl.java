package dev.shorty.core.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class OriginalUrl {
  private String value;

  public OriginalUrl() {}

  public OriginalUrl(String value) {
    this.value = normalize(value);
  }

  public static OriginalUrl fromString(String value) {
    return new OriginalUrl(value);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = normalize(value);
  }

  public URI toUri() {
    try {
      return new URI(value);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid URL: " + value, e);
    }
  }

  private static String normalize(String value) {
    String trimmed = Objects.requireNonNull(value, "value").trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("URL must not be empty");
    }
    return trimmed;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OriginalUrl that = (OriginalUrl) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
