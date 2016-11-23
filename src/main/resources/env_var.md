#### Application
`app.url=http://urlopia.example.com` - URL of your application

#### Database
`database.drop-create=true` - For development set: `true`, for production: `false`
`spring.datasource.driver-class-name=org.postgresql.Driver` - Database driver
`spring.jpa.database=postgresql` - Database provider
`spring.jpa.hibernate.ddl-auto=none` - Automatically validates or exports DDL schema
`spring.datasource.url=jdbc:postgresql://localhost:5432/urlopiadb` - Database URL
`spring.datasource.username=dbuser` - Username
`spring.datasource.password=dbpassword` - Password

#### LDAP
`ldap.initial.context.factory=com.sun.jndi.ldap.LdapCtxFactory` - Class name of LDAP Context Factory
`ldap.security.authentication=Simple` - Authentication type 
`ldap.security.principal=user` - Username
`ldap.security.credentials=password` - Password
`ldap.provider.url=ldap://adserver.example.com:999` - LDAP URL

#### Active Directory
`ad.adminsTeam=Admins` - Name of the admins team
`ad.adminUrlopia=admin.urlopia@example.com` - Additional admin principal name
`ad.team.identifier= Team,` - Suffix of teams names
`ad.user.masterLeader=master.leader@example.com` - Master Leader principal name

#### LDAP search criteria
`ad.team.urlopia.group=CN=Urlopia Team,OU=Users,DC=example,DC=pl` - for Urlopia dev group
`ad.team.main.group=CN=Main Team,OU=Users,DC=example,DC=pl` - for the company main group
`ad.team.leaders.group=CN=Team Leaders,OU=Users,DC=example,DC=pl` - for the Team Leaders group
`ad.user.container=OU=Users,DC=example,DC=pl` - for all users
`ad.user.b2b.group=CN=B2BEmployees,OU=Users,DC=example,DC=pl` - for B2B users
`ad.user.ec.group=CN=ECEmployees,OU=Users,DC=example,DC=pl` - for EC users

#### Mail
`spring.mail.host=post.example.com` - E-mail domain name
`spring.mail.username=urlopia@example.com` Urlopia e-mail address
`spring.mail.password=password` - Urlopia e-mail password
`spring.mail.properties.smtp.auth=true` - Authorized by SMTP
`spring.mail.properties.smtp.socketFactory.port=999` - SMTP port
`spring.mail.properties.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory` - Socket factory class
`spring.mail.properties.smtp.socketFactory.fallback=false` - If set to true, failure to create a socket using the specified socket factory class will cause the socket to be created using the java.net.Socket class.
`mail.template.prefix=/mails` - Prefix for mail templates
`mail.template.suffix=.hbs` - Suffix for mail templates
`mail.receiver.host=post.example.com` - Mail receiver's e-mail domain name
`mail.receiver.folder=Inbox` - Mail receiver's inbox folder name
`mail.receiver.idle-time=300000` - Receiver idle time in ms
`mail.bot.name=Urlopia` - Mail bot name (sender name for e-mail communicates)
`mail.bot.address=urlopia@example.com` - Mail bot e-mail address

#### Webtoken
`webtoken.secret=SecretKey` - Secret key for JWT generation
