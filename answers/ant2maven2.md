Understood. Since either the `version-build` or `version-rebuild` target is run during the Ant build, we'll need to replicate this selective execution in Maven.

Here’s how you can achieve it in Maven:

1. **Define Profiles for Build and Rebuild**: Use Maven profiles to distinguish between a build and a rebuild. Each profile will run the corresponding Ant target.

2. **Use the Antrun Plugin**: Configure the Antrun Plugin to run the appropriate Ant target based on the active profile.

3. **Update the Project Version**: Ensure the updated build number is reflected in the `project.version`.

### Example Configuration in `pom.xml`

#### Define Maven Profiles

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>your.group.id</groupId>
    <artifactId>your-artifact-id</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <build>
        <finalName>${project.artifactId}-${project.version}</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-antrun-plugin</artifactId>
                <version>3.0.0</version>
                <executions>
                    <execution>
                        <id>run-version-build</id>
                        <phase>generate-resources</phase>
                        <configuration>
                            <tasks>
                                <tstamp>
                                    <format property="builtDateTime" pattern="yyyy-MM-dd HH:mm" />
                                </tstamp>
                                <propertyfile file="src/menu/common/version/VersionConstants.properties" comment="Project version">
                                    <entry key="buildDateTime" value="${builtDateTime}" />
                                    <entry key="buildBy" value="Claude Toupin" />
                                    <entry key="buildNumber" type="int" operation="+"  default="-1" />
                                </propertyfile>
                                <copy file="src/menu/common/version/VersionConstants.properties" tofile="war/WEB-INF/classes/menu/common/version/VersionConstants.properties" overwrite="true" />
                                <echo message="Build number is ${buildNumber}" />
                            </tasks>
                        </configuration>
                        <goals>
                            <goal>run</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>run-version-rebuild</id>
                        <phase>generate-resources</phase>
                        <configuration>
                            <tasks>
                                <tstamp>
                                    <format property="builtDateTime" pattern="yyyy-MM-dd HH:mm" />
                                </tstamp>
                                <property file="src/menu/common/version/VersionConstants.properties" />
                                <propertyfile file="src/menu/common/version/VersionConstants.properties" comment="Project version">
                                    <entry key="buildDateTime" value="${builtDateTime}" />
                                    <entry key="buildBy" value="Claude Toupin" />
                                    <entry key="buildNumber" value="${buildNumber}" />
                                </propertyfile>
                                <copy file="src/menu/common/version/VersionConstants.properties" tofile="war/WEB-INF/classes/menu/common/version/VersionConstants.properties" overwrite="true" />
                                <echo message="Rebuild number is ${buildNumber}" />
                            </tasks>
                        </configuration>
                        <goals>
                            <goal>run</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>properties-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <phase>initialize</phase>
                        <goals>
                            <goal>read-project-properties</goal>
                        </goals>
                        <configuration>
                            <files>
                                <file>src/menu/common/version/VersionConstants.properties</file>
                            </files>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>buildnumber-maven-plugin</artifactId>
                <version>1.4</version>
                <executions>
                    <execution>
                        <phase>validate</phase>
                        <goals>
                            <goal>create</goal>
                        </goals>
                        <configuration>
                            <doCheck>false</doCheck>
                            <doUpdate>false</doUpdate>
                            <revisionOnScmFailure>true</revisionOnScmFailure>
                            <shortRevisionLength>7</shortRevisionLength>
                            <timestampFormat>{0,date,yyyyMMddHHmmss}</timestampFormat>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <profiles>
        <profile>
            <id>build</id>
            <activation>
                <property>
                    <name>env.BUILD_TYPE</name>
                    <value>build</value>
                </property>
            </activation>
            <build>
                <pluginManagement>
                    <plugins>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-antrun-plugin</artifactId>
                            <version>3.0.0</version>
                            <executions>
                                <execution>
                                    <id>run-version-build</id>
                                    <phase>generate-resources</phase>
                                    <goals>
                                        <goal>run</goal>
                                    </goals>
                                </execution>
                            </executions>
                        </plugin>
                    </plugins>
                </pluginManagement>
            </build>
        </profile>
        <profile>
            <id>rebuild</id>
            <activation>
                <property>
                    <name>env.BUILD_TYPE</name>
                    <value>rebuild</value>
                </property>
            </activation>
            <build>
                <pluginManagement>
                    <plugins>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-antrun-plugin</artifactId>
                            <version>3.0.0</version>
                            <executions>
                                <execution>
                                    <id>run-version-rebuild</id>
                                    <phase>generate-resources</phase>
                                    <goals>
                                        <goal>run</goal>
                                    </goals>
                                </execution>
                            </executions>
                        </plugin>
                    </plugins>
                </pluginManagement>
            </build>
        </profile>
    </profiles>
