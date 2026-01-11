package dev.shorty.core.model;

import java.security.SecureRandom;
import java.util.Objects;

public class ShortCode {
  private static final String ALPHABET =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final SecureRandom RANDOM = new SecureRandom();

  private String value;

  public ShortCode() {}

  public ShortCode(String value) {
    this.value = normalize(value);
  }

  public static ShortCode fromString(String value) {
    return new ShortCode(value);
  }

  public static ShortCode random(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("length must be positive");
    }
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
    }
    return new ShortCode(builder.toString());
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = normalize(value);
  }

  public static boolean isValid(String value, int length) {
    if (value == null || value.length() != length) {
      return false;
    }
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (ALPHABET.indexOf(c) < 0) {
        return false;
      }
    }
    return true;
  }

  private static String normalize(String value) {
    String trimmed = Objects.requireNonNull(value, "value").trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Short code must not be empty");
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
    ShortCode shortCode = (ShortCode) o;
    return Objects.equals(value, shortCode.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
