# TOGO
TOGO allows to record and query the measures relating to different aspects of one or more production plants, such as, for example, energy consumption, production and environmental variables.
The data in JSON format is sent to and received by TOGO via the REST services, protected through basic authentication or JSON Web Tokens, that the software exposes.
A graphical interface [TOGO-UI](https://github.com/myna-project/TOGO-UI) has been developed to facilitate the use of the REST services exposed by TOGO.
##### Why TOGO?
TOGO (October 17, 1913 – December 5, 1929) was the lead sled dog of musher Leonhard Seppala and his dog sled team. He was one of the heros of the "Serum run", the transport of diphtheria antitoxin by dog sled relay across the U.S. territory of Alaska by 20 mushers and about 150 sled dogs across 674 miles (1,085 km) in 5 ½ days, saving the small town of Nome and the surrounding communities from a developing epidemic. Why Togo and not Balto? Togo, despite the greater fame of Balto, was the dog who run across the longest part of the relay race (Balto 55 miles, Togo 260 miles). [[1]](https://en.wikipedia.org/wiki/Togo_%28dog%29)
### Installation requirements
To install TOGO you need the following:
* [Apache Maven](https://maven.apache.org/)
* a web server (we recommend [Apache Tomcat](https://tomcat.apache.org/))
* an object-relational DataBase Management System (we recommend [PostgreSQL](https://www.postgresql.org/))

### Installation example using Apache Tomcat and PostgreSQL
In PostgreSQL create an user 'togo' and a database 'togo', then execute all sql commands in file ddl/creation/togo\_x.x.x.sql where x.x.x correspond to the latest version of the software.

In Apache Tomcat folder installation edit file context.xml adding:

```
<Resource auth="Container" driverClassName="org.postgresql.Driver" maxIdle="30" maxTotal="100" maxWaitMillis="10000" name="jdbc/togo" password="secret" type="javax.sql.DataSource" url="jdbc:postgresql://{psql_ip}:{psql_port}/togo" username="togo"/>
```

inside the tag Context.

In the project folder, execute

```
mvn clean package
```

to create file togo-api.war in the /target folder.

Copy togo-api.war file in Apache Tomcat webapps folder and start Apache Tomcat to run TOGO.

### REST services
To get list of REST services exposed by TOGO go to: http://{tomcat_ip}:{tomcat_port}/togo-api/swagger/api-docs