@cart_session @checkout_robustness @phase-4
Feature: Checkout recovery and edge cases
  Use approved test accounts and synthetic data. Order tests require a test
  environment with a documented cleanup process.

  Rule: Empty and unavailable products
    @observed
    Scenario: Open checkout with an empty cart
      Given the customer has an empty cart
      When the customer opens checkout
      Then the empty cart page is shown
      And no checkout form is shown

    @pending
    Scenario: Do not add an out-of-stock product to the cart
      Given an approved out-of-stock product is configured
      When the customer tries to add that product to the cart
      Then the product cannot be added
      And the cart does not contain that product

  Rule: Keep checkout data after login or validation errors
    @pending
    Scenario: Keep cart items after a successful checkout login
      Given the cart contains the configured product
      And an approved registered test account is available
      When the customer logs in during checkout
      Then the cart still contains the configured product

    @pending
    Scenario Outline: Keep entered billing details after an email validation error
      Given the customer has entered these synthetic billing details:
        | First name     | <firstName> |
        | Last name      | <lastName> |
        | Street address | <address> |
        | Town / City    | <city> |
        | ZIP Code       | <postcode> |
      When the customer submits checkout with invalid email "<email>"
      Then the customer remains on the checkout page
      And the entered billing details are still present

      Examples:
        | firstName | lastName | address          | city      | postcode | email       |
        | Casey     | Example  | 123 Test Street  | Testville | 90210    | not-an-email |

  Rule: Prevent duplicate and lost orders
    @pending
    Scenario: Submit an order only once after repeated clicks
      Given the billing form uses these synthetic values:
        | First name     | Casey                       |
        | Last name      | Example                     |
        | Country        | United States               |
        | Street address | 123 Test Street             |
        | Town / City    | Testville                   |
        | State          | California                  |
        | ZIP Code       | 90210                       |
        | Email address  | qa.checkout@example.invalid |
      When the customer submits a valid order twice
      Then only one order is created

    @pending
    Scenario: Keep a submitted order in the customer's order history
      Given an approved registered test account is available
      And the customer has submitted a valid test order
      When the customer opens the account order history
      Then the submitted order appears once with the correct details

    @pending
    Scenario: Link a checkout order to the account created during checkout
      Given the customer creates an account during a valid test checkout
      When the customer opens the account order history
      Then the checkout order appears under the new account
