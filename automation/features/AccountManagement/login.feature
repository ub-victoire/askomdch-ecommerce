
@account-management @login
Feature: Customer Login
  As a registered customer
  I want to log in with either my username or my email address
  So that I can access my account dashboard and manage my orders

  Background:
    Given Customer is on the login page
    And Logged out on the account page


  @smoke @high-priority @positive
  Scenario Outline: Login with a registered username
    Given a registered customer exists with username and password
    When the user enters <username> in the "Username or email address" field
    And the user enters <password> in the "Password" field
    And the user clicks the "Log in" button
    Then the user should be redirected to the account dashboard at <account_url>
    And the page heading should be <page_heading>
    And the dashboard greeting should display the username <username>
    And the account navigation should contain the following links: Dashboard, Orders, Downloads, Addresses, Account details, Log out

    Examples: Registered username with a valid password
      | username  | password  | account_url                   | page_heading |
      | aline | Aline123! | https://askomdch.com/account/ | Account      |
      | victor | Victor123! | https://askomdch.com/account/ | Account      |
      | james | James123! | https://askomdch.com/account/ | Account      |

  @smoke @high-priority @positive
  Scenario Outline: Login with a registered email address
    Given a registered customer exists with username <username>, email address <email> and password <password>
    When the user enters <email> in the "Username or email address" field
    And the user enters <password> in the "Password" field
    And the user clicks the "Log in" button
    Then the user should be redirected to the account dashboard at <account_url>
    And the page heading should be <page_heading>
    And the dashboard greeting should display the username <username>
    And the account navigation should contain the following links: Dashboard, Orders, Downloads, Addresses, Account details, Log out

    Examples: Registered email address with a valid password
      | username  | email                    | password  | account_url                   | page_heading |
      | aline | aline@gmail.com | aline123! | https://askomdch.com/account/ | Account      |
      | victor | victor@gmail.com | victor123! | https://askomdch.com/account/ | Account      |
      | james | james@gmail.com | james123! | https://askomdch.com/account/ | Account      |
