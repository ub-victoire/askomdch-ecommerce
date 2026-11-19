# Java Cucumber and Selenium automation

The read-only checks cover Jira Tests **AEC-18**, **AEC-48**, **AEC-89**, **AEC-110**, **AEC-112**, and **AEC-123**. AEC-18 covers the specific search examples in AEC-15, and AEC-110 checks one product per entry point. The other 57 Tests remain candidates: their procedures, assertions, data, or safe test environment require confirmation. See the [Jira Test mapping](feature-mapping.md). Local-fixture validation is not evidence that the application passed.

## Requirements

- Java 17 or newer and Maven.
- Google Chrome and a compatible ChromeDriver. Selenium Manager attempts to obtain a driver if one is not already available.
- An explicitly authorized HTTP(S) test environment for product execution. There is no default application URL.

## Run

From the repository root, validate configuration and the Cucumber/Selenium integration against an isolated local fixture:

```sh
mvn -f automation/pom.xml test
```

The fixture's negative checks deliberately cause Cucumber failures for incorrect search results, a wrong product title, and visible empty-cart totals. The enclosing JUnit tests expect these failures. Another check expects a timeout when the recovery instruction is missing.

To run the implemented read-only scenarios against an authorized test environment:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example verify
```

Use the origin only: no URL path, credentials, query, or fragment. Do not use the public store for account-creating or order-placing tests. The implemented scenarios inspect account forms, submit read-only product searches, open product details, and navigate an empty cart; they do not submit account or order forms. Search checks require the AEC-15 catalog examples (Blue Tshirt and Green Tshirt) in the authorized environment. Results and generated reports under `automation/target/` are local and ignored by Git.

If Selenium Manager cannot resolve ChromeDriver, supply the path to an installed matching driver with `-Dwebdriver.chrome.driver=/path/to/chromedriver`.

To select a Jira Test:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example -Dcucumber.filter.tags=@AEC-48 verify
```

## Structure and data

- [`features/`](features/) contains Gherkin Rules, Backgrounds, Scenarios, and a Scenario Outline with Examples.
- [`src/test/java/`](src/test/java/) contains Selenium page objects, step definitions, hooks, configuration, and local-fixture tests.
- [`data/`](data/) is reserved for future automation data. The [proposed symbolic datasets](../test-data/README.md) are not verified fixtures. Do not commit passwords, credentials, customer information, or secrets.

The scripts use the exported Test descriptions and linked Story acceptance criteria as requirements. The CSV does not contain original Xray step definitions or execution results, and selectors have not been verified against an authorized test environment.
