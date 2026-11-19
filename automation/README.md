# Java Cucumber and Selenium automation

All 22 Checkout Tests have Gherkin scenarios. Login and order step definitions are drafts: they are blocked by `@pending` until their assertions, fixtures, and safe execution are validated. The separate empty-username login scenario and checkout controls run against a local fixture. No account or order flow has been verified against the application. See the [Jira Test mapping](feature-mapping.md) for coverage and gaps.

## Requirements

- Java 17 or newer and Maven.
- Google Chrome and a compatible ChromeDriver. Selenium Manager attempts to obtain a driver if one is not already available.
- An explicitly authorized HTTP(S) test environment for product execution. There is no default application URL.

## Run

From the repository root, validate configuration and the Cucumber/Selenium integration against an isolated local fixture:

```sh
mvn -f automation/pom.xml test
```

The fixture's negative checks deliberately cause Cucumber failures for incorrect search results, a wrong product title or category heading, visible empty-cart totals, a missing sort option, incorrect price order, incorrect cart or checkout subtotals, an unmasked checkout password, and a missing cart opt-in. The enclosing JUnit tests expect these failures. Another check expects a timeout when the recovery instruction is missing.

To run the implemented read-only scenarios against an authorized test environment:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example verify
```

Use the origin only: no URL path, credentials, query, or fragment. Do not use the public store for account-creating or order-placing tests. The implemented scenarios inspect account forms, submit read-only product searches, sort products, navigate category links, open product details, and navigate an empty cart; they do not submit account or order forms. Search checks require the AEC-15 catalog examples (Blue Tshirt and Green Tshirt) in the authorized environment. Price sorting requires a Men category fitting on one page, with at least two distinct single USD prices; unsupported price ranges or pagination fail explicitly. Other sorting orders, category contents, and price filtering are not yet checked. Results and generated reports under `automation/target/` are local and ignored by Git.

Cart-changing and checkout scenarios are excluded by default. To run them **only in an authorized non-production environment**, use a disposable in-stock product that permits at least two units, has a single USD price, and starts with an empty guest cart. Ensure the environment uses a test payment setup and cannot submit real orders:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example \
  -DallowCartChanges=true -DallowCheckoutLogin=true \
  -DcartProductPath=/product/your-test-product/ \
  '-Dcucumber.filter.tags=(@checkout_login or @checkout_controls) and not @pending' verify
```

The explicit opt-ins and product path are required. The known public store domain and its subdomains are blocked, but the operator remains responsible for ensuring any other origin is authorized and non-production. Checkout scenarios use isolated browser profiles; the environment owner must provide server-side session cleanup. Never use these scenarios against the public store.

[Checkout login](features/checkout-login.feature) and [order submission](features/checkout-orders.feature) are blocked by `@pending` even when explicitly selected. Their draft implementations require review and local end-to-end fixture validation, particularly authentication evidence, saved-order ownership, field-specific errors and boundary values. Removing the guard is not a substitute for that work. Order submission additionally requires `-DallowCheckoutOrder=true` and an approved environment incapable of processing real orders.

### Checkout fixture variables

Supply these through the environment of the test process; do not put values in source control, feature files, or command-line arguments. All values must be synthetic and approved for the selected test environment.

- Login: `ASKOMDCH_CHECKOUT_USERNAME`, `ASKOMDCH_CHECKOUT_EMAIL`, `ASKOMDCH_CHECKOUT_PASSWORD`.
- Billing: `ASKOMDCH_BILLING_FIRST_NAME`, `ASKOMDCH_BILLING_LAST_NAME`, `ASKOMDCH_BILLING_COUNTRY`, `ASKOMDCH_BILLING_ADDRESS`, `ASKOMDCH_BILLING_CITY`, `ASKOMDCH_BILLING_STATE`, `ASKOMDCH_BILLING_POSTCODE`, `ASKOMDCH_BILLING_PHONE`, `ASKOMDCH_BILLING_EMAIL`.
- Alternative shipping: `ASKOMDCH_SHIPPING_FIRST_NAME`, `ASKOMDCH_SHIPPING_LAST_NAME`, `ASKOMDCH_SHIPPING_COUNTRY`, `ASKOMDCH_SHIPPING_ADDRESS`, `ASKOMDCH_SHIPPING_CITY`, `ASKOMDCH_SHIPPING_STATE`, `ASKOMDCH_SHIPPING_POSTCODE`; `ASKOMDCH_SHIPPING_NOTES` is optional.
- Account creation: `ASKOMDCH_CHECKOUT_UNIQUE_EMAIL` must be a synthetic email template containing `{uuid}`; `ASKOMDCH_CHECKOUT_REGISTERED_EMAIL` must be an existing test account email; `ASKOMDCH_ACCOUNT_PASSWORD_VALID` and `ASKOMDCH_ACCOUNT_PASSWORD_BELOW_MINIMUM`, `..._ABOVE_MAXIMUM`, `..._MISSING_UPPERCASE`, `..._MISSING_LOWERCASE`, `..._MISSING_NUMBER`, `..._MISSING_SPECIAL`.

These draft fixture names are not yet verified. Country/state values must match options accepted by the environment. A successful order scenario can create persistent test data; use an environment with approved cleanup.

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
