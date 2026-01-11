package dev.shorty.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {
  private UserId userId;
  private OriginalUrl originalUrl;
  private ShortCode shortCode;
  private Instant createdAt;
  private Instant expiresAt;
  private int maxClicks;
  private int clickCount;

  public Link() {}

  public Link(
      UserId userId,
      OriginalUrl originalUrl,
      ShortCode shortCode,
      Instant createdAt,
      Instant expiresAt,
      int maxClicks,
      int clickCount) {
    this.userId = Objects.requireNonNull(userId, "userId");
    this.originalUrl = Objects.requireNonNull(originalUrl, "originalUrl");
    this.shortCode = Objects.requireNonNull(shortCode, "shortCode");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
    this.maxClicks = maxClicks;
    this.clickCount = clickCount;
  }

  public UserId getUserId() {
    return userId;
  }

  public void setUserId(UserId userId) {
    this.userId = userId;
  }

  public OriginalUrl getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(OriginalUrl originalUrl) {
    this.originalUrl = originalUrl;
  }

  public ShortCode getShortCode() {
    return shortCode;
  }

  public void setShortCode(ShortCode shortCode) {
    this.shortCode = shortCode;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = expiresAt;
  }

  public int getMaxClicks() {
    return maxClicks;
  }

  public void setMaxClicks(int maxClicks) {
    this.maxClicks = maxClicks;
  }

  public int getClickCount() {
    return clickCount;
  }

  public void setClickCount(int clickCount) {
    this.clickCount = clickCount;
  }

  public boolean isExpired(Instant now) {
    return now.isAfter(expiresAt) || now.equals(expiresAt);
  }

  public boolean isLimitReached() {
    return clickCount >= maxClicks;
  }

  public boolean isOwnedBy(UserId userId) {
    return Objects.equals(this.userId, userId);
  }
}
