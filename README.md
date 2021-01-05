# quarkus-qute-keycloak

Demo of using Quarkus with server-side templates via its Qute extension and Unpoly for client-side enhancements secured with Keycloak.

This example builds upon the [quarkus-qute](https://github.com/gunnarmorling/quarkus-qute) by @gunnarmorling.

To login you can use the usernames `john` or `jane` with the password `test`.  

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Database set-up

This project uses Postgres which can be started via Docker Compose:

```shell
cd compose
docker-compose up
```

A database "tododb", a user and schema are all configured automatically, so no further setup is needed.
If the database doesn't show up in PGAdmin,
the definition can be imported like this:

```shell
docker exec -it pgadmin_container python setup.py --load-servers /pgadmin4/servers.json --user pgadmin4@pgadmin.org
```

To browse the database, go to http://localhost:15050/browser/ and log in with user name "pgadmin4@pgadmin.org" and password "admin".
To connect to the "tododb" database, use "todopw" as password when requested.

## Keycloak set-up

A keycloak setup is included in the docker-compose configuration.

Keycloak can be accessed via http://localhost:9180/auth with `admin`/`admin` as username/password.

The Keycloak realm `quarkus` contains a confidential client `quarkus-todo` which represents this application.

This realm contains two users with the usernames `john` and `jane`, both with the password `test`.

## Unpoly

This project uses [Unpoly](https://unpoly.com/) for a smoother user experience:
links and form submissions will be intercepted and executed as AJAX requests,
avoiding a full page reload by replacing page fragments.

If JavaScript is disabled, the application gracefully falls back to the regular mode and experience of server-side rendered applications.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `quarkus-qute-keycloak-1.0.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/quarkus-qute-keycloak-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Docker to build the native executable using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/quarkus-qute-keycloak-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .

## Running the native executable via Docker

```
docker build -f src/main/docker/Dockerfile.native -t quarkus-examples/quarkus-qute .
docker run -i --rm -p 8080:8080 --network todo-network -e QUARKUS_DATASOURCE_URL=jdbc:postgresql://todo-db:5432/tododb quarkus-examples/quarkus-qute
```
