package dev.shorty.cli.command;

import dev.shorty.core.usecase.OpenLink;
import java.awt.Desktop;
import java.net.URI;
import java.util.Objects;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "open", description = "Open a short link in the browser")
public class OpenCommand implements Runnable {
  private final OpenLink openLink;

  @Parameters(index = "0", description = "Short code")
  private String shortCode;

  public OpenCommand(OpenLink openLink) {
    this.openLink = Objects.requireNonNull(openLink, "openLink");
  }

  @Override
  public void run() {
    OpenLink.Result result = openLink.open(shortCode);
    if (result.getStatus() != OpenLink.Status.OK) {
      return;
    }
    try {
      URI uri = result.getLink().getOriginalUrl().toUri();
      Desktop.getDesktop().browse(uri);
      System.out.println("Открыто: " + uri);
    } catch (Exception e) {
      throw new IllegalStateException("Не удалось открыть браузер", e);
    }
  }
}
