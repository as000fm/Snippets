Here's the rewritten class:

```java
import com.sun.jna.platform.win32.Credui;
import com.sun.jna.platform.win32.WinCred.CREDUI_INFO;
import com.sun.jna.ptr.IntByReference;

public class ConsolePrompt {

    /**
     * Shows the native Windows credential dialog.
     * Falls back to console prompt if the dialog fails.
     */
    public static Credentials prompt(String caption, String message) {
        CREDUI_INFO info = new CREDUI_INFO();
        info.pszCaptionText = caption;
        info.pszMessageText = message;
        info.cbSize = info.size();

        char[] username = new char[Credui.CREDUI_MAX_USERNAME_LENGTH + 1];
        char[] password = new char[Credui.CREDUI_MAX_PASSWORD_LENGTH + 1];
        IntByReference save = new IntByReference(0); // "save" checkbox state, we ignore it

        int result = Credui.INSTANCE.CredUIPromptForCredentials(
                info,
                "MyApp",                              // target name (used as key internally)
                null, 0,                              // no auth error to show
                username, username.length,
                password, password.length,
                save,
                Credui.CREDUI_FLAGS_GENERIC_CREDENTIALS  |
                Credui.CREDUI_FLAGS_ALWAYS_SHOW_UI       |
                Credui.CREDUI_FLAGS_DO_NOT_PERSIST       |
                Credui.CREDUI_FLAGS_EXCLUDE_CERTIFICATES  // username+password only, no certs
        );

        if (result == 0) { // ERROR_SUCCESS
            // Trim null padding from the char arrays
            String user = trimNull(username);
            char[] pass = trimNullChars(password);

            // Wipe the raw buffers immediately
            java.util.Arrays.fill(username, '\0');
            java.util.Arrays.fill(password, '\0');

            return new Credentials(user, pass);
        }

        if (result == 1223) { // ERROR_CANCELLED — user hit Cancel
            throw new RuntimeException("Authentication cancelled by user.");
        }

        // Any other Win32 error — fall back to console
        System.err.println("[WARN] Windows credential dialog failed (code " + result + "), falling back to console.");
        return promptConsole();
    }

    /** Convenience overload with default strings. */
    public static Credentials prompt() {
        return prompt("MyApp Authentication", "Enter your credentials to continue.");
    }

    // -------------------------------------------------------------------------
    // Console fallback (kept from original — useful in CI / headless envs)
    // -------------------------------------------------------------------------

    private static Credentials promptConsole() {
        java.io.Console console = System.console();

        String username;
        char[] password;

        if (console != null) {
            System.out.print("Username: ");
            username = console.readLine().trim();
            password = console.readPassword("Password: ");
        } else {
            System.err.println("[WARN] No system console detected — password will be echoed.");
            java.util.Scanner sc = new java.util.Scanner(System.in);
            System.out.print("Username: ");
            username = sc.nextLine().trim();
            System.out.print("Password: ");
            password = sc.nextLine().toCharArray();
        }

        return new Credentials(username, password);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Converts char[] to String, stopping at first null char. */
    private static String trimNull(char[] chars) {
        int len = 0;
        while (len < chars.length && chars[len] != '\0') len++;
        return new String(chars, 0, len);
    }

    /** Returns a trimmed char[] copy, stopping at first null char. */
    private static char[] trimNullChars(char[] chars) {
        int len = 0;
        while (len < chars.length && chars[len] != '\0') len++;
        char[] trimmed = new char[len];
        System.arraycopy(chars, 0, trimmed, 0, len);
        return trimmed;
    }
}
```

## What changed and why

**`trimNull` / `trimNullChars`** — the Win32 API fills its output buffers with null padding up to the max length. Without trimming, your username would be `"alice\0\0\0\0..."` and your password comparison would silently fail downstream.

**Wiping the raw buffers** — `username[]` and `password[]` are zeroed immediately after copying, before returning. The only live copy of the password is inside the returned `Credentials` object, which you already wipe via `creds.destroy()`.

**`ERROR_CANCELLED` (1223)** — explicitly handled so you get a clean message instead of a cryptic Win32 error code when the user hits Cancel.

**Console fallback kept** — if the dialog fails for any reason (headless server, RDP session quirk, etc.), it degrades gracefully to the console path rather than crashing hard.

**`CREDUI_FLAGS_EXCLUDE_CERTIFICATES`** — prevents the dialog from offering smart card / certificate options, keeping it to a plain username + password form.
