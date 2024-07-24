```markdown
# Project Title

Task Management Service backend with Spring MVC, Security with JWT

## Prerequisites

Before you begin, ensure you have met the following requirements:
- Java JDK 17 or higher installed
- For testing default database is H2, if needed to change, update application.properties.


## Building the Project

To build the project, follow these steps:

1. Open a terminal.
2. Navigate to the root directory of the project.
3. Run the following command:

```bash
./mvnw clean install
```

This command compiles the project and runs any tests. It also packages the application into a runnable JAR file.

## Running the Application

After building the project, you can run the application using the following command:

```bash
java -jar target/<your-jar-file>.jar
```

Replace `<your-jar-file>` with the name of the generated JAR file in the `target` directory. This name is usually based on the project's `<artifactId>` and `<version>` as defined in your `pom.xml`.


## API Documentation
https://documenter.getpostman.com/view/131973/2sA3kXDLJB