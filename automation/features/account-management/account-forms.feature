Feature: Account forms
  The existing Jira tests check the visible controls without submitting data.

  Rule: AEC-5 registration form controls
    Background:
      Given an authorized test environment is configured
      When the customer opens the account page

    @AEC-48
    Scenario Outline: Registration displays its required controls
      Then the registration form displays the "<control>" control

      Examples:
        | control  |
        | username |
        | email    |
        | password |
        | register |

  Rule: AEC-6 login form controls
    Background:
      Given an authorized test environment is configured
      When the customer opens the account page

    @AEC-89
    Scenario: Login displays its inputs, Remember me, and the login button
      Then the login form displays its required controls
