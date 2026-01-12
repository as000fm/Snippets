Yes — but the *right* fix usually isn’t “restart the pool from the WAR”, it’s to configure the pool so it **detects dead SQL Server connections and replaces them automatically** after the MSSQL service restarts.

When SQL Server restarts, every TCP connection in the pool becomes invalid. If the pool doesn’t validate connections before handing them out (or via an idle-evictor), it can keep returning “dead” connections forever.

## What to do (recommended): enable connection validation + (optionally) idle eviction

### If your JNDI DataSource uses **DBCP2** (common default in Tomcat 9)

Add (or ensure you have) these attributes on the `<Resource ...>`:

```xml
<Resource name="jdbc/MyDb"
          auth="Container"
          type="javax.sql.DataSource"
          factory="org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory"

          driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
          url="jdbc:sqlserver://DBHOST:1433;databaseName=MyDb;encrypt=true;trustServerCertificate=true"
          username="..."
          password="..."

          testOnBorrow="true"
          validationQuery="SELECT 1"
          validationQueryTimeout="5"

          testWhileIdle="true"
          timeBetweenEvictionRunsMillis="30000"
          numTestsPerEvictionRun="3"
          minEvictableIdleTimeMillis="60000"
/>
```

* `testOnBorrow + validationQuery` makes the pool check the connection **every time** it’s borrowed; if it’s broken, it gets discarded and a new one is created. ([tomcat.apache.org][1])
* The “evictor” (`timeBetweenEvictionRunsMillis` + `testWhileIdle`) also cleans up broken/idle connections in the background. ([tomcat.apache.org][1])

### If you’re using **Tomcat JDBC Pool** (`org.apache.tomcat.jdbc.pool`)

Make sure your resource explicitly uses the JDBC pool factory, then add the same validation idea:

```xml
<Resource name="jdbc/MyDb"
          auth="Container"
          type="javax.sql.DataSource"
          factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"

          driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
          url="jdbc:sqlserver://DBHOST:1433;databaseName=MyDb;encrypt=true;trustServerCertificate=true"
          username="..."
          password="..."

          testOnBorrow="true"
          validationQuery="SELECT 1"
          validationQueryTimeout="5"

          testWhileIdle="true"
          timeBetweenEvictionRunsMillis="30000"
          minEvictableIdleTimeMillis="60000"
/>
```

Tomcat’s JDBC pool is configured as a JNDI resource with that `factory` value. ([tomcat.apache.org][2])

## Can the WAR “restart the pool”?

Sometimes, but it’s not the best lever:

* If the DataSource is **container-managed via JNDI**, the pool “belongs” to Tomcat. A WAR *can* sometimes reach it (via JNDI + casting to the pool implementation) and call pool-specific methods, but that’s **non-portable** and can have side effects (and may not recreate cleanly without Tomcat doing it).
* A **webapp reload/redeploy** will recreate a *webapp-scoped* JNDI resource/pool, but won’t necessarily touch a **global** pool.

In practice, configuring validation as above is the standard way to survive DB restarts without bouncing the tcServer Windows service.

## Where to put it

* If you define the `<Resource>` in the app’s context (e.g., `conf/Catalina/localhost/<app>.xml` or the app’s `META-INF/context.xml`), it’s app-scoped.
* If it’s in a server/global context, it may be shared.

If you paste your current `<Resource ...>` block (from tcServer/Tomcat config), I can tell you which pool you’re using (DBCP2 vs tomcat-jdbc) and adjust the exact attribute names (`maxTotal` vs `maxActive`, etc.) accordingly.

[1]: https://tomcat.apache.org/tomcat-9.0-doc/jndi-resources-howto.html "Apache Tomcat 9 (9.0.113) - JNDI Resources How-To"
[2]: https://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html "Apache Tomcat 9 (9.0.113) - The Tomcat JDBC Connection Pool"
