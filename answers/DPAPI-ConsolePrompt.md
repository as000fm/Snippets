Here's the corrected `ConsolePrompt.java` that defines its own JNA binding inline:

```java
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.List;

public class ConsolePrompt {

    // -------------------------------------------------------------------------
    // Inline JNA binding for credui.dll — NOT in jna-platform
    // -------------------------------------------------------------------------

    interface Credui extends Library {

        Credui INSTANCE = Native.load("credui", Credui.class);

        // Constants from wincred.h
        int CREDUI_MAX_USERNAME_LENGTH = 513;
        int CREDUI_MAX_PASSWORD_LENGTH = 256;

        int CREDUI_FLAGS_GENERIC_CREDENTIALS   = 0x00040000;
        int CREDUI_FLAGS_DO_NOT_PERSIST        = 0x00000002;

        // Maps to CREDUI_INFO struct (wincred.h)
        class CREDUI_INFO extends Structure {
            public int     cbSize;
            public HWND    hwndParent;
            public WString pszMessageText;
            public WString pszCaptionText;
            public HBITMAP hbmBanner;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("cbSize", "hwndParent",
                        "pszMessageText", "pszCaptionText", "hbmBanner");
            }
        }

        // Maps to CredUIPromptForCredentialsW (Unicode version)
        int CredUIPromptForCredentialsW(
                CREDUI_INFO pUiInfo,
                String      pszTargetName,
                Object      pContext,       // always null
                int         dwAuthError,
                char[]      pszUserName,
                int         ulUserNameBufferSize,
                char[]      pszPassword,
                int         ulPasswordBufferSize,
                IntByReference pfSave,
                int         dwFlags
        );
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public static Credentials prompt(String caption, String message) {
        Credui.CREDUI_INFO info = new Credui.CREDUI_INFO();
        info.pszCaptionText = new WString(caption);
        info.pszMessageText = new WString(message);
        info.cbSize = info.size();

        char[] username = new char[Credui.CREDUI_MAX_USERNAME_LENGTH + 1];
        char[] password = new char[Credui.CREDUI_MAX_PASSWORD_LENGTH + 1];
        IntByReference save = new IntByReference(0);

        int result = Credui.INSTANCE.CredUIPromptForCredentialsW(
                info,
                "MyApp",
                null, 0,
                username, username.length,
                password, password.length,
                save,
                Credui.CREDUI_FLAGS_GENERIC_CREDENTIALS  |
                Credui.CREDUI_FLAGS_DO_NOT_PERSIST
        );

        if (result == 0) { // ERROR_SUCCESS
            String user = trimNull(username);
            char[] pass = trimNullChars(password);
            Arrays.fill(username, '\0');
            Arrays.fill(password, '\0');
            return new Credentials(user, pass);
        }

        if (result == 1223) { // ERROR_CANCELLED
            throw new RuntimeException("Authentication cancelled by user.");
        }

        // Fallback to console on any other error
        System.err.println("[WARN] Windows credential dialog failed (code " + result
                + "), falling back to console.");
        return promptConsole();
    }

    public static Credentials prompt() {
        return prompt("MyApp Authentication", "Enter your credentials to continue.");
    }

    // -------------------------------------------------------------------------
    // Console fallback
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
            System.err.println("[WARN] No system console — password will be echoed.");
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

    private static String trimNull(char[] chars) {
        int len = 0;
        while (len < chars.length && chars[len] != '\0') len++;
        return new String(chars, 0, len);
    }

    private static char[] trimNullChars(char[] chars) {
        int len = 0;
        while (len < chars.length && chars[len] != '\0') len++;
        char[] out = new char[len];
        System.arraycopy(chars, 0, out, 0, len);
        return out;
    }
}
```

## What changed and why

**No more missing imports** — `Credui`, `CREDUI_INFO`, and the flag constants are all defined right here inside the class. Nothing is expected from `jna-platform` for this binding.

**`Native.load()` instead of `Native.loadLibrary()`** — `loadLibrary` is deprecated since JNA 5.x; `load()` is the correct modern call.

**`CredUIPromptForCredentialsW` explicitly** — calling the `W` (Unicode/wide) suffix directly avoids JNA having to guess the charset mapping, which is the safer practice on Java where strings are always Unicode.

**The only `jna-platform` types used** — `HWND` and `HBITMAP` from `WinDef`, and `IntByReference` from `com.sun.jna.ptr` — those genuinely are in the jar and compile cleanly.

---

Yes, easy. You just need to get the current Windows username and pre-fill the `username` buffer before the call.

No extra JNA needed — `System.getProperty("user.name")` gives you the Windows login name reliably in Java. Just copy it into the `char[]` buffer before passing it to `CredUIPromptForCredentialsW`:

