# DynaTest

DynaTest is a dynamic no code API test framework.

Built as a way to validate client API integrations maintain the expected interface, with configuration via Json / Web
UI(wip).

Features:

- JSON Templating
- Multiple step tests
- Pass and persist data between tests
- JSON schema validation
- Built in REST client (partial support)
- Web Interface
- CLI for CI/CD (wip)

Example real life usage:

- Step 1: Create a new user via API, response contains userID.
- Step 2: Use this userID to submit a UGC application, returns applicationID.
- Step 3: Use userID and applicationID to query for data.
- ... etc

Each step is independently schema validated to ensure reponses are as expected.

Run with Docker:

```sh
mvn clean install
docker build -t dynatest:latest .     
# Note a local mount is required for data to persist
docker run -p 8081:8081 -v ~/dynatest:/app/data dynatest:latest
```