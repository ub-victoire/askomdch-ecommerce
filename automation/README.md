# Java Cucumber and Selenium automation

All 63 Jira Test keys are represented in feature files, including three clarification placeholders that are not business scenarios. Account login, registration and recovery have opt-in Selenium checks validated against local fixtures, not a real account environment. AEC-57, AEC-93 and AEC-94 remain blocked pending clarification. Checkout login, two payment-order checks, and an alternative-shipping order check are fixture-validated; remaining checkout order drafts remain blocked. See the [Jira Test mapping](feature-mapping.md) for coverage and gaps.

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

The [checkout login](features/checkout/checkout-login.feature) and [order submission](features/checkout/checkout-orders.feature) scenarios marked `@pending` are blocked even when explicitly selected. They require review and local end-to-end fixture validation, particularly saved-order ownership, field-specific errors and boundary values. Removing the guard is not a substitute for that work. Order submission additionally requires `-DallowCheckoutOrder=true` and an approved environment incapable of processing real orders.

The [checkout authentication checks](features/checkout/checkout-authentication.feature) for AEC-71 and AEC-73 are excluded by default. They require the checkout login and cart opt-ins, an approved product, and the synthetic `ASKOMDCH_CHECKOUT_USERNAME`, `ASKOMDCH_CHECKOUT_EMAIL`, and `ASKOMDCH_CHECKOUT_PASSWORD` environment variables. They check the account dashboard after login and return to checkout; this supplements, rather than replaces, checking the returning-customer banner. These checks are fixture-validated only.

The [checkout login scenarios](features/checkout/checkout-login.feature) are also opt-in and fixture-validated only. In addition to login variables, the saved-address scenario requires the billing and shipping variables below to match the disposable account's saved details. The unregistered-username example is `ad`, as specified by AEC-17, and the email example is `qa-unregistered@example.invalid`: confirm both are unregistered before running. Error wording and the saved-address controls may differ in the application. Arrange cleanup and run with the checkout login command above, selecting `-Dcucumber.filter.tags=@checkout_login` if needed.

The [payment-order checks](features/checkout/checkout-order-payments.feature) for AEC-144 and AEC-145 submit orders and assert the resulting payment method. They passed against a simulated local fixture, including negative checks for a wrongly recorded payment method; they have not been verified against the application. In an authorized non-production environment with a test payment setup, disposable product, synthetic billing fixtures, and approved cleanup:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example \
  -DallowCartChanges=true -DallowCheckoutOrder=true \
  -DcartProductPath=/product/your-test-product/ \
  '-Dcucumber.filter.tags=(@AEC-144 or @AEC-145) and @checkout_order and not @pending' verify
```

Provide billing variables listed below through the environment. The selected country and state must be valid options. The checks are excluded by default and the public store remains blocked.

The [alternative-shipping order check](features/checkout/checkout-orders.feature) for AEC-75 also submits an order. It compares all required shipping address values with the confirmation panel, using the visible selected country/state labels. It has only been checked against a simulated local fixture. Supply the billing and shipping variables below and use the same safeguards and opt-ins as the payment-order checks, selecting `-Dcucumber.filter.tags=@AEC-75`. Country/state representations may differ on the actual site; investigate mismatches rather than weakening the assertion.

The [clarification placeholders](features/unassigned/clarification-draft.feature) preserve AEC-81, AEC-101 and AEC-115 without asserting unconfirmed behavior or Jira links. AEC-57, AEC-93 and AEC-94 also need clarification before their drafts can be finalized.

### Registration and recovery checks

The [registration](features/account-management/registration-draft.feature) and [recovery](features/account-management/recovery-draft.feature) checks are implemented and exercised against simulated local fixtures only. They are excluded by default. To run them, arrange an authorized non-production environment, approved synthetic data, and cleanup with its owner; the public store and its subdomains are blocked. AEC-60 and AEC-131 also require the account login opt-in. AEC-131 changes the fixture account password: restore or replace that account before the next run.

Provide the following in the test process environment, never in source control or command-line arguments:

- Registration: `ASKOMDCH_REGISTRATION_USERNAME_TEMPLATE` (exactly `{letters}`), `ASKOMDCH_REGISTRATION_EMAIL_TEMPLATE` (contains `{uuid}`), `ASKOMDCH_REGISTRATION_PASSWORD_VALID`, `ASKOMDCH_REGISTRATION_EXISTING_USERNAME`, and `ASKOMDCH_REGISTRATION_EXISTING_EMAIL`. Generated usernames may collide, particularly at the five-character boundary; a collision is not proof of an application defect.
- Recovery: `ASKOMDCH_RECOVERY_USERNAME`, `ASKOMDCH_RECOVERY_EMAIL`, `ASKOMDCH_RECOVERY_OLD_PASSWORD`, `ASKOMDCH_RECOVERY_NEW_PASSWORD`, and `ASKOMDCH_TEST_MAILBOX_URL`. `ASKOMDCH_TEST_MAILBOX_TOKEN` is optional. `ASKOMDCH_TEST_MAILBOX_NEGATIVE_WINDOW_SECONDS` defaults to 5 and accepts 1–60; an empty mailbox during that window does not rule out later delivery.

The proposed mailbox adapter accepts a GET request with URL-encoded `recipient` and `since` (epoch milliseconds) query parameters, and returns JSON containing a numeric `count` and, when messages are present, a `resetUrl`. Reset links must have the same origin as `baseUrl`. This contract has only been exercised against the local fixture; integrate and validate it with a controlled mailbox before using it elsewhere. For invalid or unregistered identifiers the query checks only that identifier, not all possible mailboxes.

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example \
  -DallowAccountRegistration=true -DallowAccountRecovery=true -DallowAccountLogin=true \
  '-Dcucumber.filter.tags=(@account_registration or @account_recovery) and not @pending' verify
```

