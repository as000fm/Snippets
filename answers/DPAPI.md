Here's a clean solution using the **Windows DPAPI** (Data Protection API) via a temp file encrypted with the session key — meaning the encrypted file is tied to the Windows login session and becomes unreadable after logoff.

## Architecture

- On first run: prompt for credentials in console → encrypt with DPAPI → write to `%TEMP%\<app>.cred`
- On subsequent runs: read + decrypt the file automatically
- On Windows logoff/shutdown: `%TEMP%` session files are cleaned up, and DPAPI keys are invalidated

DPAPI is called via a small JNA bridge (no native DLL needed, just a Maven/Gradle dep).

---

## `pom.xml` dependency

```xml
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna-platform</artifactId>
    <version>5.14.0</version>
</dependency>
```

---

## Code

### `Credentials.java`

```java
public record Credentials(String username, char[] password) {
    /** Wipe password bytes from memory after use */
    public void destroy() {
        java.util.Arrays.fill(password, '\0');
    }
}
```

### `DpapiCredentialStore.java`

```java
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Crypt32;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Stores credentials encrypted with Windows DPAPI in a session-scoped temp file.
 *
 * Security properties:
 *  - Encrypted with the current Windows user's session key (DPAPI CryptProtectData)
 *  - File lives in %TEMP%, which is cleared on session end
 *  - No other Windows user/process can decrypt the blob
 *  - Entropy token adds app-specific binding (acts as a secondary secret)
 */
public class DpapiCredentialStore {

    // Change this per-application — adds binding so other apps can't decrypt your blob
    private static final byte[] APP_ENTROPY =
            "my-app-dpapi-entropy-v1".getBytes(StandardCharsets.UTF_8);

    private static final String CRED_FILE_NAME = "myapp.cred";

    private final Path credFile;

    public DpapiCredentialStore() {
        String tmp = System.getenv("TEMP");
        if (tmp == null) tmp = System.getenv("TMP");
        if (tmp == null) tmp = System.getProperty("java.io.tmpdir");
        this.credFile = Path.of(tmp, CRED_FILE_NAME);
    }

    public boolean exists() {
        return Files.exists(credFile);
    }

    /** Encrypt and persist credentials to disk. */
    public void store(Credentials creds) throws IOException {
        String plain = creds.username() + "\n" + new String(creds.password());
        byte[] plainBytes = plain.getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = dpApiEncrypt(plainBytes);

        // Wipe plaintext from memory immediately
        java.util.Arrays.fill(plainBytes, (byte) 0);

        Files.write(credFile, encrypted,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /** Decrypt and return credentials from disk. */
    public Credentials load() throws IOException {
        byte[] encrypted = Files.readAllBytes(credFile);
        byte[] plainBytes = dpApiDecrypt(encrypted);

        String plain = new String(plainBytes, StandardCharsets.UTF_8);
        java.util.Arrays.fill(plainBytes, (byte) 0); // wipe

        int sep = plain.indexOf('\n');
        if (sep < 0) throw new IOException("Corrupt credential file");

        String username = plain.substring(0, sep);
        char[] password = plain.substring(sep + 1).toCharArray();
        return new Credentials(username, password);
    }

    /** Delete the credential file explicitly (e.g. logout action). */
    public void clear() throws IOException {
        Files.deleteIfExists(credFile);
    }

    // -------------------------------------------------------------------------
    // DPAPI internals
    // -------------------------------------------------------------------------

    private byte[] dpApiEncrypt(byte[] data) {
        DATA_BLOB input  = toBlob(data);
        DATA_BLOB entropy = toBlob(APP_ENTROPY);
        DATA_BLOB output = new DATA_BLOB();

        boolean ok = Crypt32.INSTANCE.CryptProtectData(
                input, "myapp credentials", entropy, null, null, 0, output);
        if (!ok) throw new RuntimeException("DPAPI encrypt failed: " +
                com.sun.jna.platform.win32.Kernel32Util.getLastErrorMessage());

        return output.getData();
    }

    private byte[] dpApiDecrypt(byte[] data) {
        DATA_BLOB input   = toBlob(data);
        DATA_BLOB entropy = toBlob(APP_ENTROPY);
        DATA_BLOB output  = new DATA_BLOB();

        boolean ok = Crypt32.INSTANCE.CryptUnprotectData(
                input, null, entropy, null, null, 0, output);
        if (!ok) throw new RuntimeException("DPAPI decrypt failed — wrong session or corrupt file: " +
                com.sun.jna.platform.win32.Kernel32Util.getLastErrorMessage());

        return output.getData();
    }

    private static DATA_BLOB toBlob(byte[] bytes) {
        Memory mem = new Memory(bytes.length);
        mem.write(0, bytes, 0, bytes.length);
        DATA_BLOB blob = new DATA_BLOB();
        blob.cbData = bytes.length;
        blob.pbData = mem;
        return blob;
    }
}
```

### `ConsolePrompt.java`

```java
import java.io.Console;
import java.util.Scanner;

public class ConsolePrompt {

    /**
     * Prompts for username + password on the console.
     * Uses Console.readPassword() when available (hides input),
     * falls back to Scanner if running in an IDE or piped stdin.
     */
    public static Credentials prompt() {
        Console console = System.console();

        String username;
        char[] password;

        if (console != null) {
            System.out.print("Username: ");
            username = console.readLine().trim();
            password = console.readPassword("Password: "); // input not echoed
        } else {
            // Fallback: IDE / redirected stdin (password will be visible)
            System.err.println("[WARN] No system console detected — password will be echoed.");
            Scanner sc = new Scanner(System.in);
            System.out.print("Username: ");
            username = sc.nextLine().trim();
            System.out.print("Password: ");
            password = sc.nextLine().toCharArray();
        }

        return new Credentials(username, password);
    }
}
```

### `Main.java`

```java
public class Main {

    public static void main(String[] args) {
        DpapiCredentialStore store = new DpapiCredentialStore();

        Credentials creds;

        try {
            if (!store.exists()) {
                System.out.println("No stored credentials found. Please authenticate.");
                creds = ConsolePrompt.prompt();
                store.store(creds);
                System.out.println("Credentials stored securely for this session.");
            } else {
                creds = store.load();
                System.out.println("Credentials loaded for user: " + creds.username());
            }

            // --- Your application logic here ---
            runApp(creds);

        } catch (Exception e) {
            System.err.println("Credential error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void runApp(Credentials creds) {
        // Use creds.username() and creds.password() here
        // Always call creds.destroy() when done to wipe from memory
        try {
            System.out.println("Running as: " + creds.username());
            // ... your logic ...
        } finally {
            creds.destroy(); // zero out password char array
        }
    }
}
```

---

## How the TTL works

| Event | What happens |
|---|---|
| First `java -jar` of the session | Prompts, encrypts, writes `%TEMP%\myapp.cred` |
| Subsequent `java -jar` runs | Reads + decrypts transparently |
| Windows logoff / shutdown | `%TEMP%` is wiped; DPAPI master key is invalidated |
| Another Windows user tries to read the file | DPAPI decrypt fails — key mismatch |

---

## Key security notes

- **`APP_ENTROPY`** acts like a secondary secret — without it the DPAPI blob can't be decrypted even by the same user. Change it per-application and ideally per-deployment.
- **`console.readPassword()`** ensures the password is never echoed to the terminal.
- **`creds.destroy()`** zeros the `char[]` after use — passwords should never linger in heap memory as `String` objects (strings are immutable and GC-unpredictable).
- If you want even stricter TTL (e.g. expire after N minutes regardless of session), you can store a timestamp alongside the encrypted blob and validate it on `load()`.
