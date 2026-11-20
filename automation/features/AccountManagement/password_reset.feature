
@account-management @password-reset
Feature: Password Reset
  As a registered customer who has forgotten my password
  I want to request a password reset link using my username or my email address
  So that I can regain access to my account

  Background:
    Given no user session is active in the browser
    And the user navigates to the Account page at "https://askomdch.com/account/"
    When the user clicks the "Lost your password?" link
    Then the user should be redirected to "https://askomdch.com/account/lost-password/"
    And the page should display the instruction text "Lost your password? Please enter your username or email address. You will receive a link to create a new password via email."
    And the "Username or email" field should be visible and empty
    And the "Reset password" button should be visible and enabled

  
  @smoke @high-priority @positive
  Scenario Outline: Password reset link is sent for a registered username
    Given a registered customer exists with username <username> and email address <email>
    And the mailbox for <email> contains no previous password reset emails
    When the user enters <username> in the "Username or email" field
    And the user clicks the "Reset password" button
    Then the confirmation message <confirmation_message> should be displayed
    And no error message should be displayed
    And a password reset email should be received at <email> within <wait_time_minutes> seconds
    And the email subject should contain <email_subject_text>
    And the email body should reference the username <username>
    And the email body should contain a password reset link
    And the password reset link should start with <reset_link_prefix>
    And the password reset link should contain the query parameter "key"

    Examples: Registered username
      | username  | email                    | confirmation_message                | wait_time_minutes | email_subject_text | reset_link_prefix                           |
      | aline | aline@gmail.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |


  @smoke @high-priority @positive
  Scenario Outline: Password reset link is sent for a registered email address
    Given a registered customer exists with username <username> and email address <email>
    And the mailbox for <email> contains no previous password reset emails
    When the user enters <email> in the "Username or email" field
    And the user clicks the "Reset password" button
    Then the confirmation message <confirmation_message> should be displayed
    And no error message should be displayed
    And a password reset email should be received at <email> within <wait_time_minutes> seconds
    And the email subject should contain <email_subject_text>
    And the email body should reference the username <username>
    And the email body should contain a password reset link
    And the password reset link should start with <reset_link_prefix>
    And the password reset link should contain the query parameter "key"

    Examples: Registered email address
      | username  | email                    | confirmation_message                | wait_time_minutes | email_subject_text | reset_link_prefix                           |
      | qa_user01 | qa.user01@mailinator.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |
