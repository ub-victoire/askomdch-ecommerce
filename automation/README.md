# Java Cucumber and Selenium automation

The initial read-only checks cover Jira Tests **AEC-48**, **AEC-89**, and **AEC-123**. The other 60 Tests remain candidates: their procedures, assertions, data, or safe test environment require confirmation. See the [Jira Test mapping](feature-mapping.md). Local-fixture validation is not evidence that the application passed.

## Requirements

- Java 17 or newer and Maven.
- Google Chrome and a compatible ChromeDriver. Selenium Manager attempts to obtain a driver if one is not already available.
- An explicitly authorized HTTP(S) test environment for product execution. There is no default application URL.

## Run

From the repository root, validate configuration and the Cucumber/Selenium integration against an isolated local fixture:

```sh
mvn -f automation/pom.xml test
```

The fixture's negative check verifies that a missing recovery instruction is detected; the enclosing JUnit test expects the resulting timeout.

To run the implemented read-only scenarios against an authorized test environment:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example verify
```

Use the origin only: no URL path, credentials, query, or fragment. Do not use the public store for account-creating or order-placing tests. The implemented scenarios only open account and recovery pages and inspect visible controls; they do not submit forms. Results and generated reports under `automation/target/` are local and ignored by Git.

To select a Jira Test:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example -Dcucumber.filter.tags=@AEC-48 verify
```

## Structure and data

- [`features/`](features/) contains Gherkin Rules, Backgrounds, Scenarios, and a Scenario Outline with Examples.
- [`src/test/java/`](src/test/java/) contains Selenium page objects, step definitions, hooks, configuration, and local-fixture tests.
- [`data/`](data/) is reserved for future automation data. The [proposed symbolic datasets](../test-data/README.md) are not verified fixtures. Do not commit passwords, credentials, customer information, or secrets.

The scripts use the exported Test descriptions and linked Story acceptance criteria as requirements. The CSV does not contain original Xray step definitions or execution results, and the public-site form selectors have not been verified against an authorized test environment.
