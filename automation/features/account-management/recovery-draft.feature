@account_recovery
Feature: Account password recovery
  Scenarios derived from AEC-8 and the linked Jira Test descriptions.
  A controlled mailbox and disposable test accounts are required.

  Background:
    Given an authorized non-production environment and a controlled synthetic test mailbox
    And the customer opens the password recovery form

  Rule: AEC-8 sends a recovery link for a registered account
    @AEC-124
    Scenario: Request a reset link using a registered username
      Given a registered synthetic test account with access to its mailbox
      When the customer requests a password reset using the registered username
      Then the recovery notice is "Password reset email has been sent."
      And a password reset link is sent to the account mailbox

    @AEC-125
    Scenario: Request a reset link using a registered email
      Given a registered synthetic test account with access to its mailbox
      When the customer requests a password reset using the registered email
      Then the recovery notice is "Password reset email has been sent."
      And a password reset link is sent to the account mailbox

    @AEC-131 @account_login
    Scenario: Regain access using the emailed reset link
      Given a registered synthetic test account with access to its mailbox
      And a password reset link has been requested for that account
      When the customer uses the emailed link to set a new approved test password
      Then the customer can log in with the new password
      And the customer cannot log in with the old password

  Rule: AEC-8 rejects invalid recovery input
    @AEC-126
    Scenario: Reject an empty recovery field
      When the customer requests a password reset with an empty field
      Then the customer remains on the recovery page
      And the recovery error is "Enter a username or email address."
      And no password reset email is observed within the configured mail-delivery window

    @AEC-127
    Scenario: Reject an unregistered username
      Given a confirmed unregistered synthetic username
      When the customer requests a password reset using that username
      Then the customer remains on the recovery page
      And the recovery error is "Invalid username or email."
      And no password reset email is observed within the configured mail-delivery window

    @AEC-130
    Scenario: Reject an email address with an invalid format
      Given an invalid synthetic email address
      When the customer requests a password reset using that email
      Then the customer remains on the recovery page
      And the recovery error is "There is no account with that username or email address."
      And no password reset email is observed within the configured mail-delivery window
