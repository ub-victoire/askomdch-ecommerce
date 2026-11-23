
@account-management @password-reset
Feature: Password Reset
  As a registered customer who has forgotten my password
  I want to request a password reset link using my username or my email address
  So that I can regain access to my account

  Background:
    Given Given Customer is on the login page
    And Logged out on the account page

  
  @smoke @high-priority @positive
  Scenario Outline: Password reset link is sent for a registered username
    Given a registered customer exists with username and email address
    And the mailbox for <email> contains no previous password reset emails
    When the user enters <username> in the "Username or email" field
    And the user clicks the "Reset password" button
    Then the confirmation message <confirmation_message> should be displayed
    And a password reset email should be received at <email> within <wait_time_minutes> seconds
    And the email subject should contain <email_subject_text>
    And the email body should reference the username <username>
    And the email body should contain a password reset link
    And the password reset link should start with <reset_link_prefix>

    Examples: Registered username
      | username  | email                    | confirmation_message                | wait_time_minutes | email_subject_text | reset_link_prefix                           |
      | aline | aline@gmail.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |
      | victor | victor@gmail.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |
      | james | james@gmail.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |


  @smoke @high-priority @positive
  Scenario Outline: Password reset link is sent for a registered email address
    Given a registered customer exists with username and email address
    And the mailbox for <email> contains no previous password reset emails
    When the user enters <email> in the "Username or email" field
    And the user clicks the "Reset password" button
    Then the confirmation message <confirmation_message> should be displayed
    And a password reset email should be received at <email> within <wait_time_minutes> seconds
    And the email subject should contain <email_subject_text>
    And the email body should reference the username <username>
    And the email body should contain a password reset link
    And the password reset link should start with <reset_link_prefix>

    Examples: Registered email address
      | username  | email                    | confirmation_message                | wait_time_minutes | email_subject_text | reset_link_prefix                           |
      | aline | aline@gmail.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |
      | victor | victor@gmail.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |
      | james | james@gmail.com | Password reset email has been sent. | 10                | Password Reset     | https://askomdch.com/account/lost-password/ |
