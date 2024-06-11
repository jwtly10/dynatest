# DynaTest

DynaTest is a dynamic no code API test framework.

Built as a way to validate client API integrations maintain the expected interface, with configuration via Json / Web
UI(wip).

### Features:

- JSON Templating
- Multiple step tests
- Pass and persist data between tests
- JSON schema validation
- Built in REST client (partial support)
- Web Interface
- CLI for CI/CD (wip)
- Built in test logging/debugging

### Example:
This is a demo with an example server/use case. The flow of the application is:
- Step 1: Create a new user via API, response contains userID.
- Step 2: Query the data about the user based on the returned userID
- Step 3: Use this userID to submit a survey, returns surveyID.

Along the way all steps are validated for structure, and data types.

https://github.com/jwtly10/dynatest/assets/39057715/c962a0ae-f31c-49f8-81d3-6973e11492da

Run with Docker:

```sh
# Run tests and build .jar
mvn clean install

# Build & run docker container
docker build -t dynatest:latest .     
# Note a local mount is required for data to persist
docker run -p 8081:8081 -v ~/dynatest:/app/data dynatest:latest
```
