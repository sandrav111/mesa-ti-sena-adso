# Mesa TI - Aplicación web

Aplicación web básica para gestionar tickets de soporte TI.

## Tecnologías

- Java 21.
- Spring Boot 3.3.5.
- Spring MVC.
- JSP y JSTL.
- JDBC con `JdbcTemplate`.
- Hibernate/JPA.
- H2.
- Maven.

## Ejecución

```bash
JAVA_HOME="/ruta/a/java-21" mvn spring-boot:run
```

Abrir `http://localhost:8080/tickets`.

Para utilizar otro puerto durante las pruebas:

```bash
JAVA_HOME="/ruta/a/java-21" mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Pruebas

```bash
JAVA_HOME="/ruta/a/java-21" mvn test
JAVA_HOME="/ruta/a/java-21" mvn package
```

La base de datos H2 se inicializa con datos de prueba desde `schema.sql` y `data.sql`.

## API principal

- `GET /api/tickets`
- `GET /api/tickets/{id}`
- `POST /api/tickets`
- `PUT /api/tickets/{id}`
- `DELETE /api/tickets/{id}`
- `POST /api/auth/register`
- `POST /api/auth/login`

El puerto puede recibirse mediante `PORT`; si no existe, la aplicación utiliza 8080.
