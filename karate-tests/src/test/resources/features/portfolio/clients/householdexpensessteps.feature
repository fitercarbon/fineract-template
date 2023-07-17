
Feature: Create Client Household Expenses Steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def householdExpenses = read('classpath:templates/householdexpenses.json')

  @ignore
  @createhouseholdexpenses
  Scenario: Create Client Household Expenses
    Given configure ssl = true
    Given path 'clients',clientId,'householdExpenses'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request householdExpenses.createPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def client = response
