@cart_session @checkout_order
Feature: Checkout addresses, account, and order scenarios
  Each scenario checks a separate checkout behavior. Scenario outlines reuse
  the same validation for different fields; they are not duplicate order flows.
  Use approved test data and the site's test payment mode only.

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product

  Rule: AEC-21 alternative shipping
    @AEC-75
    Scenario: Submit an order with a different shipping address
      Given the approved checkout billing fixture is loaded
      And the approved alternative shipping fixture is loaded
      When the customer submits the order with the alternative shipping address
      Then the order-received shipping address matches the alternative shipping fixture

    @AEC-117 @pending
    Scenario Outline: Reject a missing mandatory shipping field
      Given the approved checkout billing fixture is loaded
      And the approved alternative shipping fixture is loaded
      And the selected country requires the "<field>" shipping field
      When the customer submits checkout with the "<field>" shipping field empty
      Then the customer remains on the checkout page
      And the "<field>" shipping field is highlighted with a red outline
      And a specific shipping field error is displayed

      Examples:
        | field            |
        | First name       |
        | Last name        |
        | Country / Region |
        | Street address   |
        | Town / City      |
        | State            |
        | ZIP Code         |

    @AEC-118 @pending
    Scenario: Accept the maximum length for alternative shipping fields
      Given the approved checkout billing fixture is loaded
      And the approved alternative shipping fixture is loaded
      And the alternative shipping fixture uses the documented maximum lengths:
        | First name     | 50  |
        | Last name      | 50  |
        | Town / City    | 50  |
        | Street address | 100 |
        | Order Notes    | 500 |
      When the customer submits the order with that alternative shipping fixture
      Then the order is submitted with the alternative shipping address

    @AEC-121 @pending
    Scenario Outline: Reject non-alphabetical shipping names and city
      Given the approved checkout billing fixture is loaded
      And the approved alternative shipping fixture is loaded
      When the customer submits checkout with non-alphabetical characters in shipping "<field>"
      Then the customer remains on the checkout page
      And the invalid shipping "<field>" is highlighted with a red outline
      And a specific shipping field error is displayed

      Examples:
        | field       |
        | First name  |
        | Last name   |
        | Town / City |

  Rule: AEC-22 order confirmation
    @AEC-157 @pending
    Scenario: Show order details after placing an order
      Given the approved checkout billing fixture is loaded
      When the customer submits a valid order
      Then the customer is on the order-received page
      And the page shows the order number, date, status, total, and item details

  Rule: AEC-23 billing and payment
    @AEC-148 @pending
    Scenario: Accept the maximum length for billing fields
      Given the approved checkout billing fixture is loaded
      And the billing fixture uses the documented maximum lengths:
        | First name     | 50  |
        | Last name      | 50  |
        | Town / City    | 50  |
        | Street address | 100 |
        | Phone          | 15  |
      When the customer submits the order with that billing fixture
      Then the order is submitted with the billing details

    @AEC-153 @pending
    Scenario Outline: Reject a missing mandatory billing field
      Given the approved checkout billing fixture is loaded
      And the selected country requires the "<field>" billing field
      When the customer submits checkout with the "<field>" billing field empty
      Then the customer remains on the checkout page
      And the "<field>" billing field has a red border
      And the AEC-23 required-field error is displayed for billing "<field>"

      Examples:
        | field            |
        | First name       |
        | Last name        |
        | Country / Region |
        | Street address   |
        | Town / City      |
        | State            |
        | ZIP Code         |
        | Email Address    |

    @AEC-156 @pending
    Scenario Outline: Reject non-alphabetical billing names and city
      Given the approved checkout billing fixture is loaded
      When the customer submits checkout with non-alphabetical characters in billing "<field>"
      Then the customer remains on checkout
      And the billing "<field>" is rejected with a field error

      Examples:
        | field       |
        | First name  |
        | Last name   |
        | Town / City |

  Rule: AEC-20 account creation during checkout
    @AEC-149 @pending
    Scenario: Create an account during a successful checkout
      Given the approved checkout billing fixture is loaded
      And an approved account username is configured
      And an approved unique billing email is configured
      When the guest creates an account with that username and places the order
      Then an account is created with the chosen username
      And the order is saved under the new account
      And the customer is signed in on the order-received page

    @AEC-149 @pending
    Scenario: Require a username when creating an account
      Given the approved checkout billing fixture is loaded
      And an approved unique billing email is configured
      When the guest enables account creation without entering a username
      Then the customer remains on checkout and the order is not placed
      And a required-field error is shown for the account username

    @AEC-149 @pending
    Scenario: Require a password when creating an account
      Given the approved checkout billing fixture is loaded
      And an approved account username is configured
      And an approved unique billing email is configured
      When the guest enables account creation and submits checkout with an empty account password
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "Please enter an account password."

    @AEC-149 @pending
    Scenario Outline: Reject an invalid checkout account password
      Given the approved checkout billing fixture is loaded
      And an approved account username is configured
      And an approved unique billing email is configured
      And an approved account password for "<validation>" is configured
      When the guest enables account creation and submits checkout with that password
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "Password must be 8-15 characters and contain uppercase, lowercase, numbers, and special characters."

      Examples:
        | validation       |
        | below_minimum    |
        | above_maximum    |
        | missing_uppercase |
        | missing_lowercase |
        | missing_number    |
        | missing_special   |

    @AEC-149 @pending
    Scenario: Reject an already registered billing email during account creation
      Given the approved checkout billing fixture is loaded
      And an approved account username is configured
      And an approved registered billing email is configured
      When the guest enables account creation and submits checkout with that email
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "An account is already registered with your email address. Please log in."
