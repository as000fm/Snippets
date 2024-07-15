### Migrating from Ant to Maven

To achieve similar functionality in a Maven build, you can use plugins to handle properties file updates and incorporate the updated build number into the project version.

### 1. Updating the Java Properties File

You can use the Maven Antrun Plugin to execute Ant tasks within a Maven build. This allows you to reuse your existing Ant targets.

#### Maven Antrun Plugin Configuration
Add the Maven Antrun Plugin to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-antrun-plugin</artifactId>
            <version>3.0.0</version>
            <executions>
                <execution>
                    <id>update-version-build</id>
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
                    <id>update-version-rebuild</id>
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
    </plugins>
</build>
```

### 2. Using the Updated Build Number as the Project Version

To dynamically update the `project.version` value in Maven, you can use the `properties-maven-plugin` and the `buildnumber-maven-plugin` to read the updated build number from the properties file and set it as the project version.

#### Properties Maven Plugin Configuration

Add the `properties-maven-plugin` to read the build number:

```xml
<build>
    <plugins>
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
    </plugins>
</build>
```

#### Buildnumber Maven Plugin Configuration

Add the `buildnumber-maven-plugin` to set the final name:

```xml
<build>
    <plugins>
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
```

#### Setting the Final Name

Set the `<finalName>` in the `build` section to use the build number:

```xml
<build>
    <finalName>${project.artifactId}-${project.version}</finalName>
    <plugins>
        <!-- existing plugins -->
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
```

### Conclusion

By using the Maven Antrun Plugin, you can execute your existing Ant targets within a Maven build. Additionally, with the properties and buildnumber plugins, you can dynamically update the `project.version` and set the final name of your build artifact. This setup allows you to migrate from Ant to Maven while preserving the functionality you need.
