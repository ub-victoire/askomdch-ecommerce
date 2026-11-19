# AskOmDch E-commerce QA

This repository contains the QA documentation and future automation planning for the AskOmDch e-commerce application. The project is organized around the existing Jira Epics, Stories, Xray Tests, Preconditions, Test Sets, Test Executions, and Test Plan.

## Application and purpose

AskOmDch supports online product discovery, cart management, account management, and checkout. The application URL recorded in Jira Test Plan AEC-140 is [https://askomdch.com/](https://askomdch.com/).

The QA scope is limited to the four Jira Epics: Account Management (AEC-1), Browsing Products (AEC-2), Cart Management (AEC-3), and Checkout (AEC-4). The repository documents the requirements and existing test inventory. Limited read-only site checks are recorded in [execution summaries](test-reports/execution-summaries/); they do not establish complete Xray Test results.

## QA objectives

- Preserve Jira keys, acceptance criteria, descriptions, and exported relationships.
- Make requirement-to-test traceability and its gaps visible.
- Provide careful manual test documentation without inventing steps or results.
- Organize proposed synthetic test data without storing credentials or personal data.
- Track the existing Tests through a Cucumber automation map; keep incomplete cases visibly blocked.

## Verified tools and approach

Jira/Xray and manual testing through a web browser are named in AEC-140. The initial automation uses Java, Cucumber, Gherkin, Selenium, and Page Object Model. The application's implementation stack is not confirmed by the Jira export.

## Repository map

| Location | Purpose |
|---|---|
| `requirements/` | System overview, Jira Epic/Story descriptions, and requirements traceability |
| `test-plans/` | Existing Jira Test Plan AEC-140 |
| `test-cases/` | The 63 existing Jira Test issues, Precondition catalog, and Test Set catalog |
| `test-data/` | Proposed, synthetic scenario data and its verification notes |
| `test-executions/` | The four existing Jira Test Executions; no fabricated outcomes |
| `test-reports/` | Evidence-based QA reports and reporting guidance |
| `automation/` | Cucumber/Selenium checks and Jira-Test-to-candidate mapping |
| `source-exports/` | Local raw exports; intentionally excluded from Git |

## Getting started

1. Clone the repository and read this page.
2. Read [the system overview](requirements/system-overview.md), [Epic and Story details](requirements/epics-overview.md), and [the traceability matrix](requirements/requirements-traceability.md).
3. Use the test case index to find a Jira Test by key. Check its linked requirements and clarification notes before preparing execution.
4. Treat files in `test-data/` as proposed symbolic data until the application and test environment are verified. No passwords, credentials, or real customer data belong in this repository.
5. Follow the existing plan in `test-plans/`. Record execution evidence in Jira/Xray; do not infer a result from an issue status.
6. For automation prerequisites, safe local fixture validation, and the explicit test-environment configuration, see [automation setup](automation/README.md).

## Testing approach

AEC-140 describes manual functional testing of the four Epics, with test cases organized into Epic-based Test Sets and evidence/results/defects recorded in Xray. The exported CSV does not provide Xray run results or membership links for Tests to Test Sets, Test Sets to Executions, or Executions to the Test Plan. Those gaps are explicitly documented rather than inferred.

## Important documentation

- [System overview](requirements/system-overview.md)
- [Epics and Stories](requirements/epics-overview.md)
- [Requirements traceability](requirements/requirements-traceability.md)
- [Existing functional Test Plan AEC-140](test-plans/AEC-140-functional-testing.md)
- [Test case index](test-cases/README.md)
- [Proposed test data](test-data/README.md)
- [Existing Test Executions](test-executions/README.md)
- [Reporting guidance](test-reports/README.md)
- [Automation setup](automation/README.md) and [Test mapping](automation/feature-mapping.md)

## Jira export handling

`source-exports/` is ignored by Git because raw Jira exports may contain personal or account-related information. Keep the export unchanged and out of commits. Obtain it through an approved project channel when needed; do not add it with force or copy sensitive fields into documentation.
