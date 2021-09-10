### What is Urlopia?
Urlopia application is a project developed by five students 
(Józef Grodzicki, Jakub Licznerski, Tomasz Pilarczyk, Tomasz Urbas, Mateusz Wiśniewski)
 during summer internship programme at [FINGO](http://www.fingo.pl/en/). 
 
 The idea was to create 
 a web application, containing e-mail handling system, which will help organizing company's vacation policies and 
 allow employees to apply and manage vacation applications easily. It also simplifies work
 of administrative workers by delivering variety of tools and automatically generating reports. Application is
 available in Polish and English language versions. The application connects with company's Active Directory via LDAP.
 
 Functionality listing is available below.
 The project is ready to deploy on a server with Docker, further instructions are available in below sections.

#### Technology used
In the project we used Java **Spring Boot 1.3.6** with **Gradle** for build automatization, 
**Angular JS** and **PostgreSQL** database with **JPA** as ORM framework. The communication
 server-client is based on RESTful services, compliant with MVC design pattern. Also:

- **Front-end**
    - Pascal Precht's [angular-translate](https://github.com/angular-translate/angular-translate) module
    - [AngularJS](https://angularjs.org/) 1.4.7
    - [Bootstrap](getbootstrap.com/) 3.3.7
    - [Bootstrap UI](https://angular-ui.github.io/bootstrap/)
    - [jQuery](https://jquery.com/) 3.1.0
- **Back-end**
    - [JSON WebToken](https://jwt.io/) for stateless authentication
    - [Handlebars](https://github.com/jknack/handlebars.java) for mail templates
- **Testing**
    - [JUnit](junit.org/) 4.12
    - [Mockito](site.mockito.org/)
- **Build and deployment**
    - [Gradle](https://gradle.org/) 2.14
    - [Jenkins](https://jenkins.io/) for build and tests automatization
    - [SonarQube](www.sonarqube.org/) for code analysis

#### Functionality
Urlopia contains following functions:

- **Administration**
    - Employees list with search and filters
    - Editing vacation pool individually
    - Editing company's days off calendar
    - Vacation requests preview
    - Annual reports generation (individually)
    - Automatic actions for managing employees remaining vacation
    - Team member vacation application consideration via email or web GUI
- **Employees**
    - Vacation history log with 
    - Remaining vacation pool preview
    - Applying for vacation via e-mail or web GUI (Apply form)
    - Occasional vacation

### HOW TO BUILD AND RUN THE APP

In terminal type the following:

(Windows) `gradlew build`

(Linux)   `./gradlew build`

Make sure you have JAVA_HOME environmental variable defined properly.

To deploy app on embedded server type:
`java -jar 'path_to_the_jar'`

In the browser the app will open by default on:
`localhost:8080`

#### Environmental variables

In order to run the app you have to provide your configuration variables.

- List of required variables with example values is available [here](src/main/resources/env_var.txt)
- List of variables with meaning description is available [here](src/main/resources/env_var.md)

*The best way is to:*
- swap example values with your project specific ones
- copy the entire file content
- in IntelliJ Idea go to: `Run`>`Edit Configurations`>`UrlopiaApplication`
- on the Configuration page select Environment variables and click the paste icon

You can also create application.properties file in src/main/resources.

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

#### Slack bot configuration
To create a slack app:
1. Go [here](https://api.slack.com/apps) and click on the `Create App` button
2. Select `from an app manifest`
3. Choose your workspace
4. Paste the `slack-app-manifest.json` file and replace `{your-domain}` part with domain Urlopia is being run on
5. Install the app in your workspace
6. Go to the `App Home` sidebar section and on the very bottom tick `Allow users to send Slash commands and messages from the messages tab`
7. In `Basic Information` sidebar section you'll find a `Signing Secret` of your app, and in the `OAuth & Permissions` you'll find `Bot User OAuth Token`. You need to add both tokens to
your `application.properties` configuration file
8. If Urlopia is up and running, you can go to the `Event Subscriptions` sidebar section, paste the `Request URL` (i.e. `{your-domain}/api/v2/slack/events`) and verify if slack is able to communicate with it
   
If everything's good you should now be able to interact with the `@Urlopia` bot in your slack workspace