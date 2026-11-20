@cart_session @checkout_order
Feature: Checkout submission scenarios
  Run only with authorized synthetic fixtures, test payments, and an approved cleanup process.
  Pending scenarios still require stronger assertions and local fixture validation.

  Background:
    Given an authorized test environment is configured
    And the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product

  Rule: AEC-21 alternative shipping
    @AEC-75
    Scenario: Place an order with a valid alternative shipping address
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
        | First Name       |
        | Last Name        |
        | Country / Region |
        | Street Address   |
        | Town / City      |
        | State / County   |
        | Postcode / ZIP   |

    @AEC-118 @pending
    Scenario: Accept documented maximum lengths for alternative shipping
      Given the approved checkout billing fixture is loaded
      And the approved alternative shipping fixture is loaded
      And the alternative shipping fixture uses the documented maximum lengths:
        | First Name     | 50  |
        | Last Name      | 50  |
        | Town / City    | 50  |
        | Street Address | 100 |
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
        | First Name  |
        | Last Name   |
        | Town / City |

  Rule: AEC-23 billing and payment
    @AEC-148 @pending
    Scenario: Accept documented maximum lengths for billing fields
      Given the approved checkout billing fixture is loaded
      And the billing fixture uses the documented maximum lengths:
        | First Name     | 50  |
        | Last Name      | 50  |
        | Town / City    | 50  |
        | Street Address | 100 |
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
        | First Name       |
        | Last Name        |
        | Country / Region |
        | Street Address   |
        | Town / City      |
        | State / County   |
        | Postcode / ZIP   |
        | Phone            |
        | Email Address    |

    @AEC-156 @pending
    Scenario Outline: Reject non-alphabetical billing names and city
      Given the approved checkout billing fixture is loaded
      When the customer submits checkout with non-alphabetical characters in billing "<field>"
      Then the billing "<field>" is rejected

      Examples:
        | field       |
        | First Name  |
        | Last Name   |
        | Town / City |

  Rule: AEC-20 account creation during checkout
    @AEC-149 @pending
    Scenario: Create an account during a successful checkout
      Given the approved checkout billing fixture is loaded
      And an approved unique billing email is configured
      When the guest enables account creation and places the order
      Then an account is created using the billing email as its login identifier
      And the order is saved under the new account
      And the customer is authenticated on the order-received page

    @AEC-149 @pending
    Scenario: Reject an empty checkout account password
      Given the approved checkout billing fixture is loaded
      And an approved unique billing email is configured
      When the guest enables account creation and submits checkout with an empty account password
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "Please enter an account password."

    @AEC-149 @pending
    Scenario Outline: Reject an invalid checkout account password
      Given the approved checkout billing fixture is loaded
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
      And an approved registered billing email is configured
      When the guest enables account creation and submits checkout with that email
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "An account is already registered with your email address. Please log in."
