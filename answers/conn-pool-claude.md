Yes, there are several ways to handle this without restarting the entire tcServer service. The connection pool should be able to recover automatically from database restarts with proper configuration.

Here are your options:

**1. Configure Connection Pool Validation (Recommended)**

Add these settings to your connection pool configuration in `context.xml` or your application's context configuration:

```xml
<Resource name="jdbc/YourDataSource" 
          auth="Container"
          type="javax.sql.DataSource"
          driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
          url="jdbc:sqlserver://yourserver:1433;databaseName=yourdb"
          username="user"
          password="pass"
          
          <!-- Connection validation settings -->
          testOnBorrow="true"
          testOnReturn="false"
          testWhileIdle="true"
          validationQuery="SELECT 1"
          validationQueryTimeout="5"
          
          <!-- Eviction settings -->
          timeBetweenEvictionRunsMillis="30000"
          minEvictableIdleTimeMillis="60000"
          
          <!-- Remove abandoned connections -->
          removeAbandoned="true"
          removeAbandonedTimeout="300"
          logAbandoned="true"
          
          <!-- Connection limits -->
          maxTotal="20"
          maxIdle="10"
          minIdle="5"
          maxWaitMillis="10000"
/>
```

Key settings explained:
- `testOnBorrow="true"` - validates connections before giving them to your application
- `testWhileIdle="true"` - validates idle connections periodically
- `validationQuery="SELECT 1"` - simple query to test connection validity
- `timeBetweenEvictionRunsMillis` - how often to check for stale connections (30 seconds here)
- `removeAbandoned="true"` - cleans up broken/abandoned connections

**2. Programmatic Pool Reset**

If you need to manually reset the pool from within your application, you can create a management endpoint:

```java
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public void resetConnectionPool() throws Exception {
    Context initContext = new InitialContext();
    Context envContext = (Context) initContext.lookup("java:/comp/env");
    DataSource ds = (DataSource) envContext.lookup("jdbc/YourDataSource");
    
    if (ds instanceof BasicDataSource) {
        BasicDataSource bds = (BasicDataSource) ds;
        bds.close();  // Close all connections
        // The pool will automatically recreate connections on next request
    }
}
```

**3. Use Tomcat Manager Application**

You can reload just your application without restarting the entire tcServer:
- Access Tomcat Manager (if enabled)
- Click "Reload" for your application
- This reinitializes the connection pool

**4. JMX-based Management**

Enable JMX and use tools like JConsole to manage the DataSource and reset connections without full restart.

The first option (connection validation) is your best bet as it handles this scenario automatically. The pool will detect dead connections and create new ones transparently to your application.
