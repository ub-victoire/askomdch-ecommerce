# AEC-96 — Session ends when the browser is closed and Remember me is unchecked

**Jira ID:** AEC-96<br>
**Test case title:** Session ends when the browser is closed and Remember me is unchecked<br>
**Jira issue type:** Test<br>
**Jira issue status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Related Epic:** AEC-1 — Account Management<br>
**Related User Story:** AEC-6 — Log In<br>
**Linked Bug(s):** None linked in the export.

## Preconditions

No Xray Test-to-Precondition relationship is present in the CSV. Do not assign a Precondition based on proximity or similar wording. Review the [Precondition catalog](../preconditions-catalog.md) and confirm any needed association in Jira/Xray.

## Test objective (from Jira Description)

Verifies that the login session ends when the browser is closed and "Remember me" is unchecked.

## Test steps

No separate structured Test Steps field is available in this export. Any procedure text in the Jira Description appears above; no steps have been added. Clarification is required before execution where the description is insufficient.

## Test data references

No Jira Test Data link or explicit dataset reference is present in the export. Use only approved synthetic fixtures; see [test-data guidance](../../test-data/README.md).

## Expected results

No separate structured Expected Results field is available in this export. Any expectation stated in the Jira Description appears above; no additional result or message has been inferred.

## Test Set membership

No Test-to-Test-Set membership relationship is present in the export. Membership is unconfirmed; see the [Test Set catalog](../test-sets.md).

## Automation status

An opt-in session expiration check is implemented in [account-login-draft.feature](../../automation/features/account-management/account-login-draft.feature) and validated against a local fixture only. Application execution is unverified.
