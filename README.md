#### :warning:This project is now part of the EE4J initiative. This repository has been archived as all activities are now happening in the [corresponding Eclipse repository](https://github.com/eclipse-ee4j/javamail). See [here](https://www.eclipse.org/ee4j/status.php) for the overall EE4J transition status.
 
---

See the [JavaMail web site](https://javaee.github.io/javamail).
 
---

## Instructions for Hexagon

This project builds [com.sun.mail:javax.mail](https://mvnrepository.com/artifact/com.sun.mail/javax.mail) by forking [https://github.com/javaee/javamail](https://github.com/javaee/javamail])

### Why:
1. To avoid classpath issues when Jakarta APIs are transitively pulled in when using [com.sun.mail:jakarta.mail](https://mvnrepository.com/artifact/com.sun.mail/jakarta.mail)
2. Need [this enhancement](https://github.com/bvfalcon/jakarta-mail/commit/5a352daaf6f8b2a0654693152c219dd0f4d0588c) (from the Eclipse repository) [to support OAuth2 logins to Outlook 365](https://github.com/eclipse-ee4j/mail/issues/416)
3. Need [this fix](https://github.com/eclipse-ee4j/mail/commit/ca3bb5ca37f4595fc4f8ccaf4454198e2140b638#diff-6c9a48bc0d510db9ea59cf4f3f41c404d7ce833ec22d2064153bcdafd67e65e9) (from the Eclipse repository) [to support 2-line auth to Outlook 365](https://github.com/eclipse-ee4j/mail/issues/461)

### To build this project:
Run the Maven 'package' phase using the 'deploy-snapshot' profile - it won't run tests and won't package javadoc. 

#### If using IntelliJ:
1. Open the Maven view 
2. Check the box next to 'deploy-snapshot'
3. Double click 'package' under the main project's (JavaMail API distribution) Lifecycle.

The above process will generate the necessary JAR file at ./mail/target/javax.mail.jar.

### To upload this JAR to our repo:
1. Create the file %HOMEPATH%/.m2/settings.xml (Windows) or ~/.m2/settings.xml (*nix) to configure authentication credentials for our internal repo. The contents of the file should be:
```
<settings>
    <servers>
        <server>
            <id>EcoSys</id>
            <username>user</username>
            <password>pass</password>
        </server>
    </servers>
</settings>
```

**Note that the <id> must be EcoSys (or match -DrepositoryId used in the cmd below in step #4)**

2. Decide on a version and change the <version> in POMs across the project
3. Build the project using the instructions listed above
4. Upload the JAR to our internal repo using the command -
```
mvn deploy:deploy-file -DgroupId=com.sun.mail -DartifactId=javax.mail -Dversion=1.6.2_461-SNAPSHOT -DpomFile=.\mail\pom.xml -Dfile=.\mail\target\javax.mail.jar -DrepositoryId=EcoSys -Durl=<repoUrl>
```

**Change the version number to match what was selected in step #2**