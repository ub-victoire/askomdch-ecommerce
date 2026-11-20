@pending @needs_clarification
Feature: Tests requiring Jira clarification
  These are placeholders, not executable business scenarios or confirmed Story assignments.
  Recover the original Xray steps and expected results before defining or automating them.

  @AEC-81
  Scenario: Clarify view cart contents
    Given the Test title refers to cart contents
    And its exported Story link is AEC-16 View Product Details
    When the Test intent and original steps are reviewed
    Then the intended Story link and expected results must be confirmed

  @AEC-101
  Scenario: Clarify proceeding to checkout with cart items
    Given the Test has no exported Story link or description
    When the Test intent and original steps are reviewed
    Then the intended Story link and expected results must be confirmed

  @AEC-115
  Scenario: Clarify invalid coupon rejection
    Given the Test has no exported Story link or description
    And AEC-3 lists promotional coupon application and removal as out of scope
    When the Test intent and original steps are reviewed
    Then the scope, intended Story link, and expected results must be confirmed
