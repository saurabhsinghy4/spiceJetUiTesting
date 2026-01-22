Feature: SpiceJet smoke tests

  @smoke
  Scenario: Open SpiceJet home page and validate basic UI
    Given I open the SpiceJet home page
    Then the page title should contain "SpiceJet"
    And the page should show a visible "Login" entry point

  @smoke
  Scenario: Open SpiceJet search with two adults and one child
    When I open the prefilled SpiceJet search for two adults and one child
    Then the search URL should reflect DEL to LKO with two adults and one child

