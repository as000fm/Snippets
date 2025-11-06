You’re right—that happened because I used the file path in the logger **name** (so the formatter printed it). Here’s a cleaned-up version of **Option A** that:

* keeps a per-file logger cache (keyed by the absolute path),
* uses a short **displayName** (defaults to the filename only),
* and **does not** print the full path anywhere.

# Drop-in: `FileLog.java` (clean display)

```java
package your.pkg;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public final class FileLog {
  private static final ConcurrentHashMap<String, Logger> CACHE = new ConcurrentHashMap<>();
  private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private FileLog() {}

  /** Get a logger that writes ONLY to the given file. Log lines show a short display name (filename). */
  public static Logger get(String absolutePath) {
    return get(absolutePath, Paths.get(absolutePath).getFileName().toString());
  }

  /** Same, but let you choose a short display name (e.g., "myapp"). */
  public static Logger get(String absolutePath, String displayName) {
    return CACHE.computeIfAbsent(absolutePath, p -> createLogger(p, displayName));
  }

  private static Logger createLogger(String path, String displayName) {
    try {
      Path p = Paths.get(path);
      Path dir = p.getParent();
      if (dir != null) Files.createDirectories(dir);

      // Stable, generic logger name (no path leakage)
      Logger logger = Logger.getLogger("your.pkg.FileLog");
      logger.setUseParentHandlers(false);
      logger.setLevel(Level.ALL);

      // Size-rotating file handler (5 MB, keep 5 files); set 'true' to append to current file
      FileHandler fh = new FileHandler(path, 5 * 1024 * 1024, 5, true);
      fh.setLevel(Level.ALL);
      fh.setFormatter(new Formatter() {
        @Override public String format(LogRecord r) {
          String ts = TS.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(r.getMillis()), ZoneId.systemDefault()));
          StringBuilder sb = new StringBuilder(256)
              .append(ts).append(' ')
              .append(r.getLevel().getName()).append(' ')
              .append('[').append(Thread.currentThread().getName()).append(']').append(' ')
              .append(displayName).append(" - ")
              .append(formatMessage(r)).append(System.lineSeparator());
          if (r.getThrown() != null) {
            java.io.StringWriter sw = new java.io.StringWriter();
            r.getThrown().printStackTrace(new java.io.PrintWriter(sw));
            sb.append(sw);
          }
          return sb.toString();
        }
      });
      logger.addHandler(fh);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        for (Handler h : logger.getHandlers()) try { h.close(); } catch (Exception ignored) {}
      }));

      return logger;
    } catch (IOException e) {
      throw new RuntimeException("Cannot create log file: " + path, e);
    }
  }
}
```

## Use it (no path printed in lines)

```java
import your.pkg.FileLog;
import java.util.logging.*;

public class Demo {
  private static final Logger LOG =
      FileLog.get(Paths.get(System.getProperty("catalina.base"), "logs", "myapp.log").toString(),
                  "myapp"); // short display name

  public void run() {
    LOG.info("Started");
    LOG.warning("Heads up");
    try { throw new IllegalStateException("Boom"); }
    catch (Exception e) { LOG.log(Level.SEVERE, "Failure", e); }
  }
}
```

### Notes

* If you want **no name at all**, just remove `displayName` from the formatter line entirely.
* If you prefer **no rotation**, swap the handler for:
  `FileHandler fh = new FileHandler(path, /*append*/ true);`
* This is totally independent of Tomcat/JULI; it will write to your file regardless of server logging config.
