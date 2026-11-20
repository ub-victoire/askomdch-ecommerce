@account_registration
Feature: Account registration
  Scenarios derived from AEC-5 and the linked Jira Test descriptions.

  Background:
    Given an authorized non-production environment with disposable synthetic registration fixtures
    And the customer opens the registration form

  Rule: AEC-5 requires a unique valid username and email
    @AEC-40
    Scenario Outline: Register with valid usernames at the documented boundaries
      Given an unused synthetic username with "<username variation>"
      And an unused valid synthetic email and valid password
      When the customer submits the registration form
      Then the account is created
      And the customer sees the account dashboard

      Examples:
        | username variation          |
        | the minimum of 5 characters |
        | the maximum of 10 characters |
        | mixed uppercase and lowercase letters |

    @AEC-42
    Scenario: Reject an existing username
      Given an existing synthetic account username
      And an unused valid synthetic email and valid password
      When the customer submits the registration form with the existing username
      Then registration is rejected
      And the registration error is "Error: An account is already registered with that username. Please choose another."

    @AEC-44
    Scenario: Register with a valid email address
      Given an unused valid synthetic username and email
      And a valid synthetic password
      When the customer submits the registration form
      Then the account is created
      And the customer sees the account dashboard

    @AEC-45
    Scenario: Reject an email address without an at sign
      Given an unused valid synthetic username and valid password
      And a synthetic email address without an at sign
      When the customer submits the registration form
      Then the customer remains on the account page
      And the registration validation message is "Please include @ in the email address"

    @AEC-49
    Scenario: Reject an empty registration email
      Given an unused valid synthetic username and valid password
      And an empty registration email
      When the customer submits the registration form
      Then the customer remains on the account page
      And the registration error is "Please provide a valid email address"

    @AEC-51
    Scenario: Reject an existing registration email
      Given an existing synthetic account email
      And an unused valid synthetic username and valid password
      When the customer submits the registration form with the existing email
      Then registration is rejected
      And the registration error is "Error: An account is already registered with your email address. Please log in."

  Rule: AEC-5 requires a valid password
    @AEC-54
    Scenario: Reject an empty registration password
      Given an unused valid synthetic username and email
      And an empty registration password
      When the customer submits the registration form
      Then the customer remains on the account page
      And the registration error is "Please enter an account password"

    @AEC-57 @needs_clarification @pending
    Scenario: Reject a blank registration form
      Given all registration fields are empty
      When the customer submits the registration form
      Then the customer remains on the account page
      And registration is rejected
      # AEC-5 specifies individual field errors, but not their order for a blank form.

  Rule: AEC-5 creates and authenticates a registered customer
    @AEC-58
    Scenario: Register and open the account dashboard
      Given unused valid synthetic registration details
      When the customer submits the registration form
      Then the account is created
      And the customer is automatically logged in
      And the account dashboard displays "Hello" followed by the username
      And the account navigation includes Dashboard, Orders, Downloads, Addresses, Account details, and Logout

    @AEC-60
    @account_login
    Scenario: Log in with the newly registered username and email
      Given the customer has registered a disposable synthetic account
      And the customer has logged out
      When the customer logs in with the new username and password
      Then the customer sees the account dashboard
      When the customer logs out and logs in with the new email and password
      Then the customer sees the account dashboard
