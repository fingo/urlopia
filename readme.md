### HOW TO BUILD AND RUN THE APP

In terminal type the following:

(Windows) `gradlew build`
(Linux)   `./gradlew build`

To deploy app on embedded server type:
`java -jar 'path_to_the_jar'`

In the browser the app will open by default on:
`localhost:8080`

**Environmental variables**

In order to run the app you have to provide your configuration variables.

List of required variables with example values is available [here](src/main/resources/env_var.txt)

List of variables with meaning description is available [here](src/main/resources/env_var.md)

*The best way is to:*
- swap example values with your project specific ones
- copy the entire file content
- in IntelliJ Idea go to: `Run`>`Edit Configurations`>`UrlopiaApplication`
- on the Configuration page select Environment variables and click the paste icon

#### PostgreSQL
To create PostgreSQL role & database, navigate to `\bin` folder in your
PostgreSQL installation location in the terminal and type:

`psql -U postgres`

`\i 'path-to-project'/src/main/resources/scripts/init.sql`

e.g. `\i C:/Users/<Username>/Urlopia/src/main/resources/scripts/init.sql`


To connect the database in IntelliJ IDEA:
`Database` > `+Data Source` > `PostgreSQL`
Then in the fields:
- Set `Host` to `localhost` and `Port` to `5432`
- Set `Database` to `urlopiadb`
- Set `User` to `urlopia`
- Set `Password` to `urlopia123`