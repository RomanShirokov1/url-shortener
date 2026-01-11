package dev.shorty.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import dev.shorty.core.model.Link;
import dev.shorty.core.model.OriginalUrl;
import dev.shorty.core.model.ShortCode;
import dev.shorty.core.model.UserId;
import dev.shorty.core.support.InMemoryLinkRepository;
import dev.shorty.core.support.InMemoryUserRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class LinkManagementTest {
  @Test
  void listLinksReturnsEmptyWhenNoUser() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    InMemoryUserRepository userRepository = new InMemoryUserRepository();
    ListLinks listLinks = new ListLinks(linkRepository, userRepository);

    assertThat(listLinks.listForCurrentUser()).isEmpty();
  }

  @Test
  void listLinksReturnsOnlyCurrentUserLinks() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    UserId userId = UserId.newId();
    UserId other = UserId.newId();
    linkRepository.save(linkFor(userId, "Ab12Cd34"));
    linkRepository.save(linkFor(other, "Ef56Gh78"));
    InMemoryUserRepository userRepository = new InMemoryUserRepository(userId);
    ListLinks listLinks = new ListLinks(linkRepository, userRepository);

    assertThat(listLinks.listForCurrentUser()).extracting(Link::getShortCode).hasSize(1);
  }

  @Test
  void getLinkInfoReturnsEmptyWhenNotOwner() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    linkRepository.save(linkFor(UserId.newId(), "Ab12Cd34"));
    InMemoryUserRepository userRepository = new InMemoryUserRepository(UserId.newId());
    GetLinkInfo getLinkInfo = new GetLinkInfo(linkRepository, userRepository);

    assertThat(getLinkInfo.getForCurrentUser("Ab12Cd34")).isEmpty();
  }

  @Test
  void editLinkUpdatesMaxClicksForOwner() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    UserId userId = UserId.newId();
    linkRepository.save(linkFor(userId, "Ab12Cd34"));
    InMemoryUserRepository userRepository = new InMemoryUserRepository(userId);
    EditLink editLink = new EditLink(linkRepository, userRepository);

    editLink.updateMaxClicks("Ab12Cd34", 10);

    Link updated = linkRepository.findByShortCode(ShortCode.fromString("Ab12Cd34")).get();
    assertThat(updated.getMaxClicks()).isEqualTo(10);
  }

  @Test
  void deleteLinkRemovesForOwner() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    UserId userId = UserId.newId();
    linkRepository.save(linkFor(userId, "Ab12Cd34"));
    InMemoryUserRepository userRepository = new InMemoryUserRepository(userId);
    DeleteLink deleteLink = new DeleteLink(linkRepository, userRepository);

    boolean deleted = deleteLink.deleteForCurrentUser("Ab12Cd34");

    assertThat(deleted).isTrue();
    assertThat(linkRepository.size()).isEqualTo(0);
  }

  @Test
  void cleanupExpiredRemovesOnlyExpiredLinks() {
    InMemoryLinkRepository linkRepository = new InMemoryLinkRepository();
    linkRepository.save(expiredLink("Ab12Cd34"));
    linkRepository.save(activeLink("Ef56Gh78"));
    CleanupExpired cleanupExpired = new CleanupExpired(linkRepository);

    int removed = cleanupExpired.cleanup();

    assertThat(removed).isEqualTo(1);
    assertThat(linkRepository.size()).isEqualTo(1);
  }

  private Link linkFor(UserId userId, String shortCode) {
    return new Link(
        userId,
        OriginalUrl.fromString("https://example.com"),
        ShortCode.fromString(shortCode),
        Instant.now().minusSeconds(10),
        Instant.now().plusSeconds(3600),
        3,
        0);
  }

  private Link expiredLink(String shortCode) {
    return new Link(
        UserId.newId(),
        OriginalUrl.fromString("https://example.com"),
        ShortCode.fromString(shortCode),
        Instant.now().minusSeconds(3600),
        Instant.now().minusSeconds(10),
        3,
        0);
  }

  private Link activeLink(String shortCode) {
    return new Link(
        UserId.newId(),
        OriginalUrl.fromString("https://example.com"),
        ShortCode.fromString(shortCode),
        Instant.now().minusSeconds(10),
        Instant.now().plusSeconds(3600),
        3,
        0);
  }
}
