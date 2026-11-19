# Test data

The CSV files in this directory are **proposed symbolic datasets**, not verified application fixtures. They are based on Jira acceptance criteria and Test descriptions. Validate them against an approved non-production environment before execution.

## Datasets

- [Positive scenarios](positive-scenarios.csv)
- [Negative scenarios](negative-scenarios.csv)
- [Boundary scenarios](boundary-scenarios.csv)
- [Existing-account scenarios](existing-account-scenarios.csv)

The datasets use scenario labels and synthetic aliases only. They do not contain passwords, credentials, real customer information, or production secrets. An alias such as `registered-account-alias` describes the required state of a fixture; it is not a working account.

For AEC-20/AEC-149, the password column contains only a validation-category label (for example, `below_minimum_length`), never a password value. A controlled test account and a unique billing-email fixture must be provisioned outside version control.

## Verification requirements

Before using any proposed row, confirm the applicable Jira acceptance criteria, environment behavior, field limits, account state, and any required email/payment test doubles. Do not assume a proposed scenario has been executed or passed.
