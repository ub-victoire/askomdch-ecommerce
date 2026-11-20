# AEC-34 — Quantity 0 removes the item

**Jira ID:** AEC-34<br>
**Test case title:** Quantity 0 removes the item<br>
**Jira issue type:** Test<br>
**Jira issue status:** To Do (not an execution result)<br>
**Priority:** High<br>
**Related Epic:** AEC-3 — Cart Management<br>
**Related User Story:** AEC-10 — Update Cart details<br>
**Linked Bug(s):** None linked in the export.

## Preconditions

No Xray Test-to-Precondition relationship is present in the CSV. Do not assign a Precondition based on proximity or similar wording. Review the [Precondition catalog](../preconditions-catalog.md) and confirm any needed association in Jira/Xray.

## Test objective (from Jira Description)

**Missing in the Jira export.** Clarification required.

## Test steps

No separate structured Test Steps field is available in this export. Any procedure text in the Jira Description appears above; no steps have been added. Clarification is required before execution where the description is insufficient.

## Test data references

No Jira Test Data link or explicit dataset reference is present in the export. Use only approved synthetic fixtures; see [test-data guidance](../../test-data/README.md).

## Expected results

No separate structured Expected Results field is available in this export. Any expectation stated in the Jira Description appears above; no additional result or message has been inferred.

## Test Set membership

No Test-to-Test-Set membership relationship is present in the export. Membership is unconfirmed; see the [Test Set catalog](../test-sets.md).

## Automation status

Implemented as a scoped scenario in [cart-updates.feature](../../automation/features/cart-management/cart-updates.feature), based on the Test title. Local fixture only; application execution and original Xray steps remain unverified.

The Test title expects quantity zero to remove an item, while linked Story AEC-10 specifies a minimum quantity of 1. Clarify the intended rule before treating either as authoritative.
