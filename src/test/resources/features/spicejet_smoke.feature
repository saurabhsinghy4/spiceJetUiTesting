Feature: SpiceJet smoke tests

  @smoke
  Scenario: Open SpiceJet home page and validate basic UI
    Given I open the SpiceJet home page
    Then the page title should contain "SpiceJet"
    And the page should show a visible "Login" entry point

  @smoke
  Scenario: Select two adults and one child as passengers
    Given I open the SpiceJet home page
    When I select 2 adults and 1 child as passengers
    Then the page title should contain "SpiceJet"

