
Feature: Search savings transactions
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def savingsSearchPayload = read('classpath:templates/searchingpayload.json')
  @Ignore
  @search
  Scenario: Search today's savings transactions
    Given configure ssl = true
    Given path 'savingsaccounts/transactions/search'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request savingsSearchPayload.savingsTodaySearchPayload
    When method POST
    Then status 200
    And  response.length > 0
    And  response.length == 2
    And match each response[*].date == today

  @search
  Scenario: Search savings transactions
    Given configure ssl = true
    Given path 'savingsaccounts/transactions/search'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request savingsSearchPayload.savingsPreviousSearchPayload
    When call read('classpath:features/portfolio/savingsaccount/savings.feature@createandactivateaccount')
    And request savingsSearchPayload.loanPreviousSearchPayload
    When method POST
    Then status 200
    And  response.length > 0
    And  response.length == 1
    And match  each response[*].amount == 1000