```java
// Pre-fill username with current Windows user
char[] username = new char[Credui.CREDUI_MAX_USERNAME_LENGTH + 1];
String currentUser = System.getProperty("user.name");
if (currentUser != null) {
    currentUser.getChars(0, Math.min(currentUser.length(), username.length - 1), username, 0);
}
```

The dialog will show the current user's name already filled in, and the user can still edit it if needed.

Also add `CREDUI_FLAGS_KEEP_USERNAME` to the flags if you want the username field to be **read-only** (shown but not editable):

```java
// Add this constant to the Credui interface if you want read-only username
int CREDUI_FLAGS_KEEP_USERNAME = 0x00100000;
```

Then the call becomes:

```java
int result = Credui.INSTANCE.CredUIPromptForCredentialsW(
        info,
        "MyApp",
        null, 0,
        username, username.length,
        password, password.length,
        save,
        Credui.CREDUI_FLAGS_GENERIC_CREDENTIALS |
        Credui.CREDUI_FLAGS_DO_NOT_PERSIST
        // | Credui.CREDUI_FLAGS_KEEP_USERNAME   // uncomment to lock the field
);
```

So the updated full `prompt()` method looks like this:

```java
public static Credentials prompt(String caption, String message) {
    Credui.CREDUI_INFO info = new Credui.CREDUI_INFO();
    info.pszCaptionText = new WString(caption);
    info.pszMessageText = new WString(message);
    info.cbSize = info.size();

    char[] username = new char[Credui.CREDUI_MAX_USERNAME_LENGTH + 1];
    char[] password = new char[Credui.CREDUI_MAX_PASSWORD_LENGTH + 1];

    // Pre-populate username with current Windows login
    String currentUser = System.getProperty("user.name");
    if (currentUser != null) {
        currentUser.getChars(0, Math.min(currentUser.length(), username.length - 1), username, 0);
    }

    IntByReference save = new IntByReference(0);

    int result = Credui.INSTANCE.CredUIPromptForCredentialsW(
            info,
            "MyApp",
            null, 0,
            username, username.length,
            password, password.length,
            save,
            Credui.CREDUI_FLAGS_GENERIC_CREDENTIALS |
            Credui.CREDUI_FLAGS_DO_NOT_PERSIST
    );

    if (result == 0) {
        String user = trimNull(username);
        char[] pass = trimNullChars(password);
        Arrays.fill(username, '\0');
        Arrays.fill(password, '\0');
        return new Credentials(user, pass);
    }

    if (result == 1223) {
        throw new RuntimeException("Authentication cancelled by user.");
    }

    System.err.println("[WARN] Windows credential dialog failed (code " + result
            + "), falling back to console.");
    return promptConsole();
}
```

The key line is `getChars()` — it copies the string characters directly into the `char[]` buffer at offset 0, leaving the rest as null padding, which is exactly what the Win32 API expects.

---

It maps to the `pfSave` parameter of `CredUIPromptForCredentialsW`, which controls the **"Remember my credentials"** checkbox in the dialog.

## What `IntByReference` means here

In Win32, `pfSave` is declared as `BOOL*` — a pointer to a boolean integer. JNA has no `BoolByReference`, so `IntByReference` is the correct mapping. The `0` in the constructor is the **initial value** written into that pointer before the call, meaning the checkbox starts **unchecked**.

## What it "saves"

Nothing in your case — and that's intentional.

The parameter has a dual role:

**Before the call** — you set it to `0` (unchecked) or `1` (checked) to control the initial state of the "Remember my credentials" checkbox that Windows would normally show in the dialog.

**After the call** — Windows writes back into it to tell you whether the user checked that box when they clicked OK.

Since you're using `CREDUI_FLAGS_DO_NOT_PERSIST`, Windows suppresses that checkbox entirely from the UI — it won't even appear. The user never sees it. So the value written back into `save` after the call is meaningless in your scenario, and you can safely ignore it.

## If you weren't using `DO_NOT_PERSIST`

The pattern would be:

```java
IntByReference save = new IntByReference(0); // checkbox starts unchecked

// ... call the dialog ...

if (save.getValue() == 1) {
    // user checked "Remember my credentials"
    // you'd be expected to persist them yourself
}
```

## Bottom line for your code

You need to pass a non-null pointer there because the Win32 signature requires it — passing `null` would risk an access violation. But since `DO_NOT_PERSIST` hides the checkbox, the `IntByReference` is essentially just a placeholder to satisfy the native call signature. You can document it as such:

```java
IntByReference save = new IntByReference(0); // required by API; checkbox hidden by DO_NOT_PERSIST flag
```
