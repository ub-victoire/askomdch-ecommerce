Feature: Checkout addresses, account, and order scenarios
  Each scenario checks a separate checkout behavior. Scenario outlines reuse
  the same validation for different fields; they are not duplicate order flows.
  Use approved test data and the site's test payment mode only.

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product
    And the billing form uses these synthetic values:
      | First name     | Casey                      |
      | Last name      | Example                    |
      | Country        | United States              |
      | Street address | 123 Test Street            |
      | Town / City    | Testville                  |
      | State          | California                 |
      | ZIP Code       | 90210                      |
      | Phone          | +15555550199               |
      | Email address  | qa.checkout@example.invalid |

  Rule: AEC-21 alternative shipping
    @AEC-75
    Scenario Outline: Submit an order with a different shipping address
      Given the customer enables a different shipping address
      And the alternative shipping form uses these synthetic values:
        | First name     | <shippingFirstName> |
        | Last name      | <shippingLastName> |
        | Country        | United States |
        | Street address | <shippingAddress> |
        | Town / City    | <shippingCity> |
        | State          | California |
        | ZIP Code       | <shippingPostcode> |
      When the customer submits the order with the alternative shipping address
      Then the order-received shipping address matches those values

      Examples:
        | shippingFirstName | shippingLastName | shippingAddress      | shippingCity | shippingPostcode |
        | Taylor            | Sample            | 456 Sample Avenue    | Sampletown   | 90211            |

    @AEC-117
    Scenario Outline: Reject a missing mandatory shipping field
      Given the customer enables a different shipping address
      And the alternative shipping form uses these synthetic values:
        | First name     | Taylor            |
        | Last name      | Sample            |
        | Country        | United States     |
        | Street address | 456 Sample Avenue |
        | Town / City    | Sampletown        |
        | State          | California        |
        | ZIP Code       | 90211             |
      When the customer submits checkout with the "<field>" shipping field empty
      Then the customer remains on the checkout page
      And the checkout shows "<error>"

      Examples:
        | field          | error                                      |
        | First name     | Shipping First name is a required field.  |
        | Last name      | Shipping Last name is a required field.   |
        | Street address | Shipping Street address is a required field. |
        | Town / City    | Shipping Town / City is a required field. |
        | ZIP Code       | Shipping ZIP Code is a required field.    |

    @AEC-118
    Scenario Outline: Accept values at the alternative shipping field limits
      Given the customer enables a different shipping address
      And the alternative shipping form uses these synthetic values:
        | First name     | Taylor            |
        | Last name      | Sample            |
        | Country        | United States     |
        | Street address | 456 Sample Avenue |
        | Town / City    | Sampletown        |
        | State          | California        |
        | ZIP Code       | 90211             |
      When the customer enters a shipping "<field>" value at the documented limit of <limit> characters
      And the customer submits checkout
      Then the shipping "<field>" value is accepted

      Examples:
        | field          | limit |
        | First name     | 50    |
        | Last name      | 50    |
        | Town / City    | 50    |
        | Street address | 100   |
        | Order notes    | 500   |

    @AEC-118
    Scenario Outline: Reject alternative shipping values over the field limits
      Given the customer enables a different shipping address
      And the alternative shipping form uses these synthetic values:
        | First name     | Taylor            |
        | Last name      | Sample            |
        | Country        | United States     |
        | Street address | 456 Sample Avenue |
        | Town / City    | Sampletown        |
        | State          | California        |
        | ZIP Code       | 90211             |
      When the customer enters a shipping "<field>" value with <limitPlusOne> characters
      And the customer submits checkout
      Then the customer remains on the checkout page
      And the shipping "<field>" value is rejected

      Examples:
        | field          | limitPlusOne |
        | First name     | 51           |
        | Last name      | 51           |
        | Town / City    | 51           |
        | Street address | 101          |
        | Order notes    | 501          |

    @AEC-121
    Scenario Outline: Reject non-alphabetical shipping names and city
      Given the customer enables a different shipping address
      And the alternative shipping form uses these synthetic values:
        | First name     | Taylor            |
        | Last name      | Sample            |
        | Country        | United States     |
        | Street address | 456 Sample Avenue |
        | Town / City    | Sampletown        |
        | State          | California        |
        | ZIP Code       | 90211             |
      When the customer enters "<value>" in shipping "<field>"
      And the customer submits checkout
      Then the customer remains on the checkout page
      And the invalid shipping "<field>" is highlighted with a red outline
      And a specific shipping field error is displayed

      Examples:
        | field       | value     |
        | First name  | Taylor123 |
        | Last name   | Sample123 |
        | Town / City | City123   |

  Rule: AEC-22 order confirmation
    @AEC-157
    Scenario: Show order details after placing an order
      When the customer submits a valid order
      Then the customer is on the order-received page
      And the page shows the order number, date, status, total, and item details

  Rule: AEC-23 billing and payment
    @AEC-148
    Scenario Outline: Accept billing values at the field limits
      When the customer enters a billing "<field>" value with <limit> characters
      And the customer submits checkout
      Then the billing "<field>" value is accepted

      Examples:
        | field          | limit |
        | First name     | 50    |
        | Last name      | 50    |
        | Town / City    | 50    |
        | Street address | 100   |
        | Phone          | 15    |

    @AEC-148
    Scenario Outline: Reject billing values over the field limits
      When the customer enters a billing "<field>" value with <limitPlusOne> characters
      And the customer submits checkout
      Then the customer remains on the checkout page
      And the billing "<field>" value is rejected

      Examples:
        | field          | limitPlusOne |
        | First name     | 51           |
        | Last name      | 51           |
        | Town / City    | 51           |
        | Street address | 101          |
        | Phone          | 16           |

    @AEC-153
    Scenario Outline: Reject a missing mandatory billing field
      When the customer submits checkout with the "<field>" billing field empty
      Then the customer remains on the checkout page
      And the checkout shows "<error>"

      Examples:
        | field          | error                                     |
        | First name     | Billing First name is a required field.  |
        | Last name      | Billing Last name is a required field.   |
        | Street address | Billing Street address is a required field. |
        | Town / City    | Billing Town / City is a required field. |
        | ZIP Code       | Billing ZIP Code is a required field.    |
        | Email address  | Billing Email address is a required field. |

    @AEC-23
    Scenario Outline: Reject an invalid billing email
      When the customer submits checkout with email "<email>"
      Then the customer remains on the checkout page
      And the checkout shows "Invalid billing email address"

      Examples:
        | email       |
        | not-an-email |

    @AEC-23
    Scenario Outline: Reject an invalid billing phone number
      When the customer enters phone "<phone>"
      And the customer submits checkout
      Then the customer remains on the checkout page
      And a billing phone validation error is displayed

      Examples:
        | phone        |
        | 555-CALL-NOW |

    @AEC-23
    Scenario Outline: Reject an invalid billing postcode
      When the customer enters postcode "<postcode>"
      And the customer submits checkout
      Then the customer remains on the checkout page
      And a billing postcode validation error is displayed

      Examples:
        | postcode |
        | INVALID! |

    @AEC-156
    Scenario Outline: Reject non-alphabetical billing names and city
      When the customer enters "<value>" in billing "<field>"
      And the customer submits checkout
      Then the customer remains on checkout
      And the billing "<field>" is rejected with a field error

      Examples:
        | field       | value     |
        | First name  | Casey123  |
        | Last name   | Example123 |
        | Town / City | City123   |

  Rule: AEC-20 account creation during checkout
    @AEC-149
    Scenario Outline: Create an account during a successful checkout
      Given the billing email is "<email>"
      When the guest enables account creation and enters username "<username>"
      And the customer places the order
      Then an account is created with the chosen username
      And the order is saved under the new account
      And the customer is signed in on the order-received page

      Examples:
        | username          | email                              |
        | qa-checkout-casey | qa.checkout@example.invalid       |

    @AEC-149
    Scenario: Require a username when creating an account
      Given an approved unique account email is configured
      When the guest enables account creation without entering a username
      Then the customer remains on checkout and the order is not placed
      And a required-field error is shown for the account username

    @AEC-149
    Scenario: Require a password when creating an account
      Given an approved account username is configured
      When the guest enables account creation and submits checkout with an empty account password
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "Please enter an account password."

    @AEC-149
    Scenario Outline: Reject an invalid checkout account password
      Given an approved account username is configured
      And an approved unique account email is configured
      When the guest enables account creation and uses password "<password>"
      And the guest submits checkout
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "Password must be 8-15 characters and contain uppercase, lowercase, numbers, and special characters."

      Examples:
        | password          |
        | Qa1!              |
        | Qa1!abcdefghijkl  |
        | qa1!test          |
        | QA1!TEST          |
        | Qa!testx          |
        | Qa1testx          |

    @AEC-149
    Scenario Outline: Accept account passwords at the length limits
      Given the billing email is "<email>"
      When the guest enables account creation and enters username "<username>" and password "<password>"
      And the customer submits the order
      Then the account is created and the order is submitted

      Examples:
        | username          | email                               | password        |
        | qa-checkout-min   | qa.checkout.min@example.invalid    | Qa1!test        |
        | qa-checkout-max   | qa.checkout.max@example.invalid    | Qa1!abcdefghijk |

    @AEC-149
    Scenario: Reject an already used account username
      Given an approved registered account username is configured
      And a different unique billing email is configured
      When the guest creates an account using that username
      And the customer submits checkout
      Then the customer remains on checkout and the order is not placed
      And the checkout shows a username-already-in-use error

    @AEC-149
    Scenario: Reject an already registered billing email during account creation
      Given an approved account username is configured
      And an approved registered account email is configured
      When the guest enables account creation and submits checkout with that email
      Then the customer remains on checkout and the order is not placed
      And the checkout error is "An account is already registered with your email address. Please log in."
