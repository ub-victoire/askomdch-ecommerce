# Requirements traceability

## Reading this matrix

The matrix uses only Jira parent keys and inward/outward issue links present in the exported CSV. The Test Set and Test Execution columns list records parented to that Epic; they do **not** imply membership of the listed Tests in those Sets or Executions. Jira status `To Do` is not a test result.

| Epic | Story | Tests linked to Story | Test Sets parented to Epic | Test Executions parented to Epic | Test Plan |
|---|---|---|---|---|---|
| AEC-1 Account Management | AEC-5 Registration | [AEC-40](../test-cases/account-management/AEC-40.md), [AEC-42](../test-cases/account-management/AEC-42.md), [AEC-44](../test-cases/account-management/AEC-44.md), [AEC-45](../test-cases/account-management/AEC-45.md), [AEC-48](../test-cases/account-management/AEC-48.md), [AEC-49](../test-cases/account-management/AEC-49.md), [AEC-51](../test-cases/account-management/AEC-51.md), [AEC-54](../test-cases/account-management/AEC-54.md), [AEC-57](../test-cases/account-management/AEC-57.md), [AEC-58](../test-cases/account-management/AEC-58.md), [AEC-60](../test-cases/account-management/AEC-60.md) | — | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-1 Account Management | AEC-6 Log In | [AEC-89](../test-cases/account-management/AEC-89.md), [AEC-90](../test-cases/account-management/AEC-90.md), [AEC-91](../test-cases/account-management/AEC-91.md), [AEC-92](../test-cases/account-management/AEC-92.md), [AEC-93](../test-cases/account-management/AEC-93.md), [AEC-94](../test-cases/account-management/AEC-94.md), [AEC-95](../test-cases/account-management/AEC-95.md), [AEC-96](../test-cases/account-management/AEC-96.md), [AEC-97](../test-cases/account-management/AEC-97.md), [AEC-98](../test-cases/account-management/AEC-98.md) | — | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-1 Account Management | AEC-8 Account Recovery | [AEC-123](../test-cases/account-management/AEC-123.md), [AEC-124](../test-cases/account-management/AEC-124.md), [AEC-125](../test-cases/account-management/AEC-125.md), [AEC-126](../test-cases/account-management/AEC-126.md), [AEC-127](../test-cases/account-management/AEC-127.md), [AEC-130](../test-cases/account-management/AEC-130.md), [AEC-131](../test-cases/account-management/AEC-131.md) | — | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-2 Browsing Products | AEC-11 Product Filtering | [AEC-133](../test-cases/browsing-products/AEC-133.md) | [AEC-155](../test-cases/test-sets.md#aec-155), [AEC-162](../test-cases/test-sets.md#aec-162) | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-2 Browsing Products | AEC-14 Product Sorting | [AEC-119](../test-cases/browsing-products/AEC-119.md) | [AEC-155](../test-cases/test-sets.md#aec-155), [AEC-162](../test-cases/test-sets.md#aec-162) | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-2 Browsing Products | AEC-15 Product Searching | [AEC-18](../test-cases/browsing-products/AEC-18.md) | [AEC-155](../test-cases/test-sets.md#aec-155), [AEC-162](../test-cases/test-sets.md#aec-162) | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-2 Browsing Products | AEC-16 View Product Details | [AEC-81](../test-cases/browsing-products/AEC-81.md), [AEC-110](../test-cases/browsing-products/AEC-110.md) | [AEC-155](../test-cases/test-sets.md#aec-155), [AEC-162](../test-cases/test-sets.md#aec-162) | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-3 Cart Management | AEC-7 Add Product to Cart | [AEC-28](../test-cases/cart-management/AEC-28.md) | [AEC-27](../test-cases/test-sets.md#aec-27) | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-3 Cart Management | AEC-10 Update Cart details | [AEC-32](../test-cases/cart-management/AEC-32.md), [AEC-34](../test-cases/cart-management/AEC-34.md), [AEC-85](../test-cases/cart-management/AEC-85.md), [AEC-108](../test-cases/cart-management/AEC-108.md) | [AEC-27](../test-cases/test-sets.md#aec-27) | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-3 Cart Management | AEC-13 View cart details | [AEC-108](../test-cases/cart-management/AEC-108.md), [AEC-112](../test-cases/cart-management/AEC-112.md) | [AEC-27](../test-cases/test-sets.md#aec-27) | — | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-4 Checkout | AEC-17 Return Customer Login During Checkout | [AEC-64](../test-cases/checkout/AEC-64.md), [AEC-69](../test-cases/checkout/AEC-69.md), [AEC-71](../test-cases/checkout/AEC-71.md), [AEC-73](../test-cases/checkout/AEC-73.md), [AEC-79](../test-cases/checkout/AEC-79.md), [AEC-82](../test-cases/checkout/AEC-82.md), [AEC-86](../test-cases/checkout/AEC-86.md), [AEC-99](../test-cases/checkout/AEC-99.md), [AEC-103](../test-cases/checkout/AEC-103.md), [AEC-105](../test-cases/checkout/AEC-105.md), [AEC-109](../test-cases/checkout/AEC-109.md) | [AEC-63](../test-cases/test-sets.md#aec-63), [AEC-113](../test-cases/test-sets.md#aec-113), [AEC-116](../test-cases/test-sets.md#aec-116) | [AEC-142](../test-executions/AEC-142.md) | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-4 Checkout | AEC-20 Account Creation During Checkout | [AEC-149](../test-cases/checkout/AEC-149.md) | [AEC-63](../test-cases/test-sets.md#aec-63), [AEC-113](../test-cases/test-sets.md#aec-113), [AEC-116](../test-cases/test-sets.md#aec-116) | [AEC-142](../test-executions/AEC-142.md) | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-4 Checkout | AEC-21 Alternative Shipping Address | [AEC-75](../test-cases/checkout/AEC-75.md), [AEC-117](../test-cases/checkout/AEC-117.md), [AEC-118](../test-cases/checkout/AEC-118.md), [AEC-121](../test-cases/checkout/AEC-121.md) | [AEC-63](../test-cases/test-sets.md#aec-63), [AEC-113](../test-cases/test-sets.md#aec-113), [AEC-116](../test-cases/test-sets.md#aec-116) | [AEC-142](../test-executions/AEC-142.md) | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-4 Checkout | AEC-22 Order review | [AEC-157](../test-cases/checkout/AEC-157.md) | [AEC-63](../test-cases/test-sets.md#aec-63), [AEC-113](../test-cases/test-sets.md#aec-113), [AEC-116](../test-cases/test-sets.md#aec-116) | [AEC-142](../test-executions/AEC-142.md) | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |
| AEC-4 Checkout | AEC-23 Billing Details & Payment method | [AEC-144](../test-cases/checkout/AEC-144.md), [AEC-145](../test-cases/checkout/AEC-145.md), [AEC-148](../test-cases/checkout/AEC-148.md), [AEC-153](../test-cases/checkout/AEC-153.md), [AEC-156](../test-cases/checkout/AEC-156.md) | [AEC-63](../test-cases/test-sets.md#aec-63), [AEC-113](../test-cases/test-sets.md#aec-113), [AEC-116](../test-cases/test-sets.md#aec-116) | [AEC-142](../test-executions/AEC-142.md) | [AEC-140](../test-plans/AEC-140-functional-testing.md) ↔ Epic |

## Traceability gaps and concerns

- AEC-101 (“Proceed to checkout with cart items”) and AEC-115 (“Reject an invalid coupon”) have no exported Story links or descriptions. They remain untraced; their Epic and Test Set are not inferred.
- Candidate investigation links, **not exported relationships**: [AEC-101](../test-cases/untraced/AEC-101.md) may relate to [AEC-13](epics-overview.md#aec-13), [AEC-143](../test-cases/preconditions-catalog.md#aec-143), and [AEC-27](../test-cases/test-sets.md#aec-27). [AEC-115](../test-cases/untraced/AEC-115.md) may relate to the coupon control described in AEC-13 and to AEC-27; the expected rejection and specific preconditions are not specified.
- AEC-115 describes an invalid-coupon test, while the AEC-3 Cart Management Epic lists promotional coupon application/removal as out of scope. With no Story link or description, this is a scope/traceability question, not a basis for reassigning or deleting the Test.
- AEC-81 is linked to AEC-16, although its title is “View cart Contents.” Preserve the link and resolve the mismatch in Jira.
- AEC-108 is linked to both AEC-10 and AEC-13. Both links are retained.
- No Test has an exported Xray Precondition link or Test Set membership link. AEC-150–AEC-152 are not assigned to AEC-149 without relationship evidence.
- The CSV does not link Tests to Test Executions, Test Sets to Test Executions, or Test Executions to AEC-140. Narrative descriptions are noted in the relevant catalogs but are not elevated to issue links.
- AEC-140 has exported links to all four Epics. It does not have direct exported links to the four Test Executions.
- AEC-113 and AEC-116 are parented to Checkout while their descriptions describe cross-Epic scope. AEC-137 and AEC-139 have no parent key.
- Test/Bug and Execution/Bug links are recorded in the Test and Execution documents; they do not imply that the associated tests failed or passed.

## Defect links

| Test | Linked Bug |
|---|---|
| AEC-57 | AEC-161 |
| AEC-103 | AEC-159 |
| AEC-117 | AEC-160 |
| AEC-124, AEC-125, AEC-131 | AEC-163 |

## Status interpretation

All exported Jira issue statuses are `To Do`. The export contains no Xray execution outcome or evidence; this matrix reports traceability only and makes no coverage-completion or pass/fail claim.
