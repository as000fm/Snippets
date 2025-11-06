Totally fair. Here are two **drop-in** ways that ignore Tomcat/JULI config and just write to the file you choose.

---

# Option A — Tiny logger class (rotates optional, thread-safe)

**File:** `FileLog.java`

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
  private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private FileLog() {}

  /** Get a java.util.logging.Logger that writes ONLY to the given file (append=true). */
  public static Logger get(String absolutePath) {
    return CACHE.computeIfAbsent(absolutePath, FileLog::createLogger);
  }

  private static Logger createLogger(String path) {
    try {
      // Ensure folder exists
      Path p = Paths.get(path);
      Files.createDirectories(p.getParent());

      Logger logger = Logger.getLogger("FileLog@" + path);
      logger.setUseParentHandlers(false);           // no console
      logger.setLevel(Level.ALL);

      // Append mode; no size rotation. If you want rotation, see comment below.
      FileHandler fh = new FileHandler(path, /*append*/ true);
      fh.setLevel(Level.ALL);
      fh.setFormatter(new Formatter() {
        @Override public String format(LogRecord r) {
          String ts = ISO.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(r.getMillis()), ZoneId.systemDefault()));
          String msg = formatMessage(r);
          StringBuilder sb = new StringBuilder(256)
            .append(ts).append(" ").append(r.getLevel().getName())
            .append(" [").append(Thread.currentThread().getName()).append("] ")
            .append(r.getLoggerName()).append(" - ").append(msg).append(System.lineSeparator());
          if (r.getThrown() != null) {
            try {
              String stack = getStackTrace(r.getThrown());
              sb.append(stack);
            } catch (Exception ignore) {}
          }
          return sb.toString();
        }
      });
      logger.addHandler(fh);

      // Close handlers on JVM shutdown
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        for (Handler h : logger.getHandlers()) try { h.close(); } catch (Exception ignored) {}
      }));

      return logger;
    } catch (IOException e) {
      throw new RuntimeException("Cannot create log file: " + path, e);
    }
  }

  private static String getStackTrace(Throwable t) {
    java.io.StringWriter sw = new java.io.StringWriter();
    t.printStackTrace(new java.io.PrintWriter(sw));
    return sw.toString();
  }
}
```

**Use it anywhere (servlet, service, etc.):**

```java
import your.pkg.FileLog;
import java.util.logging.Logger;

public class Demo {
  private static final Logger LOG =
      FileLog.get(System.getProperty("user.home") + "/myapp.log");
  // or Paths.get(System.getProperty("catalina.base"), "logs", "myapp.log").toString()

  public void run() {
    LOG.info("It works");
    try { throw new IllegalStateException("Boom"); }
    catch (Exception e) { LOG.log(java.util.logging.Level.WARNING, "Problem", e); }
  }
}
```

> Want simple size rotation? Replace the `FileHandler` with:
>
> ```java
> // 5 MB per file, keep 5 files, not append (JUL rotates via index)
> FileHandler fh = new FileHandler(path, 5 * 1024 * 1024, 5, true);
> ```

---

# Option B — Absolute simplest: append a line to a file

**File:** `Log.java`

```java
package your.pkg;

import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;

public final class Log {
  private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private Log() {}

  public static void info(String file, String message) { write(file, "INFO", message, null); }
  public static void warn(String file, String message, Throwable t) { write(file, "WARN", message, t); }

  public static synchronized void write(String file, String level, String message, Throwable t) {
    try {
      Path p = Paths.get(file);
      Files.createDirectories(p.getParent());
      String ts = ISO.format(LocalDateTime.now());
      StringBuilder sb = new StringBuilder()
          .append(ts).append(" ").append(level).append(" ").append(message).append(System.lineSeparator());
      if (t != null) {
        java.io.StringWriter sw = new java.io.StringWriter();
        t.printStackTrace(new java.io.PrintWriter(sw));
        sb.append(sw).append(System.lineSeparator());
      }
      Files.write(p, sb.toString().getBytes(StandardCharsets.UTF_8),
          StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (Exception ignored) {}
  }
}
```

**Use:**

```java
Log.info("C:/temp/myapp.log", "Started");
try { /* ... */ } catch (Exception e) { Log.warn("C:/temp/myapp.log", "Failure", e); }
```

---

### Pick one

* Want levels, handlers, rotation later? **Option A** (JUL `FileHandler`) is tiny and robust.
* Want “just write to a file now”? **Option B** is the simplest possible.

If you tell me your desired path (e.g., `${catalina.base}\\logs\\myapp.log`), I’ll plug it into one of these for you.
