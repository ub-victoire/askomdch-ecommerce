# System overview

## Source and confidence

This overview summarizes the existing Jira export and Test Plan AEC-140. It is documentation of recorded scope, not an independent inspection or test of the live application. Acceptance criteria describe required behavior; they are not execution evidence.

## Application

- **Product:** AskOmDch e-commerce web application.
- **URL recorded in Jira:** [https://askomdch.com/](https://askomdch.com/).
- **Business purpose:** Enable customers to discover products, manage accounts and carts, and complete checkout.
- **Confirmed testing approach:** Manual functional testing through a web browser, using Jira/Xray-managed tests (AEC-140).
- **Implementation technology:** Not confirmed by the exported requirements. A diagnostic hypothesis in Bug AEC-163 is not sufficient evidence to identify the application's technology stack.

## Confirmed functional scope

The Jira project has four Epics:

1. **Account Management (AEC-1):** registration, login, account recovery, dashboard/order and address management, and credential security.
2. **Browsing Products (AEC-2):** catalog navigation, search, product filtering and sorting, and product details.
3. **Cart Management (AEC-3):** adding items, changing quantities, removing items, cart totals, and the empty-cart experience. Its Epic description explicitly lists coupon redemption as out of scope.
4. **Checkout (AEC-4):** billing and optional shipping details, order review, payment-method selection, and order submission.

The Jira Story descriptions and acceptance criteria are reproduced in [Epics and Stories](epics-overview.md). The exported Story-to-Test links are in the [traceability matrix](requirements-traceability.md).

## QA status and limits

- The export contains 131 issue records; all have Jira status `To Do`.
- Jira status is not an Xray execution result. The export contains no pass/fail outcomes, run evidence, or execution metrics.
- The export does not establish Test-to-Precondition, Test-to-Test-Set, Test-Set-to-Execution, or Execution-to-Test-Plan membership.
- There is no verified application test-data fixture or credential set in this repository.
- See the traceability matrix and individual Test records for missing links, incomplete descriptions, and clarification needs.