### Account login checks

The [account login feature](features/account-management/account-login-draft.feature) has implemented checks for AEC-90, AEC-91, AEC-92, AEC-95, AEC-96, AEC-97 and AEC-98. They are excluded by default and require an authorized non-production environment and a disposable registered test account. The same environment variables used by checkout login provide its credentials: `ASKOMDCH_CHECKOUT_USERNAME`, `ASKOMDCH_CHECKOUT_EMAIL`, and `ASKOMDCH_CHECKOUT_PASSWORD`. Provide them through the environment, never command-line arguments or repository files.

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example \
  -DallowAccountLogin=true '-Dcucumber.filter.tags=@account_login and not @pending' verify
```

The known public domain and its subdomains are blocked, but the operator must confirm any other origin is non-production and authorized. Session checks use an isolated browser profile; arrange server-side account/session cleanup. AEC-98 checks masking and the browser URL, not plaintext exposure through network/API responses. These checks have not been run against the application.

### Checkout fixture variables

Supply these through the environment of the test process; do not put values in source control, feature files, or command-line arguments. All values must be synthetic and approved for the selected test environment.

- Login: `ASKOMDCH_CHECKOUT_USERNAME`, `ASKOMDCH_CHECKOUT_EMAIL`, `ASKOMDCH_CHECKOUT_PASSWORD`.
- Billing: `ASKOMDCH_BILLING_FIRST_NAME`, `ASKOMDCH_BILLING_LAST_NAME`, `ASKOMDCH_BILLING_COUNTRY`, `ASKOMDCH_BILLING_ADDRESS`, `ASKOMDCH_BILLING_CITY`, `ASKOMDCH_BILLING_STATE`, `ASKOMDCH_BILLING_POSTCODE`, `ASKOMDCH_BILLING_PHONE`, `ASKOMDCH_BILLING_EMAIL`.
- Alternative shipping: `ASKOMDCH_SHIPPING_FIRST_NAME`, `ASKOMDCH_SHIPPING_LAST_NAME`, `ASKOMDCH_SHIPPING_COUNTRY`, `ASKOMDCH_SHIPPING_ADDRESS`, `ASKOMDCH_SHIPPING_CITY`, `ASKOMDCH_SHIPPING_STATE`, `ASKOMDCH_SHIPPING_POSTCODE`; `ASKOMDCH_SHIPPING_NOTES` is optional.
- Account creation: `ASKOMDCH_CHECKOUT_UNIQUE_EMAIL` must be a synthetic email template containing `{uuid}`; `ASKOMDCH_CHECKOUT_REGISTERED_EMAIL` must be an existing test account email; `ASKOMDCH_ACCOUNT_PASSWORD_VALID` and `ASKOMDCH_ACCOUNT_PASSWORD_BELOW_MINIMUM`, `..._ABOVE_MAXIMUM`, `..._MISSING_UPPERCASE`, `..._MISSING_LOWERCASE`, `..._MISSING_NUMBER`, `..._MISSING_SPECIAL`.

The [sample dataset](../test-data/checkout-sample-fixtures.csv) is illustrative only and is not loaded by the automation. These fixture names and values are not yet verified against the application. Country/state values must match options accepted by the environment. A successful order scenario can create persistent test data; use an environment with approved cleanup.

If Selenium Manager cannot resolve ChromeDriver, supply the path to an installed matching driver with `-Dwebdriver.chrome.driver=/path/to/chromedriver`.

To select a Jira Test:

```sh
mvn -f automation/pom.xml -P e2e -DbaseUrl=https://your-test-environment.example -Dcucumber.filter.tags=@AEC-48 verify
```

## Structure and data

- [`features/`](features/) groups Gherkin by [account management](features/account-management/), [browsing products](features/browsing-products/), [cart management](features/cart-management/), and [checkout](features/checkout/). [Unassigned placeholders](features/unassigned/) are kept separate because their intended scope or relationships need clarification.
- [`src/test/java/`](src/test/java/) contains Selenium page objects, step definitions, hooks, configuration, and local-fixture tests.
- [`data/`](data/) is reserved for future automation data. The [proposed symbolic datasets](../test-data/README.md) are not verified fixtures. Do not commit passwords, credentials, customer information, or secrets.

The scripts use the exported Test descriptions and linked Story acceptance criteria as requirements. The CSV does not contain original Xray step definitions or execution results. Consult the mapping for the precise scope and execution evidence for each check.
