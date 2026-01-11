package dev.shorty.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.OriginalUrl;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.support.FixedConfig;
import dev.shorty.core.support.InMemoryLinkRepository;
import dev.shorty.core.support.InMemoryUserRepository;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CreateLinkTest {
  private static final String URL = "https://example.com";

  @Test
  void createsUserWhenMissing() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    InMemoryUserRepository userRepository = new InMemoryUserRepository();
    CreateLink createLink = new CreateLink(new FixedConfig(Duration.ofHours(1), 3), linkRepository, userRepository);

    Link link = createLink.create(URL);

    assertThat(userRepository.getUserId()).isNotNull();
    assertThat(link.getUserId()).isEqualTo(userRepository.getUserId());
    assertThat(linkRepository.size()).isEqualTo(1);
  }

  @Test
  void returnsSameLinkForSameUserAndUrl() {
    UserId userId = UserId.newId();
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    InMemoryUserRepository userRepository = new InMemoryUserRepository(userId);
    CreateLink createLink = new CreateLink(new FixedConfig(Duration.ofHours(1), 3), linkRepository, userRepository);

    Link first = createLink.create(URL);
    Link second = createLink.create(URL);

    assertThat(second.getShortCode()).isEqualTo(first.getShortCode());
    assertThat(linkRepository.size()).isEqualTo(1);
  }

  @Test
  void createsNewLinkWhenExistingExpired() {
    UserId userId = UserId.newId();
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    InMemoryUserRepository userRepository = new InMemoryUserRepository(userId);
    CreateLink createLink = new CreateLink(new FixedConfig(Duration.ofHours(1), 3), linkRepository, userRepository);

    Link expired =
        new Link(
            userId,
            OriginalUrl.fromString(URL),
            ShortCode.fromString("Ab12Cd34"),
            Instant.now().minusSeconds(3600),
            Instant.now().minusSeconds(10),
            3,
            0);
    linkRepository.save(expired);

    Link created = createLink.create(URL);

    assertThat(created.getShortCode()).isNotEqualTo(expired.getShortCode());
    assertThat(linkRepository.size()).isEqualTo(1);
  }

  @Test
  void createsDifferentShortCodesForDifferentUsers() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    CreateLink createForUser1 =
        new CreateLink(
            new FixedConfig(Duration.ofHours(1), 3),
            linkRepository,
            new InMemoryUserRepository(UserId.newId()));
    CreateLink createForUser2 =
        new CreateLink(
            new FixedConfig(Duration.ofHours(1), 3),
            linkRepository,
            new InMemoryUserRepository(UserId.newId()));

    Link first = createForUser1.create(URL);
    Link second = createForUser2.create(URL);

    assertThat(second.getShortCode()).isNotEqualTo(first.getShortCode());
  }
}
