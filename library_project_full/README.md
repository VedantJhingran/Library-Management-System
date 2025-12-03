    # Library Management System — Servlet + SQLite + Swing Client


    ## Files included


    - `src/com/example/library/server/ServerMain.java` (server bootstrap, model, DAO, service)

    - `src/com/example/library/server/BookServlet.java` (servlet API)


    - `src/com/example/library/client/ClientMain.java` (Swing client that calls server APIs)


    - `lib/` contains placeholder jars. Replace with real jars before compiling/running:
      - gson.jar
      - sqlite-jdbc.jar
      - jetty-server.jar
      - jetty-servlet.jar


## How to compile & run

1. Put required jars in `lib/`.

2. Compile server & servlet:

```bash
javac -cp "lib/*:src" src/com/example/library/server/*.java src/com/example/library/server/*.java
```

3. Run server (from project root):

```bash
java -cp "lib/*:src" com.example.library.server.ServerMain
```

4. Compile & run client in another terminal:

```bash
javac -cp "lib/*:src" src/com/example/library/client/ClientMain.java
java -cp "lib/*:src" com.example.library.client.ClientMain
```


## Notes

- Replace placeholder jars in `lib/` with actual jar files from Maven Central.
- Database file `library.db` will be created in project root on first server run.
- If you want a fat JAR instead, use Maven – I can provide `pom.xml` if you want.
