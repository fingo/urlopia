### What is Urlopia?

Urlopia application is a project developed by students during summer internship programmes
at [FINGO](http://www.fingo.pl/en/).

The idea was to create a web application, containing e-mail handling system, which will help organizing company's
vacation policies and allow employees to apply and manage vacation applications easily. It also simplifies work of
administrative workers by delivering variety of tools and automatically generating reports. Application is available in
Polish language version. The application connects with company's Active Directory via LDAP.

Functionality listing is available below. The project is ready to deploy on a server with Docker, further instructions
are available in below sections.

#### Technology used

In the project we used Java **Spring Boot 2.5.2** with **Gradle** for build automatization,
**React** and **PostgreSQL** database with **JPA** as ORM framework. The communication server-client is based on
RESTful services, compliant with MVC design pattern. Also:

- **Front-end**
    - [React](https://reactjs.org/) 17.0.
    - [Bootstrap](getbootstrap.com/) 5.0.2
    - [React Bootstrap](https://react-bootstrap.github.io/) 2.0.0
    - [React Router](https://reactrouter.com/) 5.2.0
    - [Sass](https://sass-lang.com/) 1.35.2
- **Back-end**
    - [Spring Boot](https://spring.io/projects/spring-boot) 2.5.2
    - [JSON WebToken](https://jwt.io/) for stateless authentication
    - [Handlebars](https://github.com/jknack/handlebars.java) for mail templates
- **Testing**
    - [Spock](https://spockframework.org/) 2.0
- **Build and deployment**
    - [Gradle](https://gradle.org/) 7.1.1
    - [Jenkins](https://jenkins.io/) for build and tests automatization
    - [SonarQube](www.sonarqube.org/) for code analysis

#### Functionality

Urlopia contains following functions:

- **Administration**
    - Employees list with search and filters
    - Associates list with search and filters
    - Editing vacation pool individually
    - Editing company's days off calendar
    - Vacation requests preview
    - Annual reports generating (individually)
    - Monthly reports generating
    - Automatic actions for managing employees remaining vacation
    - Team member vacation application consideration via email or web GUI
    - Adding absence with reason for a specific employee
- **Employees**
    - Vacation history log 
    - Remaining vacation pool preview
    - Applying for vacation via web GUI (Apply form)
    - Occasional vacation
    - Confirming presence at work
    - See who and when is absent on calendar

### HOW TO BUILD REACT APP
In terminal in ./view.react run
```shell
npm install
npm run build
```

Put the content of ./view.react/build to ./src/main/resources/public.

### HOW TO BUILD AND RUN THE APP

In terminal type the following:

(Windows) `gradlew build`

(Linux)   `./gradlew build`

Make sure you have JAVA_HOME environmental variable defined properly.

To deploy app on embedded server type:
`java -jar 'path_to_the_jar'`

In the browser the app will open by default on:
`localhost:8080`

### HOW TO RUN REACT APP IN DEVELOPMENT MODE
In terminal in ./view.react run
```shell
npm install
npm start
```

In the browser the app will open on:
`localhost:3000`

#### Environmental variables

In order to run the app you have to provide your configuration variables.
There is application.properties file in src/main/resources with some configuration, but you need to provide
more in application-developer.properties file.

Important properties:
```properties
spring.config.import=optional:application-developer.properties (import your own configuration)
app.url=http://localhost:8080
app.name=Urlopia

spring.mvc.async.request-timeout = 1500000 (Amount of time before asynchronous request handling times out.)

# database (Postgres database configuration)
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.continue-on-error=true
spring.jpa.hibernate.ddl-auto=validate (Validates existing schema, makes no changes to database)

# OR

# database (H2 database configuration)
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update (update to update schema, create to destroy previous data and create schema)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console (URL path for h2 console)
spring.datasource.url=jdbc:h2: (Absolute path for db file)

# database flyway
spring.flyway.enabled= (boolean flag - self explaining)
urlopia.flyway.baseline-version= (version of the last script that was run on db)

# active directory
ad.configuration.enabled=true (if false ad won't be used as data provider)
ldap.initial.context.factory=com.sun.jndi.ldap.LdapCtxFactory
ldap.security.authentication=Simple (Authentication with username and password)
ldap.security.principal= (Active directory username)
ldap.security.credentials= (Active directory password)
ldap.provider.url= (Active directory url)
ad.containers.main= (Main container)
ad.groups.ec= (Group for employees)
ad.groups.b2b= (Group for associates)
ad.groups.admin= (Group for admins)
azure.ad.clientId= (Client Id from azure AD registration)
azure.ad.tenantId= (Tenant Id from azure AD registration)

# mail
spring.mail.host= (SMTP server host. For instance, `smtp.example.com`)
spring.mail.username= (Login user of the SMTP server)
spring.mail.password= (Login password of the SMTP server)
spring.mail.port= (SMTP server port)
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true (Some SMTP servers require a TLS connection)
spring.mail.properties.smtp-auth=true
mail.template.prefix=/mails
mail.template.suffix=.hbs
mail.receiver.host=
mail.receiver.username=
mail.receiver.password=
mail.receiver.folder=Inbox
mail.receiver.idle.time=300000
mails.bot= (Mail address of mailing bot)
mails.storage= (Mail address of emails storage)
mail.title.prefix = (Prefix for mail title - can be empty)
mail.sender.enabled= (Boolean flag to set sending mail)
# slack
slack.bot-token=
slack.signing-secret=

# proxy
proxy-token= (Secret token used to validate requests coming from proxy)

# presence confirmation
urlopia.presence.confirmation.considered.months = (Number on months in past that will be considered when checking presence.
                          On example if value is 2 and current month is June, absent will be search between April and June)
```

Apart from the properties above you have to set two env variables via shell or in the `view.react/.env` file:
```
REACT_APP_OAUTH_CLIENT_ID= (Client Id from azure AD registration)
REACT_APP_OAUTH_TENANT_ID= (Tenant Id from azure AD registration)
```

#### PostgreSQL

To create PostgreSQL role & database, navigate to `\bin` folder in your PostgreSQL installation location in the terminal
and type:

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

## Flyway configuration

To turn on database migration via Flyway add

- `spring.flyway.enabled=true`

properties to the `application-developer.properties` (on set this via other possible ways).
Migration scripts are in `urlopia/src/main/resources/scripts` directory

Scripts cannot be modified after creating (and executing)!

If you already have database where some sql scripts have been run you need to add

- `urlopia.flyway.baseline-version = <last_script_version>`
  where last_script_version is number from the latest run script.

On example if latest run script was V2_7_6_1__add_count_for_next_year_column then you need to
replace `<last_script_version>` with `2_7_6_1` (without quotas)

Name convention for scripts is:

* `V<APP_VERSION>_<SCRIPT_NUMBER>__<DESCRIPTION>.sql` - update scripts
* `v<APP_VERSION>_<SCRIPT_NUMBER>__<DESCRIPTION>.sql` - update scripts to execute manually
* `U<APP_VERSION>_<SCRIPT_NUMBER>__<DESCRIPTION>.sql` - rollback scripts

where

* `<APP_VERSION>` is an application version to be released e.g., `2_7_6`
* `<SCRIPT_NUMBER>` is a counter for scripts in same app version
* `<DESCRIPTION>` is a human-readable description of the changes made by this script.

Example script name: `V2_7_6_1__add_count_for_next_year_column.sql`.

## Application without Active Directory

Urlopia has option to be run without AD. You can configure it by setting `ad.configuration.enabled = false` properties.
If this option is choose synchronization won't be run and all data will be got from database. 
In this mode there is no need to define any of Active Directory properties. 
If there is need to use frontend in this configuration, frontend should be build in no-auth mode [check details](view.react/README.md#no-auth-mode)


#### Slack bot configuration

To create a slack app:

1. Go [here](https://api.slack.com/apps) and click on the `Create App` button
2. Select `from an app manifest`
3. Choose your workspace
4. Paste the `slack-app-manifest.json` file and replace `{your-domain}` part with domain Urlopia is being run on
5. Install the app in your workspace
6. Go to the `App Home` sidebar section and on the very bottom
   tick `Allow users to send Slash commands and messages from the messages tab`
7. In `Basic Information` sidebar section you'll find a `Signing Secret` of your app, and in the `OAuth & Permissions`
   you'll find `Bot User OAuth Token`. You need to add both tokens to your `application.properties` configuration file
8. If Urlopia is up and running, you can go to the `Event Subscriptions` sidebar section, paste the `Request URL` (
   i.e. `{your-domain}/api/v2/slack/events`) and verify if slack is able to communicate with it

If everything's good you should now be able to interact with the `@Urlopia` bot in your slack workspace

## Docker compose
There's a ready-to-go docker-compose configuration that can mostly help with front-end development. 

Feel free to update it to extend the backend support.

In order to run the app follow these steps:
1. Update the configuration files for both backend (src/main/resources/application.properties) and frontend (view.react/.env).
2. Build the jar file with gradle and java: `gradlew build`.
3. run the backend dependencies (postgresql database & tomcat server with the app) `docker-compose up -d`.
4. Install frontend dependencies with npm `npm install` and start the app `npm start`.
