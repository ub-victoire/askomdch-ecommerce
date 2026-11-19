# Test case index

This directory documents the **63 existing Jira Test issues**. Jira keys and exported descriptions are preserved. No duplicate Jira Tests were created.

## By linked Epic

- [Account Management](account-management/) — Tests linked to Stories AEC-5, AEC-6, or AEC-8.
- [Browsing Products](browsing-products/) — Tests linked to Stories AEC-11, AEC-14, AEC-15, or AEC-16. AEC-81 is retained under the linked Story AEC-16 and flagged for a title/link mismatch.
- [Cart Management](cart-management/) — Tests linked to Stories AEC-7, AEC-10, or AEC-13.
- [Checkout](checkout/) — Tests linked to Stories AEC-17, AEC-20, AEC-21, AEC-22, or AEC-23.
- [Untraced Tests](untraced/) — AEC-101 and AEC-115 have no exported Story link.

## Supporting catalogs

- [Preconditions](preconditions-catalog.md) — all 32 Jira Precondition issues. The export contains no explicit Test-to-Precondition relationships.
- [Test Sets](test-sets.md) — all 8 Jira Test Set issues. The export contains no Test-to-Test-Set membership relationships.

## How to read each Test record

- `Jira status` is the Jira issue status, not an execution result.
- `Related Epic` and `Related User Story` are derived only from exported links and the linked Story's parent key.
- Original Jira description text is retained. The export does not provide consistent separate step and expected-result fields; missing fields are called out rather than invented.
- No Test Set membership or Precondition association is inferred.
- Automation status is “not implemented”; candidate mapping is documented separately in [automation/feature-mapping.md](../automation/feature-mapping.md).
