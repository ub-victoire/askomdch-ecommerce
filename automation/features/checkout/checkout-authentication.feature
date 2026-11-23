@phase-1
Feature: Returning customer checkout login

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product
    And the customer opens the checkout login form
    And an approved registered test account is available

  Rule: AEC-17 registered customer login
    @AEC-71
    Scenario: Log in using a registered username
      When the customer logs in with the test account's registered username and password
      Then the customer is logged in on checkout
      And the returning customer banner is hidden

    @AEC-73
    Scenario: Log in using a registered email address
      When the customer logs in with the test account's registered email address and password
      Then the customer is logged in on checkout
      And the returning customer banner is hidden
