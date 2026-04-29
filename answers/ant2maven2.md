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
