@cart_session @checkout_login
Feature: Checkout login and validation

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product
    And the customer opens the checkout login form

  Rule: AEC-17 saved account details and session
    @AEC-103 @phase-1
    Scenario: Saved addresses populate after checkout login
      Given an approved registered test account has known saved billing and shipping addresses
      When the customer logs in with the test account
      Then the checkout billing and shipping fields contain the account's saved addresses

    @AEC-99 @phase-4
    Scenario: Remember me retains the checkout login session
      Given an approved registered test account is available
      When the customer logs in with Remember me selected
      And the customer closes and reopens the browser using the same profile
      Then the login session is retained

  Rule: AEC-17 invalid checkout login
    @AEC-79 @phase-1
    Scenario Outline: Show an error when a required login field is empty
      When the customer submits checkout login with an empty "<field>" field
      Then the customer remains on the checkout page
      And the checkout login displays "<message>"

      Examples:
        | field             | message                            |
        | username or email | Error: Username is required.        |
        | password          | Error: The password field is empty. |

    @AEC-82 @AEC-109 @phase-1
    Scenario: Reject an incorrect password for a registered account
      Given an approved registered test account is available
      When the customer submits its username with an incorrect password
      Then the customer remains on the checkout page
      And the incorrect-password error and lost-password link are displayed

    @AEC-86
    Scenario Outline: Reject an unregistered username
      When the customer submits the unregistered username "<username>" through checkout login
      Then the customer remains on the checkout page
      And the error says that the submitted username is not registered

      Examples:
        | username                  |
        | qa-user-that-does-not-exist |

    @AEC-86
    Scenario Outline: Reject an unregistered email address
      When the customer submits the unregistered email address "<email>" through checkout login
      Then the customer remains on the checkout page
      And the checkout login displays "Unknown email address. Check again or try your username."

      Examples:
        | email                         |
        | unknown.checkout@example.invalid |
