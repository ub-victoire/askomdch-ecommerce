# Future automation workspace

This directory is prepared for future Cucumber automation using Gherkin, feature files, step definitions, hooks, Page Object Model, and managed test data. **No automation implementation is included yet.**

See [feature mapping](feature-mapping.md) for all 63 existing Jira Tests, proposed feature grouping, and readiness notes. Candidate tags/names should retain their Jira key (for example, `@AEC-149`); a mapping is not a test result or a commitment that the scenario is ready to automate.

## Planned layout

- `features/` — future Gherkin feature files
- `step_definitions/` — future step bindings
- `support/hooks/` — future lifecycle hooks
- `pages/` — future Page Object Model
- `data/` — future automation data adapters; never store credentials

Do not add `.feature` files or executable automation until the manual cases, data, environment, and expected results are confirmed.
