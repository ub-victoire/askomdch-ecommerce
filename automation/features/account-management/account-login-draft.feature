@account_login
Feature: Account login and session handling
  Scenarios derived from AEC-6 and the linked Jira Test descriptions.
  Original Xray steps are unavailable; fixtures and outcomes require review.

  Background:
    Given an authorized non-production environment with a registered synthetic test account
    And the customer opens the account login form

  Rule: AEC-6 accepts valid registered credentials
    @AEC-90
    Scenario: Log in with a registered username
      When the customer logs in using the registered username and password
      Then the customer sees the account dashboard
      And the dashboard displays "Hello" followed by the username

    @AEC-91
    Scenario: Log in with a registered email
      When the customer logs in using the registered email and password
      Then the customer sees the account dashboard
      And the dashboard displays "Hello" followed by the username

    @AEC-92
    Scenario: Match the username without regard to letter case
      When the customer logs in using a different-case registered username and the matching password
      Then the customer sees the account dashboard

    @AEC-93 @needs_clarification @pending
    Scenario: Ignore spaces around a registered username
      When the customer logs in using the registered username with leading and trailing spaces and the matching password
      Then the customer sees the account dashboard
      # AEC-6 specifies username trimming; the Test description refers to credentials.

  Rule: AEC-6 rejects invalid credentials
    @AEC-94 @needs_clarification @pending
    Scenario: Reject an incorrect password for a registered username
      When the customer logs in using the registered username and an incorrect password
      Then the customer remains on the account page
      And the incorrect-password error names the registered username
      And the "Lost your password?" link is displayed
      # The Jira title says invalid password, but its description says the password is required.

    @AEC-94 @needs_clarification @pending
    Scenario: Reject an empty login password
      When the customer logs in using the registered username and an empty password
      Then the customer remains on the account page
      And the login error is "The password field is empty."

  Rule: AEC-6 controls session persistence
    @AEC-95
    Scenario: Retain the session when Remember me is checked
      When the customer logs in with Remember me checked
      And the customer closes and reopens the browser with the same profile
      And the customer opens the account page
      Then the customer sees the account dashboard

    @AEC-96
    Scenario: End the session when Remember me is unchecked
      When the customer logs in with Remember me unchecked
      And the customer closes and reopens the browser with the same profile
      And the customer opens the account page
      Then the account login form is displayed

    @AEC-97
    Scenario: Open the account page while logged in
      Given the customer is logged in to the test account
      When the customer opens the account page
      Then the customer sees the account dashboard
      And the account login form is not displayed

  Rule: AEC-6 protects the displayed password
    @AEC-98
    Scenario: Mask the password and keep it out of the URL
      When the customer enters the test account password
      Then the password is masked on screen
      When the customer submits the login form
      Then the password does not appear in the browser URL
