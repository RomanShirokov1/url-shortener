package dev.shorty.core.model;

import java.util.Objects;
import java.util.UUID;

public class UserId {
  private UUID value;

  public UserId() {}

  public UserId(UUID value) {
    this.value = Objects.requireNonNull(value, "value");
  }

  public static UserId newId() {
    return new UserId(UUID.randomUUID());
  }

  public static UserId fromString(String value) {
    return new UserId(UUID.fromString(value));
  }

  public UUID getValue() {
    return value;
  }

  public void setValue(UUID value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value == null ? "" : value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserId userId = (UserId) o;
    return Objects.equals(value, userId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
