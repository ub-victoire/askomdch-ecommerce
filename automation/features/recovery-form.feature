Feature: Account recovery form

  Rule: AEC-8 lost-password form
    @AEC-123
    Scenario: Lost-password form displays its instruction and controls
      Given an authorized test environment is configured
      When the customer opens the lost-password page
      Then the lost-password form displays its instruction and required controls
