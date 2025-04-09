To connect to a local MSSQL server that has only Shared Memory as available protocol using JDBC in Java 11, you'll need to use the Microsoft JDBC driver with specific connection string parameters.
Here's how to do it:

First, add the Microsoft JDBC driver dependency to your project. If you're using Maven:

``` xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>9.4.1.jre11</version>
</dependency>
```

Then use this JDBC connection string format to connect using Shared Memory:

``` java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SharedMemoryConnection {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://localhost\\INSTANCENAME;integratedSecurity=true;localProtocol=lpc";
        
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            System.out.println("Connected successfully to SQL Server using Shared Memory protocol.");
            // Perform database operations here
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
```

Key points:

Replace `INSTANCENAME` with your actual SQL Server instance name
The `localProtocol=lpc` parameter specifically requests the Local Procedure Call (Shared Memory) protocol
`integratedSecurity=true` uses Windows authentication

If you need to use SQL Server authentication instead:

``` java
String connectionUrl = "jdbc:sqlserver://localhost\\INSTANCENAME;user=username;password=password;localProtocol=lpc";
```

Make sure that:

You have permissions to access the SQL Server
The SQL Server Browser service is running on the server
The Shared Memory protocol is enabled in SQL Server Configuration Manager

Would you like me to explain how to verify these settings or help with any specific issues you're encountering?

---

To configure a MSSQL connection with Shared Memory protocol as a JNDI resource in Tomcat 9, you'll need to update the context.xml file. Here's how to do it:
For Tomcat 9, you can configure this in the META-INF/context.xml file of your web application:

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource 
        name="jdbc/SQLServerDB" 
        auth="Container" 
        type="javax.sql.DataSource"
        factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
        driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
        url="jdbc:sqlserver://localhost\INSTANCENAME;localProtocol=lpc"
        username="your_username" 
        password="your_password"
        maxTotal="20" 
        maxIdle="10" 
        maxWaitMillis="10000"
        validationQuery="SELECT 1" 
        testOnBorrow="true"
        removeAbandoned="true"
        removeAbandonedTimeout="60"
        logAbandoned="true"
    />
</Context>
```

If you're using Windows Authentication instead of SQL Server authentication, use this configuration:

``` xml<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource 
        name="jdbc/SQLServerDB" 
        auth="Container" 
        type="javax.sql.DataSource"
        factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
        driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
        url="jdbc:sqlserver://localhost\INSTANCENAME;integratedSecurity=true;localProtocol=lpc"
        maxTotal="20" 
        maxIdle="10" 
        maxWaitMillis="10000"
        validationQuery="SELECT 1" 
        testOnBorrow="true"
        removeAbandoned="true"
        removeAbandonedTimeout="60"
        logAbandoned="true"
    />
</Context>
```

You'll also need to:

Place the Microsoft JDBC driver JAR file in Tomcat's lib directory:

Download the appropriate JDBC driver (e.g., mssql-jdbc-9.4.1.jre11.jar)
Copy it to TOMCAT_HOME/lib/


If using Windows Authentication, you'll need these additional steps:

Place the sqljdbc_auth.dll in a directory that's in your Java library path
The DLL must match your JVM architecture (x86 or x64)
You might need to configure Tomcat service to run as a user with proper SQL Server access


In your web application, configure the resource reference in WEB-INF/web.xml:

``` xml
<resource-ref>
    <description>SQL Server Datasource</description>
    <res-ref-name>jdbc/SQLServerDB</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```

Access the datasource in your Java code:

``` java
Context initContext = new InitialContext();
Context envContext = (Context) initContext.lookup("java:/comp/env");
DataSource ds = (DataSource) envContext.lookup("jdbc/SQLServerDB");
Connection conn = ds.getConnection();
```

Make sure to replace `INSTANCENAME` with your actual SQL Server instance name and adjust the connection pool parameters as needed for your application's requirements.
