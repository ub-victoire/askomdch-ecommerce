@cart_session @checkout_login
Feature: Checkout login scenarios

  Background:
    Given an authorized test environment is configured
    And the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product
    And the customer opens the checkout login form

  Rule: AEC-17 registered customer login
    @AEC-103
    Scenario: Saved addresses populate after checkout login
      Given an approved registered test account has known saved billing and shipping addresses
      When the customer logs in with the test account
      Then the checkout billing and shipping fields contain the account's saved addresses

    @AEC-99
    Scenario: Remember me retains the checkout login session
      Given an approved registered test account is available
      When the customer logs in with Remember me selected
      And the customer closes and reopens the browser using the same profile
      Then the login session is retained

  Rule: AEC-17 invalid checkout login
    @AEC-79
    Scenario Outline: Required login fields show their specified errors
      When the customer submits checkout login with an empty "<field>" field
      Then the customer remains on the checkout page
      And the checkout login displays "<message>"

      Examples:
        | field            | message                            |
        | username or email | Error: Username is required         |
        | password          | Error: the password field is empyt  |

    @AEC-82
    Scenario: Reject an incorrect password for a registered account
      Given an approved registered test account is available
      When the customer submits its username with an incorrect password
      Then the customer remains on the checkout page
      And the AEC-17 incorrect-password error and lost-password link are displayed

    @AEC-86
    Scenario Outline: Reject an unregistered username or email
      When the customer submits an unregistered "<identifier>" through checkout login
      Then the customer remains on the checkout page
      And the checkout login displays "<message>"

      Examples:
        | identifier | message |
        | username   | The username ad is not registered on this site. If you are unsure of your username, try your email address instead. |
        | email      | Unknown email address. Check again or try your username. |

    @AEC-109
    Scenario: Failed login stays on checkout
      Given an approved registered test account is available
      When the customer submits its username with an incorrect password
      Then the customer remains on the checkout page
