
Feature: Search loan transactions
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def loanSearchPayload = read('classpath:templates/searchingpayload.json')

  @search
  Scenario: Search today's loan transactions
    * def today = java.time.LocalDate.now()
    Given configure ssl = true
    Given path 'loans/transactions/search'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When call read('classpath:features/portfolio/loans/createloan.feature@createanddisburseloan')
    And request loanSearchPayload.loanTodaySearchPayload
    When method POST
    Then status 200
    And response.length >0
    And response.length == 2
    And match each response[*].date == today

  @Ignore
  @search
  Scenario: Search loan transactions
    Given configure ssl = true
    Given path 'loans/transactions/search'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loanSearchPayload.loanPreviousSearchPayload
    When call read('classpath:features/portfolio/loans/createloan.feature@createanddisburseloan')
    And request loanSearchPayload.loanPreviousSearchPayload
    When method POST
    Then status 200
    And  response.length > 0
    And  response.length == 2
    And match  each response[*].amount == 100000