</project>
```

### Usage

To perform a build or rebuild, activate the corresponding profile:

- For build:
  ```sh
  mvn clean install -Denv.BUILD_TYPE=build
  ```

- For rebuild:
  ```sh
  mvn clean install -Denv.BUILD_TYPE=rebuild
  ```

This setup ensures that either the `version-build` or `version-rebuild` Ant target is executed during the Maven build, not both. The properties-maven-plugin and buildnumber-maven-plugin handle reading the updated build number and setting the final name of the build artifact accordingly.

---

Unfortunately, no — there is no flag in `CredUIPromptForCredentialsW` that enforces a non-blank password. The Win32 API has no built-in validation for that.

You have to handle it yourself by re-showing the dialog in a loop until the password is non-empty. The cleanest way is to use `CREDUI_FLAGS_INCORRECT_PASSWORD` on retry, which makes Windows shake the dialog and show an error banner automatically — exactly like a failed login attempt.

Add the constant to your interface:

```java
int CREDUI_FLAGS_INCORRECT_PASSWORD = 0x00000001;
```

Then wrap the call in a loop:

```java
public static Credentials prompt(String caption, String message) {
    Credui.CREDUI_INFO info = new Credui.CREDUI_INFO();
    info.pszCaptionText = new WString(caption);
    info.pszMessageText = new WString(message);
    info.cbSize = info.size();

    IntByReference save = new IntByReference(0); // required by API; checkbox hidden by DO_NOT_PERSIST

    boolean incorrectPassword = false;

    while (true) {
        char[] username = new char[Credui.CREDUI_MAX_USERNAME_LENGTH + 1];
        char[] password = new char[Credui.CREDUI_MAX_PASSWORD_LENGTH + 1];

        // Pre-populate username with current Windows login
        String currentUser = System.getProperty("user.name");
        if (currentUser != null) {
            currentUser.getChars(0, Math.min(currentUser.length(), username.length - 1), username, 0);
        }

        int flags = Credui.CREDUI_FLAGS_GENERIC_CREDENTIALS |
                    Credui.CREDUI_FLAGS_DO_NOT_PERSIST;

        if (incorrectPassword) {
            flags |= Credui.CREDUI_FLAGS_INCORRECT_PASSWORD;
        }

        int result = Credui.INSTANCE.CredUIPromptForCredentialsW(
                info,
                "MyApp",
                null, 0,
                username, username.length,
                password, password.length,
                save,
                flags
        );

        if (result == 1223) { // ERROR_CANCELLED
            throw new RuntimeException("Authentication cancelled by user.");
        }

        if (result != 0) {
            System.err.println("[WARN] Windows credential dialog failed (code " + result
                    + "), falling back to console.");
            Arrays.fill(username, '\0');
            Arrays.fill(password, '\0');
            return promptConsole();
        }

        // Validate password is not blank
        String user = trimNull(username);
        char[] pass = trimNullChars(password);

        Arrays.fill(username, '\0');
        Arrays.fill(password, '\0');

        if (pass.length == 0) {
            incorrectPassword = true; // triggers shake + error banner on next show
            continue;
        }

        return new Credentials(user, pass);
    }
}
```

## What `CREDUI_FLAGS_INCORRECT_PASSWORD` does visually

On the first show — plain dialog, no indication of error. If the user clicks OK with a blank password, the dialog re-appears with a red error banner reading **"The user name or password is incorrect"** and animates a shake. It's the same visual feedback Windows uses for a failed domain login — the user immediately understands something was wrong with their input without you needing to write any error message yourself.
