package dev.shorty.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.OriginalUrl;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.support.CapturingNotificationPort;
import dev.shorty.core.support.InMemoryLinkRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OpenLinkTest {
  @Test
  void returnsNotFoundWhenMissingAndNotifies() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    CapturingNotificationPort notificationPort = new CapturingNotificationPort();
    OpenLink openLink = new OpenLink(linkRepository, notificationPort);

    OpenLink.Result result = openLink.open("Ab12Cd34");

    assertThat(result.getStatus()).isEqualTo(OpenLink.Status.NOT_FOUND);
    assertThat(notificationPort.getNotFound()).hasSize(1);
  }

  @Test
  void returnsExpiredDeletesAndNotifies() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    CapturingNotificationPort notificationPort = new CapturingNotificationPort();
    OpenLink openLink = new OpenLink(linkRepository, notificationPort);
    Link link =
        new Link(
            UserId.newId(),
            OriginalUrl.fromString("https://example.com"),
            ShortCode.fromString("Ab12Cd34"),
            Instant.now().minusSeconds(3600),
            Instant.now().minusSeconds(10),
            3,
            0);
    linkRepository.save(link);

    OpenLink.Result result = openLink.open("Ab12Cd34");

    assertThat(result.getStatus()).isEqualTo(OpenLink.Status.EXPIRED);
    assertThat(notificationPort.getExpired()).hasSize(1);
    assertThat(linkRepository.size()).isEqualTo(0);
  }

  @Test
  void returnsLimitReachedAndNotifies() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    CapturingNotificationPort notificationPort = new CapturingNotificationPort();
    OpenLink openLink = new OpenLink(linkRepository, notificationPort);
    Link link =
        new Link(
            UserId.newId(),
            OriginalUrl.fromString("https://example.com"),
            ShortCode.fromString("Ab12Cd34"),
            Instant.now().minusSeconds(10),
            Instant.now().plusSeconds(3600),
            1,
            1);
    linkRepository.save(link);

    OpenLink.Result result = openLink.open("Ab12Cd34");

    assertThat(result.getStatus()).isEqualTo(OpenLink.Status.LIMIT_REACHED);
    assertThat(notificationPort.getLimitReached()).hasSize(1);
  }

  @Test
  void incrementsClickCountAndSavesWhenOk() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    CapturingNotificationPort notificationPort = new CapturingNotificationPort();
    OpenLink openLink = new OpenLink(linkRepository, notificationPort);
    Link link =
        new Link(
            UserId.newId(),
            OriginalUrl.fromString("https://example.com"),
            ShortCode.fromString("Ab12Cd34"),
            Instant.now().minusSeconds(10),
            Instant.now().plusSeconds(3600),
            2,
            0);
    linkRepository.save(link);

    OpenLink.Result result = openLink.open("Ab12Cd34");

    assertThat(result.getStatus()).isEqualTo(OpenLink.Status.OK);
    assertThat(linkRepository.findByShortCode(ShortCode.fromString("Ab12Cd34")))
        .get()
        .extracting(Link::getClickCount)
        .isEqualTo(1);
  }

  @Test
  void returnsNotFoundForInvalidShortCode() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    CapturingNotificationPort notificationPort = new CapturingNotificationPort();
    OpenLink openLink = new OpenLink(linkRepository, notificationPort);

    OpenLink.Result result = openLink.open("bad");

    assertThat(result.getStatus()).isEqualTo(OpenLink.Status.NOT_FOUND);
    assertThat(notificationPort.getNotFound()).hasSize(1);
  }
}
