# AskOmDch E-commerce Acceptance Tests

This repository contains behavior-driven test specifications for the
[AskOmDch e-commerce site](https://askomdch.com/). The scenarios describe
customer-facing shopping, account, and checkout behavior in Gherkin.

## What’s covered

- **Account management** — customer login and password reset.
- **Product browsing** — product search and product details.
- **Cart management** — adding and removing products, changing quantities,
  checking totals, and handling invalid coupons.
- **Checkout** — returning-customer login, checkout controls, billing and
  shipping validation, account creation, order submission, and payment methods.

Checkout scenarios marked `@pending` are not ready for execution; their
assertions or test fixtures still need to be completed and validated.

## Repository layout

```text
automation/
└── features/
    ├── AccountManagement/  # Login and password reset
    ├── cart_management/    # Cart operations
    ├── checkout/           # Checkout, authentication, and orders
    └── product_browsing/   # Search and product details
```

Each `.feature` file groups related behavior into a Gherkin `Feature`, with
scenarios and, where useful, scenario outlines and example tables.

## Getting started

Clone the repository:

```bash
git clone https://github.com/ub-victoire/askomdch-ecommerce.git
cd askomdch-ecommerce
```

The repository currently contains feature specifications only. It does not
include a BDD runner, step definitions, dependency manifests, or runner
configuration, so there is no test command to run these scenarios yet. To
execute them, add and configure a compatible automation framework, implement
the steps used by the features, and provide the required test fixtures.

## Test environment and safety

Some scenarios interact with account login and password recovery; checkout
scenarios may submit orders or select payment methods. Run automated tests only
against an environment you are authorized to test, with approved synthetic
accounts and data, sandbox payment methods, and a documented cleanup process.
Do not use real customer credentials, payment details, or production accounts.

## Contributing scenarios

1. Add or update a `.feature` file under the directory for its area.
2. Keep scenarios focused on observable behavior and use scenario outlines
   when the same behavior should be checked with multiple examples.
3. Use tags consistently to identify suites, priorities, and tracked cases.
   Keep incomplete scenarios clearly marked, for example with `@pending`.
4. Document any required fixtures or environment assumptions in the feature
   or the corresponding test-runner documentation once one is added.

### Contributors
1. Ushindi Victoire
2. Aline
3. Paulin
4. Steven
