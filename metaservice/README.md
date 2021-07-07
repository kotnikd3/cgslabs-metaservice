# MetaService

### About the project
Microservice build in Java EE with [Dropwizard](https://www.dropwizard.io/en/latest/) framework using Maven, Hibernate, MapStruct, Jackson, OpenAPI3/Swagger and PostgreSQL database.

### Start the PostgreSQL database in Docker container

``` bash
 sudo docker run -d --name metaservice-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=vedra -p 5433:5432 postgres:13
```

### Start the MetaService service on your local computer

Compile everything and get a .jar file.
``` bash
mvn clean package
```

Migrate database schema
``` bash
java -jar metaservice-1.0-SNAPSHOT.jar db migrate config.yml
```

Start the service
``` bash
java -jar metaservice-1.0-SNAPSHOT.jar server config.yml
```

### Start the MetaService service on the server

Compile everything and get a .jar file.
``` bash
mvn clean package
```
Put .jar and .yml files on the server.  
Migrate database schema.
``` bash
java -jar metaservice-1.0-SNAPSHOT.jar db migrate config.yml
```

Create unit file in <i>/etc/systemd/system</i>.  
Reload systemd deamon.
``` bash
sudo systemctl daemon-reload
```

Enable unit file/service
``` bash
sudo systemctl enable metaservice
```

Start the unit file/service
``` bash
sudo systemctl start metaservice
```

### OpenAPI3/Swagger endpoint
Visit `http://localhost:8448/openapi.json`

### Health Check
Visit `http://localhost:8448/healthcheck`
